package com.dictionary.ui;

import com.dictionary.bofacade.BOFacade;
import com.dictionary.bofacade.IBOFacade;
import com.dictionary.bo.PatternBO;
import com.dictionary.bo.RootBO;
import com.dictionary.bo.WordBO;
import com.dictionary.dalfacade.DALFacade;
import com.dictionary.dao.PatternDAO;
import com.dictionary.dao.SqlRootDAO;
import com.dictionary.dao.WordDAO;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class BrowsingUITest {

    @Start
    private void start(Stage stage) throws Exception {
        var dal = new DALFacade(new PatternDAO(), new SqlRootDAO(), new WordDAO());
        IBOFacade facade = new BOFacade(new RootBO(dal), new WordBO(dal), new PatternBO(dal));
        new BrowsingUI(facade).show(stage, new Stage());
    }

    @Test
    void should_open_browse_screen(FxRobot robot) {
        robot.clickOn("Root");
        robot.clickOn("Search");
    }
}