package uk.org.ssvc.renewalnotifications.application.module;

import dagger.Module;
import dagger.Provides;
import uk.org.ssvc.core.domain.repository.MemberRepository;
import uk.org.ssvc.core.domain.service.EmailService;
import uk.org.ssvc.core.domain.service.SmsService;
import uk.org.ssvc.core.integration.email.NoOpEmailService;
import uk.org.ssvc.core.integration.repository.StubMemberRepository;
import uk.org.ssvc.core.integration.sms.NoOpSmsService;
import uk.org.ssvc.renewalnotifications.domain.repository.RenewalNotificationRepository;
import uk.org.ssvc.renewalnotifications.integration.repository.StubRenewalNotificationRepository;

import javax.inject.Singleton;

@Module(includes = { CommonModule.class })
public class TestingModule {

    @Provides
    @Singleton
    MemberRepository memberRepository() {
        return new StubMemberRepository();
    }

    @Provides
    @Singleton
    RenewalNotificationRepository renewalNotificationRepository() {
        return new StubRenewalNotificationRepository();
    }

    @Provides
    @Singleton
    EmailService emailService() {
        return new NoOpEmailService();
    }

    @Provides
    @Singleton
    SmsService smsService() {
        return new NoOpSmsService();
    }

}
