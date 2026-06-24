package com.selfmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type")
    @Builder.Default
    private GoalType goalType = GoalType.SHORT_TERM;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GoalStatus status = GoalStatus.NOT_STARTED;

    @Column(name = "progress_percent")
    @Builder.Default
    private Integer progressPercent = 0;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "ai_breakdown", columnDefinition = "TEXT")
    private String aiBreakdown;

    @Column(name = "is_smart")
    @Builder.Default
    private Boolean isSmart = false;

    @Column(name = "specific_text", columnDefinition = "TEXT")
    private String specificText;
    @Column(name = "measurable_text", columnDefinition = "TEXT")
    private String measurableText;
    @Column(name = "achievable_text", columnDefinition = "TEXT")
    private String achievableText;
    @Column(name = "relevant_text", columnDefinition = "TEXT")
    private String relevantText;
    @Column(name = "time_bound_text", columnDefinition = "TEXT")
    private String timeBoundText;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum GoalType { SHORT_TERM, LONG_TERM, DAILY }
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
    public enum GoalStatus { NOT_STARTED, IN_PROGRESS, COMPLETED, ABANDONED }
}
