package mk.wp.dataanswering.backend.bootstrap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mk.wp.dataanswering.backend.model.Client;
import mk.wp.dataanswering.backend.model.enums.Role;
import mk.wp.dataanswering.backend.repository.ClientRepository;

@Component
public class DataHolder {
    
    public static List<Client> users = null;

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataHolder(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostConstruct
    public void init() {
        if (clientRepository.findAll().isEmpty()) {
            users = new ArrayList<>();
            users.add(new Client(
                "admin",
                passwordEncoder.encode("admin"),
                "admin@dataanswering.mk",
                Role.ROLE_ADMIN));
            clientRepository.saveAll(users);
        }   
    }
}
