package mk.wp.dataanswering.backend.service.impl;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Client;
import mk.wp.dataanswering.backend.model.exceptions.InvalidArgumentsException;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserCredentialsException;
import mk.wp.dataanswering.backend.repository.ClientRepository;
import mk.wp.dataanswering.backend.service.AuthService;


@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ClientRepository clientRepository;

    @Override
    public Client login(String username, String password) {
        
        if (username == null || password == null){
            throw new InvalidArgumentsException();
        }

        return clientRepository.findByUsernameAndPassword(username, password).orElseThrow(InvalidUserCredentialsException::new);

    }
    
}
