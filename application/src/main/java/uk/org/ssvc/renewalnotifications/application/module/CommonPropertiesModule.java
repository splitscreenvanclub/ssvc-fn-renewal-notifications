package uk.org.ssvc.renewalnotifications.application.module;


import dagger.Module;
import dagger.Provides;
import uk.org.ssvc.core.domain.environment.EnvironmentRunType;

import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static uk.org.ssvc.core.domain.environment.EnvironmentRunType.DRY_RUN;
import static uk.org.ssvc.core.domain.environment.EnvironmentRunType.D_DAY;

@Module
public class CommonPropertiesModule {

    @Provides
    @Named("encryption.key")
    String encryptionKey() {
        return prop("encryption.key");
    }

    @Provides
    @Named("encryption.password")
    String encryptionPassword() {
        return prop("encryption.password");
    }

    @Provides
    @Named("google.clientId")
    String googleClientId() {
        return prop("google.clientId");
    }

    @Provides
    @Named("google.clientEmail")
    String googleClientEmail() {
        return prop("google.clientEmail");
    }

    @Provides
    @Named("google.privateKey")
    String googlePrivateKey() {
        return prop("google.privateKey");
    }

    @Provides
    @Named("google.privateKeyId")
    String googlePrivateKeyId() {
        return prop("google.privateKeyId");
    }

    @Provides
    @Named("google.projectId")
    String googleProjectId() {
        return prop("google.projectId");
    }

    @Provides
    @Named("sendGrid.apiKey")
    String sendGridApiKey() {
        return prop("sendGrid.apiKey");
    }

    @Provides
    @Named("nexmo.apiKey")
    String nexmoApiKey() {
        return prop("nexmo.apiKey");
    }

    @Provides
    @Named("nexmo.apiSecret")
    String nexmoApiSecret() {
        return prop("nexmo.apiSecret");
    }

    @Provides
    @Singleton
    EnvironmentRunType environmentRunType() {
        return "true".equals(prop("realDeal")) ? D_DAY : DRY_RUN;
    }

    private String prop(String key) {
        return defaultIfBlank(
            System.getenv(key),
            System.getProperty(key));
    }

}
