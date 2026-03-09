package com.dictionary.dalfacade;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;

import com.dictionary.dao.IPatternDAO;
import com.dictionary.dao.IRootDAO;
import com.dictionary.dao.IWordDAO;
import com.dictionary.dao.PatternDAO;
import com.dictionary.dao.SqlRootDAO; // CORRECT: Using your specific class
import com.dictionary.dao.WordDAO;
import com.dictionary.dto.PatternDTO;
import com.dictionary.dto.RootDTO;
import com.dictionary.dto.WordDTO;
import com.dictionary.sql.DBConnection; 

public class DALFacade implements IDALFacade {
    IPatternDAO pattern;
    IRootDAO root;
    IWordDAO word;

    // Default Constructor used by MainApp/app
    public DALFacade() {
        this.pattern = new PatternDAO();
        this.root = new SqlRootDAO(); // CORRECT: Instantiating SqlRootDAO
        this.word = new WordDAO();
        initializeDatabase();
    }

    // Constructor for injection (if needed)
    public DALFacade(IPatternDAO pattern, IRootDAO root, IWordDAO word) {
        this.pattern = pattern;
        this.root = root;
        this.word = word;
        initializeDatabase();
    }

    private void initializeDatabase() {
        String[] sqlStatements = {
            "CREATE TABLE IF NOT EXISTS root (id INTEGER PRIMARY KEY AUTOINCREMENT, root_letters TEXT UNIQUE)",
            "CREATE TABLE IF NOT EXISTS pattern (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, template TEXT, description TEXT)",
            "CREATE TABLE IF NOT EXISTS word (word_id INTEGER PRIMARY KEY AUTOINCREMENT, arabic_word TEXT, urdu_meaning TEXT, lemma TEXT, root_id INTEGER, pattern_id INTEGER, FOREIGN KEY(root_id) REFERENCES root(id), FOREIGN KEY(pattern_id) REFERENCES pattern(id))"
        };

        try (Connection con = DBConnection.getConnection(); 
             Statement stmt = con.createStatement()) {
            for (String sql : sqlStatements) stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * These re the Root Operations such as 
     * createRoot
     * getRootByID
     * updateRoot etc....
     */
    @Override
    public RootDTO createRoot(RootDTO root1) {
        return root.createRoot(root1);
    }

    @Override
    public RootDTO getRootById(int id) {
        return root.getRootById(id);
    }

    @Override
    public RootDTO getRootByLetters(String rootLetters) {
        return root.getRootByLetters(rootLetters);
    }

    @Override
    public List<RootDTO> getAllRoots() {
        return root.getAllRoots();
    }

    @Override
    public void updateRoot(RootDTO root1) {
        root.updateRoot(root1);
    }

    @Override
    public void deleteRoot(int id) {
        root.deleteRoot(id);
    }

    /**
     * These are the Word Operations such as
     * addWord
     * getAllWords
     * searchWord
     * DeleteWord etc....
     */
    @Override
    public boolean addWord(WordDTO word1) {
        return word.addWord(word1);
    }

    @Override
    public List<WordDTO> getAllWords() {
        return word.getAllWords();
    }

    @Override
    public WordDTO searchWord(String arabicWord) {
        return word.searchWord(arabicWord);
    }

    @Override
    public boolean updateWord(int id, String newMeaning) {
        return word.updateWord(id, newMeaning);
    }

    @Override
    public boolean deleteWord(int id) {
        return word.deleteWord(id);
    }

    // --- NEW BROWSING OPERATIONS (Added here) ---

    @Override
    public List<WordDTO> getWordsByRootID(int rootID) {
        return word.getWordsByRootID(rootID);
    }

    @Override
    public List<WordDTO> getWordsByPatternID(int patternID) {
        return word.getWordsByPatternID(patternID);
    }

    @Override
    public List<WordDTO> getWordsByLemma(String lemma) {
        return word.getWordsByLemma(lemma);
    }

    @Override
    public List<String> getAllDistinctLemmas() {
        return word.getAllDistinctLemmas();
    }

    /**
     * These are the Pattern Operations such as
     * addPattern
     * deletePattern
     * updatePattern etc....
     */
    @Override
    public void ensureTable() throws SQLException {
        pattern.ensureTable();
    }

    @Override
    public int addPattern(PatternDTO pattern1) throws SQLException {
        return pattern.addPattern(pattern1);
    }

    @Override
    public PatternDTO getPatternById(int id) throws SQLException {
        return pattern.getPatternById(id);
    }

    @Override
    public List<PatternDTO> getAllPatterns() throws SQLException {
        return pattern.getAllPatterns();
    }

    @Override
    public boolean updatePattern(PatternDTO pattern1) throws SQLException {
        return pattern.updatePattern(pattern1);
    }

    @Override
    public boolean deletePattern(int id) throws SQLException {
        return pattern.deletePattern(id);
    }
    
    @Override
    public List<WordDTO> searchBySubstring(String substring) {
        return word.searchBySubstring(substring);
    }

    @Override
    public List<WordDTO> searchByRegex(String regexPattern) {
        return word.searchByRegex(regexPattern);
    }

    @Override
    public List<WordDTO> searchByMeaning(String meaningQuery) {
        return word.searchByMeaning(meaningQuery);
    }
}