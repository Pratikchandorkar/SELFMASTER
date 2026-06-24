package com.selfmaster.repository;

import com.selfmaster.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByUserIdOrderByEntryDateDesc(Long userId);
    List<JournalEntry> findByUserIdAndEntryDateBetween(Long userId, LocalDate start, LocalDate end);
}
