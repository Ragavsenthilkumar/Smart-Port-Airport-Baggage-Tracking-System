package com.example.usecaseprj;

import com.example.usecaseprj.model.Airline;
import com.example.usecaseprj.model.Baggage;
import com.example.usecaseprj.model.BaggageScan;
import com.example.usecaseprj.model.Invoice;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsecaseprjApplicationTests {

    @Test
    void testBaggageModel() {
        Baggage b = new Baggage();
        b.setTagNumber("bag-test-101");
        b.setPassengerName("John Doe");
        b.setStatus("IN_TRANSIT");
        b.setOrigin("JFK");
        b.setDestination("LHR");
        b.setFlightNumber("ba-100");
        b.setLastScanTime(LocalDateTime.now().minusHours(5));

        // Verifies tag uppercase conversion and field assignment
        assertEquals("BAG-TEST-101", b.getTagNumber());
        assertEquals("BA-100", b.getFlightNumber());
        assertEquals("IN_TRANSIT", b.getStatus());
        assertNotNull(b.getLastScanTime());
    }

    @Test
    void testBaggageScanModel() {
        BaggageScan scan = new BaggageScan();
        scan.setBaggageTag("bag-test-101");
        scan.setLocation("Terminal 1 Cargo Sorter");
        scan.setStatus("LOADED");
        scan.setScannedBy("Agent Alpha");

        assertEquals("BAG-TEST-101", scan.getBaggageTag());
        assertEquals("Terminal 1 Cargo Sorter", scan.getLocation());
        assertEquals("LOADED", scan.getStatus());
        assertNotNull(scan.getScanTime());
    }

    @Test
    void testAirlineModel() {
        Airline airline = new Airline();
        airline.setCode("ek");
        airline.setName("Emirates");
        airline.setHubTerminal("Terminal 3 Concourse A");

        assertEquals("EK", airline.getCode());
        assertEquals("Emirates", airline.getName());
    }

    @Test
    void testInvoiceModel() {
        Invoice inv = new Invoice();
        inv.setInvoiceNumber("inv-2026-001");
        inv.setAmount(new BigDecimal("2500.00"));
        inv.setPaymentStatus("PENDING");

        assertEquals("INV-2026-001", inv.getInvoiceNumber());
        assertEquals(new BigDecimal("2500.00"), inv.getAmount());
        assertEquals("PENDING", inv.getPaymentStatus());
        assertNotNull(inv.getInvoiceDate());
    }
}
