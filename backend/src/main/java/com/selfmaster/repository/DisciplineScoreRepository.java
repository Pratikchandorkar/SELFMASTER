package com.selfmaster.repository;

import com.selfmaster.entity.DisciplineScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DisciplineScoreRepository extends JpaRepository<DisciplineScore, Long> {
    Optional<DisciplineScore> findByUserIdAndScoreDate(Long userId, LocalDate date);
    List<DisciplineScore> findByUserIdAndScoreDateBetweenOrderByScoreDateAsc(Long userId, LocalDate start, LocalDate end);
    Optional<DisciplineScore> findTopByUserIdOrderByScoreDateDesc(Long userId);
}
