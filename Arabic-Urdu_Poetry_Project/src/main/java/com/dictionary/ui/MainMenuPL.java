package com.dictionary.ui;

import com.dictionary.bofacade.IBOFacade;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuPL extends Application {

    private static IBOFacade boFacade;

    public static void setFacade(IBOFacade facade) {
        boFacade = facade;
    }

    // Styles
    private final String BACKGROUND_STYLE = "-fx-background-color: #f0f7e4;"; 
    private final String TITLE_STYLE = "-fx-text-fill: #2c3e50; -fx-font-size: 24px; -fx-font-weight: bold;";
    
    // Green for ALL buttons (Uniform Look)
    private final String BTN_STYLE = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
    
    // Red for Exit
    private final String EXIT_BTN_STYLE = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";

    @Override
    public void start(Stage primaryStage) {
        if (boFacade == null) {
            throw new IllegalStateException("BOFacade not set. Call MainMenuPL.setFacade(...)");
        }

        primaryStage.setTitle("Main Menu - Arabic Dictionary");

        Label title = new Label("Arabic Urdu Dictionary System");
        title.setStyle(TITLE_STYLE);

        // --- CREATE BUTTONS ---
        
        Button rootBtn = createButton("1. Root Management", BTN_STYLE);
        Button wordBtn = createButton("2. Word Management", BTN_STYLE);
        Button patternBtn = createButton("3. Pattern Management", BTN_STYLE);
        
        Button browseBtn = createButton("4. Browse Dictionary", BTN_STYLE);
        
        // Member 1 Feature
        Button genBtn = createButton("5. Generate Word", BTN_STYLE);
        
        // Member 2 Feature (NEW)
        Button poemBtn = createButton("6. Poem Parser", BTN_STYLE);
        
        Button exitBtn = createButton("Exit", EXIT_BTN_STYLE);

        // --- NAVIGATION LOGIC ---
        
        rootBtn.setOnAction(e -> {
            try {
                RootManagementUI rootUI = new RootManagementUI(boFacade);
                Stage stage = new Stage();
                primaryStage.hide();
                rootUI.show(stage, primaryStage); 
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        wordBtn.setOnAction(e -> {
            try {
                WordPresentation wordUI = new WordPresentation(boFacade);
                Stage stage = new Stage();
                primaryStage.hide();
                wordUI.show(stage, primaryStage);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        patternBtn.setOnAction(e -> {
            try {
                PatternPL patternUI = new PatternPL(boFacade);
                Stage stage = new Stage();
                primaryStage.hide();
                patternUI.show(stage, primaryStage);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        browseBtn.setOnAction(e -> {
            try {
                BrowsingUI browseUI = new BrowsingUI(boFacade);
                Stage stage = new Stage();
                primaryStage.hide();
                browseUI.show(stage, primaryStage);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        genBtn.setOnAction(e -> {
            try {
                WordGenerationUI genUI = new WordGenerationUI(boFacade);
                Stage stage = new Stage();
                primaryStage.hide();
                genUI.show(stage, primaryStage);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // NEW: Poem Parser Logic
        poemBtn.setOnAction(e -> {
            try {
                PoemParsingUI poemUI = new PoemParsingUI(boFacade);
                Stage stage = new Stage();
                primaryStage.hide();
                poemUI.show(stage, primaryStage);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        exitBtn.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(15, title, rootBtn, wordBtn, patternBtn, browseBtn, genBtn, poemBtn, exitBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle(BACKGROUND_STYLE);

        Scene scene = new Scene(root, 450, 650); // Increased height slightly for extra button
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createButton(String text, String style) {
        Button btn = new Button(text);
        btn.setPrefWidth(250); // Slightly wider to fit "Generate Word (Sarf)" text
        btn.setPrefHeight(40);
        btn.setStyle(style);
        // Add hover effect
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }
}