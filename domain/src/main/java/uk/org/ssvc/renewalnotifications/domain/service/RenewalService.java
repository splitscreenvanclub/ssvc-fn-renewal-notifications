package uk.org.ssvc.renewalnotifications.domain.service;

import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.model.member.search.MemberFilterCriteria;
import uk.org.ssvc.core.domain.repository.MemberRepository;
import uk.org.ssvc.renewalnotifications.domain.factory.RenewalNotificationCommandFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.List;

import static uk.org.ssvc.core.domain.model.member.search.MemberSearchField.EXPIRY_AFTER;
import static uk.org.ssvc.core.domain.model.member.search.MemberSearchField.EXPIRY_BEFORE;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.MEMBERSHIP_EXPIRING_SOON;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.MEMBERSHIP_RECENTLY_LAPSED;

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

    public void sendLapsedMembershipReminders() {
        commandFactory
            .createCommandFor(membershipsWhichLapsedRecently(), MEMBERSHIP_RECENTLY_LAPSED)
            .run();
    }

    public void sendMembershipUpForRenewalReminders() {
        commandFactory
            .createCommandFor(membershipsUpForRenewal(), MEMBERSHIP_EXPIRING_SOON)
            .run();
    }

    private List<Member> membershipsUpForRenewal() {
        LocalDate today = LocalDate.now();

        return memberRepository.findByCriteria(MemberFilterCriteria
            .activeMembers()
            .with(EXPIRY_BEFORE, today.plusDays(25)));
    }

    private List<Member> membershipsWhichLapsedRecently() {
        LocalDate today = LocalDate.now();

        // Don't notify immediately to reduce notifications to those currently renewing.
        return memberRepository.findByCriteria(new MemberFilterCriteria()
            .with(EXPIRY_BEFORE, today.minusDays(10))
            .with(EXPIRY_AFTER, today.minusDays(15)));
    }

}
