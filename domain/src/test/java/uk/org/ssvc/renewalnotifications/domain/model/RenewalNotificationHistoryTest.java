package uk.org.ssvc.renewalnotifications.domain.model;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.org.ssvc.renewalnotifications.domain.repository.RenewalNotificationRepository;

import java.time.ZonedDateTime;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.org.ssvc.core.domain.model.notification.NotificationChannel.EMAIL;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.MEMBERSHIP_EXPIRING_SOON;

public class RenewalNotificationHistoryTest {

    @Test
    public void eventsInLastSixtyDays_hasWhenToday() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(UTC);
        RenewalNotificationHistory subject = new RenewalNotificationHistory(null, newArrayList(
            RenewalNotificationHistoricalEvent.builder()
                .id("today")
                .memberId("member1")
                .type(MEMBERSHIP_EXPIRING_SOON)
                .dateTime(now)
                .build()));

        assertThat(subject.hasEventForMemberInLastSixtyDays("member1", MEMBERSHIP_EXPIRING_SOON)).isTrue();
    }

    @Test
    public void eventsInLastSixtyDays_hasWhenOneThirtyDaysAgo() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(UTC);
        RenewalNotificationHistory subject = new RenewalNotificationHistory(null, newArrayList(
            RenewalNotificationHistoricalEvent.builder()
                .id("59daysAgo")
                .memberId("member1")
                .type(MEMBERSHIP_EXPIRING_SOON)
                .dateTime(now.minusDays(59))
                .build()));

        assertThat(subject.hasEventForMemberInLastSixtyDays("member1", MEMBERSHIP_EXPIRING_SOON)).isTrue();
    }

    @Test
    public void eventsInLastSixtyDays_whenDoesnt() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(UTC);
        RenewalNotificationHistory subject = new RenewalNotificationHistory(null, newArrayList(
            RenewalNotificationHistoricalEvent.builder()
                .id("yearAgo")
                .memberId("member1")
                .type(MEMBERSHIP_EXPIRING_SOON)
                .dateTime(now.minusDays(365))
                .build(),
            RenewalNotificationHistoricalEvent.builder()
                .id("61daysAgo")
                .memberId("member1")
                .type(MEMBERSHIP_EXPIRING_SOON)
                .dateTime(now.minusDays(61))
                .build()
        ));

        assertThat(subject.hasEventForMemberInLastSixtyDays("member1", MEMBERSHIP_EXPIRING_SOON)).isFalse();
    }

    @Test
    public void recordEventForMember_addsToRepository() throws Exception {
        RenewalNotificationRepository repository = mock(RenewalNotificationRepository.class);
        RenewalNotificationHistory subject = new RenewalNotificationHistory(repository, newArrayList());

        subject.recordEventForMember("member1", MEMBERSHIP_EXPIRING_SOON, EMAIL);

        ArgumentCaptor<RenewalNotificationHistoricalEvent> eventCaptor = ArgumentCaptor.forClass(RenewalNotificationHistoricalEvent.class);

        verify(repository).add(eventCaptor.capture());

        assertThat(eventCaptor.getValue().getId()).isNotEmpty();
        assertThat(eventCaptor.getValue().getMemberId()).isEqualTo("member1");
        assertThat(eventCaptor.getValue().getType()).isEqualTo(MEMBERSHIP_EXPIRING_SOON);
        assertThat(eventCaptor.getValue().getChannel()).isEqualTo(EMAIL);
        assertThat(eventCaptor.getValue().getDateTime()).isNotNull();
    }

    @Test
    public void recordEventForMember_addsToCache() throws Exception {
        RenewalNotificationRepository repository = mock(RenewalNotificationRepository.class);
        RenewalNotificationHistory subject = new RenewalNotificationHistory(repository, newArrayList());

        subject.recordEventForMember("member1", MEMBERSHIP_EXPIRING_SOON, EMAIL);

        assertThat(subject.hasEventForMemberInLastSixtyDays("member1", MEMBERSHIP_EXPIRING_SOON)).isTrue();
    }

}