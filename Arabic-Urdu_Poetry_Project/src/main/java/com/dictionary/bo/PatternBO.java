package com.dictionary.bo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.SQLException;
import java.util.List;
import com.dictionary.dalfacade.IDALFacade;
import com.dictionary.dto.PatternDTO;

public class PatternBO implements IPatternBO {

    private static final Logger logger = LogManager.getLogger(PatternBO.class);
    private final IDALFacade dalFacade;

    public PatternBO(IDALFacade dal) {
        this.dalFacade = dal;
    }

    @Override
    public void ensurePatternTable() throws SQLException {
        dalFacade.ensureTable();
    }

    @Override
    public int createPattern(String name, String template, String description) throws SQLException {
        if (template == null || !template.contains("{0}")) {
             throw new IllegalArgumentException("Template must contain {0}");
        }
        PatternDTO p = new PatternDTO(name, template, description);
        return dalFacade.addPattern(p);
    }

    @Override
    public PatternDTO getPatternById(int id) throws SQLException {
        return dalFacade.getPatternById(id);
    }

    @Override
    public List<PatternDTO> listAllPatterns() throws SQLException {
        return dalFacade.getAllPatterns();
    }

    @Override
    public boolean updatePattern(int id, String name, String template, String description) throws SQLException {
        PatternDTO p = new PatternDTO(id, name, template, description);
        return dalFacade.updatePattern(p);
    }

    @Override
    public boolean deletePattern(int id) throws SQLException {
        return dalFacade.deletePattern(id);
    }

    /**
     * LOGIC ONLY: This generates the word string based on the pattern.
     * It does NOT save to the database. The UI must call WordBO.addWord to save the result.
     */
    @Override
    public String generateWord(String rootLetters, String template) {
        if (rootLetters == null || template == null) return "Error";

        String cleanRoot = rootLetters.replaceAll("\\s+", "");
        char[] letters = cleanRoot.toCharArray();
        String result = template;

        for (int i = 0; i < letters.length; i++) {
            String placeholder = "{" + i + "}";
            if (result.contains(placeholder)) {
                result = result.replace(placeholder, String.valueOf(letters[i]));
            }
        }
        return result;
    }
}