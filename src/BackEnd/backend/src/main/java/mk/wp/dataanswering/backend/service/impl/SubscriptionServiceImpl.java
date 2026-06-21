package mk.wp.dataanswering.backend.service.impl;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final PlanService planService;
    private final AuthUtils authUtils;

    @Override
    public Subscription subscribe(Long userId, Long planId) {
        subscriptionRepository.findSubscriptionByUser_UserIdAndIsActiveTrue(userId)
                .ifPresent(s -> cancel(s.getId()));

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Plan plan = planService.findById(planId);

        Subscription subscription = new Subscription();
        subscription.setRegisteredUser(user);
        subscription.setPlan(plan);
        subscription.setActive(true);
        subscription.setEndTs(plan.getPlanMonthlyCost() > 0.0 ?
                LocalDateTime.now().plusMonths(1) : LocalDateTime.now().plusYears(200));

        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription getActiveSubscription(Long userId) {
        return subscriptionRepository
                .findSubscriptionByUser_UserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("SYSTEM ERROR: Every user must have an active subscription."));
    }

    @Override
    public void cancel(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found."));
        if (subscription.getPlan().getPlanName().equals("GUEST")) {
            throw new RuntimeException("Guest plan cannot be canceled.");
        }
        subscription.setActive(false);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        subscriptionRepository.deleteAllByUser_UserId(userId);
    }

    @Override
    public List<Subscription> getSubscriptionHistory(Long userId) throws AccessDeniedException {

        RegisteredUser current = authUtils.getCurrentRegisteredUser();
        if (userId != current.getUserId()) {
            throw new AccessDeniedException("NotUsersChat");
        }
        return subscriptionRepository.findAllByUser_UserId(userId);
    }
}
