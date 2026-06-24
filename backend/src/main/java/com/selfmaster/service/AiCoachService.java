package com.selfmaster.service;

import com.selfmaster.entity.AiConversation;
import com.selfmaster.entity.User;
import com.selfmaster.repository.AiConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiCoachService {

    private final AiConversationRepository aiConversationRepository;

    @Value("${app.openai.api-key}")
    private String apiKey;
    @Value("${app.openai.model}")
    private String model;
    @Value("${app.openai.max-tokens}")
    private int maxTokens;
    @Value("${app.openai.temperature}")
    private double temperature;
    @Value("${app.openai.base-url}")
    private String baseUrl;

    private static final String SYSTEM_PROMPT = """
            You are Self-improvement, an elite AI Life Coach with deep expertise in:
            - Neuroscience of motivation, dopamine systems, and habit formation
            - Psychology of procrastination, self-control, and discipline
            - Behavioral science and cognitive behavioral therapy principles
            - Emotional regulation and mental resilience
            - Productivity systems and deep work methodology
            
            Your role is to:
            1. Analyze the user's behavior patterns deeply
            2. Identify root causes (not symptoms) of their struggles
            3. Explain the neuroscience behind their behavior (dopamine, prefrontal cortex, amygdala)
            4. Provide specific, actionable corrective strategies
            5. Be direct, honest, and empathetic - never sugarcoat
            6. Challenge comfort zones and excuses
            7. Predict potential behavioral failures and prepare the user
            8. Act as a mentor, psychologist, strategist, and accountability partner
            
            Always explain the WHY behind advice using neuroscience and psychology.
            Keep responses focused, structured, and actionable.
            Use bullet points and clear formatting.
            """;

    private static final String DECISION_SYSTEM_PROMPT = """
            You are Self-improvement Decision Analyst. When a user asks about a decision, analyze it across 5 dimensions:
            1. **Future Impact** - How will this affect their long-term goals?
            2. **Emotional Impact** - Is this driven by emotion or logic?
            3. **Productivity Impact** - Will this help or hurt their productivity?
            4. **Mental Impact** - How does this affect mental health and resilience?
            5. **Discipline Impact** - Does this build or erode self-discipline?
            
            Provide a clear recommendation with reasoning grounded in behavioral science.
            Rate each dimension as Positive/Neutral/Negative.
            Be direct and honest.
            """;

    @Transactional
    public String chat(User user, String sessionId, String userMessage, String conversationType) {
        
        AiConversation userMsg = AiConversation.builder()
                .user(user).sessionId(sessionId)
                .role(AiConversation.MessageRole.USER)
                .message(userMessage)
                .conversationType(AiConversation.ConversationType.valueOf(
                        conversationType != null ? conversationType : "LIFE_COACH"))
                .build();
        aiConversationRepository.save(userMsg);

   
        List<AiConversation> history = aiConversationRepository.findTop20BySessionIdOrderByCreatedAtAsc(sessionId);

        String systemPrompt = "DECISION_SUPPORT".equals(conversationType)
                ? DECISION_SYSTEM_PROMPT : SYSTEM_PROMPT;

      
        String aiResponse = callOpenAI(systemPrompt, history);

        
        AiConversation assistantMsg = AiConversation.builder()
                .user(user).sessionId(sessionId)
                .role(AiConversation.MessageRole.ASSISTANT)
                .message(aiResponse)
                .conversationType(userMsg.getConversationType())
                .build();
        aiConversationRepository.save(assistantMsg);

        return aiResponse;
    }

    public List<AiConversation> getConversationHistory(String sessionId) {
        return aiConversationRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    public List<AiConversation> getUserConversations(Long userId) {
        return aiConversationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @SuppressWarnings("unchecked")
    private String callOpenAI(String systemPrompt, List<AiConversation> history) {
        try {
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));

            for (AiConversation conv : history) {
                String role = conv.getRole() == AiConversation.MessageRole.USER ? "user" : "assistant";
                messages.add(Map.of("role", role, "content", conv.getMessage()));
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);

            WebClient webClient = WebClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader("Authorization", "Bearer " + apiKey)
                    .build();

            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            return "I'm currently unable to process your request. Please try again.";
        } catch (Exception e) {
            log.error("OpenAI API call failed: {}", e.getMessage());
            return generateFallbackResponse(history);
        }
    }

    private String generateFallbackResponse(List<AiConversation> history) {
        String lastMessage = history.isEmpty() ? "" :
                history.get(history.size() - 1).getMessage().toLowerCase();

        if (lastMessage.contains("procrastinat")) {
            return "**Procrastination Analysis:**\n\n" +
                    "Procrastination is not laziness — it's an emotional regulation problem. Your brain avoids tasks that trigger negative emotions (boredom, anxiety, self-doubt).\n\n" +
                    "**Root Cause:** Your amygdala (emotional brain) is hijacking your prefrontal cortex (rational brain).\n\n" +
                    "**Immediate Actions:**\n" +
                    "• **2-Minute Rule:** Start the task for just 2 minutes. This bypasses activation energy.\n" +
                    "• **Remove friction:** Close all tabs, put phone in another room.\n" +
                    "• **Identify the emotion:** What feeling are you avoiding?\n" +
                    "• **Future-self visualization:** Your future self will thank you.\n\n" +
                    "*Note: AI coach is running in offline mode. Configure your OpenAI API key for full functionality.*";
        }

        return "**I'm here to help you master yourself.**\n\n" +
                "I can help you with:\n" +
                "• Analyzing procrastination patterns\n" +
                "• Building discipline and self-control\n" +
                "• Understanding your emotional triggers\n" +
                "• Making better decisions\n" +
                "• Creating productive routines\n\n" +
                "Tell me what you're struggling with, and I'll provide science-backed guidance.\n\n" +
                "*Note: Configure your OpenAI API key for full AI coaching capabilities.*";
    }
}
