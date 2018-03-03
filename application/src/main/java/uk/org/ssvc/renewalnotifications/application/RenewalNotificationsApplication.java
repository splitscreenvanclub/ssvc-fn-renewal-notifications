package uk.org.ssvc.renewalnotifications.application;

import lombok.extern.slf4j.Slf4j;
import uk.org.ssvc.renewalnotifications.application.application.Application;
import uk.org.ssvc.renewalnotifications.application.application.DaggerLiveApplication;

@Slf4j
public class RenewalNotificationsApplication {

    public static void main(String[] args) {
        Application app = DaggerLiveApplication
            .builder()
            .build();

        app.registry().initialise();
        app.renewalService().sendLapsedMembershipReminders();
        app.renewalService().sendMembershipUpForRenewalReminders();
    }

}
