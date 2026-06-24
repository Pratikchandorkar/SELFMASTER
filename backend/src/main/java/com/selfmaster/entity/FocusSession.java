package com.selfmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "focus_sessions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FocusSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type")
    @Builder.Default
    private SessionType sessionType = SessionType.DEEP_WORK;

    @Column(name = "planned_duration_minutes", nullable = false)
    private Integer plannedDurationMinutes;

    @Column(name = "actual_duration_minutes") @Builder.Default
    private Integer actualDurationMinutes = 0;

    @Column(name = "distractions_count") @Builder.Default
    private Integer distractionsCount = 0;

    @Column(name = "focus_score") @Builder.Default
    private Double focusScore = 0.0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FocusStatus status = FocusStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum SessionType { DEEP_WORK, POMODORO, FLOW, DOPAMINE_DETOX }
    public enum FocusStatus { ACTIVE, COMPLETED, ABANDONED }
}
