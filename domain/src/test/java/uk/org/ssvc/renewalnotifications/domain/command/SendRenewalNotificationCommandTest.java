package uk.org.ssvc.renewalnotifications.domain.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.model.member.RenewalDate;
import uk.org.ssvc.core.domain.model.notification.Message;
import uk.org.ssvc.core.domain.model.notification.NotificationSendResult;
import uk.org.ssvc.core.domain.model.notification.Recipient;
import uk.org.ssvc.core.domain.service.NotificationService;
import uk.org.ssvc.core.domain.service.SsvcRegistry;
import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationHistory;

import java.time.LocalDate;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static uk.org.ssvc.core.domain.environment.EnvironmentRunType.DRY_RUN;
import static uk.org.ssvc.core.domain.environment.EnvironmentRunType.D_DAY;
import static uk.org.ssvc.core.domain.model.notification.NotificationChannel.EMAIL;
import static uk.org.ssvc.core.domain.model.notification.SendStatus.NOT_ATTEMPTED;
import static uk.org.ssvc.core.domain.model.notification.SendStatus.SENT;
import static uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationType.MEMBERSHIP_EXPIRING_SOON;

@RunWith(MockitoJUnitRunner.class)
public class SendRenewalNotificationCommandTest {

    @Mock private RenewalNotificationHistory history;
    @Mock private NotificationService notificationService;

    private Member memberOne = mock(Member.class);
    private Recipient memberOneRecipient = mock(Recipient.class);

    @Before
    public void setUp() throws Exception {
        RenewalDate renewalDate = mock(RenewalDate.class);

        when(memberOne.getId()).thenReturn("member1");
        when(memberOne.asRecipient()).thenReturn(memberOneRecipient);
        when(memberOne.getRenewalDate()).thenReturn(renewalDate);
        when(renewalDate.getExpiryDate()).thenReturn(LocalDate.now());

        when(notificationService.sendMessage(any(), any())).thenReturn(new NotificationSendResult(null, null, null));
    }

    @Test
    public void sendsNoticationThroughService() throws Exception {
        SendRenewalNotificationCommand subject = new SendRenewalNotificationCommand(
            newArrayList(memberOne), MEMBERSHIP_EXPIRING_SOON,
            history, notificationService
        );

        subject.run();

        verify(notificationService).sendMessage(eq(memberOneRecipient), any(Message.class));
    }

    @Test
    public void savesNotificationHistoryIfNotDryRun() throws Exception {
        SendRenewalNotificationCommand subject = new SendRenewalNotificationCommand(
            newArrayList(memberOne), MEMBERSHIP_EXPIRING_SOON,
            history, notificationService
        );

        new SsvcRegistry(null, null, D_DAY).initialise();

        when(notificationService.sendMessage(any(), any())).thenReturn(new NotificationSendResult(
            memberOneRecipient, SENT, EMAIL));

        subject.run();

        verify(history).recordEventForMember("member1", MEMBERSHIP_EXPIRING_SOON, EMAIL);
    }

    @Test
    public void doesntSaveNotificationHistoryIfDryRun() throws Exception {
        SendRenewalNotificationCommand subject = new SendRenewalNotificationCommand(
            newArrayList(memberOne), MEMBERSHIP_EXPIRING_SOON,
            history, notificationService
        );

        new SsvcRegistry(null, null, DRY_RUN).initialise();

        when(notificationService.sendMessage(any(), any())).thenReturn(new NotificationSendResult(
            memberOneRecipient, SENT, EMAIL));

        subject.run();

        verify(history, never()).recordEventForMember(anyString(), any(), any());
    }

    @Test
    public void doesntSaveNotificationHistoryIfNotificationNotSent() throws Exception {
        SendRenewalNotificationCommand subject = new SendRenewalNotificationCommand(
            newArrayList(memberOne), MEMBERSHIP_EXPIRING_SOON,
            history, notificationService
        );

        new SsvcRegistry(null, null, D_DAY).initialise();

        when(notificationService.sendMessage(any(), any())).thenReturn(new NotificationSendResult(
            memberOneRecipient, NOT_ATTEMPTED, null));

        subject.run();

        verify(history, never()).recordEventForMember(anyString(), any(), any());
    }

    @Test
    public void doesntSendOrSaveHistoryIfMemberHasSameEventInLastSixtyDays() throws Exception {
        SendRenewalNotificationCommand subject = new SendRenewalNotificationCommand(
            newArrayList(memberOne), MEMBERSHIP_EXPIRING_SOON,
            history, notificationService
        );

        new SsvcRegistry(null, null, D_DAY).initialise();

        when(notificationService.sendMessage(any(), any())).thenReturn(new NotificationSendResult(
            memberOneRecipient, SENT, EMAIL));
        when(history.hasEventForMemberInLastSixtyDays("member1", MEMBERSHIP_EXPIRING_SOON)).thenReturn(true);

        subject.run();

        verify(notificationService, never()).sendMessage(any(), any());
        verify(history, never()).recordEventForMember(anyString(), any(), any());
    }

    @Test
    public void messageTypeCorrelatesToNotificationType() throws Exception {
        SendRenewalNotificationCommand subject = new SendRenewalNotificationCommand(
            newArrayList(memberOne), MEMBERSHIP_EXPIRING_SOON,
            history, notificationService
        );

        subject.run();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        verify(notificationService).sendMessage(eq(memberOneRecipient), messageCaptor.capture());

        assertThat(messageCaptor.getValue().getType()).isEqualTo(MEMBERSHIP_EXPIRING_SOON.getMessageType());
    }

    @Test
    public void messageHasExpiryDateVariables() throws Exception {
        SendRenewalNotificationCommand subject = new SendRenewalNotificationCommand(
            newArrayList(memberOne), MEMBERSHIP_EXPIRING_SOON,
            history, notificationService
        );
        RenewalDate renewalDate = mock(RenewalDate.class);

        when(memberOne.getRenewalDate()).thenReturn(renewalDate);
        when(renewalDate.getExpiryDate()).thenReturn(LocalDate.now().minusDays(10));

        subject.run();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        verify(notificationService).sendMessage(eq(memberOneRecipient), messageCaptor.capture());

        Map<String, String> actualVars = messageCaptor.getValue().getVariables();

        assertThat(actualVars.get("expiryDate")).isNotEmpty();
        assertThat(actualVars.get("expiryDateFromNow")).isEqualTo("10 days ago");
    }

}