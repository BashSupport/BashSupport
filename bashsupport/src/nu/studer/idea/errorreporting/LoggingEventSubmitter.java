package nu.studer.idea.errorreporting;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LoggingEventSubmitter {
    @NonNls
    @NotNull
    protected String serverURL;
    @Nullable
    protected String pluginId;
    @Nullable
    protected String pluginName;
    @Nullable
    protected String pluginVersion;
    @Nullable
    protected String ideaBuild;
    @Nullable
    protected String[] emailTo;
    @Nullable
    protected String[] emailCc;

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

    abstract void submit(@NotNull String stackstrace, @Nullable String description, @Nullable String user) throws SubmitException;

    public static class SubmitException extends Throwable {
        public SubmitException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
