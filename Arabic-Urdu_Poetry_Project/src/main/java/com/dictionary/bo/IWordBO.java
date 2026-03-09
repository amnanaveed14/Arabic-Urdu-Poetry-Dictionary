package com.dictionary.bo;

import java.util.List;
import com.dictionary.dto.WordDTO;

/**
 * Business Object Interface for Word Logic.
 * Handles validation, business rules, and communication with the DAL.
 */
public interface IWordBO {

    /**
     * Adds a new word to the dictionary after validation.
     * @param arabicWord The word in Arabic.
     * @param urduMeaning The meaning in Urdu.
     * @param rootID The ID of the associated root.
     * @param patternID The ID of the associated pattern.
     * @return A status message ("Success" or error description).
     */
    String addWord(String arabicWord, String urduMeaning, int rootID, int patternID);

    /**
     * Logic-heavy insertion for the Poem Parser.
     * 1. Analyses the Arabic word to find a Root and Pattern automatically.
     * 2. If found, links them.
     * 3. Saves the word.
     * @param arabicWord The word to analyze and add.
     * @param urduMeaning The meaning.
     * @return Status message.
     */
    String addWordWithSuggestedRoot(String arabicWord, String urduMeaning);
    
    /**
     * Gets all words.
     * @return List of WordDTOs.
     */
    List<WordDTO> getAllWords();

    /**
     * Basic search by exact Arabic word.
     * @param arabicWord The word to search.
     * @return The WordDTO if found, else null.
     */
    WordDTO searchWord(String arabicWord);

    /**
     * Updates the meaning of an existing word.
     * @param id The word ID.
     * @param newMeaning The new Urdu meaning.
     * @return Status message.
     */
    String updateWord(int id, String newMeaning);

    /**
     * Deletes a word by ID.
     * @param id The word ID.
     * @return Status message.
     */
    String deleteWord(int id);

    // --- NLP / Analysis Methods ---

    /**
     * Returns the segmentation of the word (e.g., prefix-stem-suffix).
     * @param arabicWord The word to analyze.
     * @return Formatted segmentation string.
     */
    String getSegmentation(String arabicWord);

    /**
     * Returns the dictionary base form (lemma) of the word.
     * @param arabicWord The word to analyze.
     * @return The lemma.
     */
    String getLemma(String arabicWord);
    
    /**
     * Normalizes text (removes diacritics, unifies Alephs).
     * @param text Input text.
     * @return Normalized text.
     */
    String normalizeText(String text);

    // --- Browsing & Advanced Search ---
    
    List<WordDTO> getWordsByRootID(int rootID);
    List<WordDTO> getWordsByPatternID(int patternID);
    List<WordDTO> getWordsByLemma(String lemma);
    List<String> getAllDistinctLemmas();
    
    List<WordDTO> searchBySubstring(String query);
    List<WordDTO> searchByRegex(String pattern);
    List<WordDTO> searchByMeaning(String meaning);
}