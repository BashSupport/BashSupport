package com.ansorgit.plugins.bash.fundRaiser;

import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.ansorgit.plugins.bash.settings.BashProjectSettingsConfigurable;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class BashFundRaiser implements com.intellij.openapi.components.ProjectComponent {
    private static final Calendar WEDDING_DAY = new GregorianCalendar(2015, Calendar.APRIL, 25, 12, 0);
    //only show messages until it was dimissed this many times
    private static final int MAX_SHOW_COUNT = 2;

    private static final String closeNotificationUrl = "http://bashsupport/fundRaiser/close";
    private static final String showAgainLink = "http://bashsupport/fundRaiser/showAgain";
    private static final String donationUrl = "http://www.gofundme.com/bashsupport";
    private static final String detailsUrl = "http://www.ansorg-it.com/en/products_bashsupport.html";
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

        if (!BashProjectSettingsConfigurable.isWeddingNotificationEnabled()) {
            return;
        }

        if (BashProjectSettingsConfigurable.getWeddingNotificationShowCount() > MAX_SHOW_COUNT) {
            return;
        }

        new FundNotification(
                "BashSupport: Developer is getting married, want to send me a gift?",
                "BashSupport is in development for 6 years now and you made my plugin the 6th most downloaded ever. " +
                        "Thank you for liking it! This newest version now has some cool new changes including improved run configurations and ANSI colors!<br/><br/>" +
                        "My life also gets a cool new change: I'm getting married to my fiancée Lisa in April. " +
                        "I invite all happy users of BashSupport to chip in by sending us a wedding gift: <a href=\"" + donationUrl + "\">make a small donation here</a>. " +
                        "(Note that this will be my first and last campaign via BashSupport, ever.)<br/><br/>" +
                        "<a href=\"" + detailsUrl + "\">Details</a><br/><br/>" +
                        "Thank you all!<br/>" +
                        "Joachim Ansorg, BashSupport Developer<br/><br/>" +
                        "<small><a href=\"" + closeNotificationUrl + "\">No, thanks</a>,&nbsp;<a href=\"" + showAgainLink + "\">Show again later</a></small><br/>" +
                        "<span style=\"color: gray; font-size:smaller;\">This message won't show after the wedding. It will be displayed " + MAX_SHOW_COUNT + " times at most.</span>"
        ).notify(project);
    }

    @Override
    public void projectClosed() {
    }

    private void handleMessageShown() {
        int count = BashProjectSettingsConfigurable.getWeddingNotificationShowCount() + 1;

        if (count >= MAX_SHOW_COUNT) {
            BashProjectSettingsConfigurable.setWeddingNotificationEnabled(false);
            BashProjectSettingsConfigurable.setWeddingNotificationShowCount(0);
        } else {
            BashProjectSettingsConfigurable.setWeddingNotificationShowCount(count);
        }
    }

    private final class FundNotification extends Notification {
        public FundNotification(@NotNull String title, @NotNull String content) {
            super("BashSupport", title, content, NotificationType.INFORMATION, new FundUrlListener());
        }

        @Override
        public void setBalloon(@NotNull Balloon balloon) {
            super.setBalloon(balloon);

            balloon.addListener(new JBPopupListener() {
                @Override
                public void beforeShown(LightweightWindowEvent event) {
                    handleMessageShown();
                }

                @Override
                public void onClosed(LightweightWindowEvent event) {
                }
            });
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
                    //reset the dismissal count and make sure the setting "show notifications" is enabled
                    BashProjectSettingsConfigurable.setWeddingNotificationEnabled(true);
                    BashProjectSettingsConfigurable.setWeddingNotificationShowCount(0);
                }
            } else if (url.toString().equals(closeNotificationUrl)) {
                notification.expire();

                if (settings != null) {
                    //disable the "show notification" switch
                    BashProjectSettingsConfigurable.setWeddingNotificationEnabled(false);
                }

            } else if (url.toString().equals(donationUrl)) {
                notification.expire();

                if (settings != null) {
                    //disable the "show notification" switch
                    BashProjectSettingsConfigurable.setWeddingNotificationEnabled(false);
                }

            } else {
                BrowserUtil.browse(url);

                if (settings != null) {
                    //disable the "show notification" switch
                    BashProjectSettingsConfigurable.setWeddingNotificationEnabled(false);
                }

                notification.expire();
            }
        }
    }
}

