package uk.org.ssvc.renewalnotifications.integration.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import uk.org.ssvc.core.domain.exception.SsvcServerException;
import uk.org.ssvc.renewalnotifications.domain.model.RenewalNotificationHistoricalEvent;
import uk.org.ssvc.renewalnotifications.domain.repository.RenewalNotificationRepository;
import uk.org.ssvc.renewalnotifications.integration.repository.document.NotificationHistoricalEventDocument;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Singleton
@Slf4j
public class FirestoreRenewalNotificationRepository implements RenewalNotificationRepository {

    private static final String COLLECTION = "renewalNotifications";

    private final Firestore db;

    @Inject
    public FirestoreRenewalNotificationRepository(Firestore db) {
        this.db = db;
    }

    @Override
    public List<RenewalNotificationHistoricalEvent> findAll() {
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION).get();

        try {
            return future.get().getDocuments().stream()
                .map(docSnapshot -> docSnapshot
                    .toObject(NotificationHistoricalEventDocument.class)
                    .toDomain(docSnapshot.getId()))
                .collect(toList());
        }
        catch (Exception e) {
            throw new SsvcServerException("Failed to load renewal notification events", e);
        }
    }

    @Override
    public void add(RenewalNotificationHistoricalEvent event) {
        ApiFuture<WriteResult> future = db.collection(COLLECTION)
                .document(event.getId()).set(new NotificationHistoricalEventDocument(event));

        try {
            log.info("Successfully added notifcation event id={} updateTime={}",
                event.getId(), future.get().getUpdateTime());
        }
        catch (Exception e) {
            throw new SsvcServerException("Failed to add notification event", e);
        }
    }

}
