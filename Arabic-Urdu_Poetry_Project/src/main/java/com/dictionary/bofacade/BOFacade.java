package com.dictionary.bofacade;

import java.sql.SQLException;
import java.util.List;

// Imports for Interfaces
import com.dictionary.bo.IPatternBO;
import com.dictionary.bo.IRootBO;
import com.dictionary.bo.IWordBO;

// Imports for DTOs
import com.dictionary.dto.PatternDTO;
import com.dictionary.dto.RootDTO;
import com.dictionary.dto.WordDTO;

public class BOFacade implements IBOFacade {

    // Using Interfaces for loose coupling
    private final IRootBO rootBO;
    private final IWordBO wordBO;
    private final IPatternBO patternBO;

    // Constructor Injection using Interfaces
    public BOFacade(IRootBO rootBO, IWordBO wordBO, IPatternBO patternBO) {
        this.rootBO = rootBO;
        this.wordBO = wordBO;
        this.patternBO = patternBO;
    }

    // --- WORD OPERATIONS ---

    @Override
    public String addWord(String arabicWord, String urduMeaning, int rootID, int patternID) {
        return wordBO.addWord(arabicWord, urduMeaning, rootID, patternID);
    }

    @Override
    public String addWordWithSuggestedRoot(String arabicWord, String urduMeaning) {
        return wordBO.addWordWithSuggestedRoot(arabicWord, urduMeaning);
    }

    @Override
    public String getSegmentation(String arabicWord) {
        return wordBO.getSegmentation(arabicWord);
    }
    
    @Override
    public String getLemma(String arabicWord) {
        return wordBO.getLemma(arabicWord);
    }
    
    @Override
    public List<WordDTO> getAllWords() {
        return wordBO.getAllWords();
    }

    @Override
    public WordDTO searchWord(String arabicWord) {
        return wordBO.searchWord(arabicWord);
    }

    @Override
    public String updateWord(int id, String newMeaning) {
        return wordBO.updateWord(id, newMeaning);
    }

    @Override
    public String deleteWord(int id) {
        return wordBO.deleteWord(id);
    }
    @Override
    public List<WordDTO> getWordsByRootID(int rootID) {
        return wordBO.getWordsByRootID(rootID);
    }

    @Override
    public List<WordDTO> getWordsByPatternID(int patternID) {
        return wordBO.getWordsByPatternID(patternID);
    }

    @Override
    public List<WordDTO> getWordsByLemma(String lemma) {
        return wordBO.getWordsByLemma(lemma);
    }

    @Override
    public List<String> getAllDistinctLemmas() {
        return wordBO.getAllDistinctLemmas();
    }
    // --- ROOT OPERATIONS ---

    @Override
    public RootDTO addRoot(String rootLetters) {
        return rootBO.addRoot(rootLetters);
    }

    @Override
    public RootDTO getRoot(int id) {
        return rootBO.getRoot(id);
    }

    @Override
    public List<RootDTO> browseAllRoots() {
        return rootBO.browseAllRoots();
    }

    @Override
    public void updateRootLetters(Integer id, String newRootLetters) {
        rootBO.updateRootLetters(id, newRootLetters);
    }

    @Override
    public void deleteRoot(Integer id) {
        rootBO.deleteRoot(id);
    }

    // --- PATTERN OPERATIONS ---

    @Override
    public void ensurePatternTable() throws SQLException {
        patternBO.ensurePatternTable();
    }

    @Override
    public int createPattern(String name, String template, String description) throws SQLException {
        return patternBO.createPattern(name, template, description);
    }

    @Override
    public PatternDTO getPatternById(int id) throws SQLException {
        return patternBO.getPatternById(id);
    }

    @Override
    public List<PatternDTO> listAllPatterns() throws SQLException {
        return patternBO.listAllPatterns();
    }

    @Override
    public boolean updatePattern(int id, String name, String template, String description) throws SQLException {
        return patternBO.updatePattern(id, name, template, description);
    }

    @Override
    public boolean deletePattern(int id) throws SQLException {
        return patternBO.deletePattern(id);
    }
    @Override
	public List<WordDTO> searchBySubstring(String query) {
		return wordBO.searchBySubstring(query);
	}

	@Override
	public List<WordDTO> searchByRegex(String pattern) {
		return wordBO.searchByRegex(pattern);
	}

	@Override
	public List<WordDTO> searchByMeaning(String meaning) {
		return wordBO.searchByMeaning(meaning);
	}
	
	@Override
    public String generateWord(String rootLetters, String template) {
        return patternBO.generateWord(rootLetters, template);
    }
	
	@Override
    public String normalizeText(String text) {
        return wordBO.normalizeText(text);
    }
}