package mk.wp.dataanswering.backend.service;

// import org.springframework.security.core.userdetails.UserDetailsService;

import mk.wp.dataanswering.backend.model.Client;

public interface ClientService {

    Client register(String username, String password, String repeatPassword, String email);

}
// extends UserDetailsService
