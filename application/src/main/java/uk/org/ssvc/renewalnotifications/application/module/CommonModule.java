package uk.org.ssvc.renewalnotifications.application.module;

import com.google.cloud.firestore.Firestore;
import dagger.Module;
import dagger.Provides;
import uk.org.ssvc.core.domain.repository.MemberRepository;
import uk.org.ssvc.core.domain.service.EmailService;
import uk.org.ssvc.core.domain.service.SmsService;
import uk.org.ssvc.core.integration.email.SendGridEmailService;
import uk.org.ssvc.core.integration.encryption.repository.EncryptingMemberRepository;
import uk.org.ssvc.core.integration.encryption.service.EncryptionService;
import uk.org.ssvc.core.integration.sms.NexmoSmsService;
import uk.org.ssvc.core.integration.template.HandlebarsTemplateRendererService;
import uk.org.ssvc.core.integration.template.TemplateRenderer;
import uk.org.ssvc.firestore.integration.repository.v1.FirestoreMemberRepository;
import uk.org.ssvc.firestore.integration.service.FirestoreFactory;
import uk.org.ssvc.renewalnotifications.domain.repository.RenewalNotificationRepository;
import uk.org.ssvc.renewalnotifications.integration.repository.FirestoreRenewalNotificationRepository;

import javax.inject.Singleton;

@Module(includes = { CommonPropertiesModule.class })
public class CommonModule {

    @Provides
    @Singleton
    Firestore firestore(FirestoreFactory firestoreFactory) {
        return firestoreFactory.create();
    }

    @Provides
    @Singleton
    MemberRepository memberRepository(EncryptionService encryptionService,
                                      FirestoreMemberRepository firestoreMemberRepository) {
        return new EncryptingMemberRepository(
            firestoreMemberRepository,
            encryptionService
        );
    }

    @Provides
    @Singleton
    RenewalNotificationRepository renewalNotificationRepository(FirestoreRenewalNotificationRepository impl) {
        return impl;
    }

    @Provides
    @Singleton
    EmailService emailService(SendGridEmailService impl) {
        return impl;
    }

    @Provides
    @Singleton
    SmsService smsService(NexmoSmsService impl) {
        return impl;
    }

    @Provides
    @Singleton
    TemplateRenderer templateRenderer(HandlebarsTemplateRendererService impl) {
        return impl;
    }

}
