package com.selfmaster.controller;

import com.selfmaster.entity.User;
import com.selfmaster.service.AuthService;
import com.selfmaster.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;
    private final AuthService authService;

    @GetMapping("/journal")
    public String page(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("entries", journalService.getUserEntries(user.getId()));
        return "journal";
    }

    @PostMapping("/journal")
    public String createEntry(@RequestParam String title, @RequestParam String content,
                               @RequestParam(required = false) String mood,
                               @RequestParam(required = false) String tags) {
        User user = authService.getCurrentUser();
        journalService.createEntry(user, title, content, mood, tags);
        return "redirect:/journal";
    }

    @PostMapping("/journal/{id}/delete")
    public String deleteEntry(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        journalService.deleteEntry(id, user.getId());
        return "redirect:/journal";
    }
}
