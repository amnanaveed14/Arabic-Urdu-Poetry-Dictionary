package com.dictionary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dictionary.dto.RootDTO;
import com.dictionary.sql.DBConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SqlRootDAO implements IRootDAO {
    private static final Logger logger = LogManager.getLogger(SqlRootDAO.class);
    // Table name matches the one created in DALFacade
    private String TABLE_NAME = "root";

    @Override
    public RootDTO createRoot(RootDTO root) {
        logger.debug("createRoot called for letters: {}", root.getRootLetters());
        // FIXED: Using lowercase column names
        String query = "INSERT INTO " + TABLE_NAME + " (root_letters) VALUES (?)";
        
        try (Connection connection = DBConnection.getConnection()) {
            // We request generated keys here
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, root.getRootLetters());
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating root failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    root.setId(generatedKeys.getInt(1)); // Set the ID in the object
                    logger.info("Root created successfully with ID: {}", root.getId());
                } else {
                    throw new SQLException("Creating root failed, no ID obtained.");
                }
            }

            return root;
        } catch (SQLException e) {
            logger.error("Failed to create root: {}", root.getRootLetters(), e);
            throw new RuntimeException("Database error creating root.", e);
        }
    }

    
    @Override
    public RootDTO getRootById(int id) {
        logger.debug("getRootById called for ID: {}", id);
        // FIXED: Changed 'ID' to 'id' to match database schema
        String sql = "SELECT id, root_letters FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id); // Changed setLong to setInt
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Root found for ID: {}", id);
                    return new RootDTO(rs.getInt("id"), rs.getString("root_letters"));
                }
                logger.warn("No root found for ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Failed to get root by ID: {}", id, e);
            throw new RuntimeException("Database error reading root by ID.", e);
        }
        return null;
    }

    @Override
    public RootDTO getRootByLetters(String rootLetters) {
        logger.debug("getRootByLetters called for: {}", rootLetters);
        // FIXED: Changed 'ID' to 'id'
        String sql = "SELECT id, root_letters FROM " + TABLE_NAME + " WHERE root_letters = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rootLetters);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Root found for letters: {}", rootLetters);
                    return new RootDTO(rs.getInt("id"), rs.getString("root_letters"));
                }
                logger.warn("No root found for letters: {}", rootLetters);
            }
        } catch (SQLException e) {
            logger.error("Failed to get root by letters: {}", rootLetters, e);
            throw new RuntimeException("Database error reading root by letters: " + e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public List<RootDTO> getAllRoots() {
        logger.debug("getAllRoots called");
        List<RootDTO> roots = new ArrayList<>();
        // FIXED: Changed 'ID' to 'id'
        String sql = "SELECT id, root_letters FROM " + TABLE_NAME + " ORDER BY root_letters ASC";
        try (Connection connection = DBConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roots.add(new RootDTO(rs.getInt("id"), rs.getString("root_letters")));
            }
            logger.info("Retrieved {} roots", roots.size());
        } catch (SQLException e) {
            logger.error("Failed to get all roots", e);
            throw new RuntimeException("Database error reading all roots: " + e.getMessage(), e);
        }
        return roots;
    }

    @Override
    public void updateRoot(RootDTO root) {
        logger.debug("updateRoot called for ID: {}", root.getId());
        // FIXED: Changed 'ID' to 'id'
        String sql = "UPDATE " + TABLE_NAME + " SET root_letters = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, root.getRootLetters());
            stmt.setInt(2, root.getId()); // Changed setLong to setInt

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating root failed, ID " + root.getId() + " not found.");
            }
            logger.info("Root updated successfully for ID: {}", root.getId());
        } catch (SQLException e) {
            logger.error("Failed to update root ID: {}", root.getId(), e);
            throw new RuntimeException("Database error updating root.", e);
        }
    }

    @Override
    public void deleteRoot(int id) {
        logger.debug("deleteRoot called for ID: {}", id);
        // FIXED: Changed 'ID' to 'id'
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                logger.warn("No root found for deletion ID: {}", id);
            } else {
                logger.info("Root deleted successfully for ID: {}", id);
            }
        } catch (SQLException e) {
            // IMPORTANT CHECK FOR FOREIGN KEYS
            System.err.println("Cannot delete Root. It is currently being used by a Word.");
            logger.error("Failed to delete root ID: {}", id, e);
        }
    }
}