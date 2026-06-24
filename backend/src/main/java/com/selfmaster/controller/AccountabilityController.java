package com.selfmaster.controller;

import com.selfmaster.entity.User;
import com.selfmaster.service.AccountabilityService;
import com.selfmaster.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AccountabilityController {

    private final AccountabilityService accountabilityService;
    private final AuthService authService;

    @GetMapping("/accountability")
    public String page(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("morningPlan", accountabilityService.getTodayMorningPlan(user.getId()).orElse(null));
        model.addAttribute("nightReview", accountabilityService.getTodayNightReview(user.getId()).orElse(null));
        model.addAttribute("recentLogs", accountabilityService.getAllLogs(user.getId()));
        return "accountability";
    }

    @PostMapping("/accountability/morning")
    public String saveMorning(@RequestParam String topPriorities,
                               @RequestParam String gratitude,
                               @RequestParam String intention) {
        User user = authService.getCurrentUser();
        accountabilityService.saveMorningPlan(user, topPriorities, gratitude, intention);
        return "redirect:/accountability";
    }

    @PostMapping("/accountability/night")
    public String saveNight(@RequestParam String reflection, @RequestParam String wins,
                             @RequestParam String lessons, @RequestParam String tomorrowPlan,
                             @RequestParam Integer productivityRating,
                             @RequestParam Integer disciplineRating,
                             @RequestParam Integer overallRating) {
        User user = authService.getCurrentUser();
        accountabilityService.saveNightReview(user, reflection, wins, lessons, tomorrowPlan,
                productivityRating, disciplineRating, overallRating);
        return "redirect:/accountability";
    }
}
