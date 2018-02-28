package uk.org.ssvc.renewalnotifications.domain.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.repository.MemberRepository;
import uk.org.ssvc.renewalnotifications.domain.command.SendRenewalNotificationCommand;
import uk.org.ssvc.renewalnotifications.domain.factory.RenewalNotificationCommandFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.MEMBERSHIP_EXPIRING_SOON;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.MEMBERSHIP_RECENTLY_LAPSED;

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
        subject.sendLapsedMembershipReminders();

        verify(memberRepository).findByCriteria(any());
    }

    @Test
    public void sendLapsedMembershipReminders_createsCommand() throws Exception {
        Member member1 = mock(Member.class);
        Member member2 = mock(Member.class);
        List<Member> members = newArrayList(member1, member2);

        when(memberRepository.findByCriteria(any())).thenReturn(members);

        subject.sendLapsedMembershipReminders();

        verify(commandFactory).createCommandFor(members, MEMBERSHIP_RECENTLY_LAPSED);
    }

    @Test
    public void sendMembershipUpForRenewalReminders_callsRepo() throws Exception {
        subject.sendMembershipUpForRenewalReminders();

        verify(memberRepository).findByCriteria(any());
    }

    @Test
    public void sendMembershipUpForRenewalReminders_createsCommand() throws Exception {
        Member member1 = mock(Member.class);
        Member member2 = mock(Member.class);
        List<Member> members = newArrayList(member1, member2);

        when(memberRepository.findByCriteria(any())).thenReturn(members);

        subject.sendMembershipUpForRenewalReminders();

        verify(commandFactory).createCommandFor(members, MEMBERSHIP_EXPIRING_SOON);
    }

}