package com.example.usecaseprj.repository;

import com.example.usecaseprj.model.BaggageScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class BaggageScanRepository {

    private final JdbcTemplate jdbcTemplate;

    public BaggageScanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<BaggageScan> scanRowMapper = new RowMapper<>() {
        @Override
        public BaggageScan mapRow(ResultSet rs, int rowNum) throws SQLException {
            BaggageScan scan = new BaggageScan();
            scan.setId(rs.getLong("id"));
            scan.setBaggageId(rs.getLong("baggage_id"));
            scan.setBaggageTag(rs.getString("baggage_tag"));
            scan.setLocation(rs.getString("location"));

            Timestamp scanTime = rs.getTimestamp("scan_time");
            if (scanTime != null) {
                scan.setScanTime(scanTime.toLocalDateTime());
            }

            scan.setStatus(rs.getString("status"));
            scan.setNotes(rs.getString("notes"));
            scan.setScannedBy(rs.getString("scanned_by"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                scan.setCreatedAt(createdAt.toLocalDateTime());
            }

            return scan;
        }
    };

    public List<BaggageScan> findByBaggageId(Long baggageId) {
        String sql = "SELECT id, baggage_id, baggage_tag, location, scan_time, status, notes, scanned_by, created_at " +
                     "FROM baggage_scans WHERE baggage_id = ? ORDER BY scan_time DESC";
        return jdbcTemplate.query(sql, scanRowMapper, baggageId);
    }

    public List<BaggageScan> findByBaggageTag(String baggageTag) {
        String sql = "SELECT id, baggage_id, baggage_tag, location, scan_time, status, notes, scanned_by, created_at " +
                     "FROM baggage_scans WHERE UPPER(baggage_tag) = UPPER(?) ORDER BY scan_time DESC";
        return jdbcTemplate.query(sql, scanRowMapper, baggageTag.trim());
    }

    public List<BaggageScan> findRecentScans(int limit) {
        String sql = "SELECT id, baggage_id, baggage_tag, location, scan_time, status, notes, scanned_by, created_at " +
                     "FROM baggage_scans ORDER BY scan_time DESC LIMIT ?";
        return jdbcTemplate.query(sql, scanRowMapper, limit);
    }

    public int save(BaggageScan scan) {
        String sql = "INSERT INTO baggage_scans (baggage_id, baggage_tag, location, scan_time, status, notes, scanned_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                scan.getBaggageId(),
                scan.getBaggageTag(),
                scan.getLocation(),
                scan.getScanTime() != null ? Timestamp.valueOf(scan.getScanTime()) : Timestamp.valueOf(java.time.LocalDateTime.now()),
                scan.getStatus(),
                scan.getNotes(),
                scan.getScannedBy());
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM baggage_scans";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
