package com.selfmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "discipline_scores")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DisciplineScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "score_date", nullable = false)
    private LocalDate scoreDate;

    @Column(name = "discipline_score") @Builder.Default private Double disciplineScore = 0.0;
    @Column(name = "self_control_score") @Builder.Default private Double selfControlScore = 0.0;
    @Column(name = "focus_score") @Builder.Default private Double focusScore = 0.0;
    @Column(name = "consistency_score") @Builder.Default private Double consistencyScore = 0.0;
    @Column(name = "emotional_balance_score") @Builder.Default private Double emotionalBalanceScore = 0.0;
    @Column(name = "productivity_score") @Builder.Default private Double productivityScore = 0.0;
    @Column(name = "mental_resilience_score") @Builder.Default private Double mentalResilienceScore = 0.0;
    @Column(name = "overall_mastery_score") @Builder.Default private Double overallMasteryScore = 0.0;

    @Column(name = "ai_insights", columnDefinition = "TEXT")
    private String aiInsights;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
