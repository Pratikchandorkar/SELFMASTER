package com.selfmaster.repository;

import com.selfmaster.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    @Query("SELECT a FROM Achievement a JOIN UserAchievement ua ON ua.achievement.id = a.id WHERE ua.user.id = :userId")
    List<Achievement> findByUserId(Long userId);
}
