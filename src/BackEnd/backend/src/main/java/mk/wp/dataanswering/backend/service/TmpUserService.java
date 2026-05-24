package mk.wp.dataanswering.backend.service;

import jakarta.servlet.http.HttpSession;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.model.User;

public interface TmpUserService {
    TmpUser getTmpUserBySession(HttpSession session);
    TmpUser createTmpUser(HttpSession session);
}
