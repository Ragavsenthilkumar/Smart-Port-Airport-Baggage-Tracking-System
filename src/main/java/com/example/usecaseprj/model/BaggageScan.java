package com.example.usecaseprj.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Model representing a physical scan event at a terminal checkpoint or port berth.
 */
public class BaggageScan {

    private Long id;
    private Long baggageId;

    @NotBlank(message = "Baggage tag is required")
    private String baggageTag;

    @NotBlank(message = "Scan location is required")
    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    private LocalDateTime scanTime;

    @NotBlank(message = "Status update is required")
    private String status;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;

    @Size(max = 50, message = "Scanned by cannot exceed 50 characters")
    private String scannedBy;

    private LocalDateTime createdAt;

    public BaggageScan() {
        this.scanTime = LocalDateTime.now();
    }

    public BaggageScan(Long id, Long baggageId, String baggageTag, String location,
                       LocalDateTime scanTime, String status, String notes,
                       String scannedBy, LocalDateTime createdAt) {
        this.id = id;
        this.baggageId = baggageId;
        this.baggageTag = baggageTag;
        this.location = location;
        this.scanTime = scanTime;
        this.status = status;
        this.notes = notes;
        this.scannedBy = scannedBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaggageId() {
        return baggageId;
    }

    public void setBaggageId(Long baggageId) {
        this.baggageId = baggageId;
    }

    public String getBaggageTag() {
        return baggageTag;
    }

    public void setBaggageTag(String baggageTag) {
        this.baggageTag = baggageTag != null ? baggageTag.trim().toUpperCase() : null;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getScanTime() {
        return scanTime;
    }

    public void setScanTime(LocalDateTime scanTime) {
        this.scanTime = scanTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getScannedBy() {
        return scannedBy;
    }

    public void setScannedBy(String scannedBy) {
        this.scannedBy = scannedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BaggageScan{" +
                "id=" + id +
                ", baggageTag='" + baggageTag + '\'' +
                ", location='" + location + '\'' +
                ", scanTime=" + scanTime +
                ", status='" + status + '\'' +
                '}';
    }
}
