package com.dictionary.bo;

import java.util.List;
import com.dictionary.dto.RootDTO;

/**
 * Business Object Interface for Root Logic.
 */
public interface IRootBO {

    /**
     * Validates and adds a new Root.
     * @param rootLetters The 3 letters (e.g., "k-t-b").
     * @return The created RootDTO.
     */
    RootDTO addRoot(String rootLetters);

    /**
     * Retrieves a root by ID.
     * @param id The root ID.
     * @return The RootDTO.
     */
    RootDTO getRoot(int id);

    /**
     * Retrieves all roots for browsing.
     * @return List of roots.
     */
    List<RootDTO> browseAllRoots();

    /**
     * Deletes a root if it is not in use.
     * @param id The root ID.
     */
    void deleteRoot(Integer id);

    /**
     * Updates the letters of a root.
     * @param id The root ID.
     * @param newRootLetters The new letters.
     */
    void updateRootLetters(Integer id, String newRootLetters);
}