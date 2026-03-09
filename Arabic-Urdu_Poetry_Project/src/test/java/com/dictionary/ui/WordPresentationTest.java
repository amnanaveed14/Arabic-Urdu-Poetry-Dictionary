package com.dictionary.ui;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.dictionary.bo.PatternBO;
import com.dictionary.bo.RootBO;
import com.dictionary.bo.WordBO;
import com.dictionary.bofacade.BOFacade;
import com.dictionary.bofacade.IBOFacade;
import com.dictionary.dalfacade.DALFacade;
import com.dictionary.dao.PatternDAO;
import com.dictionary.dao.SqlRootDAO;
import com.dictionary.dao.WordDAO;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class WordPresentationTest {

    private WordPresentation ui;

    @Start
    private void start(Stage stage) throws Exception {
        var dal = new DALFacade(new PatternDAO(), new SqlRootDAO(), new WordDAO());
        IBOFacade facade = new BOFacade(new RootBO(dal), new WordBO(dal), new PatternBO(dal));
        ui = new WordPresentation(facade);
        ui.show(stage, new Stage());
    }

    
}