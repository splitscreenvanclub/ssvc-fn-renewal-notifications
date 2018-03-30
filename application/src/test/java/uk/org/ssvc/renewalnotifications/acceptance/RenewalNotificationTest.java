package uk.org.ssvc.renewalnotifications.acceptance;

import org.junit.Before;
import org.junit.Test;
import uk.org.ssvc.core.domain.model.ContactDetails;
import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.model.member.RenewalDate;
import uk.org.ssvc.core.integration.repository.StubMemberRepository;
import uk.org.ssvc.renewalnotifications.application.application.Application;
import uk.org.ssvc.renewalnotifications.application.application.DaggerTestingApplication;
import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationHistoricalEvent;
import uk.org.ssvc.renewalnotifications.domain.service.RenewalService;
import uk.org.ssvc.renewalnotifications.integration.repository.StubRenewalNotificationRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.ssvc.core.domain.model.notification.NotificationChannel.EMAIL;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.MEMBERSHIP_EXPIRING_SOON;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.MEMBERSHIP_RECENTLY_LAPSED;

public class RenewalNotificationTest {

    private Application app;
    private StubMemberRepository memberRepository;
    private StubRenewalNotificationRepository notificationRepository;
    private RenewalService renewalService;

    @Before
    public void setUp() throws Exception {
        System.setProperty("realDeal", "true");

        app = DaggerTestingApplication
            .builder()
            .build();

        app.registry().initialise();
        renewalService = app.renewalService();
        memberRepository = StubMemberRepository.instance();
        notificationRepository = StubRenewalNotificationRepository.instance();

        memberRepository.clear();
        notificationRepository.clear();
    }

    @Test
    public void renewal_due_soon() throws Exception {
        notificationRepository.add(RenewalNotificationHistoricalEvent.builder()
            .channel(EMAIL)
            .memberId("5alreadyContact")
            .type(MEMBERSHIP_EXPIRING_SOON)
            .dateTime(ZonedDateTime.now(UTC))
            .build());

        memberRepository.add(memberWithDaysTillExpiry("old", "agesOld@ssvc.org.uk", -100));
        memberRepository.add(memberWithDaysTillExpiry("9", "renewalIn9days@ssvc.org.uk", 9));
        memberRepository.add(memberWithDaysTillExpiry("10", "renewalIn10days@ssvc.org.uk", 10));
        memberRepository.add(memberWithDaysTillExpiry("11", "renewalIn11days@ssvc.org.uk", 11));
        memberRepository.add(memberWithDaysTillExpiry("5noContact", "renewalIn5daysNotContacted@ssvc.org.uk", 5));
        memberRepository.add(memberWithDaysTillExpiry("5alreadyContact", "renewalIn5daysAlreadyContacted@ssvc.org.uk", 5));

        renewalService.sendMembershipRenewalNotification(MEMBERSHIP_EXPIRING_SOON, 10);

        List<RenewalNotificationHistoricalEvent> actualEvents = notificationRepository.findAll();

        assertThat(actualEvents).hasSize(4);
        assertThat(actualEvents.get(0).getMemberId()).isEqualTo("5alreadyContact");
        assertThat(actualEvents.get(1).getMemberId()).isEqualTo("9");
        assertThat(actualEvents.get(1).getType()).isEqualTo(MEMBERSHIP_EXPIRING_SOON);
        assertThat(actualEvents.get(2).getMemberId()).isEqualTo("10");
        assertThat(actualEvents.get(2).getType()).isEqualTo(MEMBERSHIP_EXPIRING_SOON);
        assertThat(actualEvents.get(3).getMemberId()).isEqualTo("5noContact");
        assertThat(actualEvents.get(3).getType()).isEqualTo(MEMBERSHIP_EXPIRING_SOON);
    }

    @Test
    public void membership_lapsed() throws Exception {
        notificationRepository.add(RenewalNotificationHistoricalEvent.builder()
            .channel(EMAIL)
            .memberId("-14contacted")
            .type(MEMBERSHIP_RECENTLY_LAPSED)
            .dateTime(ZonedDateTime.now(UTC))
            .build());
        notificationRepository.add(RenewalNotificationHistoricalEvent.builder()
            .channel(EMAIL)
            .memberId("-14")
            .type(MEMBERSHIP_EXPIRING_SOON)
            .dateTime(ZonedDateTime.now(UTC))
            .build());

        memberRepository.add(memberWithDaysTillExpiry("old", "agesOld@ssvc.org.uk", -100));
        memberRepository.add(memberWithDaysTillExpiry("-14contacted", "due14daysAgoContacted@ssvc.org.uk", -14));
        memberRepository.add(memberWithDaysTillExpiry("-13", "due13daysAgo@ssvc.org.uk", -13));
        memberRepository.add(memberWithDaysTillExpiry("-14", "due14daysAgo@ssvc.org.uk", -14));
        memberRepository.add(memberWithDaysTillExpiry("-15", "due15daysAgo@ssvc.org.uk", -15));

        renewalService.sendMembershipRenewalNotification(MEMBERSHIP_RECENTLY_LAPSED, -14);

        List<RenewalNotificationHistoricalEvent> actualEvents = notificationRepository.findAll();

        assertThat(actualEvents).hasSize(4);
        assertThat(actualEvents.get(0).getMemberId()).isEqualTo("-14contacted");
        assertThat(actualEvents.get(0).getType()).isEqualTo(MEMBERSHIP_RECENTLY_LAPSED);
        assertThat(actualEvents.get(1).getMemberId()).isEqualTo("-14");
        assertThat(actualEvents.get(1).getType()).isEqualTo(MEMBERSHIP_EXPIRING_SOON);
        assertThat(actualEvents.get(2).getMemberId()).isEqualTo("-14");
        assertThat(actualEvents.get(2).getType()).isEqualTo(MEMBERSHIP_RECENTLY_LAPSED);
        assertThat(actualEvents.get(3).getMemberId()).isEqualTo("-15");
        assertThat(actualEvents.get(3).getType()).isEqualTo(MEMBERSHIP_RECENTLY_LAPSED);
    }

    private Member memberWithDaysTillExpiry(String id, String email, int daysTillExpiry) {
        return Member.builder()
                .id(id)
                .firstName("Ed")
                .lastName("Slocombe")
                .renewalDate(new RenewalDate(LocalDate.now().plusDays(daysTillExpiry)))
                .associates(emptySet())
                .contactDetails(new ContactDetails(emptyList(), email))
                .build();
    }

}
