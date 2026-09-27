package com.example.usecaseprj.repository;

import com.example.usecaseprj.model.Airline;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class AirlineRepository {

    private final JdbcTemplate jdbcTemplate;

    public AirlineRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Airline> airlineRowMapper = new RowMapper<>() {
        @Override
        public Airline mapRow(ResultSet rs, int rowNum) throws SQLException {
            Airline airline = new Airline();
            airline.setId(rs.getLong("id"));
            airline.setCode(rs.getString("code"));
            airline.setName(rs.getString("name"));
            airline.setContactEmail(rs.getString("contact_email"));
            airline.setContactPhone(rs.getString("contact_phone"));
            airline.setHubTerminal(rs.getString("hub_terminal"));
            Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                airline.setCreatedAt(ts.toLocalDateTime());
            }
            return airline;
        }
    };

    public List<Airline> findAll() {
        String sql = "SELECT id, code, name, contact_email, contact_phone, hub_terminal, created_at " +
                     "FROM airlines ORDER BY name ASC";
        return jdbcTemplate.query(sql, airlineRowMapper);
    }

    public Optional<Airline> findById(Long id) {
        String sql = "SELECT id, code, name, contact_email, contact_phone, hub_terminal, created_at " +
                     "FROM airlines WHERE id = ?";
        try {
            Airline airline = jdbcTemplate.queryForObject(sql, airlineRowMapper, id);
            return Optional.ofNullable(airline);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Airline> findByCode(String code) {
        String sql = "SELECT id, code, name, contact_email, contact_phone, hub_terminal, created_at " +
                     "FROM airlines WHERE UPPER(code) = UPPER(?)";
        try {
            Airline airline = jdbcTemplate.queryForObject(sql, airlineRowMapper, code);
            return Optional.ofNullable(airline);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int save(Airline airline) {
        String sql = "INSERT INTO airlines (code, name, contact_email, contact_phone, hub_terminal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                airline.getCode(),
                airline.getName(),
                airline.getContactEmail(),
                airline.getContactPhone(),
                airline.getHubTerminal());
    }

    public int update(Airline airline) {
        String sql = "UPDATE airlines SET code = ?, name = ?, contact_email = ?, " +
                     "contact_phone = ?, hub_terminal = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                airline.getCode(),
                airline.getName(),
                airline.getContactEmail(),
                airline.getContactPhone(),
                airline.getHubTerminal(),
                airline.getId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM airlines WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM airlines WHERE UPPER(code) = UPPER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code);
        return count != null && count > 0;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM airlines";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
