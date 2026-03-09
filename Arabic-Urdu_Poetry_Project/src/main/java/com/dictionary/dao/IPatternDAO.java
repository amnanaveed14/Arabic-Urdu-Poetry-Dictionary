package com.dictionary.dao;

import java.sql.SQLException;
import java.util.List;
import com.dictionary.dto.PatternDTO;

/**
 * This interface is for Data Access operation for pattern
 * It has functions for creating, adding, deleting etc....
 */
public interface IPatternDAO {

    /**
     * Ensure if the pattern table exists
     * @throws SQLException if it doesn't 
     */
    void ensureTable() throws SQLException;

    /**
     * Adds a pattern to the database 
     * @param pattern
     * @return the ID of the pattern just added
     * @throws SQLException database error occurs
     */
    int addPattern(PatternDTO pattern) throws SQLException;

    /**
     * Gets the pattern through its ID from Database
     */
    PatternDTO getPatternById(int id) throws SQLException;

    /**
     * Gets all the patterns from the database
     */
    List<PatternDTO> getAllPatterns() throws SQLException;

    /**
     * Updates the pattern which already exists
     */
    boolean updatePattern(PatternDTO pattern) throws SQLException;

    /**
     * Deletes the pattern according to its ID
     */
    boolean deletePattern(int id) throws SQLException;
}