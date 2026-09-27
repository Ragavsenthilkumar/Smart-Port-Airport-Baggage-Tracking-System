package com.example.usecaseprj.service;

import com.example.usecaseprj.model.Baggage;
import com.example.usecaseprj.model.BaggageScan;
import com.example.usecaseprj.repository.BaggageRepository;
import com.example.usecaseprj.repository.BaggageScanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScanService {

    private final BaggageScanRepository baggageScanRepository;
    private final BaggageRepository baggageRepository;

    public ScanService(BaggageScanRepository baggageScanRepository, BaggageRepository baggageRepository) {
        this.baggageScanRepository = baggageScanRepository;
        this.baggageRepository = baggageRepository;
    }

    /**
     * Requirement 2: Record a baggage scan with its location, scan time, and status.
     * Updates the baggage item's current status and last_scan_time simultaneously.
     */
    @Transactional
    public void recordScan(BaggageScan scan) {
        String tag = scan.getBaggageTag();
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException("Baggage tag cannot be empty.");
        }

        Baggage baggage = baggageRepository.findByTagNumber(tag.trim())
                .orElseThrow(() -> new IllegalArgumentException("No registered baggage found with tag: " + tag));

        if (scan.getScanTime() == null) {
            scan.setScanTime(LocalDateTime.now());
        }

        scan.setBaggageId(baggage.getId());
        scan.setBaggageTag(baggage.getTagNumber());

        // Save scan event
        baggageScanRepository.save(scan);

        // Update baggage current status and last scan timestamp
        baggageRepository.updateStatusAndLastScan(baggage.getId(), scan.getStatus(), scan.getScanTime());
    }

    public List<BaggageScan> getScansByTag(String tagNumber) {
        return baggageScanRepository.findByBaggageTag(tagNumber);
    }

    public List<BaggageScan> getScansByBaggageId(Long baggageId) {
        return baggageScanRepository.findByBaggageId(baggageId);
    }

    public List<BaggageScan> getRecentScans(int limit) {
        return baggageScanRepository.findRecentScans(limit);
    }

    public int getTotalScanCount() {
        return baggageScanRepository.countTotal();
    }
}
