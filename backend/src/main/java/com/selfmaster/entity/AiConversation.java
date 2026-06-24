package com.selfmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_conversations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AiConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type")
    @Builder.Default
    private ConversationType conversationType = ConversationType.LIFE_COACH;

    @Column(name = "tokens_used") @Builder.Default
    private Integer tokensUsed = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum MessageRole { USER, ASSISTANT, SYSTEM }
    public enum ConversationType { LIFE_COACH, DECISION_SUPPORT, BEHAVIORAL_ANALYSIS, GENERAL }
}
