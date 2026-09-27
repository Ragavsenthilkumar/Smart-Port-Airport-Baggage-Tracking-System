package com.example.usecaseprj.controller;

import com.example.usecaseprj.model.Airline;
import com.example.usecaseprj.service.AirlineService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/airlines")
public class AirlineController {

    private final AirlineService airlineService;

    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    /**
     * List all airlines and provide airline registration form.
     */
    @GetMapping
    public String listAirlines(Model model) {
        if (!model.containsAttribute("newAirline")) {
            model.addAttribute("newAirline", new Airline());
        }
        model.addAttribute("airlines", airlineService.getAllAirlines());
        return "airlines/list";
    }

    /**
     * Save new airline record.
     */
    @PostMapping
    public String addAirline(@Valid @ModelAttribute("newAirline") Airline airline,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("airlines", airlineService.getAllAirlines());
            return "airlines/list";
        }

        try {
            airlineService.saveAirline(airline);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Airline/Operator '" + airline.getName() + " (" + airline.getCode() + ")' added successfully!");
            return "redirect:/airlines";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("airlines", airlineService.getAllAirlines());
            return "airlines/list";
        }
    }
}
