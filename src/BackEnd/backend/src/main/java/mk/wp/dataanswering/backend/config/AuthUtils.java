package mk.wp.dataanswering.backend.config;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.repository.RegisteredUserRepository;
import mk.wp.dataanswering.backend.service.RegisteredUserService;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.User;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final HttpServletRequest request;
    private final RegisteredUserRepository userRepo;

    public boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !(auth instanceof AnonymousAuthenticationToken);
    }

    public RegisteredUser getCurrentRegisteredUser() {
        if (!isLoggedIn()) throw new InvalidUserException();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // return (RegisteredUser) auth.getPrincipal();

        try {
            var id = ((User) auth.getPrincipal()).getUserId();
            return userRepo.findById(id).get();
        } catch (Exception e) {
            System.out.println("[getCurrentRegisteredUser]: " + e.getMessage());
            throw new InvalidUserException();
        }
    }

    public HttpSession getCurrentSession() {
        return request.getSession();
    }
}
