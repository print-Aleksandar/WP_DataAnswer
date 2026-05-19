package mk.wp.dataanswering.backend.bootstrap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.enums.Role;
import mk.wp.dataanswering.backend.repository.RegisteredUserRepository;

@Component
public class DataHolder {
    
    public static List<RegisteredUser> users = null;

    private final RegisteredUserRepository registeredUserRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataHolder(RegisteredUserRepository registeredUserRepository, PasswordEncoder passwordEncoder) {
        this.registeredUserRepository = registeredUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostConstruct
    public void init() {
        if (registeredUserRepository.findAll().isEmpty()) {
            users = new ArrayList<>();
            users.add(new RegisteredUser(
                "admin",
                "admin",
                "admin@dataanswering.mk",
                "admin",
                passwordEncoder.encode("admin"),
                Role.ROLE_ADMIN));
            registeredUserRepository.saveAll(users);
        }   
    }
}
