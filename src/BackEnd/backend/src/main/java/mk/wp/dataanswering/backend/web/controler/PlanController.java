package mk.wp.dataanswering.backend.web.controler;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.repository.RegisteredUserRepository;
import mk.wp.dataanswering.backend.service.PlanService;
import mk.wp.dataanswering.backend.service.SubscriptionService;

@Controller
@RequestMapping("/plans")
@AllArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final RegisteredUserRepository registeredUserRepository;

    @GetMapping
    public String getPlansPage(Authentication authentication, Model model) {
        String username = authentication.getName();
        
        RegisteredUser user = registeredUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User '" + username + "' not found in database"));
        
        model.addAttribute("activeSub", subscriptionService.getActiveSubscription(user.getUserId()));
        model.addAttribute("plans", planService.listAll());
        model.addAttribute("bodyContent", "plans");
        return "master-template";
    }

    @PostMapping("/subscribe/{planId}")
    public String subscribe(@PathVariable Long planId,
                                @AuthenticationPrincipal RegisteredUser user,
                                Model model) {
        try {
            subscriptionService.subscribe(
                    user.getUserId(),
                    planId);
            return "redirect:/plans";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("bodyContent", "plans");
            return "master-template";
        }
    }

    @PostMapping("/cancel/{subscriptionId}")
    public String cancel(@PathVariable Long subscriptionId, 
                            Model model) {
        try {
            subscriptionService.cancel(subscriptionId);
            return "redirect:/plans";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("bodyContent", "plans");
            return "master-template";
        }
    }
    
}
