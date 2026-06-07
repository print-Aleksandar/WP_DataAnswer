package mk.wp.dataanswering.backend.bootstrap;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Plan;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.enums.Role;
import mk.wp.dataanswering.backend.repository.PlanRepository;
import mk.wp.dataanswering.backend.repository.RegisteredUserRepository;
import mk.wp.dataanswering.backend.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class DataHolder {

    @Value("${free.plan.day.chat.limit}")
    private int freePlanDayChatLimit;
    @Value("${free.plan.request.limit-per-chat}")
    private int freePlanRequestLimitPerChat;

    @Value("${pro.plan.day.chat.limit}")
    private int proPlanDayChatLimit;
    @Value("${pro.plan.request.limit-per-chat}")
    private int proPlanRequestLimitPerChat;
    @Value("${pro.plan.monthly.price}")
    private double proPlanMonthlyPrice;

    @Value("${guest.plan.request.limit-per-chat}")
    private int guestPlanRequestLimitPerChat;

    public static List<RegisteredUser> users = null;

    private final RegisteredUserRepository registeredUserRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionRepository subscriptionRepository;


    @PostConstruct
    public void init() {
        if (planRepository.findAll().isEmpty()) {
            Plan free = new Plan("FREE", 0.0, freePlanDayChatLimit, freePlanRequestLimitPerChat);

            Plan pro = new Plan("PRO", proPlanMonthlyPrice, proPlanDayChatLimit, proPlanRequestLimitPerChat);

            Plan guest = new Plan("GUEST", 0.0, Integer.MAX_VALUE, guestPlanRequestLimitPerChat);

            planRepository.saveAll(List.of(free, pro, guest));
        }

        if (registeredUserRepository.findAll().isEmpty()) {

            RegisteredUser admin = new RegisteredUser(
                    "admin",
                    "admin",
                    "admin@dataanswer.mk",
                    "admin",
                    passwordEncoder.encode("admin"),
                    Role.ROLE_ADMIN
            );

            RegisteredUser savedAdmin = registeredUserRepository.save(admin);

            Plan pro = planRepository.findByPlanName("PRO")
                .orElseThrow(() -> new RuntimeException("Pro plan not found"));

            Subscription subscription = new Subscription();
            subscription.setRegisteredUser(savedAdmin);
            subscription.setPlan(pro);
            subscription.setActive(true);
            subscription.setEndTs(LocalDateTime.now().plusYears(200));

            subscriptionRepository.save(subscription);
        }
    }
}
