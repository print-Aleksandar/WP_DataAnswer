package mk.wp.dataanswering.backend.config;

import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import mk.wp.dataanswering.backend.model.RegisteredUser;

@Component
public class AuthUtils {

    private final HttpServletRequest request;

    public AuthUtils(HttpServletRequest request) {
        this.request = request;
    }

    public boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !(auth instanceof AnonymousAuthenticationToken);
    }

    public RegisteredUser getCurrentRegisteredUser() {
        if (!isLoggedIn()) throw new InvalidUserException();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (RegisteredUser) auth.getPrincipal();
    }

    public String getTempSessionId() {
        return request.getSession().getId();
    }
}
