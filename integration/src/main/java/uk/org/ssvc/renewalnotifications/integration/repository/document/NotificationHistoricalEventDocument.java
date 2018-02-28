package uk.org.ssvc.renewalnotifications.integration.repository.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.org.ssvc.core.domain.model.notification.NotificationChannel;
import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationHistoricalEvent;
import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType;

import java.time.Instant;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationHistoricalEventDocument {

    private String memberId;
    private RenewalNotificationType type;
    private NotificationChannel channel;
    private long dateTime;

    public NotificationHistoricalEventDocument(RenewalNotificationHistoricalEvent event) {
        memberId = event.getMemberId();
        type = event.getType();
        channel = event.getChannel();
        dateTime = event.getDateTime().toEpochSecond();
    }

    public RenewalNotificationHistoricalEvent toDomain(String id) {
        return new RenewalNotificationHistoricalEvent(
            id,
            memberId,
            type,
            channel,
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(dateTime), UTC)
        );
    }

}
