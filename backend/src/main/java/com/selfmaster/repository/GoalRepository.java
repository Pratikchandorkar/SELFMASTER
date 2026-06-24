package com.selfmaster.repository;

import com.selfmaster.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Goal> findByUserIdAndStatus(Long userId, Goal.GoalStatus status);
    List<Goal> findByUserIdAndGoalType(Long userId, Goal.GoalType goalType);
    long countByUserIdAndStatus(Long userId, Goal.GoalStatus status);
}
