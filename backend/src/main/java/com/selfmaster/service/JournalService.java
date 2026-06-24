package com.selfmaster.service;

import com.selfmaster.entity.JournalEntry;
import com.selfmaster.entity.User;
import com.selfmaster.exception.ResourceNotFoundException;
import com.selfmaster.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;

    @Transactional
    public JournalEntry createEntry(User user, String title, String content, String mood, String tags) {
        JournalEntry entry = JournalEntry.builder()
                .user(user).title(title).content(content)
                .mood(mood).tags(tags).entryDate(LocalDate.now())
                .build();
        entry = journalEntryRepository.save(entry);
        log.info("Journal entry created for user {}", user.getEmail());
        return entry;
    }

    public List<JournalEntry> getUserEntries(Long userId) {
        return journalEntryRepository.findByUserIdOrderByEntryDateDesc(userId);
    }

    public JournalEntry getEntryById(Long id, Long userId) {
        return journalEntryRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("JournalEntry", "id", id));
    }

    @Transactional
    public JournalEntry updateEntry(Long id, Long userId, String title, String content, String mood, String tags) {
        JournalEntry entry = getEntryById(id, userId);
        if (title != null) entry.setTitle(title);
        if (content != null) entry.setContent(content);
        if (mood != null) entry.setMood(mood);
        if (tags != null) entry.setTags(tags);
        return journalEntryRepository.save(entry);
    }

    @Transactional
    public void deleteEntry(Long id, Long userId) {
        JournalEntry entry = getEntryById(id, userId);
        journalEntryRepository.delete(entry);
    }
}
