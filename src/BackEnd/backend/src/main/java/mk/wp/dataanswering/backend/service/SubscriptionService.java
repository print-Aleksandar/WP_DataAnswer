package mk.wp.dataanswering.backend.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import mk.wp.dataanswering.backend.model.Subscription;

public interface SubscriptionService {
        
    Subscription subscribe(Long userId, Long planId);
    Subscription getActiveSubscription(Long userId);
    void cancel(Long subscriptionId);
    void deleteAllByUserId(Long userId);
    List<Subscription> getSubscriptionHistory(Long userId) throws AccessDeniedException;
    void subscribeToGuest(Long userId);
}
