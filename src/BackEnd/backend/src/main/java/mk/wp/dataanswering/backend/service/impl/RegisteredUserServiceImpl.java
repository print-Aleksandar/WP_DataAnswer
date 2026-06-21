package mk.wp.dataanswering.backend.service.impl;

import jakarta.transaction.Transactional;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.enums.Role;
import mk.wp.dataanswering.backend.model.exceptions.InvalidArgumentsException;
import mk.wp.dataanswering.backend.model.exceptions.PasswordsDoNotMatchException;
import mk.wp.dataanswering.backend.model.exceptions.UsernameAlreadyExistsException;
import mk.wp.dataanswering.backend.repository.RegisteredUserRepository;
import mk.wp.dataanswering.backend.service.PlanService;
import mk.wp.dataanswering.backend.service.RegisteredUserService;

@Service
@AllArgsConstructor
public class RegisteredUserServiceImpl implements RegisteredUserService {

    private final RegisteredUserRepository registeredUserRepository;
    private final SubscriptionService subscriptionService;
    private final PlanService planService;

    private final PasswordEncoder passwordEncoder;

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

        Long planId = planService.findByPlanName("FREE").getId();
        subscriptionService.subscribe(savedUser.getUserId(), planId);

        return registeredUser;

    }

    @Override
    public boolean isAccountActive(Long id) {
        return registeredUserRepository.findByUserIdAndEnabledIsTrue(id);
    }

    @Transactional
    @Override
    public void softDelete(long userId) {
        RegisteredUser user = registeredUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.softDelete();
        registeredUserRepository.save(user);
    }
}
