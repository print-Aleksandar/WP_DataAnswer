package mk.wp.dataanswering.backend.web.controler;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Client;
import mk.wp.dataanswering.backend.service.ChatService;

@Controller
@RequestMapping(path = {"/home", "/"})
@AllArgsConstructor
public class HomeControler {

    private final ChatService chatService;
    
    @GetMapping()
    public String getHomePage(Model model, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()){
            Client client = (Client) authentication.getPrincipal();
            model.addAttribute("chats", chatService.listByClientId(client.getId()));
        }
        model.addAttribute("bodyContent", "home");
        return "master-template";
    }
    
}
