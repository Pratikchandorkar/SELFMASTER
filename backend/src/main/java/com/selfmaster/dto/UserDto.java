package com.selfmaster.dto;

import lombok.*;
import java.time.LocalDate;

public class UserDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ProfileResponse {
        private Long id;
        private String email;
        private String username;
        private String firstName;
        private String lastName;
        private String fullName;
        private String avatarUrl;
        private String bio;
        private LocalDate dateOfBirth;
        private String gender;
        private String timezone;
        private Integer xpPoints;
        private Integer level;
        private Integer currentStreak;
        private Integer longestStreak;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ProfileUpdateRequest {
        private String firstName;
        private String lastName;
        private String bio;
        private LocalDate dateOfBirth;
        private String gender;
        private String timezone;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DashboardSummary {
        private Integer totalGoals;
        private Integer completedGoals;
        private Integer activeHabits;
        private Integer currentStreak;
        private Integer xpPoints;
        private Integer level;
        private Double disciplineScore;
        private Double focusScore;
        private Double productivityScore;
        private Double overallMasteryScore;
        private Integer focusMinutesToday;
        private Integer unreadNotifications;
    }
}
