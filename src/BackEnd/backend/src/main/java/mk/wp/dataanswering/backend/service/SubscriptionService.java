package mk.wp.dataanswering.backend.service;

import java.time.LocalDateTime;

import mk.wp.dataanswering.backend.model.Subscription;

public interface SubscriptionService {
        
    Subscription subscribe(Long userId, Long planId, LocalDateTime endTs); 

    Subscription getActiveSubscription(Long userId);

    Subscription cancel(Long subscriptionId);
}
