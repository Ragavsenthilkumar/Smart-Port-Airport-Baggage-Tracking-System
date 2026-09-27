-- ===================================================================
-- Smart Port & Airport Baggage Tracking System
-- Database Schema Definition (MySQL)
-- ===================================================================

-- 1. Airlines table
CREATE TABLE IF NOT EXISTS airlines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(30),
    hub_terminal VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Baggage table (with unique constraint on tag_number and FK to airlines)
CREATE TABLE IF NOT EXISTS baggage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_number VARCHAR(50) NOT NULL UNIQUE,
    passenger_name VARCHAR(100) NOT NULL,
    airline_id BIGINT NULL,
    flight_number VARCHAR(20) NOT NULL,
    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CHECKED_IN',
    last_scan_time DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_baggage_airline FOREIGN KEY (airline_id) REFERENCES airlines(id) ON DELETE SET NULL
);

-- 3. Baggage Scans table (recording scan checkpoints and timeline)
CREATE TABLE IF NOT EXISTS baggage_scans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    baggage_id BIGINT NOT NULL,
    baggage_tag VARCHAR(50) NOT NULL,
    location VARCHAR(100) NOT NULL,
    scan_time DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL,
    notes VARCHAR(255) NULL,
    scanned_by VARCHAR(50) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scan_baggage FOREIGN KEY (baggage_id) REFERENCES baggage(id) ON DELETE CASCADE
);

-- 4. Invoices table (airline terminal handling fees and invoices)
CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    airline_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    description VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_airline FOREIGN KEY (airline_id) REFERENCES airlines(id) ON DELETE CASCADE
);
