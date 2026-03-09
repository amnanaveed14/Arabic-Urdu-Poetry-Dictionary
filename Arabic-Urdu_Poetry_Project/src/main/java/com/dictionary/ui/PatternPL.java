package com.dictionary.ui;

import com.dictionary.bofacade.IBOFacade;
import com.dictionary.dto.PatternDTO;
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

public class PatternPL {

    private final IBOFacade facade;
    
    // idField is kept for internal logic (tracking selection) but not added to UI
    private TextField idField; 
    private TextField nameField, templateField;
    private TextArea descArea;
    private TableView<PatternDTO> table;
    private ObservableList<PatternDTO> tableData = FXCollections.observableArrayList();

    // Styles
    private final String BG_STYLE = "-fx-background-color: #f0f7e4;";
    private final String HEADER_STYLE = "-fx-text-fill: #2c3e50; -fx-font-size: 22px; -fx-font-weight: bold;";
    private final String BTN_GREEN = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_BLUE = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_RED = "-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    private final String BTN_BACK = "-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";

    public PatternPL(IBOFacade facade) {
        this.facade = facade;
    }

    public void show(Stage stage, Stage parentStage) {
        stage.setTitle("Pattern Management");

        // Header & Back Button
        Label title = new Label("Pattern Management");
        title.setStyle(HEADER_STYLE);
        
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

        // Form
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(10));
        form.setAlignment(Pos.CENTER);

        // Initialize hidden ID field
        idField = new TextField(); 

        // Name Field
        form.add(new Label("Name:"), 0, 0);
        nameField = new TextField();
        form.add(nameField, 1, 0);

        // Template Field
        form.add(new Label("Template:"), 0, 1);
        templateField = new TextField();
        form.add(templateField, 1, 1);

        // Description Field
        form.add(new Label("Description:"), 0, 2);
        descArea = new TextArea(); 
        descArea.setPrefRowCount(2); 
        descArea.setPrefWidth(200);
        form.add(descArea, 1, 2);

        // Buttons
        Button addBtn = new Button("Add"); addBtn.setStyle(BTN_GREEN);
        Button updateBtn = new Button("Update"); updateBtn.setStyle(BTN_BLUE);
        Button deleteBtn = new Button("Delete"); deleteBtn.setStyle(BTN_RED);
        Button refreshBtn = new Button("Refresh"); refreshBtn.setStyle(BTN_BLUE);

        HBox btnBox = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        // Table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Removed ID Column from display
        
        TableColumn<PatternDTO, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PatternDTO, String> tempCol = new TableColumn<>("Template");
        tempCol.setCellValueFactory(new PropertyValueFactory<>("template"));
        tempCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PatternDTO, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(nameCol, tempCol, descCol);
        table.setItems(tableData);

        // Selection Listener - Updates the invisible idField so logic works
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                idField.setText(String.valueOf(newVal.getId()));
                nameField.setText(newVal.getName());
                templateField.setText(newVal.getTemplate());
                descArea.setText(newVal.getDescription());
            }
        });

        // Root Layout
        VBox centerLayout = new VBox(10, form, btnBox, table);
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle(BG_STYLE);
        root.setTop(topPane);
        root.setCenter(centerLayout);

        // Actions
        addBtn.setOnAction(e -> addAction());
        updateBtn.setOnAction(e -> updateAction());
        deleteBtn.setOnAction(e -> deleteAction());
        refreshBtn.setOnAction(e -> refreshTable());

        // Initial Load
        try { facade.ensurePatternTable(); refreshTable(); } catch(Exception e) {}

        Scene scene = new Scene(root, 750, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void addAction() {
        String name = nameField.getText();
        String temp = templateField.getText();
        String desc = descArea.getText();
        if(name.isEmpty() || temp.isEmpty()) { showAlert("Error", "Name and Template required."); return; }
        try {
            facade.createPattern(name, temp, desc);
            refreshTable(); clearInputs();
        } catch(Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void updateAction() {
        if(idField.getText().isEmpty()) return;
        try {
            int id = Integer.parseInt(idField.getText());
            facade.updatePattern(id, nameField.getText(), templateField.getText(), descArea.getText());
            refreshTable(); clearInputs();
        } catch(Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void deleteAction() {
        if(idField.getText().isEmpty()) return;
        try {
            int id = Integer.parseInt(idField.getText());
            facade.deletePattern(id);
            refreshTable(); clearInputs();
        } catch(Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void refreshTable() {
        tableData.clear();
        try {
            List<PatternDTO> list = facade.listAllPatterns();
            if(list != null) tableData.addAll(list);
        } catch(Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void clearInputs() {
        idField.clear(); nameField.clear(); templateField.clear(); descArea.clear();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}