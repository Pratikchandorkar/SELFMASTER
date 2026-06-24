package com.selfmaster.service;

import com.selfmaster.entity.EmotionalLog;
import com.selfmaster.entity.User;
import com.selfmaster.repository.EmotionalLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmotionalLogService {

    private final EmotionalLogRepository emotionalLogRepository;

    @Transactional
    public EmotionalLog logEmotion(User user, int moodScore, int energyLevel, int stressLevel,
                                    int anxietyLevel, String primaryEmotion, String triggers,
                                    String copingStrategy, String notes) {
        EmotionalLog entry = EmotionalLog.builder()
                .user(user)
                .moodScore(moodScore)
                .energyLevel(energyLevel)
                .stressLevel(stressLevel)
                .anxietyLevel(anxietyLevel)
                .primaryEmotion(primaryEmotion)
                .triggers(triggers)
                .copingStrategy(copingStrategy)
                .notes(notes)
                .logDate(LocalDate.now())
                .logTime(LocalTime.now())
                .build();
        entry = emotionalLogRepository.save(entry);
        log.info("Emotional log saved: mood={} for user {}", moodScore, user.getEmail());
        return entry;
    }

    public List<EmotionalLog> getTodayLogs(Long userId) {
        return emotionalLogRepository.findByUserIdAndLogDate(userId, LocalDate.now());
    }

    public List<EmotionalLog> getLogsForDateRange(Long userId, LocalDate start, LocalDate end) {
        return emotionalLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateDesc(userId, start, end);
    }

    public Double getAverageMood(Long userId, LocalDate start, LocalDate end) {
        return emotionalLogRepository.avgMoodBetween(userId, start, end);
    }

    public List<EmotionalLog> getAllLogs(Long userId) {
        return emotionalLogRepository.findByUserIdOrderByLogDateDescLogTimeDesc(userId);
    }
}
