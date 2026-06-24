package com.selfmaster.service;

import com.selfmaster.dto.HabitDto;
import com.selfmaster.entity.*;
import com.selfmaster.exception.ResourceNotFoundException;
import com.selfmaster.repository.HabitLogRepository;
import com.selfmaster.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;

    @Transactional
    public HabitDto.Response createHabit(User user, HabitDto.CreateRequest request) {
        Habit habit = Habit.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .frequency(request.getFrequency() != null ? Habit.Frequency.valueOf(request.getFrequency()) : Habit.Frequency.DAILY)
                .habitType(request.getHabitType() != null ? Habit.HabitType.valueOf(request.getHabitType()) : Habit.HabitType.BUILD)
                .triggerCue(request.getTriggerCue())
                .reward(request.getReward())
                .color(request.getColor() != null ? request.getColor() : "#6C5CE7")
                .icon(request.getIcon() != null ? request.getIcon() : "fa-check")
                .startDate(LocalDate.now())
                .build();

        habit = habitRepository.save(habit);
        log.info("Habit created: {} for user {}", habit.getName(), user.getEmail());
        return toResponse(habit, user.getId());
    }

    public List<HabitDto.Response> getActiveHabits(Long userId) {
        return habitRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId)
                .stream().map(h -> toResponse(h, userId)).collect(Collectors.toList());
    }

    public List<HabitDto.Response> getAllHabits(Long userId) {
        return habitRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(h -> toResponse(h, userId)).collect(Collectors.toList());
    }

    @Transactional
    public void logHabit(User user, HabitDto.LogRequest request) {
        Habit habit = habitRepository.findById(request.getHabitId())
                .filter(h -> h.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Habit", "id", request.getHabitId()));

        LocalDate logDate = request.getLogDate() != null ? request.getLogDate() : LocalDate.now();

        HabitLog logEntry = habitLogRepository.findByHabitIdAndLogDate(habit.getId(), logDate)
                .orElse(HabitLog.builder()
                        .habit(habit).user(user).logDate(logDate).build());

        logEntry.setCompleted(request.getCompleted() != null ? request.getCompleted() : true);
        logEntry.setNotes(request.getNotes());
        logEntry.setDifficultyRating(request.getDifficultyRating() != null ? request.getDifficultyRating() : 3);
        habitLogRepository.save(logEntry);

        
        if (Boolean.TRUE.equals(logEntry.getCompleted())) {
            habit.setTotalCompletions(habit.getTotalCompletions() + 1);
            habit.setCurrentStreak(habit.getCurrentStreak() + 1);
            if (habit.getCurrentStreak() > habit.getLongestStreak()) {
                habit.setLongestStreak(habit.getCurrentStreak());
            }
            
            long daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(
                    habit.getStartDate() != null ? habit.getStartDate() : habit.getCreatedAt().toLocalDate(),
                    LocalDate.now()) + 1;
            habit.setHabitScore(Math.round((double) habit.getTotalCompletions() / daysSinceStart * 100.0) / 1.0);
        } else {
            habit.setTotalMisses(habit.getTotalMisses() + 1);
            habit.setCurrentStreak(0);
        }
        habitRepository.save(habit);
        log.info("Habit logged: {} completed={} for user {}", habit.getName(), logEntry.getCompleted(), user.getEmail());
    }

    public List<HabitLog> getHabitLogsForDateRange(Long habitId, LocalDate start, LocalDate end) {
        return habitLogRepository.findByHabitIdAndLogDateBetween(habitId, start, end);
    }

    public List<HabitLog> getUserLogsForDate(Long userId, LocalDate date) {
        return habitLogRepository.findByUserIdAndLogDate(userId, date);
    }

    @Transactional
    public void deleteHabit(Long id, Long userId) {
        Habit habit = habitRepository.findById(id)
                .filter(h -> h.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Habit", "id", id));
        habitRepository.delete(habit);
    }

    private HabitDto.Response toResponse(Habit h, Long userId) {
        boolean completedToday = habitLogRepository.findByHabitIdAndLogDate(h.getId(), LocalDate.now())
                .map(HabitLog::getCompleted).orElse(false);

        return HabitDto.Response.builder()
                .id(h.getId()).name(h.getName()).description(h.getDescription())
                .category(h.getCategory()).frequency(h.getFrequency().name())
                .habitType(h.getHabitType().name()).triggerCue(h.getTriggerCue())
                .reward(h.getReward()).currentStreak(h.getCurrentStreak())
                .longestStreak(h.getLongestStreak()).totalCompletions(h.getTotalCompletions())
                .habitScore(h.getHabitScore()).isActive(h.getIsActive())
                .startDate(h.getStartDate()).color(h.getColor()).icon(h.getIcon())
                .completedToday(completedToday)
                .build();
    }
}
