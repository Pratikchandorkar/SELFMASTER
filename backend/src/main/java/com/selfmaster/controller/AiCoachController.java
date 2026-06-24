package com.selfmaster.controller;

import com.selfmaster.dto.ApiResponse;
import com.selfmaster.entity.AiConversation;
import com.selfmaster.entity.User;
import com.selfmaster.service.AiCoachService;
import com.selfmaster.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AiCoachController {

    private final AiCoachService aiCoachService;
    private final AuthService authService;

    @GetMapping("/ai-coach")
    public String coachPage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("sessionId", UUID.randomUUID().toString());
        return "ai-coach";
    }

    @GetMapping("/ai-decision")
    public String decisionPage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("sessionId", UUID.randomUUID().toString());
        return "ai-decision";
    }

    @PostMapping("/api/ai/chat")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, String>>> chat(@RequestBody Map<String, String> request) {
        User user = authService.getCurrentUser();
        String sessionId = request.getOrDefault("sessionId", UUID.randomUUID().toString());
        String message = request.get("message");
        String type = request.getOrDefault("conversationType", "LIFE_COACH");
        String response = aiCoachService.chat(user, sessionId, message, type);
        return ResponseEntity.ok(ApiResponse.success("Response generated", Map.of("response", response, "sessionId", sessionId)));
    }

    @GetMapping("/api/ai/history/{sessionId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<AiConversation>>> getHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(ApiResponse.success("History retrieved", aiCoachService.getConversationHistory(sessionId)));
    }
}
