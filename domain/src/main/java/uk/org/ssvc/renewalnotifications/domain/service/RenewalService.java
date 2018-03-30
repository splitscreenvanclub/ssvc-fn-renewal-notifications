package uk.org.ssvc.renewalnotifications.domain.service;

import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.model.member.search.MemberFilterCriteria;
import uk.org.ssvc.core.domain.repository.MemberRepository;
import uk.org.ssvc.renewalnotifications.domain.factory.RenewalNotificationCommandFactory;
import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.List;

import static uk.org.ssvc.core.domain.model.member.search.MemberSearchField.EXPIRY_AFTER;
import static uk.org.ssvc.core.domain.model.member.search.MemberSearchField.EXPIRY_BEFORE;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.*;

@Singleton
public class RenewalService {

    private final MemberRepository memberRepository;
    private final RenewalNotificationCommandFactory commandFactory;

    @Inject
    public RenewalService(MemberRepository memberRepository,
                          RenewalNotificationCommandFactory commandFactory) {
        this.memberRepository = memberRepository;
        this.commandFactory = commandFactory;
    }

    public void sendMembershipRenewalNotifications() {
        sendMembershipRenewalNotification(MEMBERSHIP_EXPIRING_SOON, 25);
        sendMembershipRenewalNotification(MEMBERSHIP_EXPIRING_NOW, 10);
        sendMembershipRenewalNotification(MEMBERSHIP_RECENTLY_LAPSED, -14);
    }

    public void sendMembershipRenewalNotification(RenewalNotificationType type, int daysUntilExpiry) {
        commandFactory
            .createCommandFor(membershipsWithDaysTillExpiry(daysUntilExpiry), type)
            .run();
    }

    private List<Member> membershipsWithDaysTillExpiry(int daysUntilExpiry) {
        LocalDate today = LocalDate.now();

        if (daysUntilExpiry > 0) {
            return memberRepository.findByCriteria(new MemberFilterCriteria()
                .with(EXPIRY_BEFORE, today.plusDays(daysUntilExpiry+1))
                .with(EXPIRY_AFTER, today));
        }

        return memberRepository.findByCriteria(new MemberFilterCriteria()
            .with(EXPIRY_BEFORE, today.plusDays(daysUntilExpiry+1))
            .with(EXPIRY_AFTER, today.plusDays(-30)));
    }

}
