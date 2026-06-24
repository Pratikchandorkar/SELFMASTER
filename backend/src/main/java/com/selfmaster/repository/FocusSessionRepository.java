package com.selfmaster.repository;

import com.selfmaster.entity.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {
    List<FocusSession> findByUserIdOrderByStartedAtDesc(Long userId);
    List<FocusSession> findByUserIdAndStartedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    List<FocusSession> findByUserIdAndStatus(Long userId, FocusSession.FocusStatus status);

    @Query("SELECT COALESCE(SUM(f.actualDurationMinutes), 0) FROM FocusSession f WHERE f.user.id = :userId AND f.startedAt BETWEEN :start AND :end")
    int sumDurationBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
