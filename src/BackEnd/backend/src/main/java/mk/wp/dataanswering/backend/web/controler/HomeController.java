package mk.wp.dataanswering.backend.web.controler;

import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(path={"/", "/home"})
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getHomePage(Model model) {
        model.addAttribute("bodyContent", "home");
        return "master-template";
    }

    @GetMapping("/start-chat")
    public String startChat(Model model) {
        return "chatDUMMY";
    }
}
