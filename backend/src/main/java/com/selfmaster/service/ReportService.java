package com.selfmaster.service;

import com.selfmaster.entity.*;
import com.selfmaster.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final DisciplineScoreRepository disciplineScoreRepository;
    private final HabitRepository habitRepository;
    private final FocusSessionRepository focusSessionRepository;
    private final EmotionalLogRepository emotionalLogRepository;
    private final GoalRepository goalRepository;

    public byte[] generateWeeklyReportPdf(User user) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);
        return generateReportPdf(user, start, end, "Weekly");
    }

    public byte[] generateMonthlyReportPdf(User user) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);
        return generateReportPdf(user, start, end, "Monthly");
    }

    private byte[] generateReportPdf(User user, LocalDate start, LocalDate end, String type) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            
            DeviceRgb primaryColor = new DeviceRgb(108, 92, 231); 
            DeviceRgb darkColor = new DeviceRgb(45, 52, 54); 

            
            Paragraph title = new Paragraph("Self-improvement - " + type + " Behavioral Intelligence Report")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(primaryColor)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            Paragraph info = new Paragraph("Generated for: " + user.getFullName() + " (" + user.getEmail() + ")\n" +
                    "Period: " + start.format(DateTimeFormatter.ISO_LOCAL_DATE) + " to "
                    + end.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .setFontSize(10)
                    .setFontColor(darkColor)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(info);

            document.add(new Paragraph("\n"));
            SolidLine line = new SolidLine(1f);
            line.setColor(primaryColor);
            document.add(new LineSeparator(line));
            document.add(new Paragraph("\n"));

            
            document.add(new Paragraph("1. Self-Mastery & Discipline Scores")
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(primaryColor));

            List<DisciplineScore> scores = disciplineScoreRepository
                    .findByUserIdAndScoreDateBetweenOrderByScoreDateAsc(user.getId(), start, end);

            if (scores.isEmpty()) {
                document.add(new Paragraph(
                        "No discipline scores recorded for this period. Try completing your habits and planning daily logs."));
            } else {
                double avgOverall = scores.stream().mapToDouble(DisciplineScore::getOverallMasteryScore).average()
                        .orElse(0.0);
                double avgDiscipline = scores.stream().mapToDouble(DisciplineScore::getDisciplineScore).average()
                        .orElse(0.0);
                double avgFocus = scores.stream().mapToDouble(DisciplineScore::getFocusScore).average().orElse(0.0);
                double avgSelfControl = scores.stream().mapToDouble(DisciplineScore::getSelfControlScore).average()
                        .orElse(0.0);
                double avgConsistency = scores.stream().mapToDouble(DisciplineScore::getConsistencyScore).average()
                        .orElse(0.0);
                double avgResilience = scores.stream().mapToDouble(DisciplineScore::getMentalResilienceScore).average()
                        .orElse(0.0);

                Table scoresTable = new Table(UnitValue.createPercentArray(new float[] { 40, 60 }))
                        .useAllAvailableWidth();
                scoresTable.addCell(new Cell().add(new Paragraph("Metric").setBold()));
                scoresTable.addCell(new Cell().add(new Paragraph("Average Score (out of 100)").setBold()));

                scoresTable.addCell(new Cell().add(new Paragraph("Overall Mastery Score")));
                scoresTable.addCell(new Cell().add(new Paragraph(String.format("%.1f", avgOverall))));

                scoresTable.addCell(new Cell().add(new Paragraph("Discipline Score")));
                scoresTable.addCell(new Cell().add(new Paragraph(String.format("%.1f", avgDiscipline))));

                scoresTable.addCell(new Cell().add(new Paragraph("Focus Score")));
                scoresTable.addCell(new Cell().add(new Paragraph(String.format("%.1f", avgFocus))));

                scoresTable.addCell(new Cell().add(new Paragraph("Self-Control Score")));
                scoresTable.addCell(new Cell().add(new Paragraph(String.format("%.1f", avgSelfControl))));

                scoresTable.addCell(new Cell().add(new Paragraph("Consistency Score")));
                scoresTable.addCell(new Cell().add(new Paragraph(String.format("%.1f", avgConsistency))));

                scoresTable.addCell(new Cell().add(new Paragraph("Mental Resilience Score")));
                scoresTable.addCell(new Cell().add(new Paragraph(String.format("%.1f", avgResilience))));

                document.add(scoresTable);
            }

            document.add(new Paragraph("\n"));

            
            document.add(new Paragraph("2. Habits & Consistency Tracker")
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(primaryColor));

            List<Habit> habits = habitRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            if (habits.isEmpty()) {
                document.add(new Paragraph("No habits created yet. Go to the Habits page to start logging."));
            } else {
                Table habitsTable = new Table(UnitValue.createPercentArray(new float[] { 30, 20, 20, 15, 15 }))
                        .useAllAvailableWidth();
                habitsTable.addCell(new Cell().add(new Paragraph("Habit Name").setBold()));
                habitsTable.addCell(new Cell().add(new Paragraph("Category").setBold()));
                habitsTable.addCell(new Cell().add(new Paragraph("Score").setBold()));
                habitsTable.addCell(new Cell().add(new Paragraph("Streak").setBold()));
                habitsTable.addCell(new Cell().add(new Paragraph("Total Logs").setBold()));

                for (Habit h : habits) {
                    habitsTable.addCell(new Cell().add(new Paragraph(h.getName())));
                    habitsTable.addCell(
                            new Cell().add(new Paragraph(h.getCategory() != null ? h.getCategory() : "General")));
                    habitsTable.addCell(new Cell().add(new Paragraph(
                            String.format("%.1f%%", h.getHabitScore() != null ? h.getHabitScore() : 0.0))));
                    habitsTable.addCell(new Cell().add(
                            new Paragraph(String.valueOf(h.getCurrentStreak() != null ? h.getCurrentStreak() : 0))));
                    habitsTable.addCell(new Cell().add(new Paragraph(
                            String.valueOf(h.getTotalCompletions() != null ? h.getTotalCompletions() : 0))));
                }
                document.add(habitsTable);
            }

            document.add(new Paragraph("\n"));

           
            document.add(new Paragraph("3. Focus Sessions & Deep Work")
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(primaryColor));

            List<FocusSession> focusSessions = focusSessionRepository.findByUserIdAndStartedAtBetween(
                    user.getId(), start.atStartOfDay(), end.plusDays(1).atStartOfDay());

            if (focusSessions.isEmpty()) {
                document.add(new Paragraph(
                        "No focus sessions recorded during this period. Try using the Focus Pomodoro Timer."));
            } else {
                int totalMins = focusSessions.stream().mapToInt(FocusSession::getActualDurationMinutes).sum();
                double avgFocusScore = focusSessions.stream().mapToDouble(FocusSession::getFocusScore).average()
                        .orElse(0.0);
                int totalDistractions = focusSessions.stream().mapToInt(FocusSession::getDistractionsCount).sum();

                Paragraph stats = new Paragraph(String.format(
                        "Total Focus Sessions: %d\n" +
                                "Total Focus Duration: %d minutes\n" +
                                "Average Focus Score: %.1f / 100\n" +
                                "Total Distractions: %d times",
                        focusSessions.size(), totalMins, avgFocusScore, totalDistractions))
                        .setFontSize(11);
                document.add(stats);
            }

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("4. Emotional Balance & Logs")
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(primaryColor));

            List<EmotionalLog> emotionalLogs = emotionalLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateDesc(
                    user.getId(), start, end);

            if (emotionalLogs.isEmpty()) {
                document.add(new Paragraph(
                        "No emotional logs recorded for this period. Try logging daily on the Emotional Log page."));
            } else {
                double avgMood = emotionalLogs.stream().mapToDouble(EmotionalLog::getMoodScore).average().orElse(0.0);
                double avgEnergy = emotionalLogs.stream().mapToDouble(EmotionalLog::getEnergyLevel).average()
                        .orElse(0.0);
                double avgStress = emotionalLogs.stream().mapToDouble(EmotionalLog::getStressLevel).average()
                        .orElse(0.0);

                Paragraph emotionalStats = new Paragraph(String.format(
                        "Average Mood Rating: %.1f / 10\n" +
                                "Average Energy Level: %.1f / 10\n" +
                                "Average Stress Level: %.1f / 10",
                        avgMood, avgEnergy, avgStress))
                        .setFontSize(11);
                
                document.add(emotionalStats);
            }

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate PDF report: {}", e.getMessage());
            return new byte[0];
        }
    }

    public byte[] exportDataExcel(User user) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            
            Sheet goalsSheet = workbook.createSheet("Goals");
            Row gHeader = goalsSheet.createRow(0);
            String[] gCols = { "ID", "Title", "Description", "Category", "Type", "Priority", "Status", "Progress",
                    "Target Date" };
            for (int i = 0; i < gCols.length; i++) {
                gHeader.createCell(i).setCellValue(gCols[i]);
            }
            List<Goal> goals = goalRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            int rowIdx = 1;
            for (Goal g : goals) {
                Row row = goalsSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(g.getId());
                row.createCell(1).setCellValue(g.getTitle());
                row.createCell(2).setCellValue(g.getDescription() != null ? g.getDescription() : "");
                row.createCell(3).setCellValue(g.getCategory() != null ? g.getCategory() : "");
                row.createCell(4).setCellValue(g.getGoalType() != null ? g.getGoalType().name() : "");
                row.createCell(5).setCellValue(g.getPriority() != null ? g.getPriority().name() : "");
                row.createCell(6).setCellValue(g.getStatus() != null ? g.getStatus().name() : "");
                row.createCell(7).setCellValue(g.getProgressPercent() != null ? g.getProgressPercent() : 0);
                row.createCell(8).setCellValue(g.getTargetDate() != null ? g.getTargetDate().toString() : "");
            }

            
            Sheet habitsSheet = workbook.createSheet("Habits");
            Row hHeader = habitsSheet.createRow(0);
            String[] hCols = { "ID", "Name", "Description", "Category", "Frequency", "Type", "Trigger", "Reward",
                    "Current Streak", "Longest Streak", "Completions", "Score" };
            for (int i = 0; i < hCols.length; i++) {
                hHeader.createCell(i).setCellValue(hCols[i]);
            }
            List<Habit> habits = habitRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            rowIdx = 1;
            for (Habit h : habits) {
                Row row = habitsSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(h.getId());
                row.createCell(1).setCellValue(h.getName());
                row.createCell(2).setCellValue(h.getDescription() != null ? h.getDescription() : "");
                row.createCell(3).setCellValue(h.getCategory() != null ? h.getCategory() : "");
                row.createCell(4).setCellValue(h.getFrequency() != null ? h.getFrequency().name() : "");
                row.createCell(5).setCellValue(h.getHabitType() != null ? h.getHabitType().name() : "");
                row.createCell(6).setCellValue(h.getTriggerCue() != null ? h.getTriggerCue() : "");
                row.createCell(7).setCellValue(h.getReward() != null ? h.getReward() : "");
                row.createCell(8).setCellValue(h.getCurrentStreak() != null ? h.getCurrentStreak() : 0);
                row.createCell(9).setCellValue(h.getLongestStreak() != null ? h.getLongestStreak() : 0);
                row.createCell(10).setCellValue(h.getTotalCompletions() != null ? h.getTotalCompletions() : 0);
                row.createCell(11).setCellValue(h.getHabitScore() != null ? h.getHabitScore() : 0.0);
            }

            
            Sheet focusSheet = workbook.createSheet("Focus Sessions");
            Row fHeader = focusSheet.createRow(0);
            String[] fCols = { "ID", "Title", "Type", "Planned Duration", "Actual Duration", "Distractions",
                    "Focus Score", "Started At", "Status" };
            for (int i = 0; i < fCols.length; i++) {
                fHeader.createCell(i).setCellValue(fCols[i]);
            }
            List<FocusSession> sessions = focusSessionRepository.findByUserIdOrderByStartedAtDesc(user.getId());
            rowIdx = 1;
            for (FocusSession s : sessions) {
                Row row = focusSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getTitle());
                row.createCell(2).setCellValue(s.getSessionType() != null ? s.getSessionType().name() : "");
                row.createCell(3).setCellValue(s.getPlannedDurationMinutes());
                row.createCell(4).setCellValue(s.getActualDurationMinutes() != null ? s.getActualDurationMinutes() : 0);
                row.createCell(5).setCellValue(s.getDistractionsCount() != null ? s.getDistractionsCount() : 0);
                row.createCell(6).setCellValue(s.getFocusScore() != null ? s.getFocusScore() : 0.0);
                row.createCell(7).setCellValue(s.getStartedAt() != null ? s.getStartedAt().toString() : "");
                row.createCell(8).setCellValue(s.getStatus() != null ? s.getStatus().name() : "");
            }

            
            Sheet emotionsSheet = workbook.createSheet("Emotional Logs");
            Row eHeader = emotionsSheet.createRow(0);
            String[] eCols = { "ID", "Mood", "Energy", "Stress", "Anxiety", "Emotion", "Triggers", "Coping", "Notes",
                    "Date" };
            for (int i = 0; i < eCols.length; i++) {
                eHeader.createCell(i).setCellValue(eCols[i]);
            }
            List<EmotionalLog> logs = emotionalLogRepository.findByUserIdOrderByLogDateDescLogTimeDesc(user.getId());
            rowIdx = 1;
            for (EmotionalLog l : logs) {
                Row row = emotionsSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(l.getId());
                row.createCell(1).setCellValue(l.getMoodScore());
                row.createCell(2).setCellValue(l.getEnergyLevel() != null ? l.getEnergyLevel() : 0);
                row.createCell(3).setCellValue(l.getStressLevel() != null ? l.getStressLevel() : 0);
                row.createCell(4).setCellValue(l.getAnxietyLevel() != null ? l.getAnxietyLevel() : 0);
                row.createCell(5).setCellValue(l.getPrimaryEmotion() != null ? l.getPrimaryEmotion() : "");
                row.createCell(6).setCellValue(l.getTriggers() != null ? l.getTriggers() : "");
                row.createCell(7).setCellValue(l.getCopingStrategy() != null ? l.getCopingStrategy() : "");
                row.createCell(8).setCellValue(l.getNotes() != null ? l.getNotes() : "");
                row.createCell(9).setCellValue(l.getLogDate() != null ? l.getLogDate().toString() : "");
            }

           
            Sheet scoresSheet = workbook.createSheet("Discipline Scores");
            Row sHeader = scoresSheet.createRow(0);
            String[] sCols = { "Date", "Overall Mastery", "Discipline", "Self-Control", "Focus", "Consistency",
                    "Emotional Balance", "Productivity", "Resilience" };
            for (int i = 0; i < sCols.length; i++) {
                sHeader.createCell(i).setCellValue(sCols[i]);
            }
            List<DisciplineScore> scores = disciplineScoreRepository.findByUserIdAndScoreDateBetweenOrderByScoreDateAsc(
                    user.getId(), LocalDate.now().minusDays(180), LocalDate.now());
            rowIdx = 1;
            for (DisciplineScore sc : scores) {
                Row row = scoresSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(sc.getScoreDate().toString());
                row.createCell(1).setCellValue(sc.getOverallMasteryScore() != null ? sc.getOverallMasteryScore() : 0.0);
                row.createCell(2).setCellValue(sc.getDisciplineScore() != null ? sc.getDisciplineScore() : 0.0);
                row.createCell(3).setCellValue(sc.getSelfControlScore() != null ? sc.getSelfControlScore() : 0.0);
                row.createCell(4).setCellValue(sc.getFocusScore() != null ? sc.getFocusScore() : 0.0);
                row.createCell(5).setCellValue(sc.getConsistencyScore() != null ? sc.getConsistencyScore() : 0.0);
                row.createCell(6)
                        .setCellValue(sc.getEmotionalBalanceScore() != null ? sc.getEmotionalBalanceScore() : 0.0);
                row.createCell(7).setCellValue(sc.getProductivityScore() != null ? sc.getProductivityScore() : 0.0);
                row.createCell(8)
                        .setCellValue(sc.getMentalResilienceScore() != null ? sc.getMentalResilienceScore() : 0.0);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to export Excel report: {}", e.getMessage());
            return new byte[0];
        }
    }

    public byte[] exportDataCsv(User user) {
        StringBuilder csv = new StringBuilder();
        csv.append(
                "Date,Overall Mastery,Discipline,Focus,Self-Control,Consistency,Emotional Balance,Productivity,Resilience\n");
        List<DisciplineScore> scores = disciplineScoreRepository.findByUserIdAndScoreDateBetweenOrderByScoreDateAsc(
                user.getId(), LocalDate.now().minusDays(180), LocalDate.now());
        for (DisciplineScore sc : scores) {
            csv.append(String.format("%s,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f\n",
                    sc.getScoreDate().toString(),
                    sc.getOverallMasteryScore() != null ? sc.getOverallMasteryScore() : 0.0,
                    sc.getDisciplineScore() != null ? sc.getDisciplineScore() : 0.0,
                    sc.getFocusScore() != null ? sc.getFocusScore() : 0.0,
                    sc.getSelfControlScore() != null ? sc.getSelfControlScore() : 0.0,
                    sc.getConsistencyScore() != null ? sc.getConsistencyScore() : 0.0,
                    sc.getEmotionalBalanceScore() != null ? sc.getEmotionalBalanceScore() : 0.0,
                    sc.getProductivityScore() != null ? sc.getProductivityScore() : 0.0,
                    sc.getMentalResilienceScore() != null ? sc.getMentalResilienceScore() : 0.0));
        }
        return csv.toString().getBytes();
    }
}
