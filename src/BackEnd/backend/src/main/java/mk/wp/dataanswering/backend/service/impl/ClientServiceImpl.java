package mk.wp.dataanswering.backend.service.impl;

import org.springframework.stereotype.Service;

import mk.wp.dataanswering.backend.model.Client;
import mk.wp.dataanswering.backend.service.ClientService;

// implements ClientService

@Service
public class ClientServiceImpl implements ClientService {

    @Override
    public Client register(String username, String password, String repeatPassword, String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }
    
    // private final ClientRepository clientRepository;
    // private final PasswordEncoder passwordEncoder;

    // public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
    //     this.clientRepository = clientRepository;
    //     this.passwordEncoder = passwordEncoder;
    // }

    // @Override
    // public Client register(String username, String password, String repeatPassword, String email) {
    //     if (username==null || password==null || repeatPassword==null || email==null) {
    //         throw new InvalidArgumentsException();
    //     }

    //     if (!password.equals(repeatPassword)) {
    //         throw new PasswordsDoNotMatchException();
    //     }

    //     if (this.clientRepository.findByUsername(username).isPresent()) {
    //         throw new UsernameAlreadyExistsException(username);
    //     }

    //     Client client = new Client(username, passwordEncoder.encode(password), email);
    //     return this.clientRepository.save(client);
    // }

    // @Override
    // public Client loadUserByUsername(String username) throws UsernameNotFoundException {
    //     return this.clientRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    // }

}
