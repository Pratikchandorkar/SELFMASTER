package com.selfmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "habits")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Frequency frequency = Frequency.DAILY;

    @Enumerated(EnumType.STRING)
    @Column(name = "habit_type")
    @Builder.Default
    private HabitType habitType = HabitType.BUILD;

    @Column(name = "trigger_cue", columnDefinition = "TEXT")
    private String triggerCue;

    @Column(columnDefinition = "TEXT")
    private String reward;

    @Column(name = "current_streak") @Builder.Default
    private Integer currentStreak = 0;
    @Column(name = "longest_streak") @Builder.Default
    private Integer longestStreak = 0;
    @Column(name = "total_completions") @Builder.Default
    private Integer totalCompletions = 0;
    @Column(name = "total_misses") @Builder.Default
    private Integer totalMisses = 0;
    @Column(name = "habit_score") @Builder.Default
    private Double habitScore = 0.0;
    @Column(name = "is_active") @Builder.Default
    private Boolean isActive = true;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Builder.Default
    private String color = "#6C5CE7";
    @Builder.Default
    private String icon = "fa-check";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum Frequency { DAILY, WEEKLY, CUSTOM }
    public enum HabitType { BUILD, BREAK }
}
