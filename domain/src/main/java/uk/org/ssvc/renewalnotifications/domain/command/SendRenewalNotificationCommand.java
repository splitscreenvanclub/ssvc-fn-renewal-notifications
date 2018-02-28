package uk.org.ssvc.renewalnotifications.domain.command;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.model.notification.Message;
import uk.org.ssvc.core.domain.model.notification.NotificationSendResult;
import uk.org.ssvc.core.domain.service.NotificationService;
import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationHistory;
import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static uk.org.ssvc.core.domain.model.notification.SendStatus.SENT;
import static uk.org.ssvc.core.domain.service.SsvcRegistry.isDryRun;

@AllArgsConstructor
@Slf4j
public class SendRenewalNotificationCommand {

    private final List<Member> membersToNotify;
    private final RenewalNotificationType notificationType;
    private final RenewalNotificationHistory history;
    private final NotificationService notificationService;

    public void run() {
        log.info("Running renewal notification {} against {} members (will skip those who have already been notified)...",
            notificationType, membersToNotify.size());

        membersToNotify.stream()
            .filter(m -> !history.hasEventForMemberInLastSixtyDays(m.getId(), notificationType))
            .forEach(member -> {
                NotificationSendResult result = notificationService.sendMessage(
                    member.asRecipient(),
                    buildMessage(member));

                if (result.getStatus() == SENT && !isDryRun()) {
                    // Not perfectly transactional, but at least we'll stop here if we fail to
                    // record event (and we can manually record event for now when this happens):
                    history.recordEventForMember(member.getId(), notificationType, result.getChannelUsed());
                }
            });
    }

    private Message buildMessage(Member member) {
        Message message = new Message(notificationType.getMessageType());
        LocalDate expiryDate = member.getRenewalDate().getExpiryDate();

        message = message.withVariable("expiryDate", expiryDate.format(ofPattern("dd MMM yyyy")));
        message = message.withVariable("expiryDateFromNow",
            ChronoUnit.DAYS.between(expiryDate, LocalDate.now(ZoneOffset.UTC)) + " days ago");

        return message;
    }

}
