package mk.wp.dataanswering.backend.web.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.enums.Role;
import mk.wp.dataanswering.backend.service.RegisteredUserService;

@Controller
@RequestMapping("/register")
@AllArgsConstructor
public class RegisterController {
    
    private final RegisteredUserService registeredUserService;

    @GetMapping
    public String getRegisterPage(Model model) {
        model.addAttribute("bodyContent", "register");
        return "register.html";
    }

    @PostMapping
    public String register(@RequestParam String username,
                           @RequestParam String userFirstName,
                           @RequestParam String userLastName,
                           @RequestParam String userEmail,
                           @RequestParam String password,
                           @RequestParam String repeatPassword,
                           Model model) {

        try {
            this.registeredUserService.register(username, userFirstName, userLastName, userEmail, password,repeatPassword, Role.ROLE_USER);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("bodyContent", "register");
            return "register.html";
        }

    }

    
}
