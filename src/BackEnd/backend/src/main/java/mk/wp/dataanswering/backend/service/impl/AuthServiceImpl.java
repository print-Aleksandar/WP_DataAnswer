package mk.wp.dataanswering.backend.service.impl;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.exceptions.InvalidArgumentsException;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserCredentialsException;
import mk.wp.dataanswering.backend.repository.RegisteredUserRepository;
import mk.wp.dataanswering.backend.service.AuthService;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegisteredUserRepository registeredUserRepository;

    @Override
    public RegisteredUser login(String username, String password) {
       if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        return this.registeredUserRepository.findByUsernameAndPassword(username, password)
                                  .orElseThrow(InvalidUserCredentialsException::new);

    }
    
}
