package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.RegisteredUser;

public interface AuthService {
    RegisteredUser login(String username, String password);
}
