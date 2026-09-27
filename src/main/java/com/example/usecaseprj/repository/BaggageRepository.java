package com.example.usecaseprj.repository;

import com.example.usecaseprj.model.Baggage;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BaggageRepository {

    private final JdbcTemplate jdbcTemplate;

    public BaggageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Baggage> baggageRowMapper = new RowMapper<>() {
        @Override
        public Baggage mapRow(ResultSet rs, int rowNum) throws SQLException {
            Baggage b = new Baggage();
            b.setId(rs.getLong("id"));
            b.setTagNumber(rs.getString("tag_number"));
            b.setPassengerName(rs.getString("passenger_name"));

            long airlineId = rs.getLong("airline_id");
            if (!rs.wasNull()) {
                b.setAirlineId(airlineId);
            }
            b.setAirlineName(rs.getString("airline_name"));
            b.setAirlineCode(rs.getString("airline_code"));

            b.setFlightNumber(rs.getString("flight_number"));
            b.setOrigin(rs.getString("origin"));
            b.setDestination(rs.getString("destination"));
            b.setStatus(rs.getString("status"));

            Timestamp lastScan = rs.getTimestamp("last_scan_time");
            if (lastScan != null) {
                b.setLastScanTime(lastScan.toLocalDateTime());
            }

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                b.setCreatedAt(createdAt.toLocalDateTime());
            }

            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                b.setUpdatedAt(updatedAt.toLocalDateTime());
            }

            return b;
        }
    };

    private static final String SELECT_BASE =
            "SELECT b.id, b.tag_number, b.passenger_name, b.airline_id, " +
            "a.name AS airline_name, a.code AS airline_code, " +
            "b.flight_number, b.origin, b.destination, b.status, " +
            "b.last_scan_time, b.created_at, b.updated_at " +
            "FROM baggage b " +
            "LEFT JOIN airlines a ON b.airline_id = a.id ";

    public List<Baggage> findAll() {
        String sql = SELECT_BASE + "ORDER BY b.created_at DESC";
        return jdbcTemplate.query(sql, baggageRowMapper);
    }

    public Optional<Baggage> findById(Long id) {
        String sql = SELECT_BASE + "WHERE b.id = ?";
        try {
            Baggage baggage = jdbcTemplate.queryForObject(sql, baggageRowMapper, id);
            return Optional.ofNullable(baggage);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Baggage> findByTagNumber(String tagNumber) {
        String sql = SELECT_BASE + "WHERE UPPER(b.tag_number) = UPPER(?)";
        try {
            Baggage baggage = jdbcTemplate.queryForObject(sql, baggageRowMapper, tagNumber.trim());
            return Optional.ofNullable(baggage);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int save(Baggage b) {
        String sql = "INSERT INTO baggage (tag_number, passenger_name, airline_id, flight_number, " +
                     "origin, destination, status, last_scan_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                b.getTagNumber(),
                b.getPassengerName(),
                b.getAirlineId(),
                b.getFlightNumber(),
                b.getOrigin(),
                b.getDestination(),
                b.getStatus() != null ? b.getStatus() : "CHECKED_IN",
                b.getLastScanTime() != null ? Timestamp.valueOf(b.getLastScanTime()) : null);
    }

    public int updateStatusAndLastScan(Long baggageId, String status, LocalDateTime scanTime) {
        String sql = "UPDATE baggage SET status = ?, last_scan_time = ?, updated_at = NOW() WHERE id = ?";
        return jdbcTemplate.update(sql,
                status,
                scanTime != null ? Timestamp.valueOf(scanTime) : Timestamp.valueOf(LocalDateTime.now()),
                baggageId);
    }

    /**
     * Requirement: Automatically flag baggage as MISPLACED when it is in active transit
     * and has had no scan for more than four hours.
     */
    public int markOverdueBaggageAsMisplaced() {
        String sql = "UPDATE baggage SET status = 'MISPLACED', updated_at = NOW() " +
                     "WHERE status = 'IN_TRANSIT' " +
                     "AND ( " +
                     "  (last_scan_time IS NOT NULL AND TIMESTAMPDIFF(HOUR, last_scan_time, NOW()) >= 4) " +
                     "  OR " +
                     "  (last_scan_time IS NULL AND TIMESTAMPDIFF(HOUR, created_at, NOW()) >= 4) " +
                     ")";
        return jdbcTemplate.update(sql);
    }

    public boolean existsByTagNumber(String tagNumber) {
        String sql = "SELECT COUNT(*) FROM baggage WHERE UPPER(tag_number) = UPPER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tagNumber.trim());
        return count != null && count > 0;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM baggage";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public Map<String, Integer> countByStatus() {
        String sql = "SELECT status, COUNT(*) as count FROM baggage GROUP BY status";
        Map<String, Integer> statusCounts = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            statusCounts.put(rs.getString("status"), rs.getInt("count"));
        });
        return statusCounts;
    }
}
