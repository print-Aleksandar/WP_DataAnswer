package mk.wp.dataanswering.backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.enums.Role;

public interface RegisteredUserService extends UserDetailsService {
    
    RegisteredUser register(String username,String userFirstName, String userEmail, String userLastName,String password, String repeatPassword,Role role);
    
}
