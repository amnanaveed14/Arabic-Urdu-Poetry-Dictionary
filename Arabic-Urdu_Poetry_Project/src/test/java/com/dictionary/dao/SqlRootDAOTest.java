package com.dictionary.dao;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dictionary.dto.RootDTO;
import com.dictionary.sql.DBConnection;

class SqlRootDAOTest {

    private SqlRootDAO rootDAO;

    @BeforeEach
    void setUp() throws Exception {
        rootDAO = new SqlRootDAO();

        // Prepare H2 Database for 'root' table
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("DROP TABLE IF EXISTS root");
            
            // Create table matching SqlRootDAO requirements
            stmt.execute("CREATE TABLE root (" +
                         "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                         "root_letters VARCHAR(255))");
        }
    }

    @Test
    void testCreateRoot() {
        RootDTO newRoot = new RootDTO();
        newRoot.setRootLetters("k-t-b");

        // Act
        RootDTO savedRoot = rootDAO.createRoot(newRoot);

        // Assert
        assertNotNull(savedRoot);
        assertTrue(savedRoot.getId() > 0, "Root should have a generated ID");
        assertEquals("k-t-b", savedRoot.getRootLetters());
    }

    @Test
    void testGetRootByLetters() {
        // Arrange: Insert a root first
        RootDTO r = new RootDTO();
        r.setRootLetters("d-r-s");
        rootDAO.createRoot(r);

        // Act
        RootDTO found = rootDAO.getRootByLetters("d-r-s");

        // Assert
        assertNotNull(found);
        assertEquals("d-r-s", found.getRootLetters());
    }

    @Test
    void testUpdateRoot() {
        // Arrange
        RootDTO r = new RootDTO();
        r.setRootLetters("old-root");
        r = rootDAO.createRoot(r);

        // Act
        r.setRootLetters("new-root");
        rootDAO.updateRoot(r);
        
        // Assert
        RootDTO updated = rootDAO.getRootById(r.getId());
        assertEquals("new-root", updated.getRootLetters());
    }

    @Test
    void testDeleteRoot() {
        // Arrange
        RootDTO r = new RootDTO();
        r.setRootLetters("del-me");
        r = rootDAO.createRoot(r);
        int id = r.getId();

        // Act
        rootDAO.deleteRoot(id);

        // Assert
        RootDTO found = rootDAO.getRootById(id);
        assertNull(found, "Root should be null after deletion");
    }
}