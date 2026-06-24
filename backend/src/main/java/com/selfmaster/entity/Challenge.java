package com.selfmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenges")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Challenge {

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

    @Column(name = "challenge_type")
    private String challengeType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Difficulty difficulty = Difficulty.MEDIUM;

    @Column(name = "duration_days") @Builder.Default
    private Integer durationDays = 7;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChallengeStatus status = ChallengeStatus.PENDING;

    @Column(name = "progress_percent") @Builder.Default
    private Integer progressPercent = 0;

    @Column(name = "xp_reward") @Builder.Default
    private Integer xpReward = 100;

    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "end_date") private LocalDate endDate;
    @Column(name = "ai_generated") @Builder.Default private Boolean aiGenerated = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum Difficulty { EASY, MEDIUM, HARD, EXTREME }
    public enum ChallengeStatus { ACTIVE, COMPLETED, FAILED, PENDING }
}
