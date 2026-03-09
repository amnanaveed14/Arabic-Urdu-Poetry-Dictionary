package com.dictionary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

import com.dictionary.dto.WordDTO;
import com.dictionary.sql.DBConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class handles all Database operations for Word
 * It connects to data base through {@link DBConnection}
 * It has methods to create, update delete, etc....
 */

public class WordDAO implements IWordDAO {

    private static final Logger logger = LogManager.getLogger(WordDAO.class);

    @Override
    public boolean addWord(WordDTO word) {
        logger.debug("addWord called for: {}", word.getArabicWord());
        
        // Note: We do NOT insert 'word_id' here because it is Auto-Incremented by the database
        String sql = "INSERT INTO word (arabic_word, urdu_meaning, root_id, pattern_id, lemma) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, word.getArabicWord());
            stmt.setString(2, word.getUrduMeaning());

            if (word.getRootID() <= 0) stmt.setNull(3, java.sql.Types.INTEGER);
            else stmt.setInt(3, word.getRootID());
            
            if (word.getPatternID() <= 0) stmt.setNull(4, java.sql.Types.INTEGER);
            else stmt.setInt(4, word.getPatternID());

            // Set the Lemma
            if (word.getLemma() == null) stmt.setString(5, "");
            else stmt.setString(5, word.getLemma());

            int rowsInserted = stmt.executeUpdate();
            boolean success = rowsInserted > 0;
            if (success) {
                logger.info("Word added successfully: {}", word.getArabicWord());
            } else {
                logger.warn("Failed to add word: {}", word.getArabicWord());
            }
            return success;

        } catch (SQLException e) {
            logger.error("Failed to add word: {}", word.getArabicWord(), e);
            e.printStackTrace();
        }
        return false;
    }

    // Gets all the words from Database
    @Override
    public List<WordDTO> getAllWords() {
        logger.debug("getAllWords called");
        List<WordDTO> wordList = new ArrayList<>();
        String sql = "SELECT * FROM word";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                wordList.add(mapRow(rs)); // Used helper method
            }
            logger.info("Retrieved {} words", wordList.size());

        } catch (SQLException e) {
            logger.error("Failed to get all words", e);
            System.out.println("Fetch All Words Error:");
            e.printStackTrace();
        }
        return wordList;
    }

    // Searches word 
    @Override
    public WordDTO searchWord(String arabicWord) {
        logger.debug("searchWord called for: {}", arabicWord);
       String sql = "SELECT * FROM word WHERE " +
                     "TRIM(arabic_word) = ? " + 
                     "OR " +
                     "REPLACE(REPLACE(REPLACE(TRIM(arabic_word), 'أ', 'ا'), 'إ', 'ا'), 'آ', 'ا') = ?";
        
        WordDTO word = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, arabicWord);
            stmt.setString(2, arabicWord);
            
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                word = mapRow(rs); 
                logger.debug("Word found: {}", arabicWord);
            } else {
                logger.warn("No word found for: {}", arabicWord);
            }

        } catch (SQLException e) {
            logger.error("Failed to search word: {}", arabicWord, e);
            System.out.println(" Search Word Error:");
            e.printStackTrace();
        }
        return word;
    }

    // Updates a word
    @Override
    public boolean updateWord(int id, String newMeaning) {
        logger.debug("updateWord called for ID: {}", id);
        
        // FIX: Changed 'id' to 'word_id' to match database column
        String sql = "UPDATE word SET urdu_meaning = ? WHERE word_id = ?"; 
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newMeaning);
            stmt.setInt(2, id);

            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                logger.info("Word updated successfully for ID: {}", id);
            } else {
                logger.warn("No word updated for ID: {}", id);
            }
            return success;
        } catch (Exception e) {
            logger.error("Failed to update word ID: {}", id, e);
            System.out.println(" Update Word Error:");
            e.printStackTrace();
        }
        return false;
    }

    // Deletes a Word
    @Override
    public boolean deleteWord(int id) {
        logger.debug("deleteWord called for ID: {}", id);
        
        // FIX: Changed 'id' to 'word_id' to match database column
        String sql = "DELETE FROM word WHERE word_id = ?"; 
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                logger.info("Word deleted successfully for ID: {}", id);
            } else {
                logger.warn("No word deleted for ID: {}", id);
            }
            return success;
        } catch (Exception e) {
            logger.error("Failed to delete word ID: {}", id, e);
            System.out.println(" Delete Word Error:");
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------
    // --- NEW BROWSING METHODS (Requirement for Phase 4) ---
    // -------------------------------------------------------

    @Override
    public List<WordDTO> getWordsByRootID(int rootID) {
        logger.debug("getWordsByRootID called for: {}", rootID);
        List<WordDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM word WHERE root_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rootID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                logger.info("Retrieved {} words for root ID: {}", list.size(), rootID);
            }
        } catch (SQLException e) {
            logger.error("Failed to get words by root ID: {}", rootID, e);
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<WordDTO> getWordsByPatternID(int patternID) {
        logger.debug("getWordsByPatternID called for: {}", patternID);
        List<WordDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM word WHERE pattern_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patternID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                logger.info("Retrieved {} words for pattern ID: {}", list.size(), patternID);
            }
        } catch (SQLException e) {
            logger.error("Failed to get words by pattern ID: {}", patternID, e);
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<WordDTO> getWordsByLemma(String lemma) {
        logger.debug("getWordsByLemma called for: {}", lemma);
        List<WordDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM word WHERE lemma = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lemma);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                logger.info("Retrieved {} words for lemma: {}", list.size(), lemma);
            }
        } catch (SQLException e) {
            logger.error("Failed to get words by lemma: {}", lemma, e);
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<String> getAllDistinctLemmas() {
        logger.debug("getAllDistinctLemmas called");
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT lemma FROM word WHERE lemma IS NOT NULL AND lemma != '' ORDER BY lemma";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("lemma"));
            }
            logger.info("Retrieved {} distinct lemmas", list.size());
        } catch (SQLException e) {
            logger.error("Failed to get all distinct lemmas", e);
            e.printStackTrace();
        }
        return list;
    }

    // --- HELPER METHOD TO MAP DB ROW TO OBJECT ---
    private WordDTO mapRow(ResultSet rs) throws SQLException {
        WordDTO word = new WordDTO();
        
        // FIX: Changed "id" to "word_id" to match the actual database column name
        // IMPORTANT: Ensure your WordDTO has setWordID, or change this to setId
        word.setWordID(rs.getInt("word_id")); 
        
        word.setArabicWord(rs.getString("arabic_word"));
        word.setUrduMeaning(rs.getString("urdu_meaning"));
        word.setRootID(rs.getInt("root_id"));
        word.setPatternID(rs.getInt("pattern_id"));
        
        // Safe check for lemma
        String l = rs.getString("lemma");
        if(l == null) l = "";
        word.setLemma(l);
        
        return word;
    }
    
    @Override
    public List<WordDTO> searchBySubstring(String substring) {
        logger.debug("searchBySubstring called for: {}", substring);
        List<WordDTO> wordList = new ArrayList<>();
        // SQL LIKE with wildcards for substring matching
        String sql = "SELECT * FROM word WHERE arabic_word LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + substring + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                wordList = mapResultSetToWordList(rs);
                logger.info("Retrieved {} words by substring: {}", wordList.size(), substring);
            }
        } catch (SQLException e) {
            logger.error("Failed to search by substring: {}", substring, e);
            System.out.println("Search By Substring Error:");
            e.printStackTrace();
        }
        return wordList;
    }

    @Override
    public List<WordDTO> searchByRegex(String regexPattern) {
        logger.debug("searchByRegex called for: {}", regexPattern);
        List<WordDTO> results = new ArrayList<>();
        
        // 1. Get ALL words (SQLite is fast enough for < 100k words)
        List<WordDTO> allWords = getAllWords(); 
        
        try {
            // 2. Compile the pattern once
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(regexPattern);
            
            // 3. Filter in Java
            for (WordDTO word : allWords) {
                if (word.getArabicWord() != null) {
                    // Check if the regex matches the arabic word
                    if (p.matcher(word.getArabicWord()).find()) {
                        results.add(word);
                    }
                }
            }
            logger.info("Found {} matches for regex", results.size());
        } catch (Exception e) {
            logger.error("Invalid Regex Pattern: {}", regexPattern);
            System.err.println("Invalid Regex Pattern");
        }
        return results;
    }

    @Override
    public List<WordDTO> searchByMeaning(String meaningQuery) {
        logger.debug("searchByMeaning called for: {}", meaningQuery);
        List<WordDTO> wordList = new ArrayList<>();
        // SQL LIKE with wildcards for substring matching in urdu_meaning
        String sql = "SELECT * FROM word WHERE urdu_meaning LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + meaningQuery + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                wordList = mapResultSetToWordList(rs);
                logger.info("Retrieved {} words by meaning: {}", wordList.size(), meaningQuery);
            }
        } catch (SQLException e) {
            logger.error("Failed to search by meaning: {}", meaningQuery, e);
            System.out.println("Reverse Search By Meaning Error:");
            e.printStackTrace();
        }
        return wordList;
    }

    private List<WordDTO> mapResultSetToWordList(ResultSet rs) throws SQLException {
        List<WordDTO> wordList = new ArrayList<>();
        while (rs.next()) {
           wordList.add(mapRow(rs));
        }
        return wordList;
    }
}