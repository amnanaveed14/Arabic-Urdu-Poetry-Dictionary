package com.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.control.TextField;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class CalculatorTest {

    private CalculatorApp app;

    @Start
    public void start(Stage stage) throws Exception {
        app = new CalculatorApp();
        app.start(stage);
    }

    @Test
    public void testBasicAddition(FxRobot robot) {
        robot.clickOn("2");
        robot.clickOn("+");
        robot.clickOn("3");
        robot.clickOn("=");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("5", display.getText());
    }

    @Test
    public void testBasicSubtraction(FxRobot robot) {
        robot.clickOn("9");
        robot.clickOn("-");
        robot.clickOn("4");
        robot.clickOn("=");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("5", display.getText());
    }

    @Test
    public void testBasicMultiplication(FxRobot robot) {
        robot.clickOn("6");
        robot.clickOn("*");
        robot.clickOn("7");
        robot.clickOn("=");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("42", display.getText());
    }

    @Test
    public void testBasicDivision(FxRobot robot) {
        robot.clickOn("8");
        robot.clickOn("/");
        robot.clickOn("2");
        robot.clickOn("=");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("4", display.getText());
    }

    @Test
    public void testDivisionByZero(FxRobot robot) {
        robot.clickOn("5");
        robot.clickOn("/");
        robot.clickOn("0");
        robot.clickOn("=");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("Cannot divide by zero", display.getText());
    }

    @Test
    public void testDecimalCalculation(FxRobot robot) {
        robot.clickOn("2");
        robot.clickOn(".");
        robot.clickOn("5");
        robot.clickOn("+");
        robot.clickOn("1");
        robot.clickOn(".");
        robot.clickOn("5");
        robot.clickOn("=");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("4", display.getText());
    }

    @Test
    public void testSquareFunction(FxRobot robot) {
        robot.clickOn("5");
        robot.clickOn("SQR");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("25", display.getText());
    }

    @Test
    public void testSquareRootFunction(FxRobot robot) {
        robot.clickOn("1");
        robot.clickOn("6");
        robot.clickOn("SQRT");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("4", display.getText());
    }

    @Test
    public void testReciprocalFunction(FxRobot robot) {
        robot.clickOn("4");
        robot.clickOn("1/x");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("0.25", display.getText());
    }

    @Test
    public void testPercentFunction(FxRobot robot) {
        robot.clickOn("5");
        robot.clickOn("0");
        robot.clickOn("PCT");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("0.5", display.getText());
    }

    @Test
    public void testSignChange(FxRobot robot) {
        robot.clickOn("5");
        robot.clickOn("+/-");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("-5", display.getText());
        
        robot.clickOn("+/-");
        assertEquals("5", display.getText());
    }

    @Test
    public void testClearButton(FxRobot robot) {
        robot.clickOn("7");
        robot.clickOn("+");
        robot.clickOn("3");
        robot.clickOn("C");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("0", display.getText());
        
        // Verify calculator state is cleared
        robot.clickOn("5");
        robot.clickOn("=");
        assertEquals("5", display.getText());
    }

    @Test
    public void testClearEntryButton(FxRobot robot) {
        robot.clickOn("7");
        robot.clickOn("+");
        robot.clickOn("3");
        robot.clickOn("CE");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("0", display.getText());
        
        // Verify operation is still pending
        robot.clickOn("5");
        robot.clickOn("=");
        assertEquals("12", display.getText());
    }

    @Test
    public void testBackspaceButton(FxRobot robot) {
        robot.clickOn("1");
        robot.clickOn("2");
        robot.clickOn("3");
        robot.clickOn("DEL");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("12", display.getText());
    }

    @Test
    public void testChainedOperations(FxRobot robot) {
        robot.clickOn("2");
        robot.clickOn("+");
        robot.clickOn("3");
        robot.clickOn("*");
        robot.clickOn("4");
        robot.clickOn("=");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("12", display.getText());
    }

    @Test
    public void testMultipleDecimalPrevention(FxRobot robot) {
        robot.clickOn("1");
        robot.clickOn(".");
        robot.clickOn("2");
        robot.clickOn(".");
        robot.clickOn("3");
        
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("1.23", display.getText());
    }

    @Test
    public void testDisplayInitialState(FxRobot robot) {
        TextField display = robot.lookup("#display").queryAs(TextField.class);
        assertEquals("0", display.getText());
        assertFalse(display.isEditable());
    }
}