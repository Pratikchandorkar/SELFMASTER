package com.selfmaster.controller;

import com.selfmaster.entity.User;
import com.selfmaster.service.AuthService;
import com.selfmaster.service.EmotionalLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class EmotionalLogController {

    private final EmotionalLogService emotionalLogService;
    private final AuthService authService;

    @GetMapping("/emotions")
    public String page(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("todayLogs", emotionalLogService.getTodayLogs(user.getId()));
        model.addAttribute("allLogs", emotionalLogService.getAllLogs(user.getId()));
        return "emotions";
    }

    @PostMapping("/emotions")
    public String logEmotion(@RequestParam int moodScore, @RequestParam int energyLevel,
                              @RequestParam int stressLevel, @RequestParam int anxietyLevel,
                              @RequestParam String primaryEmotion,
                              @RequestParam(required = false) String triggers,
                              @RequestParam(required = false) String copingStrategy,
                              @RequestParam(required = false) String notes) {
        User user = authService.getCurrentUser();
        emotionalLogService.logEmotion(user, moodScore, energyLevel, stressLevel, anxietyLevel,
                primaryEmotion, triggers, copingStrategy, notes);
        return "redirect:/emotions";
    }
}
