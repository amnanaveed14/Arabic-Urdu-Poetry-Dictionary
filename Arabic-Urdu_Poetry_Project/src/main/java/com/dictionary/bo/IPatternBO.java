package com.dictionary.bo;

import java.sql.SQLException;
import java.util.List;
import com.dictionary.dto.PatternDTO;

/**
 * Business Object Interface for Pattern Logic.
 */
public interface IPatternBO {
    
    /**
     * Ensures the database table exists on startup.
     */
    void ensurePatternTable() throws SQLException;

    /**
     * Creates a new pattern.
     * @param name Name of the pattern (e.g., "Fa'al").
     * @param template The template string (e.g., "F-A-L").
     * @param description Usage description.
     * @return The ID of the created pattern.
     */
    int createPattern(String name, String template, String description) throws SQLException;
    
    PatternDTO getPatternById(int id) throws SQLException;
    
    List<PatternDTO> listAllPatterns() throws SQLException;
    
    boolean updatePattern(int id, String name, String template, String description) throws SQLException;
    
    boolean deletePattern(int id) throws SQLException;
    
    /**
     * Logic-only method: Merges Root letters into a Pattern Template.
     * Does NOT save to database.
     * @param rootLetters The root (e.g., "k-t-b").
     * @param template The template (e.g., "Fa3iL").
     * @return The generated word (e.g., "katib").
     */
    String generateWord(String rootLetters, String template);
}