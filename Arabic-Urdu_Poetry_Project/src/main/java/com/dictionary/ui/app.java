package com.dictionary.ui;

import com.dictionary.bofacade.BOFacade;
import com.dictionary.bofacade.IBOFacade;
import com.dictionary.bo.PatternBO;
import com.dictionary.bo.RootBO;
import com.dictionary.bo.WordBO;
import com.dictionary.dalfacade.DALFacade;
import com.dictionary.dalfacade.IDALFacade;
import com.dictionary.dao.IPatternDAO;
import com.dictionary.dao.IRootDAO;
import com.dictionary.dao.IWordDAO;
import com.dictionary.dao.PatternDAO;
import com.dictionary.dao.SqlRootDAO;
import com.dictionary.dao.WordDAO;
import javafx.application.Application;  

/**
 * This class initializes All Business Objects and Data Access Layers components 
 * They re connected through {@link DALFacade}
 * It also launches the main UI menu
 * 
 * 
 * All DAOs are injected into a single {@link DALFacade} instance which is then passed to BO classes.
 * This ensures a consistent and centralized access point to the database layer.
 */

public class app {

    private static IDALFacade createDALFacade() throws Exception {
        IRootDAO rootDAO = new SqlRootDAO();
        IWordDAO wordDAO = new WordDAO();
        IPatternDAO patternDAO = new PatternDAO();
        return new DALFacade(patternDAO, rootDAO, wordDAO);
    }

    public static void main(String[] args) {
        try {
            IDALFacade dalFacade = createDALFacade();

            RootBO rootBO = new RootBO(dalFacade);
            WordBO wordBO = new WordBO(dalFacade);
            PatternBO patternBO = new PatternBO(dalFacade);

            IBOFacade boFacade = new BOFacade(rootBO, wordBO, patternBO);

            // Inject BOFacade into JavaFX MainMenuPL
            MainMenuPL.setFacade(boFacade);

            // Launch JavaFX Application
            Application.launch(MainMenuPL.class, args);

        } catch (Exception e) {
            System.err.println("FATAL ERROR: Application failed to start.");
            e.printStackTrace();
        }
    }
}
