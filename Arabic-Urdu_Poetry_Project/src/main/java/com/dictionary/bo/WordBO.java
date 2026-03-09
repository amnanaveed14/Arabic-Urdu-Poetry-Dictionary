package com.dictionary.bo;

import java.util.Collections;
import java.util.List;
import com.dictionary.dalfacade.IDALFacade;
import com.dictionary.dto.RootDTO;
import com.dictionary.dto.WordDTO;
import com.dictionary.dto.PatternDTO;
// NLP Import
import net.oujda_nlp_team.AlKhalil2Analyzer;
import net.oujda_nlp_team.entity.Result;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;   

public class WordBO implements IWordBO {

    private static final Logger logger = LogManager.getLogger(WordBO.class);  

    private IDALFacade dalFacade;

    public WordBO(IDALFacade dalFacade) {
        this.dalFacade = dalFacade;
    }

    /**
     * Called when adding a word manually.
     */
    @Override
    public String addWord(String arabicWord, String urduMeaning, int rootID, int patternID) {
        logger.debug("addWord(manual) called - word: {}, rootID: {}, patternID: {}", arabicWord, rootID, patternID);

        if (arabicWord == null || arabicWord.trim().isEmpty()) {
            return "Error! Arabic word is required.";
        }
        if (urduMeaning == null || urduMeaning.trim().isEmpty()) {
            return "Error! Urdu meaning is required.";
        }
        
        // Validation: If it's a manual add, we usually expect a Root ID.
        // However, if the user leaves it blank (0), we allow it as "Unknown Root".
        if (rootID < 0) rootID = 0;
        if (patternID < 0) patternID = 0;

        WordDTO word = new WordDTO();
        word.setArabicWord(arabicWord.trim());
        word.setUrduMeaning(urduMeaning.trim());
        word.setRootID(rootID);
        word.setPatternID(patternID);
        
        // Use the word itself as Lemma if manual
        word.setLemma(arabicWord.trim());

        boolean success = dalFacade.addWord(word);
        if (success) {
            logger.info("Manual word added successfully: {}", arabicWord.trim());
            return "Word added successfully!";
        } else {
            logger.warn("Failed to add manual word: {}", arabicWord.trim());
            return "Failed to add word to Database.";
        }
    }

    /**
     * CRITICAL FOR POEM PARSER: 
     * This attempts to find a root. If it fails, it SAVES THE WORD ANYWAY.
     */
    @Override
    public String addWordWithSuggestedRoot(String arabicWord, String urduMeaning) {
        logger.info("addWordWithSuggestedRoot called for: {}", arabicWord);

        if (arabicWord == null || arabicWord.trim().isEmpty()) return "Error! Word required.";
        
        // Default values (Unknown)
        int finalRootId = 0;
        int finalPatternId = 0;
        String detectedLemma = arabicWord;
        String detectedRootStr = null;
        String detectedPatternStr = "Default";

        // 1. Try NLP Analysis
        try {
             List<Result> results = AlKhalil2Analyzer.getInstance().analyzerToken(arabicWord.trim());
             
             if (results != null && !results.isEmpty()) {
                 for (Result r : results) {
                    String root = r.getRoot();
                    if (root != null && !root.equals("-") && !root.equals("#")) {
                        detectedRootStr = root;
                        detectedLemma = r.getLemma();
                        if(r.getPatternStem() != null) detectedPatternStr = r.getPatternStem(); 
                        break; 
                    }
                 }
             }
        } catch (Throwable e) { 
            // Catching Throwable handles NoClassDefFoundError if jar is missing
            logger.error("AlKhalil analyzer failed (Saving word without root): {}", arabicWord, e);
        }

        // 2. Handle Root (If found)
        if (detectedRootStr != null) {
            try {
                RootDTO rootDTO = dalFacade.getRootByLetters(detectedRootStr);
                if (rootDTO == null) {
                    RootDTO newRoot = new RootDTO();
                    newRoot.setRootLetters(detectedRootStr);
                    rootDTO = dalFacade.createRoot(newRoot); 
                }
                if (rootDTO != null) finalRootId = rootDTO.getId();
            } catch (Exception e) {
                logger.error("Error creating/fetching root: {}", detectedRootStr, e);
            }
        }

        // 3. Handle Pattern (If found)
        if (detectedPatternStr != null && !detectedPatternStr.equals("Default")) {
            try {
                // Ideally we check if pattern exists, but for now we try add
                PatternDTO p = new PatternDTO();
                p.setName(detectedPatternStr);
                p.setTemplate(detectedPatternStr);
                p.setDescription("Auto-detected");
                finalPatternId = dalFacade.addPattern(p);
            } catch (Exception e) {
                // If add fails (maybe duplicate), ignore and leave ID as 0
                logger.warn("Could not add auto-pattern: {}", detectedPatternStr);
            }
        }

        // 4. Save the Word (Even if Root/Pattern are 0)
        WordDTO word = new WordDTO();
        word.setArabicWord(arabicWord.trim());
        word.setUrduMeaning(urduMeaning.trim());
        word.setRootID(finalRootId);
        word.setPatternID(finalPatternId); 
        word.setLemma(detectedLemma);

        boolean success = dalFacade.addWord(word);
        
        if (success) {
            String msg = "Saved! " + (detectedRootStr != null ? "Root: " + detectedRootStr : "(No Root)");
            logger.info("Auto add success: {}", msg);
            return msg;
        } else {
            logger.error("Database failed to save word: {}", arabicWord);
            return "DB Error: Could not save word.";
        }
    }

    @Override
    public String getSegmentation(String arabicWord) {
        if (arabicWord == null) return "";
        try {
            List<Result> results = AlKhalil2Analyzer.getInstance().analyzerToken(arabicWord.trim());
            if (results == null || results.isEmpty()) return "No analysis found.";
            
            Result r = results.get(0);
            return "Prefix: " + (r.getProclitic()==null?"-":r.getProclitic()) + 
                   " | Stem: " + (r.getStem()==null?"-":r.getStem()) + 
                   " | Suffix: " + (r.getEnclitic()==null?"-":r.getEnclitic());
        } catch (Throwable e) {
            return "Segmentation Error.";
        }
    }

    @Override
    public String getLemma(String arabicWord) {
        if (arabicWord == null) return "";
        try {
            List<Result> results = AlKhalil2Analyzer.getInstance().analyzerToken(arabicWord.trim());
            if (results != null && !results.isEmpty()) return results.get(0).getLemma();
        } catch (Throwable e) {
            logger.error("Lemma lookup failed", e);
        }
        return arabicWord; // Fallback to original
    }

    @Override
    public List<WordDTO> getAllWords() {
        return dalFacade.getAllWords();
    }

    @Override
    public WordDTO searchWord(String arabicWord) {
        if (arabicWord == null) return null;
        return dalFacade.searchWord(arabicWord.trim());
    }

    @Override
    public String updateWord(int id, String newMeaning) {
        if (id <= 0) return "Invalid ID.";
        boolean res = dalFacade.updateWord(id, newMeaning);
        return res ? "Updated!" : "Update Failed.";
    }

    @Override
    public String deleteWord(int id) {
        if (id <= 0) return "Invalid ID.";
        boolean res = dalFacade.deleteWord(id);
        return res ? "Deleted!" : "Delete Failed (Check Foreign Keys).";
    }

    // --- BROWSING DELEGATES (Fixed in DAO, passed through here) ---

    @Override
    public List<WordDTO> getWordsByRootID(int rootID) {
        return dalFacade.getWordsByRootID(rootID);
    }

    @Override
    public List<WordDTO> getWordsByPatternID(int patternID) {
        return dalFacade.getWordsByPatternID(patternID);
    }

    @Override
    public List<WordDTO> getWordsByLemma(String lemma) {
        return dalFacade.getWordsByLemma(lemma);
    }

    @Override
    public List<String> getAllDistinctLemmas() {
        return dalFacade.getAllDistinctLemmas();
    }
    
    @Override
    public List<WordDTO> searchBySubstring(String query) {
        if (query == null) return Collections.emptyList();
        return dalFacade.searchBySubstring(query);
    }

    @Override
    public List<WordDTO> searchByRegex(String pattern) {
        if (pattern == null) return Collections.emptyList();
        return dalFacade.searchByRegex(pattern);
    }
    
    @Override
    public List<WordDTO> searchByMeaning(String meaning) {
        if (meaning == null) return Collections.emptyList();
        return dalFacade.searchByMeaning(meaning);
    }

    @Override
    public String normalizeText(String text) {
        if (text == null) return "";
        String n = text.replaceAll("[\\u064B-\\u065F]", ""); // Remove Tashkeel
        n = n.replaceAll("[أإآ]", "ا"); // Normalize Alif
        return n.trim();
    }
}