package mk.wp.dataanswering.backend.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Plan;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.repository.RegisteredUserRepository;
import mk.wp.dataanswering.backend.repository.SubscriptionRepository;
import mk.wp.dataanswering.backend.service.PlanService;
import mk.wp.dataanswering.backend.service.SubscriptionService;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final RegisteredUserRepository registeredUserRepository;
    private final PlanService planService;


    @Override
    public Subscription subscribe(Long userId, Long planId, LocalDateTime endTs) {
        subscriptionRepository.findByRegisteredUser_UserIdAndIsActiveTrue(userId).ifPresent(e->{
            e.setActive(false);
            subscriptionRepository.save(e);
        });


        RegisteredUser user = registeredUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Plan plan = planService.findById(planId);

        Subscription subscription = new Subscription();
        subscription.setRegisteredUser(user);
        subscription.setPlan(plan);
        subscription.setActive(true);
        subscription.setEndTs(endTs);

        return subscriptionRepository.save(subscription);

    }

    @Override
    public Subscription getActiveSubscription(Long userId) {
        return subscriptionRepository
                .findByRegisteredUser_UserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("SYSTEM ERROR: Every user must have an active subscription"));
    }

    @Override
    public Subscription cancel(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        subscription.setActive(false);
        subscriptionRepository.save(subscription);
        
        Plan freePlan = planService.findByPlanName("Free");
        Subscription freeSubscription = new Subscription();
        freeSubscription.setRegisteredUser(subscription.getRegisteredUser());
        freeSubscription.setPlan(freePlan);
        freeSubscription.setActive(true);
        freeSubscription.setEndTs(LocalDateTime.MAX);

        return subscriptionRepository.save(subscription);
    }
}
