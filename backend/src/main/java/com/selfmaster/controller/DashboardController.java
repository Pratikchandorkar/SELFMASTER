package com.selfmaster.controller;

import com.selfmaster.entity.*;
import com.selfmaster.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AuthService authService;
    private final GoalService goalService;
    private final HabitService habitService;
    private final FocusSessionService focusSessionService;
    private final DisciplineScoreService disciplineScoreService;
   

    @GetMapping("/analytics")
    public String analyticsPage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        DisciplineScore scores = disciplineScoreService.getLatestScores(user.getId());
        model.addAttribute("scores", scores);
        model.addAttribute("goals", goalService.getUserGoals(user.getId()));
        model.addAttribute("habits", habitService.getActiveHabits(user.getId()));
        model.addAttribute("focusMinutes", focusSessionService.getTodayFocusMinutes(user.getId()));
        return "analytics";
    }

    @GetMapping("/discipline")
    public String disciplinePage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        DisciplineScore scores = disciplineScoreService.calculateDailyScores(user);
        model.addAttribute("scores", scores);
        return "discipline";
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        DisciplineScore scores = disciplineScoreService.getLatestScores(user.getId());
        model.addAttribute("scores", scores);
        return "profile";
    }

    @GetMapping("/challenges")
    public String challengesPage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        return "challenges";
    }

    @GetMapping("/reports")
    public String reportsPage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        return "reports";
    }
}
