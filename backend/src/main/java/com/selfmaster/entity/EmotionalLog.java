package com.selfmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "emotional_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmotionalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "mood_score", nullable = false)
    private Integer moodScore;

    @Column(name = "energy_level") @Builder.Default
    private Integer energyLevel = 5;
    @Column(name = "stress_level") @Builder.Default
    private Integer stressLevel = 5;
    @Column(name = "anxiety_level") @Builder.Default
    private Integer anxietyLevel = 5;

    @Column(name = "primary_emotion", length = 50)
    private String primaryEmotion;

    @Column(columnDefinition = "TEXT")
    private String triggers;
    @Column(name = "coping_strategy", columnDefinition = "TEXT")
    private String copingStrategy;
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;
    @Column(name = "log_time")
    private LocalTime logTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
