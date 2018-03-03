package uk.org.ssvc.renewalnotifications.application.module;

import dagger.Module;
import dagger.Provides;
import uk.org.ssvc.core.integration.template.HandlebarsTemplateRendererService;
import uk.org.ssvc.core.integration.template.TemplateRenderer;

import javax.inject.Singleton;

@Module(includes = { CommonPropertiesModule.class })
public class CommonModule {

    @Provides
    @Singleton
    TemplateRenderer templateRenderer(HandlebarsTemplateRendererService impl) {
        return impl;
    }

}
