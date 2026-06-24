package com.selfmaster.controller;

import com.selfmaster.entity.User;
import com.selfmaster.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AuthService authService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            User user = authService.getCurrentUser();
            model.addAttribute("user", user);
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
        return "dashboard";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
