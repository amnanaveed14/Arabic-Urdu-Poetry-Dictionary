package com.dictionary.bo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dictionary.dalfacade.IDALFacade;
import com.dictionary.dto.PatternDTO;

@ExtendWith(MockitoExtension.class)
class PatternBOTest {

    @Mock
    private IDALFacade dalFacade;

    @InjectMocks
    private PatternBO patternBO;

    // TEST createPattern (Validation)

    @Test
    void testCreatePattern_InvalidTemplate_ThrowsException() {
        // Rule: Template must contain "{0}"
        assertThrows(IllegalArgumentException.class, () -> {
            patternBO.createPattern("Test", "InvalidTemplate", "Desc");
        });
        
        // Ensure DB is not called
        try {
            verify(dalFacade, never()).addPattern(any());
        } catch (SQLException e) { fail("Should not throw SQL Exception"); }
    }

    @Test
    void testCreatePattern_Success() throws SQLException {
        // Valid template: has {0}
        when(dalFacade.addPattern(any(PatternDTO.class))).thenReturn(100);

        int id = patternBO.createPattern("Fa'il", "{0}a{1}i{2}", "Doer");

        assertEquals(100, id);
    }
    
    // TEST generateWord 
    
    @Test
    void testGenerateWord_Success() {
        // Logic: Replace {0}, {1}, {2} with K, T, B
        String root = "KTB";
        String template = "ma{0}{1}uu{2}"; // maqtul pattern

        String result = patternBO.generateWord(root, template);
        
        // Expected: ma + K + T + uu + B = maKTuuB
        assertEquals("maKTuuB", result);
    }

    @Test
    void testGenerateWord_LengthMismatch() {
        // Logic: Root has 3 letters, but template asks for {3} (4th letter)
        String root = "KTB"; // Indices 0, 1, 2
        String template = "{0}{1}{2}{3}"; // Asks for index 3

        String result = patternBO.generateWord(root, template);
        
        assertEquals("Error: Root length mismatch with Pattern", result);
    }

    @Test
    void testGenerateWord_NullInputs() {
        String result = patternBO.generateWord(null, "{0}");
        assertEquals("Invalid Input", result);
    }
    

    // TEST CRUD Operations


    @Test
    void testUpdatePattern_Success() throws SQLException {
        when(dalFacade.updatePattern(any(PatternDTO.class))).thenReturn(true);
        
        boolean result = patternBO.updatePattern(1, "Name", "{0}a{1}a{2}", "Desc");
        
        assertTrue(result);
    }
    
    @Test
    void testDeletePattern_Success() throws SQLException {
        when(dalFacade.deletePattern(5)).thenReturn(true);
        boolean result = patternBO.deletePattern(5);
        assertTrue(result);
    }
}