package com.dictionary.ui;

import com.dictionary.bofacade.IBOFacade;
import com.dictionary.dto.RootDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class RootManagementUI {

    private final IBOFacade facade;
    
    // idField is kept in memory to track selection, but won't be added to the UI layout
    private TextField idField; 
    private TextField rootLettersField;
    private TableView<RootRow> rootTable;
    private ObservableList<RootRow> rootData = FXCollections.observableArrayList();

    // Styles
    private final String BG_STYLE = "-fx-background-color: #f0f7e4;";
    private final String HEADER_STYLE = "-fx-text-fill: #2c3e50; -fx-font-size: 22px; -fx-font-weight: bold;";
    private final String BTN_GREEN = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_BLUE = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_RED = "-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_BACK = "-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";

    public RootManagementUI(IBOFacade facade) {
        this.facade = facade;
    }

    public void show(Stage stage, Stage parentStage) {
        stage.setTitle("Root Management");

        Label title = new Label("Root Management");
        title.setStyle(HEADER_STYLE);
        
        // Back Button
        Button backBtn = new Button("Back");
        backBtn.setStyle(BTN_BACK);
        backBtn.setOnAction(e -> {
            stage.close();
            parentStage.show();
        });
        
        BorderPane topPane = new BorderPane();
        topPane.setLeft(backBtn);
        topPane.setCenter(title);
        topPane.setPadding(new Insets(0,0,10,0));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle(BG_STYLE);
        
        root.setTop(new VBox(10, topPane, createFormPane()));
        root.setCenter(createTablePane());
        root.setBottom(createButtonPane());

        refreshRootList();

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createFormPane() {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10); formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));
        formGrid.setAlignment(Pos.CENTER);
        
        // Initialize hidden ID field for logic
        idField = new TextField(); 
        
        // Only adding the Root Letters field to the UI
        formGrid.add(new Label("Letters:"), 0, 0);
        rootLettersField = new TextField(); 
        rootLettersField.setAlignment(Pos.CENTER_RIGHT);
        formGrid.add(rootLettersField, 1, 0);
        
        return new VBox(formGrid);
    }

    private TableView<RootRow> createTablePane() {
        rootTable = new TableView<>();
        rootTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Only creating the Letters Column
        TableColumn<RootRow, String> lettersCol = new TableColumn<>("Root Letters");
        lettersCol.setCellValueFactory(new PropertyValueFactory<>("rootLetters"));
        lettersCol.setStyle("-fx-alignment: CENTER;");
        
        rootTable.getColumns().add(lettersCol);
        rootTable.setItems(rootData);

        // Selection Listener updates the hidden ID field and visible Letters field
        rootTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idField.setText(newSel.getId().toString());
                rootLettersField.setText(newSel.getRootLetters());
            }
        });
        return rootTable;
    }

    private HBox createButtonPane() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));

        Button addBtn = new Button("Add"); addBtn.setStyle(BTN_GREEN);
        Button updateBtn = new Button("Update"); updateBtn.setStyle(BTN_BLUE);
        Button deleteBtn = new Button("Delete"); deleteBtn.setStyle(BTN_RED);
        Button refreshBtn = new Button("Refresh"); refreshBtn.setStyle(BTN_BLUE);

        addBtn.setOnAction(e -> addRootAction());
        updateBtn.setOnAction(e -> updateRootAction());
        deleteBtn.setOnAction(e -> deleteRootAction());
        refreshBtn.setOnAction(e -> refreshRootList());

        box.getChildren().addAll(addBtn, updateBtn, deleteBtn, refreshBtn);
        return box;
    }

    private void addRootAction() {
        String letters = rootLettersField.getText().trim();
        if (letters.isEmpty()) { return; }
        try {
            facade.addRoot(letters);
            refreshRootList(); rootLettersField.clear();
        } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
    }

    private void updateRootAction() {
        String idText = idField.getText();
        String letters = rootLettersField.getText().trim();
        if (idText.isEmpty() || letters.isEmpty()) return;
        try {
            facade.updateRootLetters(Integer.parseInt(idText), letters);
            refreshRootList(); idField.clear(); rootLettersField.clear();
        } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
    }

    private void deleteRootAction() {
        String idText = idField.getText();
        if (idText.isEmpty()) return;
        try {
            facade.deleteRoot(Integer.parseInt(idText));
            refreshRootList(); idField.clear(); rootLettersField.clear();
        } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
    }

    private void refreshRootList() {
        rootData.clear();
        try {
            List<RootDTO> roots = facade.browseAllRoots();
            for (RootDTO r : roots) rootData.add(new RootRow(r.getId(), r.getRootLetters()));
        } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait();
    }

    public static class RootRow {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty rootLetters;
        public RootRow(int id, String letters) {
            this.id = new SimpleIntegerProperty(id);
            this.rootLetters = new SimpleStringProperty(letters);
        }
        public Integer getId() { return id.get(); }
        public String getRootLetters() { return rootLetters.get(); }
    }
}