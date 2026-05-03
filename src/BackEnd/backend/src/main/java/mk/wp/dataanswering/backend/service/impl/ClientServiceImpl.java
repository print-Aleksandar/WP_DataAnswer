package mk.wp.dataanswering.backend.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import mk.wp.dataanswering.backend.model.Client;
import mk.wp.dataanswering.backend.model.enums.Role;
import mk.wp.dataanswering.backend.model.exceptions.InvalidArgumentsException;
import mk.wp.dataanswering.backend.model.exceptions.PasswordsDoNotMatchException;
import mk.wp.dataanswering.backend.model.exceptions.UsernameAlreadyExistsException;
import mk.wp.dataanswering.backend.model.exceptions.UsernameNotFoundException;
import mk.wp.dataanswering.backend.repository.ClientRepository;
import mk.wp.dataanswering.backend.service.ClientService;


@Service
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Client register(String username, String password, String repeatPassword, String email, Role role) {
        if (username==null || password==null || repeatPassword==null || email==null || username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || email.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        if (!password.equals(repeatPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        if (this.clientRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException(username);
        }

        Client client = new Client(username, passwordEncoder.encode(password), email, role);
        return this.clientRepository.save(client);
    }

    @Override
    public Client loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.clientRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
