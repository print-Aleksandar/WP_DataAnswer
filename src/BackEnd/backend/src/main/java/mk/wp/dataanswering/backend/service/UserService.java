package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.User;

public interface UserService {

    User getCurrentUser();
    String getUsersName();
}
