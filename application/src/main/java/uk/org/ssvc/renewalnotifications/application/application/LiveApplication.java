package uk.org.ssvc.renewalnotifications.application.application;

import dagger.Component;
import uk.org.ssvc.renewalnotifications.application.module.LiveModule;

import javax.inject.Singleton;

@Component(modules = { LiveModule.class})
@Singleton
public interface LiveApplication extends Application {
}
