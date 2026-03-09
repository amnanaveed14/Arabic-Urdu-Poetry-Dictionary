package com.dictionary.ui;

import com.dictionary.bofacade.IBOFacade;
import com.dictionary.dto.PatternDTO;
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

import java.util.List;

public class BrowsingUI {

    private final IBOFacade facade;
    private TableView<WordDTO> table;
    private ObservableList<WordDTO> tableData = FXCollections.observableArrayList();
    
    private ComboBox<String> filterTypeCombo;
    private ComboBox<String> selectionCombo; // Dynamic content based on filter type
    private Label statusLabel;

    // Cache lists to map names back to IDs
    private List<RootDTO> allRoots;
    private List<PatternDTO> allPatterns;

    // Styles
    private final String BG_STYLE = "-fx-background-color: #f0f7e4;";
    private final String HEADER_STYLE = "-fx-text-fill: #2c3e50; -fx-font-size: 22px; -fx-font-weight: bold;";
    private final String BTN_BLUE = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_BACK = "-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";

    public BrowsingUI(IBOFacade facade) {
        this.facade = facade;
    }

    public void show(Stage stage, Stage parentStage) {
        stage.setTitle("Dictionary Browser");

        // 1. Top Bar (Back + Title)
        Button backBtn = new Button("Back");
        backBtn.setStyle(BTN_BACK);
        backBtn.setOnAction(e -> {
            stage.close();
            parentStage.show();
        });

        Label title = new Label("Browse Dictionary");
        title.setStyle(HEADER_STYLE);

        BorderPane topPane = new BorderPane();
        topPane.setLeft(backBtn);
        topPane.setCenter(title);
        topPane.setPadding(new Insets(0, 0, 15, 0));

        // 2. Filter Controls
        GridPane filterPane = new GridPane();
        filterPane.setHgap(10); filterPane.setVgap(10);
        filterPane.setAlignment(Pos.CENTER);
        filterPane.setPadding(new Insets(10));

        filterPane.add(new Label("Browse By:"), 0, 0);
        filterTypeCombo = new ComboBox<>();
        filterTypeCombo.getItems().addAll("Root", "Pattern", "Lemma");
        filterTypeCombo.getSelectionModel().selectFirst();
        filterTypeCombo.setPrefWidth(150);
        filterPane.add(filterTypeCombo, 1, 0);

        filterPane.add(new Label("Select Value:"), 2, 0);
        selectionCombo = new ComboBox<>();
        selectionCombo.setPrefWidth(250);
        filterPane.add(selectionCombo, 3, 0);

        Button searchBtn = new Button("Search");
        searchBtn.setStyle(BTN_BLUE);
        filterPane.add(searchBtn, 4, 0);

        // 3. Table
        table = new TableView<>();
        table.setItems(tableData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Arabic Column
        TableColumn<WordDTO, String> arabicCol = new TableColumn<>("Arabic");
        arabicCol.setCellValueFactory(new PropertyValueFactory<>("arabicWord"));
        arabicCol.setStyle("-fx-alignment: CENTER;");

        // Lemma Column
        TableColumn<WordDTO, String> lemmaCol = new TableColumn<>("Lemma");
        lemmaCol.setCellValueFactory(new PropertyValueFactory<>("lemma"));
        lemmaCol.setStyle("-fx-alignment: CENTER;");

        // Urdu Column
        TableColumn<WordDTO, String> urduCol = new TableColumn<>("Urdu");
        urduCol.setCellValueFactory(new PropertyValueFactory<>("urduMeaning"));
        urduCol.setStyle("-fx-alignment: CENTER;");

        // Root Column (Shows Letters, NOT ID)
        TableColumn<WordDTO, String> rootCol = new TableColumn<>("Root");
        rootCol.setCellValueFactory(cellData -> {
            try {
                RootDTO r = facade.getRoot(cellData.getValue().getRootID());
                return new SimpleStringProperty(r != null ? r.getRootLetters() : "-");
            } catch (Exception e) { return new SimpleStringProperty("-"); }
        });
        rootCol.setStyle("-fx-alignment: CENTER;");
        
        // Pattern Column (Shows Name, NOT ID)
        TableColumn<WordDTO, String> patCol = new TableColumn<>("Pattern");
        patCol.setCellValueFactory(cellData -> {
            try {
                PatternDTO p = facade.getPatternById(cellData.getValue().getPatternID());
                return new SimpleStringProperty(p != null ? p.getName() : "-");
            } catch (Exception e) { return new SimpleStringProperty("-"); }
        });
        patCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(arabicCol, lemmaCol, urduCol, rootCol, patCol);

        // 4. Status Label
        statusLabel = new Label("Select a category and click Search.");
        statusLabel.setTextFill(Color.DARKSLATEBLUE);

        // Layout Assembly
        VBox centerBox = new VBox(10, filterPane, table);
        
        BorderPane root = new BorderPane();
        root.setTop(topPane);
        root.setCenter(centerBox);
        root.setBottom(statusLabel);
        root.setPadding(new Insets(15));
        root.setStyle(BG_STYLE);
        BorderPane.setAlignment(statusLabel, Pos.CENTER_LEFT);

        // --- Logic Hooks ---
        
        // When Filter Type changes, reload the Selection Combo
        filterTypeCombo.setOnAction(e -> loadSelectionOptions());
        
        // When Search clicked, fetch data
        searchBtn.setOnAction(e -> performSearch());

        // Initial Load
        loadDataFromDB(); // Pre-fetch roots/patterns to cache
        loadSelectionOptions();

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void loadDataFromDB() {
        try {
            allRoots = facade.browseAllRoots();
            allPatterns = facade.listAllPatterns();
        } catch (Exception e) {
            statusLabel.setText("Error loading meta-data: " + e.getMessage());
        }
    }

    private void loadSelectionOptions() {
        selectionCombo.getItems().clear();
        String type = filterTypeCombo.getValue();
        
        try {
            if ("Root".equals(type)) {
                if (allRoots != null) {
                    for (RootDTO r : allRoots) selectionCombo.getItems().add(r.getRootLetters());
                }
            } else if ("Pattern".equals(type)) {
                if (allPatterns != null) {
                    for (PatternDTO p : allPatterns) selectionCombo.getItems().add(p.getName());
                }
            } else if ("Lemma".equals(type)) {
                List<String> lemmas = facade.getAllDistinctLemmas();
                if (lemmas != null) selectionCombo.getItems().addAll(lemmas);
            }
            
            if (!selectionCombo.getItems().isEmpty()) {
                selectionCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            statusLabel.setText("Error populating lists.");
        }
    }

    private void performSearch() {
        String type = filterTypeCombo.getValue();
        String selectedValue = selectionCombo.getValue();
        
        if (selectedValue == null || selectedValue.isEmpty()) return;

        tableData.clear();
        List<WordDTO> results = null;

        try {
            if ("Root".equals(type)) {
                // Find ID for selected Root string
                int id = allRoots.stream()
                        .filter(r -> r.getRootLetters().equals(selectedValue))
                        .findFirst().map(RootDTO::getId).orElse(-1);
                if(id != -1) results = facade.getWordsByRootID(id);
                
            } else if ("Pattern".equals(type)) {
                // Find ID for selected Pattern string
                int id = allPatterns.stream()
                        .filter(p -> p.getName().equals(selectedValue))
                        .findFirst().map(PatternDTO::getId).orElse(-1);
                if(id != -1) results = facade.getWordsByPatternID(id);
                
            } else if ("Lemma".equals(type)) {
                results = facade.getWordsByLemma(selectedValue);
            }

            if (results != null && !results.isEmpty()) {
                tableData.addAll(results);
                statusLabel.setText("Found " + results.size() + " words for " + type + ": " + selectedValue);
            } else {
                statusLabel.setText("No words found.");
            }
        } catch (Exception e) {
            statusLabel.setText("Search error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}