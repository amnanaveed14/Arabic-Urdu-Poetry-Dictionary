package com.dictionary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dictionary.dto.PatternDTO;
import com.dictionary.sql.DBConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class handles all Database operations for pattern
 * It connects to data base through {@link DBConnection}
 * It has methods to create, update delete, etc....
 */

public class PatternDAO implements IPatternDAO
{
    private static final Logger logger = LogManager.getLogger(PatternDAO.class);

	// Ensures Table Creation
    public void ensureTable() throws SQLException {
        logger.debug("ensureTable called");
        // FIXED: Changed table name to 'pattern' (singular) and syntax to SQLite
        final String sql = "CREATE TABLE IF NOT EXISTS pattern ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "template TEXT NOT NULL, "
                + "description TEXT"
                + ")";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
            logger.info("Pattern table ensured");
        } catch (SQLException e) {
            logger.error("Failed to ensure pattern table", e);
            throw e;
        }
    }

    // Creates a pattern
    public int addPattern(PatternDTO pattern) throws SQLException {
        logger.debug("addPattern called for name: {}", pattern.getName());
        // FIXED: Changed table name to 'pattern'
        final String sql = "INSERT INTO pattern (name, template, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pattern.getName());
            ps.setString(2, pattern.getTemplate());
            ps.setString(3, pattern.getDescription());
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Insert failed, no rows affected.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    pattern.setId(id);
                    logger.info("Pattern added successfully with ID: {}", id);
                    return id;
                } else {
                    throw new SQLException("Insert succeeded but no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to add pattern: {}", pattern.getName(), e);
            throw e;
        }
    }

    // Gets the pattern by ID
    public PatternDTO getPatternById(int id) throws SQLException {
        logger.debug("getPatternById called for ID: {}", id);
        // FIXED: Changed table name to 'pattern'
        final String sql = "SELECT id, name, template, description FROM pattern WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Pattern found for ID: {}", id);
                    return mapRow(rs);
                }
                logger.warn("No pattern found for ID: {}", id);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Failed to get pattern by ID: {}", id, e);
            throw e;
        }
    }

    // Gets all the patterns
    public List<PatternDTO> getAllPatterns() throws SQLException {
        logger.debug("getAllPatterns called");
        // FIXED: Changed table name to 'pattern'
        final String sql = "SELECT id, name, template, description FROM pattern ORDER BY id";
        List<PatternDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.info("Retrieved {} patterns", list.size());
        } catch (SQLException e) {
            logger.error("Failed to get all patterns", e);
            throw e;
        }
        return list;
    }

    // Updates the pattern
    public boolean updatePattern(PatternDTO pattern) throws SQLException {
        logger.debug("updatePattern called for ID: {}", pattern.getId());
        // FIXED: Changed table name to 'pattern'
        final String sql = "UPDATE pattern SET name = ?, template = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern.getName());
            ps.setString(2, pattern.getTemplate());
            ps.setString(3, pattern.getDescription());
            ps.setInt(4, pattern.getId());
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                logger.info("Pattern updated successfully for ID: {}", pattern.getId());
            } else {
                logger.warn("No pattern updated for ID: {}", pattern.getId());
            }
            return success;
        } catch (SQLException e) {
            logger.error("Failed to update pattern ID: {}", pattern.getId(), e);
            throw e;
        }
    }

    // Deletes the pattern
    public boolean deletePattern(int id) throws SQLException {
        logger.debug("deletePattern called for ID: {}", id);
        // FIXED: Changed table name to 'pattern'
        final String sql = "DELETE FROM pattern WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                logger.info("Pattern deleted successfully for ID: {}", id);
            } else {
                logger.warn("No pattern deleted for ID: {}", id);
            }
            return success;
        } catch (SQLException e) {
            logger.error("Failed to delete pattern ID: {}", id, e);
            // It is likely a foreign key constraint failure (Used by a Word)
            System.err.println("Cannot delete pattern. It might be assigned to a word.");
            return false; 
        }
    }

    private PatternDTO mapRow(ResultSet rs) throws SQLException {
        PatternDTO p = new PatternDTO();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setTemplate(rs.getString("template"));
        p.setDescription(rs.getString("description"));
        return p;
    }
}