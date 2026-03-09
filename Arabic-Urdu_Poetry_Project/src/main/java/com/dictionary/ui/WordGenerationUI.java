package com.dictionary.ui;

import com.dictionary.bofacade.IBOFacade;
import com.dictionary.dto.PatternDTO;
import com.dictionary.dto.RootDTO;
import com.dictionary.dto.WordDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class WordGenerationUI {

    private final IBOFacade facade;
    
    private ComboBox<RootDTO> rootCombo;
    private ComboBox<PatternDTO> patternCombo;
    private VBox resultsContainer; 
    private Label statusLabel;

    private final String BG_STYLE = "-fx-background-color: #f0f7e4;";
    private final String BTN_BLUE = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_GREEN = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_BACK = "-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";

    public WordGenerationUI(IBOFacade facade) {
        this.facade = facade;
    }

    public void show(Stage stage, Stage parentStage) {
        stage.setTitle("Generate Words");

        // 1. Top Bar
        Button backBtn = new Button("Back");
        backBtn.setStyle(BTN_BACK);
        backBtn.setOnAction(e -> {
            stage.close();
            parentStage.show();
        });

        Label title = new Label("Word Generation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));

        BorderPane topPane = new BorderPane();
        topPane.setLeft(backBtn);
        topPane.setCenter(title);
        topPane.setPadding(new Insets(0,0,15,0));

        // 2. Selection Area
        GridPane controls = new GridPane();
        controls.setHgap(10); controls.setVgap(10);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(15));
        controls.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        // Root Combo
        controls.add(new Label("Select Root:"), 0, 0);
        rootCombo = new ComboBox<>();
        rootCombo.setPrefWidth(200);
        configureRootCombo(); 
        controls.add(rootCombo, 1, 0);

        // Pattern Combo
        controls.add(new Label("Select Pattern:"), 0, 1);
        patternCombo = new ComboBox<>();
        patternCombo.setPrefWidth(200);
        configurePatternCombo(); 
        controls.add(patternCombo, 1, 1);

        Button generateBtn = new Button("Generate");
        generateBtn.setStyle(BTN_BLUE);
        controls.add(generateBtn, 2, 1);

        // 3. Results Area
        resultsContainer = new VBox(10);
        resultsContainer.setAlignment(Pos.CENTER);
        resultsContainer.setPadding(new Insets(20));
        
        ScrollPane scroll = new ScrollPane(resultsContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        statusLabel = new Label("Select Root and Pattern to start.");
        statusLabel.setTextFill(Color.DARKSLATEBLUE);

        // 4. Main Layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(new VBox(10, topPane, controls));
        mainLayout.setCenter(scroll);
        mainLayout.setBottom(statusLabel);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle(BG_STYLE);

        // Logic
        loadData();
        generateBtn.setOnAction(e -> generateAction());

        Scene scene = new Scene(mainLayout, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void loadData() {
        try {
            List<RootDTO> roots = facade.browseAllRoots();
            if (roots != null) rootCombo.getItems().addAll(roots);

            List<PatternDTO> patterns = facade.listAllPatterns();
            if (patterns != null) patternCombo.getItems().addAll(patterns);
        } catch (Exception e) {
            statusLabel.setText("Error loading data: " + e.getMessage());
        }
    }

    private void generateAction() {
        resultsContainer.getChildren().clear(); // Clear previous result (Optional: remove to keep history)
        
        RootDTO selectedRoot = rootCombo.getValue();
        PatternDTO selectedPattern = patternCombo.getValue();

        if (selectedRoot == null || selectedPattern == null) {
            statusLabel.setText("Please select both Root and Pattern.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        // 1. Generate the Word String
        String generatedWord = facade.generateWord(selectedRoot.getRootLetters(), selectedPattern.getTemplate());

        // 2. Check Database
        WordDTO existingWord = facade.searchWord(generatedWord);

        // 3. Create Visual Card
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        Label wordLabel = new Label(generatedWord);
        wordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        
        Label descLabel = new Label();
        descLabel.setFont(Font.font("Arial", 14));

        if (existingWord != null) {
            // Case: FOUND (Black)
            wordLabel.setTextFill(Color.BLACK);
            descLabel.setText("Found: " + existingWord.getUrduMeaning());
            descLabel.setTextFill(Color.GREEN);
            card.getChildren().addAll(wordLabel, descLabel);
        } else {
            // Case: NOT FOUND (Red + Clickable)
            wordLabel.setTextFill(Color.RED);
            wordLabel.setCursor(javafx.scene.Cursor.HAND);
            wordLabel.setTooltip(new Tooltip("Click to add to dictionary"));
            
            descLabel.setText("(Missing in Dictionary - Click Word to Add)");
            descLabel.setTextFill(Color.RED);
            
            // Click Action
            wordLabel.setOnMouseClicked(e -> openPopup(generatedWord, selectedRoot, selectedPattern));
            
            card.getChildren().addAll(wordLabel, descLabel);
        }

        resultsContainer.getChildren().add(card);
        statusLabel.setText("Generated: " + generatedWord);
        statusLabel.setTextFill(Color.DARKSLATEBLUE);
    }

    private void openPopup(String word, RootDTO root, PatternDTO pattern) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add Word to Dictionary");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        // Read-only fields
        TextField arabicF = new TextField(word); arabicF.setEditable(false);
        Label rootLbl = new Label("Root: " + root.getRootLetters());
        Label patLbl = new Label("Pattern: " + pattern.getName());

        // Input field
        TextField urduF = new TextField(); 
        urduF.setPromptText("Enter Urdu Meaning");

        grid.add(new Label("Arabic Word:"), 0, 0); grid.add(arabicF, 1, 0);
        grid.add(new Label("Urdu Meaning:"), 0, 1); grid.add(urduF, 1, 1);
        grid.add(rootLbl, 0, 2);
        grid.add(patLbl, 1, 2);

        Button saveBtn = new Button("Save Word");
        saveBtn.setStyle(BTN_GREEN);

        saveBtn.setOnAction(e -> {
            if (urduF.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Meaning required").show();
                return;
            }
            String result = facade.addWord(word, urduF.getText(), root.getId(), pattern.getId());
            
            if (result.toLowerCase().contains("success")) {
                popup.close();
                generateAction();
            } else {
                new Alert(Alert.AlertType.ERROR, result).show();
            }
        });

        grid.add(saveBtn, 1, 3);

        Scene scene = new Scene(grid, 400, 250);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void configureRootCombo() {
        rootCombo.setButtonCell(new ListCell<RootDTO>() { 
            @Override protected void updateItem(RootDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getRootLetters());
            }
        });

        rootCombo.setCellFactory(lv -> new ListCell<RootDTO>() { 
            @Override protected void updateItem(RootDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getRootLetters());
            }
        });
    }

    private void configurePatternCombo() {
        patternCombo.setButtonCell(new ListCell<PatternDTO>() { 
            @Override protected void updateItem(PatternDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });

        patternCombo.setCellFactory(lv -> new ListCell<PatternDTO>() { 
            @Override protected void updateItem(PatternDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
    }
}