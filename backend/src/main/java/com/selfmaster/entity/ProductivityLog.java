package com.selfmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "productivity_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "tasks_planned") @Builder.Default private Integer tasksPlanned = 0;
    @Column(name = "tasks_completed") @Builder.Default private Integer tasksCompleted = 0;
    @Column(name = "deep_work_minutes") @Builder.Default private Integer deepWorkMinutes = 0;
    @Column(name = "distractions_count") @Builder.Default private Integer distractionsCount = 0;
    @Column(name = "productivity_score") @Builder.Default private Double productivityScore = 0.0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
