package com.example.usecaseprj.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Model representing an Airline or Marine Cargo Operator.
 */
public class Airline {

    private Long id;

    @NotBlank(message = "Airline code is required")
    @Size(min = 2, max = 10, message = "Code must be between 2 and 10 characters")
    private String code;

    @NotBlank(message = "Airline name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    private String contactEmail;
    private String contactPhone;
    private String hubTerminal;
    private LocalDateTime createdAt;

    public Airline() {
    }

    public Airline(Long id, String code, String name, String contactEmail, String contactPhone, String hubTerminal, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.hubTerminal = hubTerminal;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code.trim().toUpperCase() : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getHubTerminal() {
        return hubTerminal;
    }

    public void setHubTerminal(String hubTerminal) {
        this.hubTerminal = hubTerminal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Airline{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", hubTerminal='" + hubTerminal + '\'' +
                '}';
    }
}
