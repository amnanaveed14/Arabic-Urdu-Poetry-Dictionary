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
public class AppLaunchTest {

	@Start
    private void start(Stage stage) throws Exception {
        // Use the same setup as your successful MainMenuPLTest:
        var dal = new DALFacade(new PatternDAO(), new SqlRootDAO(), new WordDAO());
        IBOFacade facade = new BOFacade(new RootBO(dal), new WordBO(dal), new PatternBO(dal));
        // You'll need to make sure 'MainMenuPL' is accessible and has setFacade/start methods.
        MainMenuPL.setFacade(facade); 
        new MainMenuPL().start(stage); // <-- Launch UI directly on the test stage
    }

    @Test
    void should_launch_app_successfully(FxRobot robot) {
        verifyThat("Arabic Urdu Dictionary System", hasText("Arabic Urdu Dictionary System"));
        robot.clickOn("2. Word Management");
        robot.clickOn("Back");
        robot.clickOn("Exit");
    }
}