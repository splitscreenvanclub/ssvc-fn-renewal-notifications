package uk.org.ssvc.renewalnotifications.domain.model;

import lombok.AllArgsConstructor;
import uk.org.ssvc.core.domain.model.notification.NotificationChannel;
import uk.org.ssvc.renewalnotifications.domain.repository.RenewalNotificationRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

@AllArgsConstructor
public class RenewalNotificationHistory {

    private final RenewalNotificationRepository repository;
    private final List<RenewalNotificationHistoricalEvent> events;

    public boolean hasEventForMemberInLastSixtyDays(String memberId, RenewalNotificationType notificationType) {
        return events.stream()
            .anyMatch(e ->
                e.getMemberId().equals(memberId) &&
                e.getType().equals(notificationType) &&
                e.getDateTime().toLocalDate().plusDays(60).isAfter(ZonedDateTime.now(UTC).toLocalDate()));
    }

    public void recordEventForMember(String memberId, RenewalNotificationType type, NotificationChannel channel) {
        RenewalNotificationHistoricalEvent event = new RenewalNotificationHistoricalEvent(
            UUID.randomUUID().toString(),
            memberId,
            type,
            channel,
            ZonedDateTime.now(UTC));

        repository.add(event);
        events.add(event);
    }

}
