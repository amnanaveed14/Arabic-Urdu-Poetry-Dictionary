package com.dictionary.ui;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class AppLaunchTest {

    @Start
    private void start(Stage stage) throws Exception {
        app.main(new String[0]);  
    }

    @Test
    void should_launch_app_successfully(FxRobot robot) {
        verifyThat("Arabic Urdu Dictionary System", hasText("Arabic Urdu Dictionary System"));
        robot.clickOn("2. Word Management");
        robot.clickOn("Back");
        robot.clickOn("Exit");
    }
}