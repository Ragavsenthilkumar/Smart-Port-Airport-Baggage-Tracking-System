package com.example.usecaseprj.controller;

import com.example.usecaseprj.model.BaggageScan;
import com.example.usecaseprj.service.BaggageService;
import com.example.usecaseprj.service.ScanService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/scans")
public class ScanController {

    private final ScanService scanService;
    private final BaggageService baggageService;

    public ScanController(ScanService scanService, BaggageService baggageService) {
        this.scanService = scanService;
        this.baggageService = baggageService;
    }

    /**
     * List all recent scans across terminals and ports.
     */
    @GetMapping
    public String listScans(Model model) {
        model.addAttribute("scans", scanService.getRecentScans(50));
        return "scans/list";
    }

    /**
     * Show form to add a checkpoint scan.
     */
    @GetMapping("/add")
    public String showAddScanForm(@RequestParam(value = "tag", required = false) String tag,
                                  Model model) {
        BaggageScan scan = new BaggageScan();
        if (tag != null && !tag.isBlank()) {
            scan.setBaggageTag(tag.trim().toUpperCase());
        }
        scan.setScanTime(LocalDateTime.now());
        model.addAttribute("scan", scan);
        model.addAttribute("allBaggage", baggageService.getAllBaggage());
        return "scans/add";
    }

    /**
     * Process new scan recording.
     */
    @PostMapping("/add")
    public String processAddScan(@Valid @ModelAttribute("scan") BaggageScan scan,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allBaggage", baggageService.getAllBaggage());
            return "scans/add";
        }

        try {
            scanService.recordScan(scan);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Scan recorded successfully for tag " + scan.getBaggageTag() + " at " + scan.getLocation() + " (Status: " + scan.getStatus() + ")");
            return "redirect:/baggage/details/" + scan.getBaggageTag();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("allBaggage", baggageService.getAllBaggage());
            return "scans/add";
        }
    }
}
