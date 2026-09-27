package com.example.usecaseprj.controller;

import com.example.usecaseprj.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Requirement 7: Show a basic terminal report with baggage counts by status and invoice totals.
     */
    @GetMapping
    public String showTerminalReport(Model model) {
        Map<String, Object> reportData = reportService.getTerminalReportData();
        model.addAllAttributes(reportData);
        return "reports/terminal";
    }
}
