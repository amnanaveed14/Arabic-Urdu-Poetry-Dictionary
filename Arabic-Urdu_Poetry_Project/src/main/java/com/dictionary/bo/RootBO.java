package com.dictionary.bo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import com.dictionary.dalfacade.IDALFacade;
import com.dictionary.dto.RootDTO;

public class RootBO implements IRootBO {
    private static final Logger logger = LogManager.getLogger(RootBO.class);
    private final IDALFacade dALFacade;

    public RootBO(IDALFacade IDALFacade) {
        this.dALFacade = IDALFacade;
    }

    @Override
    public RootDTO addRoot(String rootLetters) {
        if (rootLetters == null || rootLetters.trim().isEmpty()) {
            throw new IllegalArgumentException("Root letters cannot be empty.");
        }
        String normalized = rootLetters.trim();
        
        // Check duplicate
        RootDTO existingRoot = dALFacade.getRootByLetters(normalized);
        if (existingRoot != null) {
            return existingRoot;
        }

        RootDTO newRoot = new RootDTO(0, normalized);
        return dALFacade.createRoot(newRoot);
    }

    @Override
    public RootDTO getRoot(int id) {
        return dALFacade.getRootById(id);
    }

    @Override
    public List<RootDTO> browseAllRoots() {
        return dALFacade.getAllRoots();
    }

    @Override
    public void deleteRoot(Integer id) {
        logger.info("Requesting delete for Root ID: {}", id);
        // DAO handles the foreign key exception internally
        dALFacade.deleteRoot(id);
    }

    @Override
    public void updateRootLetters(Integer id, String newRootLetters) {
        if (newRootLetters == null || newRootLetters.trim().length() < 3) {
            throw new IllegalArgumentException("Root must be at least 3 letters.");
        }
        RootDTO updateObj = new RootDTO(id, newRootLetters.trim());
        dALFacade.updateRoot(updateObj);
    }
}