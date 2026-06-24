package com.selfmaster.controller;

import com.selfmaster.dto.ApiResponse;
import com.selfmaster.dto.HabitDto;
import com.selfmaster.entity.User;
import com.selfmaster.service.AuthService;
import com.selfmaster.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;
    private final AuthService authService;

    @GetMapping("/habits")
    public String habitsPage(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("habits", habitService.getActiveHabits(user.getId()));
        model.addAttribute("createRequest", new HabitDto.CreateRequest());
        return "habits";
    }

    @PostMapping("/habits")
    public String createHabit(@ModelAttribute HabitDto.CreateRequest request) {
        User user = authService.getCurrentUser();
        habitService.createHabit(user, request);
        return "redirect:/habits";
    }

    @PostMapping("/habits/{id}/log")
    public String logHabit(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        HabitDto.LogRequest logRequest = HabitDto.LogRequest.builder().habitId(id).completed(true).build();
        habitService.logHabit(user, logRequest);
        return "redirect:/habits";
    }

    // REST APIs
    @GetMapping("/api/habits")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<HabitDto.Response>>> getHabits() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Habits retrieved", habitService.getActiveHabits(user.getId())));
    }

    @PostMapping("/api/habits")
    @ResponseBody
    public ResponseEntity<ApiResponse<HabitDto.Response>> createHabitApi(@RequestBody @Valid HabitDto.CreateRequest request) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Habit created", habitService.createHabit(user, request)));
    }

    @PostMapping("/api/habits/log")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> logHabitApi(@RequestBody HabitDto.LogRequest request) {
        User user = authService.getCurrentUser();
        habitService.logHabit(user, request);
        return ResponseEntity.ok(ApiResponse.success("Habit logged"));
    }

    @DeleteMapping("/api/habits/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> deleteHabit(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        habitService.deleteHabit(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Habit deleted"));
    }
}
