package uk.org.ssvc.renewalnotifications.domain.repository;

import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationHistoricalEvent;

import java.util.List;

public interface RenewalNotificationRepository {

    List<RenewalNotificationHistoricalEvent> findAll();

    void add(RenewalNotificationHistoricalEvent event);

}