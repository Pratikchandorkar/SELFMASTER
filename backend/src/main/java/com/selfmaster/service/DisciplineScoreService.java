package com.selfmaster.service;

import com.selfmaster.entity.*;
import com.selfmaster.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisciplineScoreService {

    private final DisciplineScoreRepository disciplineScoreRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final GoalRepository goalRepository;
    private final FocusSessionRepository focusSessionRepository;
    private final AccountabilityLogRepository accountabilityLogRepository;
    private final EmotionalLogRepository emotionalLogRepository;

    @Transactional
    public DisciplineScore calculateDailyScores(User user) {
        Long userId = user.getId();
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        DisciplineScore score = disciplineScoreRepository.findByUserIdAndScoreDate(userId, today)
                .orElse(DisciplineScore.builder().user(user).scoreDate(today).build());

        
        double habitCompletionRate = calculateHabitCompletionRate(userId, weekAgo, today);
        boolean hasMorningPlan = accountabilityLogRepository
                .findByUserIdAndLogDateAndLogType(userId, today, AccountabilityLog.LogType.MORNING_PLAN).isPresent();
        double disciplineBase = habitCompletionRate * 0.7 + (hasMorningPlan ? 30 : 0);
        score.setDisciplineScore(clamp(disciplineBase));

        
        double focusAvg = focusSessionRepository.findByUserIdAndStartedAtBetween(userId, weekAgo.atStartOfDay(), today.plusDays(1).atStartOfDay())
                .stream().mapToDouble(FocusSession::getFocusScore).average().orElse(50);
        score.setFocusScore(clamp(focusAvg));

        long activeHabits = habitRepository.countByUserIdAndIsActiveTrue(userId);
        int longestStreak = user.getLongestStreak() != null ? user.getLongestStreak() : 0;
        double selfControl = Math.min(100, activeHabits * 5 + longestStreak * 2);
        score.setSelfControlScore(clamp(selfControl));

       
        double avgHabitScore = habitRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId)
                .stream().mapToDouble(Habit::getHabitScore).average().orElse(50);
        score.setConsistencyScore(clamp(avgHabitScore));

        
        Double avgMood = emotionalLogRepository.avgMoodBetween(userId, weekAgo, today);
        score.setEmotionalBalanceScore(clamp(avgMood != null ? avgMood * 10 : 50));

      
        long completedGoals = goalRepository.countByUserIdAndStatus(userId, Goal.GoalStatus.COMPLETED);
        long totalGoals = goalRepository.findByUserIdOrderByCreatedAtDesc(userId).size();
        double goalRate = totalGoals > 0 ? ((double) completedGoals / totalGoals) * 100 : 50;
        int focusMinutes = focusSessionRepository.sumDurationBetween(userId, weekAgo.atStartOfDay(), today.plusDays(1).atStartOfDay());
        double productivity = goalRate * 0.5 + Math.min(50, focusMinutes / 60.0 * 5);
        score.setProductivityScore(clamp(productivity));

     
        double resilience = (score.getDisciplineScore() * 0.3 + score.getEmotionalBalanceScore() * 0.3
                + score.getConsistencyScore() * 0.4);
        score.setMentalResilienceScore(clamp(resilience));

      
        double overall = (score.getDisciplineScore() * 0.2 + score.getFocusScore() * 0.15
                + score.getSelfControlScore() * 0.15 + score.getConsistencyScore() * 0.15
                + score.getEmotionalBalanceScore() * 0.1 + score.getProductivityScore() * 0.15
                + score.getMentalResilienceScore() * 0.1);
        score.setOverallMasteryScore(clamp(overall));

        score = disciplineScoreRepository.save(score);
        log.info("Discipline scores calculated for user {}: overall={}", user.getEmail(), score.getOverallMasteryScore());
        return score;
    }

    public DisciplineScore getLatestScores(Long userId) {
        return disciplineScoreRepository.findTopByUserIdOrderByScoreDateDesc(userId)
                .orElse(DisciplineScore.builder().scoreDate(LocalDate.now()).build());
    }

    private double calculateHabitCompletionRate(Long userId, LocalDate start, LocalDate end) {
        long total = habitRepository.countByUserIdAndIsActiveTrue(userId);
        if (total == 0) return 50;
        long days = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        long completed = habitLogRepository.findByUserIdAndLogDateBetween(userId, start, end)
                .stream().filter(hl -> Boolean.TRUE.equals(hl.getCompleted())).count();
        return Math.min(100, (completed / (double)(total * days)) * 100);
    }

    private double clamp(double value) {
        return Math.max(0, Math.min(100, Math.round(value * 10.0) / 10.0));
    }
}
