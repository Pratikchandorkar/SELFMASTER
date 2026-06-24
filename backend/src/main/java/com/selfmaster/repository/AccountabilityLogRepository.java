package com.selfmaster.repository;

import com.selfmaster.entity.AccountabilityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountabilityLogRepository extends JpaRepository<AccountabilityLog, Long> {
    Optional<AccountabilityLog> findByUserIdAndLogDateAndLogType(Long userId, LocalDate date, AccountabilityLog.LogType logType);
    List<AccountabilityLog> findByUserIdAndLogDateBetweenOrderByLogDateDesc(Long userId, LocalDate start, LocalDate end);
    List<AccountabilityLog> findByUserIdOrderByLogDateDesc(Long userId);
}
