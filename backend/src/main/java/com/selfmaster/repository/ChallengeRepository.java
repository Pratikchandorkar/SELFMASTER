package com.selfmaster.repository;

import com.selfmaster.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Challenge> findByUserIdAndStatus(Long userId, Challenge.ChallengeStatus status);
    long countByUserIdAndStatus(Long userId, Challenge.ChallengeStatus status);
}
