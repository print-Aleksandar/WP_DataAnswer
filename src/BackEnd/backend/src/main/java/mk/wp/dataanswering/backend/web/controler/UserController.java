package mk.wp.dataanswering.backend.web.controler;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.service.RegisteredUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final RegisteredUserService registeredUserService;
    private final AuthUtils authUtils;

    @PostMapping("/delete")
    public String deleteAccount() {
        RegisteredUser user = authUtils.getCurrentRegisteredUser();
        registeredUserService.softDelete(user.getUserId());
        return "redirect:/logout";
    }
}