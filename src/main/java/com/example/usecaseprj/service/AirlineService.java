package com.example.usecaseprj.service;

import com.example.usecaseprj.model.Airline;
import com.example.usecaseprj.repository.AirlineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirlineService {

    private final AirlineRepository airlineRepository;

    public AirlineService(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }

    public List<Airline> getAllAirlines() {
        return airlineRepository.findAll();
    }

    public Airline getAirlineById(Long id) {
        return airlineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with ID: " + id));
    }

    public Optional<Airline> findByCode(String code) {
        return airlineRepository.findByCode(code);
    }

    public void saveAirline(Airline airline) {
        if (airlineRepository.existsByCode(airline.getCode())) {
            throw new IllegalArgumentException("Airline code '" + airline.getCode() + "' is already registered.");
        }
        airlineRepository.save(airline);
    }

    public void updateAirline(Airline airline) {
        airlineRepository.update(airline);
    }

    public void deleteAirline(Long id) {
        airlineRepository.deleteById(id);
    }

    public boolean codeExists(String code) {
        return airlineRepository.existsByCode(code);
    }

    public int getAirlineCount() {
        return airlineRepository.countTotal();
    }
}
