package mk.wp.dataanswering.backend.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Plan;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.enums.Role;
import mk.wp.dataanswering.backend.model.exceptions.InvalidArgumentsException;
import mk.wp.dataanswering.backend.model.exceptions.PasswordsDoNotMatchException;
import mk.wp.dataanswering.backend.model.exceptions.UsernameAlreadyExistsException;
import mk.wp.dataanswering.backend.repository.PlanRepository;
import mk.wp.dataanswering.backend.repository.RegisteredUserRepository;
import mk.wp.dataanswering.backend.repository.SubscriptionRepository;
import mk.wp.dataanswering.backend.service.RegisteredUserService;

@Service
@AllArgsConstructor
public class RegisteredUserServiceImpl implements RegisteredUserService {

    private final RegisteredUserRepository registeredUserRepository;
    private final PlanRepository planRepository;

    private final PasswordEncoder passwordEncoder;

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return registeredUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public RegisteredUser register(String username,String userFirstName, String userEmail, String userLastName,String password, String repeatPassword,Role role){

        if (username == null || userFirstName == null || userEmail == null || userLastName == null || password == null) {
            throw new InvalidArgumentsException();
        }

        if (username.isEmpty() || userFirstName.isEmpty() || userEmail.isEmpty() || userLastName.isEmpty() || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        if (!password.equals(repeatPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        if (this.registeredUserRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException(username);
        }
    
        RegisteredUser registeredUser = new RegisteredUser(username, userFirstName, userEmail, userLastName, passwordEncoder.encode(password), role);

        RegisteredUser savedUser = registeredUserRepository.save(registeredUser);

        Plan free = planRepository.findByPlanName("Free").orElseThrow(() -> new RuntimeException("Free plan not found — check DataHolder"));

        Subscription subscription = new Subscription();
        subscription.setRegisteredUser(savedUser);
        subscription.setPlan(free);
        subscription.setActive(true);
        subscription.setEndTs(LocalDateTime.now().plusYears(200)); //200 zashto e free plan nikogash nema da isteche

        subscriptionRepository.save(subscription);

        return registeredUser;

    }

    
    
}
