package mk.wp.dataanswering.backend.bootstrap;

import java.time.LocalDateTime;
import java.util.List;

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

@Component
@AllArgsConstructor
public class DataHolder {
    
    public static List<RegisteredUser> users = null;

    private final RegisteredUserRepository registeredUserRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionRepository subscriptionRepository;

    @PostConstruct
    public void init() {

        if (planRepository.findAll().isEmpty()) {
            Plan free = new Plan();
            free.setPlanName("Free");
            free.setPlanCost(0.0);
            free.setDayChatLimit(5);
            free.setDayPromptLimit(10);

            Plan pro = new Plan();
            pro.setPlanName("Pro");
            pro.setPlanCost(9.99);
            pro.setDayChatLimit(50);
            pro.setDayPromptLimit(200);

            Plan tmp = new Plan();
            tmp.setPlanName("TemporaryChat");
            tmp.setPlanCost(0.0);
            tmp.setDayChatLimit(1);
            tmp.setDayPromptLimit(5);

            planRepository.saveAll(List.of(free, pro, tmp));
        }

        if (registeredUserRepository.findAll().isEmpty()) {
            // users = new ArrayList<>();
            // users.add(new RegisteredUser(
            //     "admin",
            //     "admin",
            //     "admin@dataanswering.mk",
            //     "admin",
            //     passwordEncoder.encode("admin"),
            //     Role.ROLE_ADMIN));
            // registeredUserRepository.saveAll(users);

            RegisteredUser admin = new RegisteredUser(
                    "admin",
                    "admin",
                    "admin@dataanswering.mk",
                    "admin",
                    passwordEncoder.encode("admin"),
                    Role.ROLE_ADMIN
            );
            RegisteredUser savedAdmin = registeredUserRepository.save(admin);

            Plan pro = planRepository.findByPlanName("Pro")
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
