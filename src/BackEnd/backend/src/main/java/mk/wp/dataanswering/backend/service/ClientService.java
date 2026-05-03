package mk.wp.dataanswering.backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import mk.wp.dataanswering.backend.model.Client;
import mk.wp.dataanswering.backend.model.enums.Role;

public interface ClientService extends UserDetailsService {

    Client register(String username, String password, String repeatPassword, String email, Role role);

}

