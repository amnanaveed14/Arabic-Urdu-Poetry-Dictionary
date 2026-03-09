package com.dictionary.dao;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dictionary.dto.PatternDTO;
import com.dictionary.sql.DBConnection;

class PatternDAOTest {

    private PatternDAO patternDAO;

    @BeforeEach
    void setUp() throws Exception {
        patternDAO = new PatternDAO();

        // Prepare H2 Database for 'patterns' table
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("DROP TABLE IF EXISTS patterns");
            
            // Create table matching PatternDAO requirements
            stmt.execute("CREATE TABLE patterns (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "name VARCHAR(255), " +
                         "template TEXT, " +
                         "description TEXT, " +
                         "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        }
    }

    @Test
    void testAddPattern() throws SQLException {
        PatternDTO p = new PatternDTO();
        p.setName("Fa'ala");
        p.setTemplate("F-A-L");
        p.setDescription("Simple verb");

        // Act
        int newId = patternDAO.addPattern(p);

        // Assert
        assertTrue(newId > 0, "Pattern ID should be generated");
    }

    @Test
    void testGetAllPatterns() throws SQLException {
        // Arrange
        PatternDTO p1 = new PatternDTO(0, "P1", "T1", "D1");
        PatternDTO p2 = new PatternDTO(0, "P2", "T2", "D2");
        patternDAO.addPattern(p1);
        patternDAO.addPattern(p2);

        // Act
        List<PatternDTO> list = patternDAO.getAllPatterns();

        // Assert
        assertEquals(2, list.size());
    }

    @Test
    void testUpdatePattern() throws SQLException {
        // Arrange
        PatternDTO p = new PatternDTO();
        p.setName("OldName");
        p.setTemplate("OldTemp");
        p.setDescription("OldDesc");
        int id = patternDAO.addPattern(p);
        p.setId(id); // Ensure object has the ID

        // Act
        p.setName("NewName");
        boolean success = patternDAO.updatePattern(p);

        // Assert
        assertTrue(success);
        PatternDTO updated = patternDAO.getPatternById(id);
        assertEquals("NewName", updated.getName());
    }

    @Test
    void testDeletePattern() throws SQLException {
        // Arrange
        PatternDTO p = new PatternDTO();
        p.setName("ToDelete");
        p.setTemplate("Temp");
        p.setDescription("Desc");
        int id = patternDAO.addPattern(p);

        // Act
        boolean success = patternDAO.deletePattern(id);

        // Assert
        assertTrue(success);
        assertNull(patternDAO.getPatternById(id));
    }
}