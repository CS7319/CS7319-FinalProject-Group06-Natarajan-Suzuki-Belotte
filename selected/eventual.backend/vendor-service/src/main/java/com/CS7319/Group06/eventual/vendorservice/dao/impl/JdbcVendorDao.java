package com.CS7319.Group06.eventual.vendorservice.dao.impl;

import com.CS7319.Group06.eventual.vendorservice.dao.VendorDao;
import com.CS7319.Group06.eventual.vendorservice.exception.DaoException;
import com.CS7319.Group06.eventual.vendorservice.model.Vendor;
import com.CS7319.Group06.eventual.vendorservice.model.VendorReview;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

/**
 * JDBC implementation of VendorDao.
 */
@Component
public class JdbcVendorDao implements VendorDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcVendorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Vendor> vendorRowMapper = (rs, rowNum) -> {
        Vendor v = new Vendor();
        v.setVendorId(rs.getInt("id"));
        v.setName(rs.getString("name"));
        v.setServiceType(rs.getString("service_type"));
        v.setContactEmail(rs.getString("contact_email"));
        v.setContactPhone(rs.getString("contact_phone"));
        v.setDescription(rs.getString("description"));
        v.setWebsite(rs.getString("website"));
        v.setPreferred(rs.getBoolean("is_preferred"));
        v.setAddedBy(rs.getString("added_by"));
        v.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        v.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return v;
    };

    private final RowMapper<VendorReview> reviewRowMapper = (rs, rowNum) -> {
        VendorReview r = new VendorReview();
        r.setReviewId(rs.getInt("id"));
        r.setVendorId(rs.getInt("vendor_id"));
        r.setReviewerEmail(rs.getString("reviewer_email"));
        r.setReviewerName(rs.getString("reviewer_name"));
        r.setComment(rs.getString("comment"));
        int rating = rs.getInt("rating");
        r.setRating(rs.wasNull() ? null : rating);
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return r;
    };

    @Override
    public List<Vendor> getAllVendors() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM vendors ORDER BY is_preferred DESC, name ASC", vendorRowMapper);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<Vendor> getPreferredVendors() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM vendors WHERE is_preferred = true ORDER BY name ASC", vendorRowMapper);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public Vendor getVendorById(int id) {
        try {
            List<Vendor> results = jdbcTemplate.query(
                    "SELECT * FROM vendors WHERE id = ?", vendorRowMapper, id);
            return results.isEmpty() ? null : results.getFirst();
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public Vendor createVendor(Vendor vendor) {
        String sql = "INSERT INTO vendors (name, service_type, contact_email, contact_phone, " +
                "description, website, is_preferred, added_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, vendor.getName());
                ps.setString(2, vendor.getServiceType());
                ps.setString(3, vendor.getContactEmail());
                ps.setString(4, vendor.getContactPhone());
                ps.setString(5, vendor.getDescription());
                ps.setString(6, vendor.getWebsite());
                ps.setBoolean(7, vendor.isPreferred());
                ps.setString(8, vendor.getAddedBy());
                return ps;
            }, keyHolder);
            int id = ((Number) keyHolder.getKeys().get("id")).intValue();
            return getVendorById(id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public Vendor updateVendor(Vendor vendor) {
        String sql = "UPDATE vendors SET " +
                "name          = COALESCE(?, name), " +
                "service_type  = COALESCE(?, service_type), " +
                "contact_email = COALESCE(?, contact_email), " +
                "contact_phone = COALESCE(?, contact_phone), " +
                "description   = COALESCE(?, description), " +
                "website       = COALESCE(?, website), " +
                "is_preferred  = COALESCE(?, is_preferred), " +
                "updated_at    = CURRENT_TIMESTAMP " +
                "WHERE id = ?";
        try {
            int rows = jdbcTemplate.update(sql,
                    vendor.getName(), vendor.getServiceType(),
                    vendor.getContactEmail(), vendor.getContactPhone(),
                    vendor.getDescription(), vendor.getWebsite(),
                    vendor.isPreferred(), vendor.getVendorId());
            if (rows == 0) throw new DaoException("Zero rows affected, expected at least one");
            return getVendorById(vendor.getVendorId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public VendorReview addReview(VendorReview review) {
        String sql = "INSERT INTO vendor_reviews (vendor_id, reviewer_email, comment, rating) " +
                "VALUES (?, ?, ?, ?) RETURNING id, created_at";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, review.getVendorId());
                ps.setString(2, review.getReviewerEmail());
                ps.setString(3, review.getComment());
                if (review.getRating() != null) ps.setInt(4, review.getRating());
                else ps.setNull(4, java.sql.Types.INTEGER);
                return ps;
            }, keyHolder);
            review.setReviewId(((Number) keyHolder.getKeys().get("id")).intValue());
            Timestamp ts = (Timestamp) keyHolder.getKeys().get("created_at");
            if (ts != null) review.setCreatedAt(ts.toLocalDateTime());
            return review;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public List<VendorReview> getReviewsByVendorId(int vendorId) {
        String sql = "SELECT vr.*, u.name AS reviewer_name " +
                "FROM vendor_reviews vr JOIN users u ON vr.reviewer_email = u.email " +
                "WHERE vr.vendor_id = ? ORDER BY vr.created_at DESC";
        try {
            return jdbcTemplate.query(sql, reviewRowMapper, vendorId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<Vendor> getVendorsByEventId(int eventId) {
        String sql = "SELECT v.* FROM vendors v " +
                "JOIN event_vendors ev ON v.id = ev.vendor_id " +
                "WHERE ev.event_id = ? ORDER BY v.name ASC";
        try {
            return jdbcTemplate.query(sql, vendorRowMapper, eventId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public void addVendorToEvent(int eventId, int vendorId) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO event_vendors (event_id, vendor_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                    eventId, vendorId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation — check event and vendor IDs exist", e);
        }
    }

    @Override
    public void removeVendorFromEvent(int eventId, int vendorId) {
        try {
            jdbcTemplate.update(
                    "DELETE FROM event_vendors WHERE event_id = ? AND vendor_id = ?",
                    eventId, vendorId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }
}
