package com.selfmaster.repository;

import com.selfmaster.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId);
    List<Habit> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Habit> findByUserIdAndCategory(Long userId, String category);
    long countByUserIdAndIsActiveTrue(Long userId);
}
