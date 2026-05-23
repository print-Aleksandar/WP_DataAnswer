package mk.wp.dataanswering.backend.web.controler;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(path={"/", "/home"})
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final ChatServiceRegistry chatServiceRegistry;


    @GetMapping()
    public String getHomePage(Model model) {
        model.addAttribute("bodyContent", "home");
        return "master-template";
    }

    @GetMapping("/start-chat")
    public String startChat(Model model) {
        model.addAttribute("Id", userService.getCurrentUser().getUserId());
        chatServiceRegistry.getCorrectChatService().startNewChat();
        return "chatDUMMY";
    }
}
