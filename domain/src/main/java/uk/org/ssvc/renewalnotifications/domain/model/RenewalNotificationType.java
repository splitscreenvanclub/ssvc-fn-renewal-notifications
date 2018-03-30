package uk.org.ssvc.renewalnotifications.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.org.ssvc.core.domain.model.notification.MessageType;

import static uk.org.ssvc.core.domain.model.notification.MessageType.*;

@AllArgsConstructor
@Getter
public enum RenewalNotificationType {

    MEMBERSHIP_EXPIRING_SOON(MEMBERSHIP_DUE_FOR_RENEWAL_SHORTLY),
    MEMBERSHIP_EXPIRING_NOW(MEMBERSHIP_DUE_FOR_RENEWAL_NOW),
    MEMBERSHIP_RECENTLY_LAPSED(MEMBERSHIP_LAPSED);

    private final MessageType messageType;

}
