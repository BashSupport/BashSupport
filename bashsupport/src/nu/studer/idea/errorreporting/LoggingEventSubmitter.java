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

import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * This class sends logging events to the error receiving server.
 *
 * @author <a href="mailto:intellij@studer.nu">Etienne Studer</a>, Jun 1, 2006
 */
@SuppressWarnings({"HardCodedStringLiteral"})
public class LoggingEventSubmitter {
    private static final Logger LOGGER = Logger.getInstance(LoggingEventSubmitter.class.getName());

    @NonNls
    @NotNull
    private String serverURL;

    @Nullable
    private String pluginId;
    @Nullable
    private String pluginName;
    @Nullable
    private String pluginVersion;
    @Nullable
    private String ideaBuild;

    @Nullable
    private String[] emailTo;
    @Nullable
    private String[] emailCc;

    public LoggingEventSubmitter(@NonNls @NotNull String serverURL) {
        this.serverURL = serverURL;
    }

    public void setPluginId(@Nullable String pluginId) {
        this.pluginId = pluginId;
    }

    public void setPluginName(@Nullable String pluginName) {
        this.pluginName = pluginName;
    }

    public void setPluginVersion(@Nullable String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public void setIdeaBuild(@Nullable String ideaBuild) {
        this.ideaBuild = ideaBuild;
    }

    public void setEmailTo(@Nullable String[] emailTo) {
        this.emailTo = emailTo;
    }

    public void setEmailCc(@Nullable String[] emailCc) {
        this.emailCc = emailCc;
    }

    void submit(@NotNull IdeaLoggingEvent[] events, @Nullable String description, @Nullable String user) throws SubmitException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("About to send logging events " + Arrays.asList(events));
        }

        // open connection
        URLConnection connection;
        try {
            URL url = new URL(serverURL);
            connection = url.openConnection();
            connection.setRequestProperty("Content-type", "application/octet-stream");
            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);
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
        DataOutputStream stream = null;
        try {
            stream = new DataOutputStream(new DeflaterOutputStream(connection.getOutputStream()));
            stream.writeUTF(pluginId != null ? pluginId : "");
            stream.writeUTF(pluginName != null ? pluginName : "");
            stream.writeUTF(pluginVersion != null ? pluginVersion : "");
            stream.writeUTF(ideaBuild != null ? ideaBuild : "");
            stream.writeUTF(emailTo != null ? StringUtil.join(emailTo, ":") : "");
            stream.writeUTF(emailCc != null ? StringUtil.join(emailCc, ":") : "");
            stream.writeUTF(user != null ? user : "");
            stream.writeUTF(description != null ? description : "");
            stream.writeInt(events.length);
            for (IdeaLoggingEvent event : events) {
                stream.writeUTF(event.getMessage() != null ? event.getMessage() : "");
                stream.writeUTF(event.getThrowableText() != null ? event.getThrowableText() : "");
            }
            stream.flush();
        } catch (IOException ioe) {
            LOGGER.info("Unable to send data to server", ioe);
            throw new SubmitException("Unable to send data to server", ioe);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ioe) {
                    LOGGER.info("Unable to disconnect from server after sending data", ioe);
                }
            }
        }

        LOGGER.debug("Logging events sent successfully");

        // read response from server
        DataInputStream inputStream;
        try {
            inputStream = new DataInputStream(new InflaterInputStream(connection.getInputStream()));
            boolean statusOK = inputStream.readBoolean();
            String statusMessage = inputStream.readUTF();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Status OK: " + statusOK);
                LOGGER.debug("Status message: " + statusMessage);
            }

            if (!statusOK) {
                LOGGER.info("Status returned by server is NOK");
                throw new SubmitException(statusMessage, null);
            } else {
                LOGGER.info("Status returned by server is OK");
            }
        } catch (IOException ioe) {
            LOGGER.info("Unable to receive data from server", ioe);
            throw new SubmitException("Unable to receive data from server", ioe);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ioe) {
                    LOGGER.info("Unable to disconnect from server after receiving data", ioe);
                }
            }
        }

        // disconnect connection
        if (connection instanceof HttpURLConnection) {
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

    public static class SubmitException extends Throwable {
        public SubmitException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
