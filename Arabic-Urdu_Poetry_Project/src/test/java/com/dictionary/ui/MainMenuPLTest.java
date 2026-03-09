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
public class MainMenuPLTest {

    @Start
    private void start(Stage stage) throws Exception {
        var dal = new DALFacade(new PatternDAO(), new SqlRootDAO(), new WordDAO());
        IBOFacade facade = new BOFacade(new RootBO(dal), new WordBO(dal), new PatternBO(dal));
        MainMenuPL.setFacade(facade);
        new MainMenuPL().start(stage);
    }

    @Test
    void should_contain_title(FxRobot robot) {
        verifyThat("Arabic Urdu Dictionary System", hasText("Arabic Urdu Dictionary System"));
    }

    @Test
    void should_open_word_management(FxRobot robot) {
        robot.clickOn("2. Word Management");
        // No crash = success
    }

    @Test
    void should_open_root_management(FxRobot robot) {
        robot.clickOn("1. Root Management");
    }

    @Test
    void should_open_pattern_management(FxRobot robot) {
        robot.clickOn("3. Pattern Management");
    }

    @Test
    void should_open_browse_dictionary(FxRobot robot) {
        robot.clickOn("4. Browse Dictionary");
    }

    @Test
    void should_open_generate_word(FxRobot robot) {
        robot.clickOn("5. Generate Word");
    }

    @Test
    void should_open_poem_parser(FxRobot robot) {
        robot.clickOn("6. Poem Parser");
    }
}