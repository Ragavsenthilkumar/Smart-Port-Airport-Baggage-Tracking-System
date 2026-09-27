package com.example.usecaseprj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Smart Port & Airport Baggage Tracking System
 * Main Spring Boot Application Entry Point.
 */
@SpringBootApplication
@EnableScheduling
public class UsecaseprjApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsecaseprjApplication.class, args);
	}

}
