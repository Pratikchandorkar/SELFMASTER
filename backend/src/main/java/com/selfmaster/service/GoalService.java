package com.selfmaster.service;

import com.selfmaster.dto.GoalDto;
import com.selfmaster.entity.Goal;
import com.selfmaster.entity.User;
import com.selfmaster.exception.ResourceNotFoundException;
import com.selfmaster.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository goalRepository;

    @Transactional
    public GoalDto.Response createGoal(User user, GoalDto.CreateRequest request) {
        Goal goal = Goal.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .goalType(request.getGoalType() != null ? Goal.GoalType.valueOf(request.getGoalType()) : Goal.GoalType.SHORT_TERM)
                .priority(request.getPriority() != null ? Goal.Priority.valueOf(request.getPriority()) : Goal.Priority.MEDIUM)
                .targetDate(request.getTargetDate())
                .isSmart(request.getIsSmart() != null && request.getIsSmart())
                .specificText(request.getSpecificText())
                .measurableText(request.getMeasurableText())
                .achievableText(request.getAchievableText())
                .relevantText(request.getRelevantText())
                .timeBoundText(request.getTimeBoundText())
                .build();

        goal = goalRepository.save(goal);
        log.info("Goal created: {} for user {}", goal.getTitle(), user.getEmail());
        return toResponse(goal);
    }

    public List<GoalDto.Response> getUserGoals(Long userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<GoalDto.Response> getUserGoalsByStatus(Long userId, String status) {
        return goalRepository.findByUserIdAndStatus(userId, Goal.GoalStatus.valueOf(status))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public GoalDto.Response getGoalById(Long id, Long userId) {
        Goal goal = goalRepository.findById(id)
                .filter(g -> g.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        return toResponse(goal);
    }

    @Transactional
    public GoalDto.Response updateProgress(Long id, Long userId, GoalDto.UpdateProgress request) {
        Goal goal = goalRepository.findById(id)
                .filter(g -> g.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));

        if (request.getProgressPercent() != null) {
            goal.setProgressPercent(request.getProgressPercent());
        }
        if (request.getStatus() != null) {
            goal.setStatus(Goal.GoalStatus.valueOf(request.getStatus()));
            if ("COMPLETED".equals(request.getStatus())) {
                goal.setCompletedAt(LocalDateTime.now());
                goal.setProgressPercent(100);
            }
        }
        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Transactional
    public void deleteGoal(Long id, Long userId) {
        Goal goal = goalRepository.findById(id)
                .filter(g -> g.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        goalRepository.delete(goal);
    }

    private GoalDto.Response toResponse(Goal g) {
        return GoalDto.Response.builder()
                .id(g.getId()).title(g.getTitle()).description(g.getDescription())
                .category(g.getCategory()).goalType(g.getGoalType().name())
                .priority(g.getPriority().name()).status(g.getStatus().name())
                .progressPercent(g.getProgressPercent()).targetDate(g.getTargetDate())
                .isSmart(g.getIsSmart()).aiBreakdown(g.getAiBreakdown())
                .specificText(g.getSpecificText()).measurableText(g.getMeasurableText())
                .achievableText(g.getAchievableText()).relevantText(g.getRelevantText())
                .timeBoundText(g.getTimeBoundText())
                .build();
    }
}
