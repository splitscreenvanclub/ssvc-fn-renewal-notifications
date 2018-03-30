package uk.org.ssvc.renewalnotifications.application.application;

import dagger.Component;
import uk.org.ssvc.renewalnotifications.application.module.TrialRunModule;

import javax.inject.Singleton;

@Component(modules = { TrialRunModule.class })
@Singleton
public interface TrialRunApplication extends Application {

}
