package com.dictionary.dao;

import java.util.List;
import com.dictionary.dto.WordDTO;

/**
 * This interface is for Data Access operation for word
 * It has functions for creating, adding, deleting etc....
 */
public interface IWordDAO {
	
	/**
	 * This creates word in database
	 * @param word
	 */
    boolean addWord(WordDTO word);
    
    /**
	 * Gets all the words from database into a list
	 * @return those words
	 */
    List<WordDTO> getAllWords();
    
    /**
     * Searches the Arabic word
     * @param arabicWord
     * @return the Arabic word
     */
    WordDTO searchWord(String arabicWord);
    
    /**
	 * Updates the specific word in the database
	 * @param word
	 */
    boolean updateWord(int id, String newMeaning);
    
    /**
	 * Deletes the specific word form the database
	 * @param id
	 */
    boolean deleteWord(int id);
    
    // --- NEW METHODS (Phase 4) ---

    /**
     * Retrieves all words associated with a specific Root ID.
     * @param rootID The unique ID of the root.
     * @return List of words derived from this root.
     */
    List<WordDTO> getWordsByRootID(int rootID);

    /**
     * Retrieves all words associated with a specific Pattern ID.
     * @param patternID The unique ID of the pattern.
     * @return List of words following this pattern.
     */
    List<WordDTO> getWordsByPatternID(int patternID);

    /**
     * Retrieves all words matching a specific lemma (dictionary base form).
     * @param lemma The lemma string.
     * @return List of words sharing this lemma.
     */
    List<WordDTO> getWordsByLemma(String lemma);

    /**
     * Retrieves a list of all unique lemmas currently stored in the database.
     * Useful for dropdowns or browsing lists.
     * @return List of distinct lemma strings.
     */
    List<String> getAllDistinctLemmas();

    /**
     * Searches for words where the Urdu meaning contains the query string.
     * @param meaningQuery The substring to search for in the meaning.
     * @return List of matching words.
     */
	List<WordDTO> searchByMeaning(String meaningQuery);

    /**
     * Searches for words matching a specific Regular Expression.
     * @param regexPattern The regex pattern (e.g., "^[أا].*").
     * @return List of matching words.
     */
	List<WordDTO> searchByRegex(String regexPattern);

    /**
     * Searches for words containing a specific substring in the Arabic text.
     * @param substring The substring to find.
     * @return List of matching words.
     */
	List<WordDTO> searchBySubstring(String substring);
}