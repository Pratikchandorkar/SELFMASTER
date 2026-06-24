package com.selfmaster.controller;

import com.selfmaster.entity.User;
import com.selfmaster.service.AuthService;
import com.selfmaster.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final AuthService authService;

    @GetMapping("/api/reports/weekly")
    public ResponseEntity<byte[]> getWeeklyReport() {
        User user = authService.getCurrentUser();
        byte[] pdf = reportService.generateWeeklyReportPdf(user);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=weekly-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/api/reports/monthly")
    public ResponseEntity<byte[]> getMonthlyReport() {
        User user = authService.getCurrentUser();
        byte[] pdf = reportService.generateMonthlyReportPdf(user);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/api/reports/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        User user = authService.getCurrentUser();
        byte[] excel = reportService.exportDataExcel(user);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=selfmaster-data.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/api/reports/export/csv")
    public ResponseEntity<byte[]> exportCsv() {
        User user = authService.getCurrentUser();
        byte[] csv = reportService.exportDataCsv(user);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=selfmaster-scores.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv);
    }
}
