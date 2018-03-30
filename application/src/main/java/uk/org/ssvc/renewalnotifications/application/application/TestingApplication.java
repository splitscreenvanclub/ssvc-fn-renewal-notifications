package uk.org.ssvc.renewalnotifications.application.application;

import dagger.Component;
import uk.org.ssvc.renewalnotifications.application.module.TestingModule;

import javax.inject.Singleton;

@Component(modules = { TestingModule.class })
@Singleton
public interface TestingApplication extends Application {

}
