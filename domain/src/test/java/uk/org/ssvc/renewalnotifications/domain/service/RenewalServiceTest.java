package uk.org.ssvc.renewalnotifications.domain.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.model.member.search.MemberFilterCriteria;
import uk.org.ssvc.core.domain.repository.MemberRepository;
import uk.org.ssvc.renewalnotifications.domain.command.SendRenewalNotificationCommand;
import uk.org.ssvc.renewalnotifications.domain.factory.RenewalNotificationCommandFactory;

import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static uk.org.ssvc.core.domain.model.member.search.MemberSearchField.EXPIRY_AFTER;
import static uk.org.ssvc.core.domain.model.member.search.MemberSearchField.EXPIRY_BEFORE;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.MEMBERSHIP_EXPIRING_SOON;

@RunWith(MockitoJUnitRunner.class)
public class RenewalServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private RenewalNotificationCommandFactory commandFactory;
    @InjectMocks private RenewalService subject;

    @Before
    public void setUp() throws Exception {
        when(commandFactory.createCommandFor(anyList(), any())).thenReturn(mock(SendRenewalNotificationCommand.class));
    }

    @Test
    public void sendLapsedMembershipReminders_callsRepo() throws Exception {
        subject.sendMembershipRenewalNotification(MEMBERSHIP_EXPIRING_SOON, 10);

        verify(memberRepository).findByCriteria(any());
    }

    @Test
    public void sendExpiringSoon_usesCorrectFilterCriteria() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate elevenDaysTime = today.plusDays(11);

        subject.sendMembershipRenewalNotification(MEMBERSHIP_EXPIRING_SOON, 10);

        ArgumentCaptor<MemberFilterCriteria> criteria = ArgumentCaptor.forClass(MemberFilterCriteria.class);

        verify(memberRepository).findByCriteria(criteria.capture());

        assertThat(criteria.getValue().valueFor(EXPIRY_BEFORE).get()).isEqualTo(elevenDaysTime);
        assertThat(criteria.getValue().valueFor(EXPIRY_AFTER).get()).isEqualTo(today);
    }

    @Test
    public void sendLapsedMembershipReminders_usesCorrectFilterCriteria() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate fourteenDaysAgo = today.minusDays(14);
        LocalDate thirtyDaysAgo = today.minusDays(30);

        subject.sendMembershipRenewalNotification(MEMBERSHIP_EXPIRING_SOON, -15);

        ArgumentCaptor<MemberFilterCriteria> criteria = ArgumentCaptor.forClass(MemberFilterCriteria.class);

        verify(memberRepository).findByCriteria(criteria.capture());

        assertThat(criteria.getValue().valueFor(EXPIRY_BEFORE).get()).isEqualTo(fourteenDaysAgo);
        assertThat(criteria.getValue().valueFor(EXPIRY_AFTER).get()).isEqualTo(thirtyDaysAgo);
    }

    @Test
    public void sendLapsedMembershipReminders_createsCommand() throws Exception {
        Member member1 = mock(Member.class);
        Member member2 = mock(Member.class);
        List<Member> members = newArrayList(member1, member2);

        when(memberRepository.findByCriteria(any())).thenReturn(members);

        subject.sendMembershipRenewalNotification(MEMBERSHIP_EXPIRING_SOON, 10);

        verify(commandFactory).createCommandFor(members, MEMBERSHIP_EXPIRING_SOON);
    }

    @Test
    public void sendMembershipUpForRenewalReminders_callsRepo() throws Exception {
        subject.sendMembershipRenewalNotification(MEMBERSHIP_EXPIRING_SOON, 10);

        verify(memberRepository).findByCriteria(any());
    }

    @Test
    public void sendMembershipUpForRenewalReminders_createsCommand() throws Exception {
        Member member1 = mock(Member.class);
        Member member2 = mock(Member.class);
        List<Member> members = newArrayList(member1, member2);

        when(memberRepository.findByCriteria(any())).thenReturn(members);

        subject.sendMembershipRenewalNotification(MEMBERSHIP_EXPIRING_SOON, 10);

        verify(commandFactory).createCommandFor(members, MEMBERSHIP_EXPIRING_SOON);
    }

}