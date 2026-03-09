package com.dictionary.bo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dictionary.dalfacade.IDALFacade;
import com.dictionary.dto.RootDTO;

@ExtendWith(MockitoExtension.class)
class RootBOTest {

    @Mock
    private IDALFacade dalFacade; // The Fake Database

    @InjectMocks
    private RootBO rootBO; // The Logic Class

    // TEST addRoot (Creation Logic)

    @Test
    void testAddRoot_NullInput_ThrowsException() {
        // Validation check: Should crash if input is empty
        assertThrows(IllegalArgumentException.class, () -> {
            rootBO.addRoot(""); 
        });
        verify(dalFacade, never()).createRoot(any());
    }

    @Test
    void testAddRoot_ExistingRoot_ReturnsExisting() {
        // Scenario: User adds "k-t-b" but it already exists in DB.
        // Expected: BO should return the existing one instead of creating a duplicate.
        
        RootDTO existing = new RootDTO(5, "k-t-b");
        when(dalFacade.getRootByLetters("k-t-b")).thenReturn(existing);

        RootDTO result = rootBO.addRoot("k-t-b");

        assertEquals(5, result.getId());
        // Verify createRoot was NEVER called
        verify(dalFacade, never()).createRoot(any());
    }

    @Test
    void testAddRoot_NewRoot_CreatesSuccessfully() {
        // Scenario: User adds "d-r-s", it does NOT exist.
        // Expected: BO should create it.
        
        when(dalFacade.getRootByLetters("d-r-s")).thenReturn(null); // Not found
        
        RootDTO newRoot = new RootDTO(10, "d-r-s");
        when(dalFacade.createRoot(any(RootDTO.class))).thenReturn(newRoot);

        RootDTO result = rootBO.addRoot("d-r-s");

        assertEquals(10, result.getId());
        verify(dalFacade).createRoot(any(RootDTO.class));
    }

    // TEST updateRootLetters (Validation Logic)

    @Test
    void testUpdateRoot_InvalidLength_ThrowsException() {
        // Rule: Root must be 3 or 4 letters
        assertThrows(IllegalArgumentException.class, () -> rootBO.updateRootLetters(1, "ab")); // Too short
        assertThrows(IllegalArgumentException.class, () -> rootBO.updateRootLetters(1, "abcde")); // Too long
    }

    @Test
    void testUpdateRoot_DuplicateConflict_ThrowsException() {
        // Scenario: Change ID 1 to "ktb" (3 letters), but ID 2 already owns "ktb".
        
        // 1. Setup the Mock to return a conflict for "ktb"
        RootDTO conflictRoot = new RootDTO(2, "ktb");
        when(dalFacade.getRootByLetters("ktb")).thenReturn(conflictRoot);

        // 2. Assert that it throws IllegalStateException (Conflict)
        assertThrows(IllegalStateException.class, () -> {
            // Use "ktb" (3 chars) so it passes the length check!
            rootBO.updateRootLetters(1, "ktb");
        });
        
        // 3. Verify we never tried to save it
        verify(dalFacade, never()).updateRoot(any());
    }

    @Test
    void testUpdateRoot_Success() {
        // Scenario: Valid update, no conflict
        when(dalFacade.getRootByLetters("new")).thenReturn(null);

        rootBO.updateRootLetters(1, "new");

        verify(dalFacade).updateRoot(any(RootDTO.class));
    }
    
    // TEST Pass-through methods (Browse/Get/Delete)
    
    @Test
    void testBrowseAllRoots() {
        when(dalFacade.getAllRoots()).thenReturn(Arrays.asList(new RootDTO(1, "a"), new RootDTO(2, "b")));
        List<RootDTO> list = rootBO.browseAllRoots();
        assertEquals(2, list.size());
    }
}