package com.example.usecaseprj.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Model representing a Baggage item in the Port & Airport tracking system.
 */
public class Baggage {

    private Long id;

    @NotBlank(message = "Baggage tag number is required")
    @Size(min = 3, max = 50, message = "Tag number must be between 3 and 50 characters")
    private String tagNumber;

    @NotBlank(message = "Passenger name is required")
    @Size(max = 100, message = "Passenger name cannot exceed 100 characters")
    private String passengerName;

    private Long airlineId;
    private String airlineName;
    private String airlineCode;

    @NotBlank(message = "Flight/Voyage number is required")
    @Size(max = 20, message = "Flight number cannot exceed 20 characters")
    private String flightNumber;

    @NotBlank(message = "Origin is required")
    @Size(max = 50, message = "Origin cannot exceed 50 characters")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(max = 50, message = "Destination cannot exceed 50 characters")
    private String destination;

    private String status = "CHECKED_IN";
    private LocalDateTime lastScanTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Baggage() {
    }

    public Baggage(Long id, String tagNumber, String passengerName, Long airlineId, String flightNumber,
                   String origin, String destination, String status, LocalDateTime lastScanTime,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tagNumber = tagNumber;
        this.passengerName = passengerName;
        this.airlineId = airlineId;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.status = status;
        this.lastScanTime = lastScanTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber != null ? tagNumber.trim().toUpperCase() : null;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Long getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(Long airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber != null ? flightNumber.trim().toUpperCase() : null;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin != null ? origin.trim().toUpperCase() : null;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination != null ? destination.trim().toUpperCase() : null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastScanTime() {
        return lastScanTime;
    }

    public void setLastScanTime(LocalDateTime lastScanTime) {
        this.lastScanTime = lastScanTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Baggage{" +
                "id=" + id +
                ", tagNumber='" + tagNumber + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
