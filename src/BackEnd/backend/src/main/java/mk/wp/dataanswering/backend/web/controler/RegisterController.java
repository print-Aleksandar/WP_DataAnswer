package mk.wp.dataanswering.backend.web.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.enums.Role;
import mk.wp.dataanswering.backend.service.ClientService;



@Controller
@RequestMapping(path = {"/register"})
@AllArgsConstructor
public class RegisterController {
    private final ClientService clientService;

    @GetMapping()
    public String getRegisterPage(Model model) {
        model.addAttribute("bodyContent", "register");
        return "master-template";
    }
   
    @PostMapping
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String repeatPassword,
                           @RequestParam String mail,
                           Model model) {
        try {
            clientService.register(username, password, repeatPassword, mail, Role.ROLE_USER);
            return "redirect:/home";
        } catch (RuntimeException  e) {
            model.addAttribute("error", e.getMessage());
            return "register";

        }
    }
}
