/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: LoggingEventSubmitter.java, Class: LoggingEventSubmitter
 * Last modified: 2010-06-03 22:08
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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.net.HttpConfigurable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * This class sends logging events to the error receiving server.
 *
 * @author <a href="mailto:intellij@studer.nu">Etienne Studer</a>, Jun 1, 2006
 */
@SuppressWarnings({"HardCodedStringLiteral"})
public class TextStreamLoggingEventSubmitter extends LoggingEventSubmitter {
    private static final Logger LOGGER = Logger.getInstance(TextStreamLoggingEventSubmitter.class.getName());

    public TextStreamLoggingEventSubmitter(@NonNls @NotNull String serverURL) {
        super(serverURL);
    }

    @Override
    void submit(@NotNull String stacktrace, @Nullable String description, @Nullable String user) throws SubmitException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("About to send logging events " + stacktrace);
        }

        // open connection
        URLConnection connection;
        try {
            connection = HttpConfigurable.getInstance().openHttpConnection(serverURL);
            connection.setRequestProperty("Content-type", "text/plain");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            connection.getOutputStream();
        } catch (IOException ioe) {
            LOGGER.info("Unable to connect to server", ioe);
            throw new SubmitException("Unable to connect to server", ioe);
        }

        // write events to server
        PrintStream stream = null;
        try {
            stream = new PrintStream(connection.getOutputStream());

            stream.append("PluginId: ").println(pluginId != null ? pluginId : "");
            stream.append("Plugin name: ").println(pluginName != null ? pluginName : "");
            stream.append("Plugin version: ").println(pluginVersion != null ? pluginVersion : "");
            stream.append("IDEA build: ").println(ideaBuild != null ? ideaBuild : "");
            if (emailTo != null && emailTo.length > 0) {
                stream.append("To: ").println(emailTo != null ? StringUtil.join(emailTo, ":") : "");
            }
            if (emailCc != null && emailCc.length > 0) {
                stream.append("CC: ").println(emailCc != null ? StringUtil.join(emailCc, ":") : "");
            }
            stream.append("User: ").println(user != null ? user : "");
            stream.append("Description: ").println(description != null ? description : "");
            stream.println();
            stream.println();

            stream.println(stacktrace);

            stream.flush();
        } catch (IOException ioe) {
            LOGGER.info("Unable to send data to server", ioe);
            throw new SubmitException("Unable to send data to server", ioe);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        LOGGER.debug("Logging events sent successfully");

        // read response from server
        try {
            String response = StreamUtil.readText(connection.getInputStream(), "UTF-8");

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Status message: " + response);
            }

            if (!response.equals("OK")) {
                LOGGER.info("Status returned by server is NOK");
                throw new SubmitException(response, null);
            } else {
                LOGGER.info("Status returned by server is OK");
            }
        } catch (IOException ioe) {
            LOGGER.info("Unable to receive data from server", ioe);
            throw new SubmitException("Unable to receive data from server", ioe);
        } finally {
            try {
                connection.getOutputStream().close();
            } catch (IOException ioe) {
                LOGGER.info("Unable to disconnect from server after receiving data", ioe);
            }
        }

        // disconnect connection
        HttpURLConnection httpConnection = (HttpURLConnection) connection;
        try {
            int responseCode = httpConnection.getResponseCode();
            String responseMessage = httpConnection.getResponseMessage();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Response code: " + responseCode);
                LOGGER.debug("Response message: " + responseMessage);
            }
        } catch (IOException ioe) {
            LOGGER.info("Unable to retrieve response status");
        } finally {
            httpConnection.disconnect();
        }
    }
}
