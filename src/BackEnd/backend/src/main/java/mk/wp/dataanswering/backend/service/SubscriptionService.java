package mk.wp.dataanswering.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import mk.wp.dataanswering.backend.model.Subscription;

public interface SubscriptionService {
        
    Subscription subscribe(Long userId, String planName);
    Subscription getActiveSubscription(Long userId);
    void cancel(Long subscriptionId);
    void deleteAllByUserId(Long userId);
}
