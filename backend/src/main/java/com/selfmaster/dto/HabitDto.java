package com.selfmaster.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

public class HabitDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {
        @NotBlank(message = "Habit name is required")
        private String name;
        private String description;
        private String category;
        private String frequency;
        private String habitType;
        private String triggerCue;
        private String reward;
        private String color;
        private String icon;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private String category;
        private String frequency;
        private String habitType;
        private String triggerCue;
        private String reward;
        private Integer currentStreak;
        private Integer longestStreak;
        private Integer totalCompletions;
        private Double habitScore;
        private Boolean isActive;
        private LocalDate startDate;
        private String color;
        private String icon;
        private Boolean completedToday;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LogRequest {
        private Long habitId;
        private LocalDate logDate;
        private Boolean completed;
        private String notes;
        private Integer difficultyRating;
    }
}
