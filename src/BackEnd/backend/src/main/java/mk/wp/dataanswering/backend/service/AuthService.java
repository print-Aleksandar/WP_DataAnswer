package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.Client;

public interface AuthService {
    Client login(String username, String password);
}
