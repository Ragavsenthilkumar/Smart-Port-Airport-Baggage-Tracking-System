-- ===================================================================
-- Sample Data for Smart Port & Airport Baggage Tracking System
-- ===================================================================

-- Sample Airlines / Marine Logistics Operators
INSERT IGNORE INTO airlines (id, code, name, contact_email, contact_phone, hub_terminal) VALUES
(1, 'EK', 'Emirates Airline', 'cargo@emirates.com', '+971-4-214-4444', 'Airport Terminal 3 Concourse A'),
(2, 'BA', 'British Airways', 'baggage@ba.com', '+44-20-8738-5050', 'Airport Terminal 5 Gate B'),
(3, 'MSK', 'Maersk Line Shipping', 'portops@maersk.com', '+45-33-633-363', 'Port Terminal Quay 4'),
(4, 'LH', 'Lufthansa Cargo', 'support@lufthansa.com', '+49-69-6960', 'Airport Terminal 1 Cargo City'),
(5, 'SQ', 'Singapore Airlines', 'groundops@singaporeair.com', '+65-6541-5555', 'Airport Terminal 2 Wing C');

-- Sample Baggage
-- Notice BAG-1002 has its last scan over 5 hours ago in 'IN_TRANSIT' status,
-- making it an ideal demonstration for the 4-hour MISPLACED detection rule!
INSERT IGNORE INTO baggage (id, tag_number, passenger_name, airline_id, flight_number, origin, destination, status, last_scan_time, created_at) VALUES
(1, 'BAG-1001', 'Alice Johnson', 1, 'EK-202', 'DXB', 'JFK', 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(2, 'BAG-1002', 'Carlos Silva', 2, 'BA-117', 'LHR', 'ORD', 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 8 HOUR)),
(3, 'BAG-1003', 'Elena Rostova', 1, 'EK-505', 'DXB', 'BOM', 'CLAIMED', DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(4, 'BAG-1004', 'David Zhang', 3, 'MSK-SEA-09', 'SIN', 'ROT', 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 10 HOUR)),
(5, 'BAG-1005', 'Sophia Patel', 4, 'LH-400', 'FRA', 'DEL', 'CHECKED_IN', DATE_SUB(NOW(), INTERVAL 15 MINUTE), DATE_SUB(NOW(), INTERVAL 15 MINUTE));

-- Sample Scan History
INSERT IGNORE INTO baggage_scans (id, baggage_id, baggage_tag, location, scan_time, status, notes, scanned_by) VALUES
(1, 1, 'BAG-1001', 'DXB Check-in Counter 14', DATE_SUB(NOW(), INTERVAL 3 HOUR), 'CHECKED_IN', 'Standard bag 22.4kg tagged', 'Agent Sarah'),
(2, 1, 'BAG-1001', 'DXB Conveyor Sorter B-4', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'LOADED', 'Routed to flight EK-202 cargo hold', 'Scanner-Bot-02'),
(3, 1, 'BAG-1001', 'DXB Ramp Apron Gate A-10', DATE_SUB(NOW(), INTERVAL 1 HOUR), 'IN_TRANSIT', 'Loaded into aircraft belly', 'Ramp Handler Mark'),

(4, 2, 'BAG-1002', 'LHR Terminal 5 Bag Drop', DATE_SUB(NOW(), INTERVAL 8 HOUR), 'CHECKED_IN', 'Checked luggage 18.0kg', 'Agent Emma'),
(5, 2, 'BAG-1002', 'LHR Transfer Pier B', DATE_SUB(NOW(), INTERVAL 5 HOUR), 'IN_TRANSIT', 'Transferred to transit cart (overdue since)', 'Ramp Agent John'),

(6, 3, 'BAG-1003', 'DXB Terminal 3 Gate 22', DATE_SUB(NOW(), INTERVAL 6 HOUR), 'CHECKED_IN', 'Priority business class bag', 'Agent Liam'),
(7, 3, 'BAG-1003', 'BOM Carousel Belt 4', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'CLAIMED', 'Passenger claimed at arrival carousel', 'Arrivals Staff Mia'),

(8, 4, 'BAG-1004', 'Port Singapore Berth 12', DATE_SUB(NOW(), INTERVAL 10 HOUR), 'CHECKED_IN', 'Sea cargo container transfer', 'Port Officer Tan'),
(9, 4, 'BAG-1004', 'Vessel Maersk Mc-Kinney', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'IN_TRANSIT', 'Secured on vessel hold deck A', 'Crane Operator Lee');

-- Sample Handling Invoices
INSERT IGNORE INTO invoices (id, invoice_number, airline_id, amount, invoice_date, due_date, payment_status, description) VALUES
(1, 'INV-2026-001', 1, 12500.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'PAID', 'Terminal 3 baggage sorting & ramp handling fee'),
(2, 'INV-2026-002', 2, 8750.50, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 25 DAY), 'PENDING', 'Ground handling & conveyor maintenance charge'),
(3, 'INV-2026-003', 3, 19400.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'OVERDUE', 'Port berth cargo transfer & container staging operations'),
(4, 'INV-2026-004', 4, 6200.00, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 28 DAY), 'PENDING', 'Cargo City customs inspection bay handling');
