package mk.wp.dataanswering.backend.web.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = {"/home", "/"})
public class HomeControler {
    
    @GetMapping()
    public String getHomePage(Model model) {
        model.addAttribute("bodyContent", "home");
        return "master-template";
    }
    
}
