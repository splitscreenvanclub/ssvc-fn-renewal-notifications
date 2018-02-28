package uk.org.ssvc.renewalnotifications.domain.factory;

import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationHistory;
import uk.org.ssvc.renewalnotifications.domain.repository.RenewalNotificationRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RenewalNotificationHistoryFactory {

    private final RenewalNotificationRepository repository;

    @Inject
    public RenewalNotificationHistoryFactory(RenewalNotificationRepository repository) {
        this.repository = repository;
    }

    public RenewalNotificationHistory create() {
        return new RenewalNotificationHistory(repository, repository.findAll());
    }

}
