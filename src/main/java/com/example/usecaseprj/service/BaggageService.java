package com.example.usecaseprj.service;

import com.example.usecaseprj.model.Baggage;
import com.example.usecaseprj.model.BaggageScan;
import com.example.usecaseprj.repository.BaggageRepository;
import com.example.usecaseprj.repository.BaggageScanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BaggageService {

    private static final Logger log = LoggerFactory.getLogger(BaggageService.class);

    private final BaggageRepository baggageRepository;
    private final BaggageScanRepository baggageScanRepository;

    public BaggageService(BaggageRepository baggageRepository, BaggageScanRepository baggageScanRepository) {
        this.baggageRepository = baggageRepository;
        this.baggageScanRepository = baggageScanRepository;
    }

    /**
     * Requirement 4: Automatically flag baggage as MISPLACED when it is in
     * active transit ('IN_TRANSIT') and has had no scan for more than four hours.
     *
     * How it works:
     * 1. This method queries MySQL using TIMESTAMPDIFF(HOUR, last_scan_time, NOW()) >= 4.
     * 2. Any piece of baggage currently marked 'IN_TRANSIT' that hasn't received a checkpoint
     *    scan within the last 4 hours is automatically updated to 'MISPLACED'.
     * 3. It runs automatically in the background every 60 seconds (@Scheduled), and is also
     *    called on-demand whenever the dashboard or baggage list is accessed.
     */
    @Scheduled(fixedRate = 60000)
    public int checkAndFlagMisplacedBaggage() {
        int flaggedCount = baggageRepository.markOverdueBaggageAsMisplaced();
        if (flaggedCount > 0) {
            log.warn("Auto-flagged {} in-transit baggage item(s) as MISPLACED (no scan in > 4 hours).", flaggedCount);
        }
        return flaggedCount;
    }

    public List<Baggage> getAllBaggage() {
        // Ensure status is updated prior to returning list
        baggageRepository.markOverdueBaggageAsMisplaced();
        return baggageRepository.findAll();
    }

    public Optional<Baggage> findByTagNumber(String tagNumber) {
        baggageRepository.markOverdueBaggageAsMisplaced();
        return baggageRepository.findByTagNumber(tagNumber);
    }

    public Baggage getBaggageByTag(String tagNumber) {
        return findByTagNumber(tagNumber)
                .orElseThrow(() -> new IllegalArgumentException("No baggage found with tag number: " + tagNumber));
    }

    public Baggage getBaggageById(Long id) {
        baggageRepository.markOverdueBaggageAsMisplaced();
        return baggageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No baggage found with ID: " + id));
    }

    @Transactional
    public void registerBaggage(Baggage baggage) {
        if (baggageRepository.existsByTagNumber(baggage.getTagNumber())) {
            throw new IllegalArgumentException("Baggage tag '" + baggage.getTagNumber() + "' is already registered in the system.");
        }

        if (baggage.getStatus() == null || baggage.getStatus().isBlank()) {
            baggage.setStatus("CHECKED_IN");
        }

        LocalDateTime now = LocalDateTime.now();
        baggage.setLastScanTime(now);

        baggageRepository.save(baggage);

        // Fetch back to obtain generated id and create initial scan entry
        Optional<Baggage> saved = baggageRepository.findByTagNumber(baggage.getTagNumber());
        if (saved.isPresent()) {
            Baggage b = saved.get();
            BaggageScan initialScan = new BaggageScan();
            initialScan.setBaggageId(b.getId());
            initialScan.setBaggageTag(b.getTagNumber());
            initialScan.setLocation("Origin Check-in: " + b.getOrigin());
            initialScan.setScanTime(now);
            initialScan.setStatus(b.getStatus());
            initialScan.setNotes("Initial baggage registration and tag assignment for flight " + b.getFlightNumber());
            initialScan.setScannedBy("Counter Agent");
            baggageScanRepository.save(initialScan);
        }
    }

    public boolean tagExists(String tagNumber) {
        return baggageRepository.existsByTagNumber(tagNumber);
    }

    public Map<String, Integer> getBaggageStatusCounts() {
        baggageRepository.markOverdueBaggageAsMisplaced();
        return baggageRepository.countByStatus();
    }

    public int getTotalBaggageCount() {
        return baggageRepository.countTotal();
    }
}
