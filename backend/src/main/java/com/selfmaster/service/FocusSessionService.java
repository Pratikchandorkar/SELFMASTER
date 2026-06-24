package com.selfmaster.service;

import com.selfmaster.entity.FocusSession;
import com.selfmaster.entity.User;
import com.selfmaster.exception.ResourceNotFoundException;
import com.selfmaster.repository.FocusSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;

    @Transactional
    public FocusSession startSession(User user, String title, String sessionType, int plannedMinutes) {
        FocusSession session = FocusSession.builder()
                .user(user)
                .title(title)
                .sessionType(sessionType != null ? FocusSession.SessionType.valueOf(sessionType) : FocusSession.SessionType.DEEP_WORK)
                .plannedDurationMinutes(plannedMinutes)
                .startedAt(LocalDateTime.now())
                .status(FocusSession.FocusStatus.ACTIVE)
                .build();
        session = focusSessionRepository.save(session);
        log.info("Focus session started: {} for user {}", title, user.getEmail());
        return session;
    }

    @Transactional
    public FocusSession endSession(Long sessionId, Long userId, int distractions, String notes) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("FocusSession", "id", sessionId));

        session.setEndedAt(LocalDateTime.now());
        session.setActualDurationMinutes((int) ChronoUnit.MINUTES.between(session.getStartedAt(), session.getEndedAt()));
        session.setDistractionsCount(distractions);
        session.setNotes(notes);
        session.setStatus(FocusSession.FocusStatus.COMPLETED);

        double rawScore = ((double) session.getActualDurationMinutes() / session.getPlannedDurationMinutes()) * 100;
        rawScore -= distractions * 5;
        session.setFocusScore(Math.max(0, Math.min(100, rawScore)));

        session = focusSessionRepository.save(session);
        log.info("Focus session completed: {} mins, score {}", session.getActualDurationMinutes(), session.getFocusScore());
        return session;
    }

    public List<FocusSession> getUserSessions(Long userId) {
        return focusSessionRepository.findByUserIdOrderByStartedAtDesc(userId);
    }

    public int getTodayFocusMinutes(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return focusSessionRepository.sumDurationBetween(userId, startOfDay, endOfDay);
    }

    public List<FocusSession> getSessionsForDateRange(Long userId, LocalDate start, LocalDate end) {
        return focusSessionRepository.findByUserIdAndStartedAtBetween(userId, start.atStartOfDay(), end.plusDays(1).atStartOfDay());
    }
}
