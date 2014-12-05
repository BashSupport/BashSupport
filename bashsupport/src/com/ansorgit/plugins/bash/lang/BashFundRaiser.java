package com.ansorgit.plugins.bash.lang;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.NotNull;


public class BashFundRaiser implements com.intellij.openapi.components.ProjectComponent {
    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "BashFundRaiser";
    }

    @Override
    public void projectOpened() {
        new FundNotification("Please give!", "If you like BashSupport please consider a donation...").notify(null);
    }

    @Override
    public void projectClosed() {

    }

    private class FundNotification extends Notification {

        public FundNotification(@NotNull String title, @NotNull String content) {
            super("Bash", title, content, NotificationType.INFORMATION);
        }
    }
}
