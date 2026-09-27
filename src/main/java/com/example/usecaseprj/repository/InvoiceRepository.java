package com.example.usecaseprj.repository;

import com.example.usecaseprj.model.Invoice;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class InvoiceRepository {

    private final JdbcTemplate jdbcTemplate;

    public InvoiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Invoice> invoiceRowMapper = new RowMapper<>() {
        @Override
        public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
            Invoice inv = new Invoice();
            inv.setId(rs.getLong("id"));
            inv.setInvoiceNumber(rs.getString("invoice_number"));
            inv.setAirlineId(rs.getLong("airline_id"));
            inv.setAirlineName(rs.getString("airline_name"));
            inv.setAirlineCode(rs.getString("airline_code"));
            inv.setAmount(rs.getBigDecimal("amount"));

            Date invDate = rs.getDate("invoice_date");
            if (invDate != null) {
                inv.setInvoiceDate(invDate.toLocalDate());
            }

            Date dueDate = rs.getDate("due_date");
            if (dueDate != null) {
                inv.setDueDate(dueDate.toLocalDate());
            }

            inv.setPaymentStatus(rs.getString("payment_status"));
            inv.setDescription(rs.getString("description"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                inv.setCreatedAt(createdAt.toLocalDateTime());
            }

            return inv;
        }
    };

    private static final String SELECT_BASE =
            "SELECT i.id, i.invoice_number, i.airline_id, " +
            "a.name AS airline_name, a.code AS airline_code, " +
            "i.amount, i.invoice_date, i.due_date, i.payment_status, " +
            "i.description, i.created_at " +
            "FROM invoices i " +
            "JOIN airlines a ON i.airline_id = a.id ";

    public List<Invoice> findAll() {
        String sql = SELECT_BASE + "ORDER BY i.invoice_date DESC, i.id DESC";
        return jdbcTemplate.query(sql, invoiceRowMapper);
    }

    public Optional<Invoice> findById(Long id) {
        String sql = SELECT_BASE + "WHERE i.id = ?";
        try {
            Invoice inv = jdbcTemplate.queryForObject(sql, invoiceRowMapper, id);
            return Optional.ofNullable(inv);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Invoice> findByAirlineId(Long airlineId) {
        String sql = SELECT_BASE + "WHERE i.airline_id = ? ORDER BY i.invoice_date DESC";
        return jdbcTemplate.query(sql, invoiceRowMapper, airlineId);
    }

    public int save(Invoice inv) {
        String sql = "INSERT INTO invoices (invoice_number, airline_id, amount, invoice_date, due_date, payment_status, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                inv.getInvoiceNumber(),
                inv.getAirlineId(),
                inv.getAmount(),
                inv.getInvoiceDate() != null ? Date.valueOf(inv.getInvoiceDate()) : Date.valueOf(java.time.LocalDate.now()),
                inv.getDueDate() != null ? Date.valueOf(inv.getDueDate()) : null,
                inv.getPaymentStatus() != null ? inv.getPaymentStatus() : "PENDING",
                inv.getDescription());
    }

    public int updatePaymentStatus(Long id, String status) {
        String sql = "UPDATE invoices SET payment_status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public boolean existsByInvoiceNumber(String invoiceNumber) {
        String sql = "SELECT COUNT(*) FROM invoices WHERE UPPER(invoice_number) = UPPER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, invoiceNumber.trim());
        return count != null && count > 0;
    }

    public BigDecimal getTotalInvoicedAmount() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM invoices";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    public BigDecimal getTotalPaidAmount() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM invoices WHERE UPPER(payment_status) = 'PAID'";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    public BigDecimal getTotalPendingAmount() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM invoices WHERE UPPER(payment_status) IN ('PENDING', 'OVERDUE')";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM invoices";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
