package com.selfmaster.controller;

import com.selfmaster.dto.ApiResponse;
import com.selfmaster.dto.GoalDto;
import com.selfmaster.entity.User;
import com.selfmaster.service.AuthService;
import com.selfmaster.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final AuthService authService;

    @GetMapping("/goals")
    public String goalsPage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("goals", goalService.getUserGoals(user.getId()));
        model.addAttribute("createRequest", new GoalDto.CreateRequest());
        return "goals";
    }

    @PostMapping("/goals")
    public String createGoal(@ModelAttribute GoalDto.CreateRequest request) {
        User user = authService.getCurrentUser();
        goalService.createGoal(user, request);
        return "redirect:/goals";
    }

    // REST APIs
    @GetMapping("/api/goals")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<GoalDto.Response>>> getGoals() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Goals retrieved", goalService.getUserGoals(user.getId())));
    }

    @PostMapping("/api/goals")
    @ResponseBody
    public ResponseEntity<ApiResponse<GoalDto.Response>> createGoalApi(@RequestBody @Valid GoalDto.CreateRequest request) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Goal created", goalService.createGoal(user, request)));
    }

    @PutMapping("/api/goals/{id}/progress")
    @ResponseBody
    public ResponseEntity<ApiResponse<GoalDto.Response>> updateProgress(@PathVariable Long id, @RequestBody GoalDto.UpdateProgress request) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Progress updated", goalService.updateProgress(id, user.getId(), request)));
    }

    @DeleteMapping("/api/goals/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> deleteGoal(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        goalService.deleteGoal(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Goal deleted"));
    }
}
