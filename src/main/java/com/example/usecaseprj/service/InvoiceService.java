package com.example.usecaseprj.service;

import com.example.usecaseprj.model.Invoice;
import com.example.usecaseprj.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with ID: " + id));
    }

    public List<Invoice> getInvoicesByAirline(Long airlineId) {
        return invoiceRepository.findByAirlineId(airlineId);
    }

    public void createInvoice(Invoice invoice) {
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isBlank()) {
            invoice.setInvoiceNumber("INV-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }

        if (invoiceRepository.existsByInvoiceNumber(invoice.getInvoiceNumber())) {
            throw new IllegalArgumentException("Invoice number '" + invoice.getInvoiceNumber() + "' already exists.");
        }

        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }

        if (invoice.getPaymentStatus() == null || invoice.getPaymentStatus().isBlank()) {
            invoice.setPaymentStatus("PENDING");
        }

        invoiceRepository.save(invoice);
    }

    public void updatePaymentStatus(Long id, String status) {
        invoiceRepository.updatePaymentStatus(id, status);
    }

    public BigDecimal getTotalInvoicedAmount() {
        return invoiceRepository.getTotalInvoicedAmount();
    }

    public BigDecimal getTotalPaidAmount() {
        return invoiceRepository.getTotalPaidAmount();
    }

    public BigDecimal getTotalPendingAmount() {
        return invoiceRepository.getTotalPendingAmount();
    }

    public int getTotalInvoiceCount() {
        return invoiceRepository.countTotal();
    }
}
