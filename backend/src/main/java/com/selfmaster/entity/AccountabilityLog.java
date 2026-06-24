package com.selfmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accountability_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AccountabilityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false)
    private LogType logType;

    @Column(name = "top_priorities", columnDefinition = "TEXT")
    private String topPriorities;
    @Column(columnDefinition = "TEXT")
    private String gratitude;
    @Column(columnDefinition = "TEXT")
    private String intention;
    @Column(columnDefinition = "TEXT")
    private String reflection;
    @Column(columnDefinition = "TEXT")
    private String wins;
    @Column(columnDefinition = "TEXT")
    private String lessons;
    @Column(name = "tomorrow_plan", columnDefinition = "TEXT")
    private String tomorrowPlan;

    @Column(name = "productivity_rating")
    private Integer productivityRating;
    @Column(name = "discipline_rating")
    private Integer disciplineRating;
    @Column(name = "overall_rating")
    private Integer overallRating;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum LogType { MORNING_PLAN, NIGHT_REVIEW }
}
