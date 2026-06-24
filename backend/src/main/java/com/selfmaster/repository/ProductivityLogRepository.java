package com.selfmaster.repository;

import com.selfmaster.entity.ProductivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductivityLogRepository extends JpaRepository<ProductivityLog, Long> {
    Optional<ProductivityLog> findByUserIdAndLogDate(Long userId, LocalDate date);
    List<ProductivityLog> findByUserIdAndLogDateBetweenOrderByLogDateAsc(Long userId, LocalDate start, LocalDate end);
}
