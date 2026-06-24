package com.selfmaster.service;

import com.selfmaster.entity.AccountabilityLog;
import com.selfmaster.entity.User;
import com.selfmaster.repository.AccountabilityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountabilityService {

    private final AccountabilityLogRepository accountabilityLogRepository;

    @Transactional
    public AccountabilityLog saveMorningPlan(User user, String topPriorities, String gratitude, String intention) {
        AccountabilityLog logEntry = accountabilityLogRepository
                .findByUserIdAndLogDateAndLogType(user.getId(), LocalDate.now(), AccountabilityLog.LogType.MORNING_PLAN)
                .orElse(AccountabilityLog.builder()
                        .user(user).logDate(LocalDate.now()).logType(AccountabilityLog.LogType.MORNING_PLAN).build());

        logEntry.setTopPriorities(topPriorities);
        logEntry.setGratitude(gratitude);
        logEntry.setIntention(intention);
        logEntry = accountabilityLogRepository.save(logEntry);
        log.info("Morning plan saved for user {}", user.getEmail());
        return logEntry;
    }

    @Transactional
    public AccountabilityLog saveNightReview(User user, String reflection, String wins, String lessons,
                                              String tomorrowPlan, Integer productivityRating,
                                              Integer disciplineRating, Integer overallRating) {
        AccountabilityLog logEntry = accountabilityLogRepository
                .findByUserIdAndLogDateAndLogType(user.getId(), LocalDate.now(), AccountabilityLog.LogType.NIGHT_REVIEW)
                .orElse(AccountabilityLog.builder()
                        .user(user).logDate(LocalDate.now()).logType(AccountabilityLog.LogType.NIGHT_REVIEW).build());

        logEntry.setReflection(reflection);
        logEntry.setWins(wins);
        logEntry.setLessons(lessons);
        logEntry.setTomorrowPlan(tomorrowPlan);
        logEntry.setProductivityRating(productivityRating);
        logEntry.setDisciplineRating(disciplineRating);
        logEntry.setOverallRating(overallRating);
        logEntry = accountabilityLogRepository.save(logEntry);
        log.info("Night review saved for user {}", user.getEmail());
        return logEntry;
    }

    public Optional<AccountabilityLog> getTodayMorningPlan(Long userId) {
        return accountabilityLogRepository.findByUserIdAndLogDateAndLogType(userId, LocalDate.now(), AccountabilityLog.LogType.MORNING_PLAN);
    }

    public Optional<AccountabilityLog> getTodayNightReview(Long userId) {
        return accountabilityLogRepository.findByUserIdAndLogDateAndLogType(userId, LocalDate.now(), AccountabilityLog.LogType.NIGHT_REVIEW);
    }

    public List<AccountabilityLog> getLogsForDateRange(Long userId, LocalDate start, LocalDate end) {
        return accountabilityLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateDesc(userId, start, end);
    }

    public List<AccountabilityLog> getAllLogs(Long userId) {
        return accountabilityLogRepository.findByUserIdOrderByLogDateDesc(userId);
    }
}
