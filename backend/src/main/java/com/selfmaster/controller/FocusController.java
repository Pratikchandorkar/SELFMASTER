package com.selfmaster.controller;

import com.selfmaster.dto.ApiResponse;
import com.selfmaster.entity.FocusSession;
import com.selfmaster.entity.User;
import com.selfmaster.service.AuthService;
import com.selfmaster.service.FocusSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class FocusController {

    private final FocusSessionService focusSessionService;
    private final AuthService authService;

    @GetMapping("/focus")
    public String focusPage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("sessions", focusSessionService.getUserSessions(user.getId()));
        model.addAttribute("todayMinutes", focusSessionService.getTodayFocusMinutes(user.getId()));
        return "focus";
    }

    @PostMapping("/api/focus/start")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> startSession(@RequestBody Map<String, Object> request) {
        User user = authService.getCurrentUser();
        String title = (String) request.getOrDefault("title", "Focus Session");
        String type = (String) request.getOrDefault("sessionType", "DEEP_WORK");
        int minutes = (Integer) request.getOrDefault("plannedMinutes", 25);
        FocusSession session = focusSessionService.startSession(user, title, type, minutes);
        return ResponseEntity.ok(ApiResponse.success("Session started", Map.of("sessionId", session.getId())));
    }

    @PostMapping("/api/focus/{id}/end")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> endSession(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        User user = authService.getCurrentUser();
        int distractions = (Integer) request.getOrDefault("distractions", 0);
        String notes = (String) request.getOrDefault("notes", "");
        FocusSession session = focusSessionService.endSession(id, user.getId(), distractions, notes);
        return ResponseEntity.ok(ApiResponse.success("Session completed", Map.of(
                "duration", session.getActualDurationMinutes(),
                "focusScore", session.getFocusScore()
        )));
    }

    @GetMapping("/api/focus/sessions")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<FocusSession>>> getSessions() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Sessions retrieved", focusSessionService.getUserSessions(user.getId())));
    }
}
