package com.example.usecaseprj.controller;

import com.example.usecaseprj.model.Baggage;
import com.example.usecaseprj.model.BaggageScan;
import com.example.usecaseprj.service.AirlineService;
import com.example.usecaseprj.service.BaggageService;
import com.example.usecaseprj.service.InvoiceService;
import com.example.usecaseprj.service.ScanService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class BaggageController {

    private final BaggageService baggageService;
    private final AirlineService airlineService;
    private final ScanService scanService;
    private final InvoiceService invoiceService;

    public BaggageController(BaggageService baggageService,
                             AirlineService airlineService,
                             ScanService scanService,
                             InvoiceService invoiceService) {
        this.baggageService = baggageService;
        this.airlineService = airlineService;
        this.scanService = scanService;
        this.invoiceService = invoiceService;
    }

    /**
     * Dashboard homepage showing high-level stats and operations.
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        baggageService.checkAndFlagMisplacedBaggage();

        Map<String, Integer> statusCounts = baggageService.getBaggageStatusCounts();
        model.addAttribute("totalBaggage", baggageService.getTotalBaggageCount());
        model.addAttribute("inTransitCount", statusCounts.getOrDefault("IN_TRANSIT", 0));
        model.addAttribute("misplacedCount", statusCounts.getOrDefault("MISPLACED", 0));
        model.addAttribute("claimedCount", statusCounts.getOrDefault("CLAIMED", 0) + statusCounts.getOrDefault("DELIVERED", 0));
        model.addAttribute("checkedInCount", statusCounts.getOrDefault("CHECKED_IN", 0));

        model.addAttribute("totalAirlines", airlineService.getAirlineCount());
        model.addAttribute("totalInvoiced", invoiceService.getTotalInvoicedAmount());
        model.addAttribute("recentScans", scanService.getRecentScans(6));
        model.addAttribute("allBaggage", baggageService.getAllBaggage());

        return "dashboard";
    }

    /**
     * List all registered baggage.
     */
    @GetMapping("/baggage")
    public String listBaggage(Model model) {
        model.addAttribute("baggageList", baggageService.getAllBaggage());
        return "baggage/list";
    }

    /**
     * Show baggage registration form.
     */
    @GetMapping("/baggage/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("baggage")) {
            model.addAttribute("baggage", new Baggage());
        }
        model.addAttribute("airlines", airlineService.getAllAirlines());
        return "baggage/register";
    }

    /**
     * Process baggage registration.
     */
    @PostMapping("/baggage/register")
    public String registerBaggage(@Valid @ModelAttribute("baggage") Baggage baggage,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("airlines", airlineService.getAllAirlines());
            return "baggage/register";
        }

        try {
            baggageService.registerBaggage(baggage);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Baggage tag " + baggage.getTagNumber() + " registered successfully for passenger " + baggage.getPassengerName() + "!");
            return "redirect:/baggage/details/" + baggage.getTagNumber();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("airlines", airlineService.getAllAirlines());
            return "baggage/register";
        }
    }

    /**
     * Search baggage by tag number.
     */
    @GetMapping("/baggage/search")
    public String searchBaggage(@RequestParam(value = "tag", required = false) String tag,
                                Model model) {
        if (tag != null && !tag.trim().isEmpty()) {
            Optional<Baggage> baggageOpt = baggageService.findByTagNumber(tag.trim());
            if (baggageOpt.isPresent()) {
                Baggage baggage = baggageOpt.get();
                List<BaggageScan> scans = scanService.getScansByTag(baggage.getTagNumber());
                model.addAttribute("baggage", baggage);
                model.addAttribute("scans", scans);
                model.addAttribute("searchedTag", tag.trim());
                return "baggage/details";
            } else {
                model.addAttribute("errorMessage", "No baggage found matching tag number '" + tag.trim() + "'.");
                model.addAttribute("searchedTag", tag.trim());
            }
        }
        return "baggage/search";
    }

    /**
     * View detailed timeline and scan history for a specific tag.
     */
    @GetMapping("/baggage/details/{tag}")
    public String viewBaggageDetails(@PathVariable("tag") String tag, Model model) {
        Optional<Baggage> baggageOpt = baggageService.findByTagNumber(tag);
        if (baggageOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Baggage with tag '" + tag + "' does not exist.");
            return "baggage/search";
        }
        Baggage baggage = baggageOpt.get();
        List<BaggageScan> scans = scanService.getScansByTag(baggage.getTagNumber());
        model.addAttribute("baggage", baggage);
        model.addAttribute("scans", scans);
        return "baggage/details";
    }
}
