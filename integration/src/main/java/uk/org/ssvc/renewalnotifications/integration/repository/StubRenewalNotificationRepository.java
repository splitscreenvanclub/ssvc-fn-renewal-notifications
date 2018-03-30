package uk.org.ssvc.renewalnotifications.integration.repository;

import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationHistoricalEvent;
import uk.org.ssvc.renewalnotifications.domain.repository.RenewalNotificationRepository;

import java.util.ArrayList;
import java.util.List;

public class StubRenewalNotificationRepository implements RenewalNotificationRepository {

    private static StubRenewalNotificationRepository instance;
    private final List<RenewalNotificationHistoricalEvent> events = new ArrayList<>();

    public StubRenewalNotificationRepository() {
        instance = this;
    }

    public static StubRenewalNotificationRepository instance() {
        return instance;
    }

    @Override
    public List<RenewalNotificationHistoricalEvent> findAll() {
        return new ArrayList<>(events);
    }

    @Override
    public void add(RenewalNotificationHistoricalEvent event) {
        events.add(event);
    }

    public void clear() {
        events.clear();
    }

}
