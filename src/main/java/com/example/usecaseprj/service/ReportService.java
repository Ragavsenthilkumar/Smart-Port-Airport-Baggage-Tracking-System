package com.example.usecaseprj.service;

import com.example.usecaseprj.model.Baggage;
import com.example.usecaseprj.model.Invoice;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final BaggageService baggageService;
    private final InvoiceService invoiceService;
    private final AirlineService airlineService;
    private final ScanService scanService;

    public ReportService(BaggageService baggageService,
                         InvoiceService invoiceService,
                         AirlineService airlineService,
                         ScanService scanService) {
        this.baggageService = baggageService;
        this.invoiceService = invoiceService;
        this.airlineService = airlineService;
        this.scanService = scanService;
    }

    public Map<String, Object> getTerminalReportData() {
        Map<String, Object> report = new HashMap<>();

        // Ensure overdue bags are flagged
        baggageService.checkAndFlagMisplacedBaggage();

        List<Baggage> allBaggage = baggageService.getAllBaggage();
        Map<String, Integer> statusCounts = baggageService.getBaggageStatusCounts();

        int totalBags = allBaggage.size();
        int inTransitBags = statusCounts.getOrDefault("IN_TRANSIT", 0);
        int misplacedBags = statusCounts.getOrDefault("MISPLACED", 0);
        int claimedBags = statusCounts.getOrDefault("CLAIMED", 0) + statusCounts.getOrDefault("DELIVERED", 0);
        int checkedInBags = statusCounts.getOrDefault("CHECKED_IN", 0);
        int loadedBags = statusCounts.getOrDefault("LOADED", 0);

        List<Baggage> misplacedList = allBaggage.stream()
                .filter(b -> "MISPLACED".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());

        // Invoices and Financial metrics
        List<Invoice> allInvoices = invoiceService.getAllInvoices();
        BigDecimal totalInvoiced = invoiceService.getTotalInvoicedAmount();
        BigDecimal totalPaid = invoiceService.getTotalPaidAmount();
        BigDecimal totalPending = invoiceService.getTotalPendingAmount();

        int totalAirlines = airlineService.getAirlineCount();
        int totalScans = scanService.getTotalScanCount();

        report.put("totalBags", totalBags);
        report.put("inTransitBags", inTransitBags);
        report.put("misplacedBags", misplacedBags);
        report.put("claimedBags", claimedBags);
        report.put("checkedInBags", checkedInBags);
        report.put("loadedBags", loadedBags);
        report.put("statusCounts", statusCounts);
        report.put("misplacedList", misplacedList);

        report.put("totalInvoices", allInvoices.size());
        report.put("totalInvoiced", totalInvoiced);
        report.put("totalPaid", totalPaid);
        report.put("totalPending", totalPending);
        report.put("invoices", allInvoices);

        report.put("totalAirlines", totalAirlines);
        report.put("totalScans", totalScans);

        return report;
    }
}
