package com.selfmaster.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

public class GoalDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        private String title;
        private String description;
        private String category;
        private String goalType;
        private String priority;
        private LocalDate targetDate;
        private Boolean isSmart;
        private String specificText;
        private String measurableText;
        private String achievableText;
        private String relevantText;
        private String timeBoundText;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String category;
        private String goalType;
        private String priority;
        private String status;
        private Integer progressPercent;
        private LocalDate targetDate;
        private Boolean isSmart;
        private String aiBreakdown;
        private String specificText;
        private String measurableText;
        private String achievableText;
        private String relevantText;
        private String timeBoundText;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateProgress {
        private Integer progressPercent;
        private String status;
    }
}
