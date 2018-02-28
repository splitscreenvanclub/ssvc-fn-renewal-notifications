package uk.org.ssvc.renewalnotifications.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.org.ssvc.core.domain.model.notification.MessageType;

import static uk.org.ssvc.core.domain.model.notification.MessageType.MEMBERSHIP_DUE_FOR_RENEWAL_NOW;
import static uk.org.ssvc.core.domain.model.notification.MessageType.MEMBERSHIP_DUE_FOR_RENEWAL_SHORTLY;

@AllArgsConstructor
@Getter
public enum RenewalNotificationType {

    MEMBERSHIP_EXPIRING_SOON(MEMBERSHIP_DUE_FOR_RENEWAL_SHORTLY),
    MEMBERSHIP_RECENTLY_LAPSED(MEMBERSHIP_DUE_FOR_RENEWAL_NOW);

    private final MessageType messageType;

}
