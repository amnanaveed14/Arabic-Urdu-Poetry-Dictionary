package com.dictionary.ui;

import com.dictionary.bofacade.IBOFacade;
import com.dictionary.dto.RootDTO;
import com.dictionary.dto.WordDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class PoemParsingUI {

    private final IBOFacade facade;
    
    private TextArea inputArea;
    private TextFlow outputFlow; 
    private Label statusLabel;
    
    private TitledPane glossaryPane;
    private Accordion glossaryAccordion; 

    // Styles
    private final String BG_STYLE = "-fx-background-color: #f0f7e4;";
    private final String BTN_BLUE = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_GREEN = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_BACK = "-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";

    public PoemParsingUI(IBOFacade facade) {
        this.facade = facade;
    }

    public void show(Stage stage, Stage parentStage) {
        stage.setTitle("Poem Parser");

        Button backBtn = new Button("Back");
        backBtn.setStyle(BTN_BACK);
        backBtn.setOnAction(e -> {
            stage.close();
            parentStage.show();
        });

        Label title = new Label("Poem Parser & Unknown Word Identifier");
        title.setFont(Font.font("Arial", 20));
        title.setTextFill(Color.web("#2c3e50"));

        BorderPane topPane = new BorderPane();
        topPane.setLeft(backBtn);
        topPane.setCenter(title);
        topPane.setPadding(new Insets(0,0,15,0));

        inputArea = new TextArea();
        inputArea.setPromptText("Paste your Arabic poem here...");
        inputArea.setPrefRowCount(5);
        inputArea.setWrapText(true);
        
        Button parseBtn = new Button("Parse Text");
        parseBtn.setStyle(BTN_BLUE);
        parseBtn.setPrefWidth(150);
        
        VBox inputBox = new VBox(10, new Label("Input Text:"), inputArea, parseBtn);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ccc;");

        outputFlow = new TextFlow();
        outputFlow.setPadding(new Insets(15));
        outputFlow.setStyle("-fx-background-color: white;");
        outputFlow.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
        
        ScrollPane scrollPane = new ScrollPane(outputFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: #ccc;");
        
        glossaryAccordion = new Accordion();
        glossaryPane = new TitledPane("Glossary by Root", glossaryAccordion);
        glossaryPane.setCollapsible(false);
        
        ScrollPane glossaryScroll = new ScrollPane(glossaryPane);
        glossaryScroll.setFitToWidth(true);
        glossaryScroll.setPrefHeight(250);

        statusLabel = new Label("Ready.");
        statusLabel.setTextFill(Color.DARKSLATEBLUE);

        VBox mainLayout = new VBox(15, topPane, inputBox, new Label("Result (Red = Unknown):"), scrollPane, glossaryScroll, statusLabel);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle(BG_STYLE);

        parseBtn.setOnAction(e -> parseAction());

        Scene scene = new Scene(mainLayout, 800, 750);
        stage.setScene(scene);
        stage.show();
    }

    private void parseAction() {
        outputFlow.getChildren().clear();
        glossaryAccordion.getPanes().clear(); // Reset glossary
        
        String rawText = inputArea.getText();
        if (rawText.trim().isEmpty()) return;

        // --- Data Collection for Glossary ---
        // Map to hold <Root Letters, List of Words>
        Map<String, List<WordDTO>> glossaryMap = new HashMap<>();
        int unknownCount = 0;

        // --- The Loop (Highlighting + Collecting) ---
        String[] lines = rawText.split("\\n");

        for (String line : lines) {
            // Using your preferred simple split
            String[] words = line.split("[\\s\\p{Punct}،؛؟]+"); 

            for (String word : words) {
                if (word.trim().isEmpty()) continue;

                String cleanWord = facade.normalizeText(word);
                WordDTO found = null;
                
                if (!cleanWord.isEmpty()) {
                    found = facade.searchWord(cleanWord);
                }

                Text textNode = new Text(word + " "); // Add space for readability
                textNode.setFont(Font.font("Arial", 20));

                if (found != null) {
                    // === FOUND (BLACK) ===
                    textNode.setFill(Color.BLACK);
                    
                    // >> GLOSSARY COLLECTION <<
                    // We have the word DTO, let's get the Root immediately
                    String rootStr = "Unlinked Root";
                    try {
                        if (found.getRootID() > 0) {
                            RootDTO rootDto = facade.getRoot(found.getRootID());
                            if (rootDto != null) {
                                rootStr = rootDto.getRootLetters();
                            }
                        }
                    } catch (Exception e) { /* Ignore DB errors here */ }
                    
                    // Add to map
                    glossaryMap.computeIfAbsent(rootStr, k -> new ArrayList<>()).add(found);

                } else {
                    // === NOT FOUND (RED) ===
                    textNode.setFill(Color.RED);
                    textNode.setUnderline(true);
                    textNode.setCursor(javafx.scene.Cursor.HAND);
                    
                    final String wordToPopUp = cleanWord;
                    textNode.setOnMouseClicked(e -> openAddWordPopup(wordToPopUp)); 
                    unknownCount++;
                }

                outputFlow.getChildren().add(textNode);
            }
            // Add new line to text flow
            outputFlow.getChildren().add(new Text("\n"));
        }
        
        // --- Build the Glossary UI from the collected map ---
        populateGlossary(glossaryMap);
        
        statusLabel.setText("Found " + unknownCount + " unknown words.");
    }
    
    private void populateGlossary(Map<String, List<WordDTO>> glossaryMap) {
        int total = 0;
        for (Map.Entry<String, List<WordDTO>> entry : glossaryMap.entrySet()) {
            String root = entry.getKey();
            List<WordDTO> words = entry.getValue();
            Set<String> unique = new HashSet<>(); // Avoid duplicate display
            
            VBox content = new VBox(5);
            content.setPadding(new Insets(10));
            
            for (WordDTO w : words) {
                if (unique.contains(w.getArabicWord())) continue;
                unique.add(w.getArabicWord());
                total++;
                
                Label l = new Label(w.getArabicWord() + " : " + w.getUrduMeaning());
                l.setTextFill(Color.DARKGREEN);
                l.setFont(Font.font("Arial", 14));
                content.getChildren().add(l);
            }
            
            TitledPane pane = new TitledPane("Root: " + root + " (" + unique.size() + ")", content);
            glossaryAccordion.getPanes().add(pane);
        }
        glossaryPane.setText("Glossary (" + total + " unique words)");
    }

    private void openAddWordPopup(String wordToAdd) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add Word");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        TextField arabicF = new TextField(wordToAdd);
        arabicF.setEditable(false);
        TextField urduF = new TextField();
        urduF.setPromptText("Meaning");

        ComboBox<RootDTO> rootCombo = new ComboBox<>();
        // Simple Pattern ID fallback
        int defaultPatternId = 1; 

        try {
            List<RootDTO> roots = facade.browseAllRoots();
            if(roots != null) rootCombo.getItems().addAll(roots);
        } catch(Exception e) {}

        rootCombo.setCellFactory(lv -> new ListCell<RootDTO>() {
            @Override protected void updateItem(RootDTO item, boolean empty) {
                super.updateItem(item, empty); 
                setText(empty || item == null ? null : item.getRootLetters());
            }
        });
        rootCombo.setButtonCell(new ListCell<RootDTO>() { 
             @Override protected void updateItem(RootDTO item, boolean empty) {
                super.updateItem(item, empty); 
                setText(empty || item == null ? null : item.getRootLetters());
            }
        });

        grid.add(new Label("Word:"), 0, 0); grid.add(arabicF, 1, 0);
        grid.add(new Label("Meaning:"), 0, 1); grid.add(urduF, 1, 1);
        grid.add(new Label("Root:"), 0, 2); grid.add(rootCombo, 1, 2);

        Button saveBtn = new Button("Save");
        saveBtn.setStyle(BTN_GREEN);

        saveBtn.setOnAction(e -> {
            if (urduF.getText().isEmpty() || rootCombo.getValue() == null) {
                new Alert(Alert.AlertType.ERROR, "Data missing.").show();
                return;
            }
            
            String res = facade.addWord(wordToAdd, urduF.getText(), rootCombo.getValue().getId(), defaultPatternId);
            
            if (res.toLowerCase().contains("success")) {
                popup.close();
                parseAction(); 
            } else {
                new Alert(Alert.AlertType.ERROR, res).show();
            }
        });

        grid.add(saveBtn, 1, 3);
        Scene scene = new Scene(grid, 400, 250);
        popup.setScene(scene);
        popup.showAndWait();
    }
}