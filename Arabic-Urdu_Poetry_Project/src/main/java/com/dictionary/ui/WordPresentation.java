package com.dictionary.ui;

import com.dictionary.bofacade.IBOFacade;
import com.dictionary.dto.RootDTO;
import com.dictionary.dto.WordDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.oujda_nlp_team.AlKhalil2Analyzer;
import net.oujda_nlp_team.entity.Result;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordPresentation {

    private final IBOFacade facade;
    private TextField arabicField, urduField;
    private Label statusLabel;
    private TableView<WordDTO> table;
    private ObservableList<WordDTO> tableData = FXCollections.observableArrayList();
    private ListView<String> suggestedRootsList;
    
    // Search Fields
    private TextField searchField;
    private ComboBox<String> searchTypeComboBox;
    private Button searchBtn;
    private Button clearSearchBtn;

    // Styles
    private final String BG_STYLE = "-fx-background-color: #f0f7e4;";
    private final String HEADER_STYLE = "-fx-text-fill: #2c3e50; -fx-font-size: 22px; -fx-font-weight: bold;";
    private final String BTN_GREEN = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_BLUE = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_RED = "-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_ORANGE = "-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_BACK = "-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";

    public WordPresentation(IBOFacade facade) {
        this.facade = facade;
    }

    public void show(Stage stage, Stage parentStage) {
        Label title = new Label("Word Management");
        title.setStyle(HEADER_STYLE);

        GridPane form = new GridPane();
        form.setHgap(15); form.setVgap(15); form.setPadding(new Insets(15));
        form.setAlignment(Pos.CENTER);

        arabicField = new TextField(); arabicField.setPromptText("Arabic Word...");
        urduField = new TextField();   urduField.setPromptText("Urdu Meaning...");

        form.add(new Label("Arabic Word:"), 0, 0); form.add(arabicField, 1, 0);
        form.add(new Label("Urdu Meaning:"), 0, 1); form.add(urduField, 1, 1);

        suggestedRootsList = new ListView<>();
        suggestedRootsList.setPrefHeight(80);
        form.add(new Label("Detected Roots:"), 0, 2);
        form.add(suggestedRootsList, 1, 2);

        // Buttons
        Button addBtn = new Button("Add Word"); addBtn.setStyle(BTN_GREEN);
        Button updateBtn = new Button("Update"); updateBtn.setStyle(BTN_BLUE);
        Button deleteBtn = new Button("Delete"); deleteBtn.setStyle(BTN_RED);
        Button refreshBtn = new Button("Refresh"); refreshBtn.setStyle(BTN_BLUE);
        Button segmentBtn = new Button("Segmentation"); segmentBtn.setStyle(BTN_ORANGE);
        Button lemmaBtn = new Button("Lemmatize"); lemmaBtn.setStyle(BTN_ORANGE);
        
        // BACK BUTTON
        Button backBtn = new Button("Back"); 
        backBtn.setStyle(BTN_BACK);
        backBtn.setOnAction(e -> {
            stage.close();
            parentStage.show(); 
        });

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn, segmentBtn, lemmaBtn);
        buttons.setAlignment(Pos.CENTER);

        // Top Layout
        BorderPane topPane = new BorderPane();
        topPane.setLeft(backBtn);
        topPane.setCenter(title);
        topPane.setPadding(new Insets(0,0,15,0));
        BorderPane.setAlignment(title, Pos.CENTER);

        VBox leftPane = new VBox(10, topPane, form, buttons);
        leftPane.setPadding(new Insets(15));
        
        // --- TABLE CONFIGURATION ---
        table = new TableView<>(); 
        table.setItems(tableData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<WordDTO, String> arabicCol = new TableColumn<>("Arabic Word"); 
        arabicCol.setCellValueFactory(new PropertyValueFactory<>("arabicWord"));
        arabicCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<WordDTO, String> lemmaCol = new TableColumn<>("Lemma"); 
        lemmaCol.setCellValueFactory(new PropertyValueFactory<>("lemma"));
        lemmaCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<WordDTO, String> urduCol = new TableColumn<>("Urdu Meaning"); 
        urduCol.setCellValueFactory(new PropertyValueFactory<>("urduMeaning"));
        urduCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<WordDTO, String> rootCol = new TableColumn<>("Root"); 
        rootCol.setCellValueFactory(cellData -> {
            int rootId = cellData.getValue().getRootID();
            try {
                RootDTO root = facade.getRoot(rootId);
                if(root != null) return new SimpleStringProperty(root.getRootLetters());
            } catch (Exception e) { }
            return new SimpleStringProperty("-"); 
        });
        rootCol.setStyle("-fx-alignment: CENTER;");
        
        table.getColumns().addAll(arabicCol, lemmaCol, urduCol, rootCol);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) loadIntoForm(newSel);
        });

        statusLabel = new Label("Ready"); statusLabel.setTextFill(Color.DARKSLATEBLUE);

        // --- SEARCH PANEL SETUP ---
        searchField = new TextField();
        searchField.setPromptText("Enter search query...");
        searchField.setPrefWidth(200);

        searchTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(
            "1. Exact Substring Search (Arabic)", 
            "2. Regex Search (Arabic)", 
            "3. Reverse Search (Urdu Meaning)"
        ));
        searchTypeComboBox.getSelectionModel().selectFirst();
        
        searchBtn = new Button("Search");
        searchBtn.setStyle(BTN_BLUE);
        clearSearchBtn = new Button("Clear Search");
        clearSearchBtn.setStyle(BTN_BLUE);
        
        HBox searchPanel = new HBox(10, 
            new Label("Search Type:"), 
            searchTypeComboBox, 
            new Label("Query:"),
            searchField, 
            searchBtn,
            clearSearchBtn
        );
        searchPanel.setAlignment(Pos.CENTER_LEFT);
        searchPanel.setPadding(new Insets(10, 0, 10, 0));
        
        // --- Center Layout ---
        VBox centerContent = new VBox(0, searchPanel, table);
        VBox.setVgrow(table, Priority.ALWAYS); 
        
        BorderPane rootPane = new BorderPane();
        rootPane.setTop(leftPane);
        rootPane.setCenter(centerContent); 
        rootPane.setBottom(statusLabel);
        rootPane.setStyle(BG_STYLE);
        BorderPane.setAlignment(statusLabel, Pos.CENTER_LEFT);
        rootPane.setPadding(new Insets(15));

        // Actions
        addBtn.setOnAction(e -> addWord());
        updateBtn.setOnAction(e -> updateWord());
        deleteBtn.setOnAction(e -> deleteWord(stage));
        refreshBtn.setOnAction(e -> refreshTable());
        segmentBtn.setOnAction(e -> showSegmentation(stage));
        lemmaBtn.setOnAction(e -> showLemma(stage));
        urduField.setOnAction(e -> addBtn.fire());
        arabicField.textProperty().addListener((obs, oldText, newText) -> updateSuggestedRoots(newText));

        // Search Actions
        searchBtn.setOnAction(e -> performSearch());
        clearSearchBtn.setOnAction(e -> {
            searchField.clear();
            refreshTable(); 
        });
        
        Scene scene = new Scene(rootPane, 1100, 650); 
        stage.setScene(scene);
        stage.setTitle("Word Management");
        stage.show();

        refreshTable();
    }

    // =========================================================================
    //                            SEARCH LOGIC
    // =========================================================================
    
    private void performSearch() {
        String query = safeTrim(searchField.getText());
        String searchType = searchTypeComboBox.getValue();

        if (query.isEmpty()) {
            setStatus("Please enter a search query.", true);
            return;
        }

        try {
            // Get ALL words first, then filter locally (Client-side filtering)
            // This avoids needing to change the Backend code for now.
            List<WordDTO> allWords = facade.getAllWords();
            List<WordDTO> results;

            if (searchType.contains("1. Exact Substring Search")) {
                results = allWords.stream()
                    .filter(w -> w.getArabicWord().contains(query))
                    .collect(Collectors.toList());
            } else if (searchType.contains("2. Regex Search")) {
                results = allWords.stream()
                    .filter(w -> {
                        try {
                            return Pattern.compile(query).matcher(w.getArabicWord()).find();
                        } catch (Exception e) { return false; }
                    })
                    .collect(Collectors.toList());
            } else { // Reverse Search (Urdu)
                results = allWords.stream()
                    .filter(w -> w.getUrduMeaning().contains(query))
                    .collect(Collectors.toList());
            }

            if (results != null) {
                tableData.clear();
                tableData.addAll(results);
                setStatus("Found " + results.size() + " word(s).", false);
            } 

        } catch (Exception ex) {
            setStatus("Search Error: " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }

    // --- LOGIC METHODS ---

    private void addWord() {
        String arabic = safeTrim(arabicField.getText());
        String urdu = safeTrim(urduField.getText());

        if (arabic.isEmpty() || urdu.isEmpty()) {
            setStatus("Error: Arabic & Urdu required.", true);
            return;
        }

        setStatus("Auto-detecting...", false);
        String res = facade.addWordWithSuggestedRoot(arabic, urdu);
        boolean success = res.toLowerCase().contains("success") || res.toLowerCase().contains("saved");
        setStatus(res, !success);
        if (success) { clearInputs(); refreshTable(); }
    }

    private void refreshTable() {
        tableData.clear();
        try {
            List<WordDTO> words = facade.getAllWords();
            if (words != null) tableData.addAll(words);
            setStatus("Loaded " + tableData.size() + " words.", false);
        } catch (Exception ex) { setStatus("Error loading: " + ex.getMessage(), true); }
    }

    private void updateSuggestedRoots(String word) {
        suggestedRootsList.getItems().clear();
        if (word == null || word.isEmpty()) return;
        try {
            List<Result> results = AlKhalil2Analyzer.getInstance().analyzerToken(word);            
            Set<String> rootsSet = new HashSet<>();
            if (results != null) {
                for (Result r : results) {
                    if (r.getRoot() != null && !r.getRoot().equals("-")) rootsSet.add(r.getRoot());
                }
            }
            suggestedRootsList.getItems().addAll(rootsSet);
        } catch (Exception e) { }
    }

    private void showSegmentation(Stage stage) {
        String arabic = getArabicWordFromFormOrSelection();
        if (arabic == null) return;
        String result = facade.getSegmentation(arabic);
        showPopup(stage, "Word Segmentation", "Morphology", result);
    }

    private void showLemma(Stage stage) {
        String arabic = getArabicWordFromFormOrSelection();
        if (arabic == null) return;
        String result = facade.getLemma(arabic);
        showPopup(stage, "Word Lemmatization", "Lemma Result", result);
    }

    private void updateWord() {
        String arabic = safeTrim(arabicField.getText());
        String newMeaning = safeTrim(urduField.getText());
        if (arabic.isEmpty()) { setStatus("Enter Arabic to update.", true); return; }
        
        WordDTO found = facade.searchWord(arabic);
        if (found == null) { setStatus("Word not found.", true); return; }
        
        String result = facade.updateWord(found.getWordID(), newMeaning);
        boolean isSuccess = result.toLowerCase().contains("success") || result.toLowerCase().contains("updated");
        
        setStatus(result, !isSuccess);
        if (isSuccess) { clearInputs(); refreshTable(); }
    }

    private void deleteWord(Stage stage) {
        String arabic = safeTrim(arabicField.getText());
        if (arabic.isEmpty()) { setStatus("Enter Arabic to delete.", true); return; }
        
        WordDTO found = facade.searchWord(arabic);
        if (found == null) { setStatus("Word not found.", true); return; }
        
        String result = facade.deleteWord(found.getWordID());
        boolean isSuccess = result.toLowerCase().contains("success") || result.toLowerCase().contains("deleted");
        
        setStatus(result, !isSuccess);
        if (isSuccess) { clearInputs(); refreshTable(); }
    }

    private String getArabicWordFromFormOrSelection() {
        String arabic = safeTrim(arabicField.getText());
        if (arabic.isEmpty()) {
            WordDTO selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                arabic = selected.getArabicWord();
                arabicField.setText(arabic);
            } else {
                setStatus("Select or enter word first.", true);
                return null;
            }
        }
        return arabic;
    }

    private void loadIntoForm(WordDTO w) {
        if (w == null) return;
        arabicField.setText(safeString(w.getArabicWord()));
        urduField.setText(safeString(w.getUrduMeaning()));
        setStatus("Selected: " + w.getArabicWord(), false);
    }

    private void clearInputs() {
        arabicField.clear(); urduField.clear(); arabicField.requestFocus();
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isError ? Color.CRIMSON : Color.DARKGREEN);
    }
    
    private void showPopup(Stage owner, String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(owner); alert.setTitle(title); alert.setHeaderText(header); alert.setContentText(content);
        alert.showAndWait();
    }
    
    private String safeTrim(String s) { return s == null ? "" : s.trim(); }
    private String safeString(String s) { return s == null ? "" : s; }
}