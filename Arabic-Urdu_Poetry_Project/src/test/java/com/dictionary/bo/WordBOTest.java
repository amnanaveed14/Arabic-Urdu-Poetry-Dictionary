package com.dictionary.bo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dictionary.dalfacade.IDALFacade;
import com.dictionary.dto.WordDTO;

@ExtendWith(MockitoExtension.class)
class WordBOTest {

    @Mock
    private IDALFacade dalFacade; // The Fake Database

    @InjectMocks
    private WordBO wordBO; // The Class Under Test

    // 1. TESTING addWord (Manual Entry)
    
    @Test
    void testAddWord_NullArabic_ReturnsError() {
        String result = wordBO.addWord(null, "Meaning", 1, 1);
        assertEquals("Error! Arabic word is required.", result);
        verify(dalFacade, never()).addWord(any());
    }

    @Test
    void testAddWord_NullMeaning_ReturnsError() {
        String result = wordBO.addWord("Kitab", "", 1, 1);
        assertEquals("Error! Urdu meaning is required.", result);
        verify(dalFacade, never()).addWord(any());
    }

    @Test
    void testAddWord_InvalidRoot_ReturnsError() {
        String result = wordBO.addWord("Kitab", "Book", 0, 1);
        assertEquals("Error! Root ID is required (or use Auto-Suggestion).", result);
    }

    @Test
    void testAddWord_Success() {
        // Teach puppet to return true
        when(dalFacade.addWord(any(WordDTO.class))).thenReturn(true);

        String result = wordBO.addWord("Kitab", "Book", 1, 1);

        assertEquals("Word added successfully!", result);
        // Verify DAL was called exactly once
        verify(dalFacade, times(1)).addWord(any(WordDTO.class));
    }

    // 2. TESTING updateWord

    @Test
    void testUpdateWord_InvalidID_ReturnsError() {
        String result = wordBO.updateWord(-5, "New Meaning");
        assertEquals("Invalid Word ID.", result);
        verify(dalFacade, never()).updateWord(anyInt(), any());
    }

    @Test
    void testUpdateWord_EmptyMeaning_ReturnsError() {
        String result = wordBO.updateWord(10, "");
        assertEquals("Meaning cannot be empty.", result);
    }

    @Test
    void testUpdateWord_Success() {
        when(dalFacade.updateWord(10, "New Meaning")).thenReturn(true);

        String result = wordBO.updateWord(10, "New Meaning");

        assertEquals("Word updated successfully!", result);
        verify(dalFacade).updateWord(10, "New Meaning");
    }

    // 3. TESTING deleteWord

    @Test
    void testDeleteWord_InvalidID() {
        String result = wordBO.deleteWord(0);
        assertEquals("Invalid Word ID.", result);
        verify(dalFacade, never()).deleteWord(anyInt());
    }

    @Test
    void testDeleteWord_Success() {
        when(dalFacade.deleteWord(5)).thenReturn(true);

        String result = wordBO.deleteWord(5);

        assertEquals("Word deleted successfully!", result);
        verify(dalFacade).deleteWord(5);
    }

    // 4. TESTING normalizeText 

    @Test
    void testNormalizeText_RemovesDiacritics() {
        // Input: "kátábá" (with fake diacritics for example) or Arabic Tashkeel
        // Arabic Example: "كَتَبَ" -> "كتب"
        String input = "كَتَبَ"; 
        String expected = "كتب"; 
        
        String result = wordBO.normalizeText(input);
        
        assertEquals(expected, result, "Should remove Fatha/Damma/Kasra");
    }

    @Test
    void testNormalizeText_UnifiesAlif() {
        // Input: "أحمد" (Hamza) -> Expected: "احمد" (No Hamza)
        String input = "أحمد";
        String expected = "احمد";

        String result = wordBO.normalizeText(input);
        
        assertEquals(expected, result, "Should turn Hamza-Alif into simple Alif");
    }
    
    // 5. TESTING Search & Retrieval (Pass-throughs)

    @Test
    void testSearchWord_EmptyInput() {
        assertNull(wordBO.searchWord(""));
        verify(dalFacade, never()).searchWord(any());
    }

    @Test
    void testSearchWord_Valid() {
        WordDTO mockWord = new WordDTO();
        mockWord.setArabicWord("Test");
        
        when(dalFacade.searchWord("Test")).thenReturn(mockWord);

        WordDTO result = wordBO.searchWord("Test");
        
        assertNotNull(result);
        assertEquals("Test", result.getArabicWord());
    }

    @Test
    void testSearchBySubstring_Empty() {
        List<WordDTO> list = wordBO.searchBySubstring(null);
        assertTrue(list.isEmpty());
    }
    
    @Test
    void testSearchBySubstring_Valid() {
        // Logic: BO should trim the string and pass it to DAL
        when(dalFacade.searchBySubstring("test")).thenReturn(Arrays.asList(new WordDTO()));
        
        List<WordDTO> list = wordBO.searchBySubstring(" test "); // Note spaces
        
        assertEquals(1, list.size());
        verify(dalFacade).searchBySubstring("test"); // Verify BO trimmed the input
    }

}