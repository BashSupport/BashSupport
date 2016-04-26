/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: PluginErrorReportSubmitter.java, Class: PluginErrorReportSubmitter
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package nu.studer.idea.errorreporting;

import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Consumer;
import com.intellij.util.net.HttpConfigurable;
import com.intellij.util.net.IOExceptionDialog;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Properties;

/**
 * This class is notified about errors caused by its owning plugin. It bundles the information to be sent to the error receiving server. Configuration
 * options like the email recipient etc. are first extracted from the plugin descriptor (vendor's email etc.) but can be overwritten through
 * properties specified in the properties file (email.to, email.cc, and server.address).
 * <br>
 * An indirection is applied when looking up the error receiving server address. This allows to change the location, i.e. address of the error
 * receiving server without having to reconfigure/recompile the plugin (all that needs to be changed is the server address returned by the lookup
 * server).
 *
 * @author <a href="mailto:intellij@studer.nu">Etienne Studer</a>, May 17, 2006
 * @see TextStreamLoggingEventSubmitter
 */
@SuppressWarnings({"AnalyzingLoggingWithoutLogLevelCheck"})
public class PluginErrorReportSubmitter extends ErrorReportSubmitter {
    private static final Logger LOGGER = Logger.getInstance(TextStreamLoggingEventSubmitter.class.getName());

    @NonNls
    private static final String SERVER_LOOKUP_URL = "https://www.ansorg-it.com/bashsupport/errorReceiverRedirect.txt";
    @NonNls
    private static final String FALLBACK_SERVER_URL = "https://www.ansorg-it.com/bashsupport/errorReceiver.pl";
    @NonNls
    private static final String ERROR_SUBMITTER_PROPERTIES_PATH = "errorReporter.properties";

    @NonNls
    private static final String PLUGIN_ID_PROPERTY_KEY = "plugin.id";
    @NonNls
    private static final String PLUGIN_NAME_PROPERTY_KEY = "plugin.name";
    @NonNls
    private static final String PLUGIN_VERSION_PROPERTY_KEY = "plugin.version";
    @NonNls
    private static final String EMAIL_TO_PROPERTY_KEY = "email.to";
    @NonNls
    private static final String EMAIL_CC_PROPERTY_KEY = "email.cc";
    @NonNls
    private static final String SERVER_PROPERTY_KEY = "server.address";

    @NonNls
    private String serverUrl;

    public String getReportActionText() {
        return "Report error to plugin vendor";
    }

    @Override
    public SubmittedReportInfo submit(IdeaLoggingEvent[] events, final Component parentComponent) {
        final DataContext dataContext = DataManager.getInstance().getDataContext(parentComponent);
        final Project project = CommonDataKeys.PROJECT.getData(dataContext);

        StringBuilder stacktrace = new StringBuilder();
        for (IdeaLoggingEvent event : events) {
            stacktrace.append(event.getMessage()).append("\n");
            stacktrace.append(event.getThrowableText()).append("\n");
        }

        Properties properties = new Properties();
        queryPluginDescriptor(getPluginDescriptor(), properties);

        StringBuilder versionId = new StringBuilder();
        versionId.append(properties.getProperty(PLUGIN_ID_PROPERTY_KEY)).append(" ").append(properties.getProperty(PLUGIN_VERSION_PROPERTY_KEY));
        versionId.append(", ").append(ApplicationInfo.getInstance().getBuild().asString());

        // show modal error submission dialog
        PluginErrorSubmitDialog dialog = new PluginErrorSubmitDialog(parentComponent);
        dialog.prepare("", stacktrace.toString(), versionId.toString());
        dialog.show();

        final SubmittedReportInfo[] result = {null};

        // submit error to server if user pressed SEND
        int code = dialog.getExitCode();
        if (code == DialogWrapper.OK_EXIT_CODE) {
            dialog.persist();

            String description = dialog.getDescription();
            String user = dialog.getUser();
            String editedStacktrace = dialog.getStackTrace();

            submitToServer(project, editedStacktrace, description, user,
                    new Consumer<SubmittedReportInfo>() {
                        @Override
                        public void consume(SubmittedReportInfo submittedReportInfo) {
                            result[0] = submittedReportInfo;
                            //Messages.showInfoMessage(parentComponent, PluginErrorReportSubmitterBundle.message("successful.dialog.message"), PluginErrorReportSubmitterBundle.message("successful.dialog.title"));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void consume(Throwable throwable) {
                            LOGGER.info("Error submission failed", throwable);
                            result[0 ] = new SubmittedReportInfo("http://www.ansorg-it.com/en/products_bashsupport.html", "BashSupport", SubmittedReportInfo.SubmissionStatus.FAILED);
                        }
                    }
            );
        }

        return result[0];
    }

    private void submitToServer(Project project,
                                final String stacktrace,
                                @Nullable final String description,
                                @Nullable final String user,
                                final Consumer<SubmittedReportInfo> successConsumer,
                                Consumer<Throwable> errorConsumer) {

        PluginDescriptor pluginDescriptor = getPluginDescriptor();

        // the properties that define the error report content/envelope
        final Properties properties = new Properties();

        // set default server address (through look-up on intellij.net)
        @NonNls final String defaultServerUrl = getServerUrl();
        properties.put(SERVER_PROPERTY_KEY, defaultServerUrl);

        // first, query the plugin descriptor and try to extract as much information as possible
        queryPluginDescriptor(pluginDescriptor, properties);
        LOGGER.debug("Properties read from plugin descriptor: " + properties);

        // second, try to read the settings from the optional properties file (and override any previous properties)
        queryPropertiesFile(pluginDescriptor, properties);
        LOGGER.debug("Final properties to be applied: " + properties);

        // the final server address
        @NonNls final String serverUrl = properties.getProperty(SERVER_PROPERTY_KEY);

        // check if connection to server can be established, i.e. proxy settings are correct
        if (!tryConnectOnly(serverUrl)) {
            errorConsumer.consume(null);
            return;
        }

        Runnable task = new Runnable() {
            @Override
            public void run() {
                LoggingEventSubmitter submitter = new TextStreamLoggingEventSubmitter(serverUrl);
                submitter.setPluginId(properties.getProperty(PLUGIN_ID_PROPERTY_KEY));
                submitter.setPluginName(properties.getProperty(PLUGIN_NAME_PROPERTY_KEY));
                submitter.setPluginVersion(properties.getProperty(PLUGIN_VERSION_PROPERTY_KEY));
                submitter.setIdeaBuild(ApplicationInfo.getInstance().getBuild().asString());
                submitter.setEmailTo(splitByBlanks(properties.getProperty(EMAIL_TO_PROPERTY_KEY)));
                submitter.setEmailCc(splitByBlanks(properties.getProperty(EMAIL_CC_PROPERTY_KEY)));

                try {
                    submitter.submit(stacktrace, description, user);

                    successConsumer.consume(new SubmittedReportInfo("http://www.ansorg-it.com/en/products_bashsupport.html", "BashSupport", SubmittedReportInfo.SubmissionStatus.NEW_ISSUE));
                } catch (LoggingEventSubmitter.SubmitException e) {
                    //ignore
                }
            }
        };

        ProgressManager.getInstance().runProcessWithProgressSynchronously(task, "", false, project);
    }

    private boolean tryConnectOnly(String serverUrl) {
        boolean tryAgain = false;
        do {
            try {
                HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
                httpConfigurable.prepareURL(serverUrl);
            } catch (IOException ioe) {
                LOGGER.info("Connection error", ioe);
                tryAgain = IOExceptionDialog.showErrorDialog("Error",
                        String.format("Unable to connect to \"%s\". Make sure your proxy settings are correct.", serverUrl)
                );

                // abort if cannot connect to server and user does not want to try again
                if (!tryAgain) {
                    return false;
                }
            }
        } while (tryAgain);

        return true;
    }

    private void queryPluginDescriptor(@NotNull PluginDescriptor pluginDescriptor, @NotNull Properties properties) {
        PluginId descPluginId = pluginDescriptor.getPluginId();
        if (descPluginId != null) {
            String pluginIdString = descPluginId.getIdString();
            if (!StringUtil.isEmptyOrSpaces(pluginIdString)) {
                properties.put(PLUGIN_ID_PROPERTY_KEY, pluginIdString);
            }
        }

        if (pluginDescriptor instanceof IdeaPluginDescriptor) {
            IdeaPluginDescriptor ideaPluginDescriptor = (IdeaPluginDescriptor) pluginDescriptor;

            String descName = ideaPluginDescriptor.getName();
            if (!StringUtil.isEmptyOrSpaces(descName)) {
                properties.put(PLUGIN_NAME_PROPERTY_KEY, descName);
            }

            String descVersion = ideaPluginDescriptor.getVersion();
            if (!StringUtil.isEmptyOrSpaces(descVersion)) {
                properties.put(PLUGIN_VERSION_PROPERTY_KEY, descVersion);
            }

            String descEmail = ideaPluginDescriptor.getVendorEmail();
            if (!StringUtil.isEmptyOrSpaces(descEmail)) {
                properties.put(EMAIL_TO_PROPERTY_KEY, descEmail);
            }
        }
    }

    private void queryPropertiesFile(@NotNull PluginDescriptor pluginDescriptor, @NotNull Properties properties) {
        ClassLoader loader = pluginDescriptor.getPluginClassLoader();
        InputStream stream = loader.getResourceAsStream(ERROR_SUBMITTER_PROPERTIES_PATH);
        if (stream != null) {
            LOGGER.debug("Reading errorReporter.properties from file system: " + ERROR_SUBMITTER_PROPERTIES_PATH);

            try {
                properties.load(stream);
            } catch (Exception e) {
                LOGGER.info("Could not read in errorReporter.properties from file system", e);
            }
        }
    }

    @NotNull
    private String[] splitByBlanks(@Nullable String s) {
        if (s == null) {
            return new String[0];
        }

        List<String> strings = StringUtil.split(s, " ");
        return strings.toArray(new String[strings.size()]);
    }

    @NotNull
    private String getServerUrl() {
        // determine server URL only once per lifetime of the error report submitter instance
        if (serverUrl == null) {
            // try to query server URL from lookup location --> this indirection allows to change the
            // server URL without having to redistribute a new version of the error report submitter
            @NonNls String serverUrl = readUrlContentWithProxy(SERVER_LOOKUP_URL);
            if (serverUrl == null) {
                // as a last resort, fallback to hard-coded server URL
                serverUrl = FALLBACK_SERVER_URL;
                LOGGER.warn("Cannot determine server URL, using default server URL " + serverUrl);
            }

            this.serverUrl = serverUrl;
            LOGGER.debug("Server URL " + this.serverUrl);
        }
        return serverUrl;
    }

    @Nullable
    private String readUrlContentWithProxy(String urlString) {
        // first, check if connection to server can be established, i.e. proxy settings are correct
        boolean tryAgain = false;
        do {
            try {
                HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
                httpConfigurable.prepareURL(urlString);
            } catch (IOException ioe) {
                LOGGER.info("Connection error", ioe);
                tryAgain = IOExceptionDialog.showErrorDialog("Error", String.format("Unable to connect to \"%s\". Make sure your proxy settings are correct.", urlString));

                // abort if cannot connect to server and user does not want to try again
                if (!tryAgain) {
                    return null;
                }
            }
        } while (tryAgain);

        // second, connect to server and read content
        return readUrlContent(urlString);
    }

    @Nullable
    private String readUrlContent(String urlString) {
        HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();

        HttpURLConnection connection = null;

        try {
            connection = httpConfigurable.openHttpConnection(urlString);

            String text = StreamUtil.readText(connection.getInputStream(), "UTF-8");
            return text.trim();
        } catch (IOException e) {
            //ignored
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }
}
