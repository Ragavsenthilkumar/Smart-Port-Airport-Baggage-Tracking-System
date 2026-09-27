package com.example.usecaseprj.controller;

import com.example.usecaseprj.model.Invoice;
import com.example.usecaseprj.service.AirlineService;
import com.example.usecaseprj.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final AirlineService airlineService;

    public InvoiceController(InvoiceService invoiceService, AirlineService airlineService) {
        this.invoiceService = invoiceService;
        this.airlineService = airlineService;
    }

    /**
     * List all handling invoices with financial summaries.
     */
    @GetMapping
    public String listInvoices(Model model) {
        if (!model.containsAttribute("newInvoice")) {
            model.addAttribute("newInvoice", new Invoice());
        }
        model.addAttribute("invoices", invoiceService.getAllInvoices());
        model.addAttribute("airlines", airlineService.getAllAirlines());
        model.addAttribute("totalInvoiced", invoiceService.getTotalInvoicedAmount());
        model.addAttribute("totalPaid", invoiceService.getTotalPaidAmount());
        model.addAttribute("totalPending", invoiceService.getTotalPendingAmount());
        return "invoices/list";
    }

    /**
     * Create new handling invoice for an airline.
     */
    @PostMapping
    public String createInvoice(@Valid @ModelAttribute("newInvoice") Invoice invoice,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("invoices", invoiceService.getAllInvoices());
            model.addAttribute("airlines", airlineService.getAllAirlines());
            model.addAttribute("totalInvoiced", invoiceService.getTotalInvoicedAmount());
            model.addAttribute("totalPaid", invoiceService.getTotalPaidAmount());
            model.addAttribute("totalPending", invoiceService.getTotalPendingAmount());
            return "invoices/list";
        }

        try {
            invoiceService.createInvoice(invoice);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Invoice " + invoice.getInvoiceNumber() + " created successfully!");
            return "redirect:/invoices";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("invoices", invoiceService.getAllInvoices());
            model.addAttribute("airlines", airlineService.getAllAirlines());
            model.addAttribute("totalInvoiced", invoiceService.getTotalInvoicedAmount());
            model.addAttribute("totalPaid", invoiceService.getTotalPaidAmount());
            model.addAttribute("totalPending", invoiceService.getTotalPendingAmount());
            return "invoices/list";
        }
    }

    /**
     * Quick status update for an invoice (e.g., mark as PAID).
     */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") String status,
                               RedirectAttributes redirectAttributes) {
        try {
            invoiceService.updatePaymentStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Invoice status updated to " + status + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/invoices";
    }
}
