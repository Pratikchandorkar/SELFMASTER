package com.selfmaster.repository;

import com.selfmaster.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    Optional<HabitLog> findByHabitIdAndLogDate(Long habitId, LocalDate logDate);
    List<HabitLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);
    List<HabitLog> findByHabitIdAndLogDateBetween(Long habitId, LocalDate start, LocalDate end);
    List<HabitLog> findByUserIdAndLogDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COUNT(hl) FROM HabitLog hl WHERE hl.habit.id = :habitId AND hl.completed = true AND hl.logDate BETWEEN :start AND :end")
    long countCompletedBetween(Long habitId, LocalDate start, LocalDate end);
}
