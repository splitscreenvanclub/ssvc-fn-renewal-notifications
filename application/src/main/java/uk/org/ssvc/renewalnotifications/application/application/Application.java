package uk.org.ssvc.renewalnotifications.application.application;

import dagger.Component;
import uk.org.ssvc.core.domain.service.SsvcRegistry;
import uk.org.ssvc.renewalnotifications.application.module.CommonModule;
import uk.org.ssvc.renewalnotifications.domain.service.RenewalService;

import javax.inject.Singleton;

@Component(modules = { CommonModule.class })
@Singleton
public interface Application {

    SsvcRegistry registry();

    RenewalService renewalService();

}
