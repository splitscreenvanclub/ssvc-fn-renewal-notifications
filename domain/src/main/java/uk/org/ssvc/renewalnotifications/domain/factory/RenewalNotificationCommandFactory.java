package uk.org.ssvc.renewalnotifications.domain.factory;

import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.service.NotificationService;
import uk.org.ssvc.renewalnotifications.domain.command.SendRenewalNotificationCommand;
import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class RenewalNotificationCommandFactory {

    private final RenewalNotificationHistoryFactory notificationHistoryFactory;
    private final NotificationService notificationService;

    @Inject
    public RenewalNotificationCommandFactory(RenewalNotificationHistoryFactory notificationHistoryFactory,
                                             NotificationService notificationService) {
        this.notificationHistoryFactory = notificationHistoryFactory;
        this.notificationService = notificationService;
    }

    public SendRenewalNotificationCommand createCommandFor(List<Member> membersToNotify, RenewalNotificationType type) {
        return new SendRenewalNotificationCommand(
            membersToNotify, type,
            notificationHistoryFactory.create(),
            notificationService);
    }

}
