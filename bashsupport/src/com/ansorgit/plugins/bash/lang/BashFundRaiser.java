package com.ansorgit.plugins.bash.lang;

import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class BashFundRaiser implements com.intellij.openapi.components.ProjectComponent {
    private static final Calendar WEDDING_DAY = new GregorianCalendar(2015, Calendar.APRIL, 25, 12, 0);
    private static final String closeNotificationUrl = "http://bashsupport/fundRaiser/close";
    private static final String showAgainLink = "http://bashsupport/fundRaiser/showAgain";
    private static final String donationUrl = "http://www.ansorg-it.com/bashsupport.html";
    private final Project project;

    public BashFundRaiser(com.intellij.openapi.project.Project project) {
        this.project = project;
    }

    /**
     * @return True if the wedding is still to come. False if the wedding already took place.
     */
    public static boolean isActive() {
        return Calendar.getInstance().before(WEDDING_DAY);
    }

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
        if (!isActive()) {
            return;
        }

        BashProjectSettings settings = BashProjectSettings.storedSettings(project);

        if (settings != null && settings.isWeddingNotification()) {
            new FundNotification(
                    "Wedding gift",
                    "I, the author of BashSupport, will marry in April 2015. " +
                            "If you like BashSupport please consider a gift to my wedding: <a href=\"" + donationUrl + "\">Donation</a>.<br/>" +
                            "It is highly appreciated!<br/><br/>" +
                            "<a href=\"" + closeNotificationUrl + "\">No, thanks</a><br/><a href=\"" + showAgainLink + "\">Show again later</a>"
            ).notify(project);
        }
    }

    @Override
    public void projectClosed() {
    }

    private final class FundNotification extends Notification {
        public FundNotification(@NotNull String title, @NotNull String content) {
            super("BashSupport", title, content, NotificationType.INFORMATION, new FundUrlListener());
        }
    }

    private class FundUrlListener extends NotificationListener.Adapter {
        @Override
        protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
            URL url = event.getURL();

            if (url == null) {
                return;
            }

            BashProjectSettings settings = BashProjectSettings.storedSettings(project);
            if (url.toString().equals(showAgainLink)) {
                notification.expire();

                if (settings != null) {
                    settings.setWeddingNotification(true);
                }
            } else if (url.toString().equals(closeNotificationUrl)) {
                notification.expire();

                if (settings != null) {
                    settings.setWeddingNotification(false);
                }

            } else {
                notification.expire();

                BrowserUtil.browse(url);

                if (settings != null) {
                    settings.setWeddingNotification(false);
                }
            }
        }
    }
}

