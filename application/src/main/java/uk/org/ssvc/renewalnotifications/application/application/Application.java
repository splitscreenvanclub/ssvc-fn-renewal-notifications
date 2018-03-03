package uk.org.ssvc.renewalnotifications.application.application;

import uk.org.ssvc.core.domain.service.SsvcRegistry;
import uk.org.ssvc.renewalnotifications.domain.service.RenewalService;

public interface Application {

    SsvcRegistry registry();

    RenewalService renewalService();

}
