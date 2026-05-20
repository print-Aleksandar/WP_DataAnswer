package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.model.User;

public interface UserService {
    User getCurrentUser();
    TmpUser getTmpUserBySession(String sessionId);
    TmpUser createTmpUser(String sessionId);
}
