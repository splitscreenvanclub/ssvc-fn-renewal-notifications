package uk.org.ssvc.renewalnotifications.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import uk.org.ssvc.core.domain.model.notification.NotificationChannel;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Builder
@ToString(of = { "memberId", "type" })
public class RenewalNotificationHistoricalEvent {

    private final String id;
    private final String memberId;
    private final RenewalNotificationType type;
    private final NotificationChannel channel;
    private final ZonedDateTime dateTime;

}
