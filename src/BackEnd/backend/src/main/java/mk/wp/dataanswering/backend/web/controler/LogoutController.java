package mk.wp.dataanswering.backend.web.controler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping(path = {"/logout"})
public class LogoutController {

    @GetMapping()
    public String getMethodName(HttpServletRequest req) {
        req.getSession().invalidate();
        return "redirect:/home";
    }
    
}
