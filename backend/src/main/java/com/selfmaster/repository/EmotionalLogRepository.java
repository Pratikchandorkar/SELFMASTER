package com.selfmaster.repository;

import com.selfmaster.entity.EmotionalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmotionalLogRepository extends JpaRepository<EmotionalLog, Long> {
    List<EmotionalLog> findByUserIdAndLogDateBetweenOrderByLogDateDesc(Long userId, LocalDate start, LocalDate end);
    List<EmotionalLog> findByUserIdOrderByLogDateDescLogTimeDesc(Long userId);
    List<EmotionalLog> findByUserIdAndLogDate(Long userId, LocalDate date);

    @Query("SELECT AVG(e.moodScore) FROM EmotionalLog e WHERE e.user.id = :userId AND e.logDate BETWEEN :start AND :end")
    Double avgMoodBetween(Long userId, LocalDate start, LocalDate end);
}
