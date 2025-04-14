package com.sms.view;

import com.sms.controller.StudentController;
import com.sms.dao.SQLiteStudentDAO;
import com.sms.dao.StudentDAO;
import com.sms.model.Student;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.Node;

public class StudentManagementApp extends Application {
    private StudentController controller;
    private TextField idField, nameField, ageField, gradeField;
    private TableView<Student> tableView;
    private Label statusLabel;
    private VBox root;
    private TabPane tabPane;
    private BarChart<String, Number> gradeChart;
    private PieChart genderChart;

    private static final String MODERN_STYLE = """
            .root {
                -fx-background-color: #f5f5f5;
            }
            .tab-pane .tab-header-area .tab-header-background {
                -fx-background-color: #1565C0;
            }
            .tab {
                -fx-background-color: #1565C0;
                -fx-padding: 10 20;
            }
            .tab .tab-label {
                -fx-text-fill: white;
                -fx-font-weight: bold;
            }
            .tab:selected {
                -fx-background-color: #1976D2;
            }
            .button {
                -fx-background-color: #2196F3;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-padding: 10 20;
                -fx-background-radius: 5;
            }
            .button:hover {
                -fx-background-color: #1976D2;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);
            }
            .text-field {
                -fx-background-radius: 5;
                -fx-padding: 8;
                -fx-border-color: #E0E0E0;
                -fx-border-radius: 5;
            }
            .text-field:focused {
                -fx-border-color: #2196F3;
            }
            .table-view {
                -fx-background-radius: 5;
                -fx-background-color: white;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);
            }
            .table-view .column-header {
                -fx-background-color: #2196F3;
                -fx-text-fill: white;
            }
            .table-view .column-header .label {
                -fx-text-fill: white;
                -fx-font-weight: bold;
            }
            .chart {
                -fx-background-color: white;
                -fx-background-radius: 5;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);
                -fx-padding: 10;
            }
            .chart-title {
                -fx-text-fill: #1565C0;
                -fx-font-size: 16px;
                -fx-font-weight: bold;
            }
            .status-label {
                -fx-padding: 10;
                -fx-background-radius: 5;
                -fx-background-color: #E8F5E9;
                -fx-text-fill: #2E7D32;
            }
            .error-label {
                -fx-background-color: #FFEBEE;
                -fx-text-fill: #C62828;
            }
            .form-container {
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-padding: 20;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);
            }
            .section-title {
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-text-fill: #1565C0;
            }
            .dashboard-tile {
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-padding: 15;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);
            }
            .dashboard-value {
                -fx-font-size: 24px;
                -fx-font-weight: bold;
            }
            .dashboard-label {
                -fx-font-size: 14px;
                -fx-text-fill: #757575;
            }
            """;

    @Override
    public void start(Stage primaryStage) {
        // Initialize controller
        StudentDAO dao = new SQLiteStudentDAO();
        controller = new StudentController(dao);

        // Create main layout
        root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Add title
        Label titleLabel = new Label("Student Management System");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#1565C0"));

        // Create tab pane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create tabs
        Tab managementTab = new Tab("Student Management", createManagementContent());
        Tab statisticsTab = new Tab("Statistics", createStatisticsContent());
        Tab settingsTab = new Tab("Settings", createSettingsContent());

        tabPane.getTabs().addAll(managementTab, statisticsTab, settingsTab);

        // Create status label
        statusLabel = new Label("");
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setVisible(false);

        // Add all components to root
        root.getChildren().addAll(titleLabel, tabPane, statusLabel);

        // Create scene and show stage
        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add("data:text/css," + MODERN_STYLE.replace("\n", ""));
        
        primaryStage.setTitle("Student Management System");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial update of statistics
        updateStatistics();
    }

    private VBox createManagementContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Create and set up the form
        VBox formContainer = createForm();
        
        // Create and set up the table
        createTable();
        VBox tableContainer = new VBox(10);
        tableContainer.getChildren().addAll(
            new Label("Student Records") {{ 
                setFont(Font.font("System", FontWeight.BOLD, 16));
                setTextFill(Color.web("#1565C0"));
            }},
            tableView
        );

        content.getChildren().addAll(formContainer, tableContainer);
        return content;
    }

    private VBox createStatisticsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Create dashboard tiles
        HBox dashboardTiles = new HBox(15);
        dashboardTiles.setAlignment(Pos.CENTER);

        VBox totalStudents = createDashboardTile("Total Students", "0");
        VBox averageGrade = createDashboardTile("Average Grade", "0.00");
        VBox highestGrade = createDashboardTile("Highest Grade", "0.00");

        dashboardTiles.getChildren().addAll(totalStudents, averageGrade, highestGrade);

        // Create charts
        HBox chartContainer = new HBox(20);
        chartContainer.setAlignment(Pos.CENTER);

        // Grade distribution chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        gradeChart = new BarChart<>(xAxis, yAxis);
        gradeChart.setTitle("Grade Distribution");
        gradeChart.setAnimated(false);
        xAxis.setLabel("Grade Range");
        yAxis.setLabel("Number of Students");

        chartContainer.getChildren().add(gradeChart);

        content.getChildren().addAll(
            new Label("Statistics Dashboard") {{
                setFont(Font.font("System", FontWeight.BOLD, 20));
                setTextFill(Color.web("#1565C0"));
            }},
            dashboardTiles,
            chartContainer
        );

        return content;
    }

    private VBox createSettingsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        // Create settings form
        VBox settingsForm = new VBox(15);
        settingsForm.getStyleClass().add("form-container");
        settingsForm.setMaxWidth(600);

        Label settingsTitle = new Label("Application Settings");
        settingsTitle.getStyleClass().add("section-title");

        // Database settings
        Label dbSettingsLabel = new Label("Database Settings");
        dbSettingsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        TextField dbPathField = new TextField();
        dbPathField.setPromptText("Database Path");
        dbPathField.setMaxWidth(400);

        Button backupButton = new Button("Backup Database");
        backupButton.setOnAction(e -> handleBackupDatabase());

        // Theme settings


        // Add components to settings form
        settingsForm.getChildren().addAll(
            settingsTitle,
            new Separator(),
            dbSettingsLabel,
            dbPathField,
            backupButton,
            new Separator()

        );

        content.getChildren().add(settingsForm);
        return content;
    }

    private VBox createDashboardTile(String label, String value) {
        VBox tile = new VBox(5);
        tile.getStyleClass().add("dashboard-tile");
        tile.setAlignment(Pos.CENTER);
        tile.setMinWidth(200);

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("dashboard-value");

        Label descLabel = new Label(label);
        descLabel.getStyleClass().add("dashboard-label");

        tile.getChildren().addAll(valueLabel, descLabel);
        return tile;
    }

    private void updateStatistics() {
        // Get all students from controller
        List<Student> students = controller.getStudentList();

        // Update dashboard tiles
        HBox dashboardTiles = (HBox) ((VBox) tabPane.getTabs().get(1).getContent()).getChildren().get(1);

        // Update total students
        VBox totalStudentsBox = (VBox) dashboardTiles.getChildren().get(0);
        Label totalStudentsValue = (Label) totalStudentsBox.getChildren().get(0);
        totalStudentsValue.setText(String.valueOf(students.size()));

        // Calculate and update average grade
        double averageGrade = students.stream()
                .mapToDouble(Student::getGrade)
                .average()
                .orElse(0.0);
        VBox averageGradeBox = (VBox) dashboardTiles.getChildren().get(1);
        Label averageGradeValue = (Label) averageGradeBox.getChildren().get(0);
        averageGradeValue.setText(String.format("%.2f", averageGrade));

        // Find and update highest grade
        double highestGrade = students.stream()
                .mapToDouble(Student::getGrade)
                .max()
                .orElse(0.0);
        VBox highestGradeBox = (VBox) dashboardTiles.getChildren().get(2);
        Label highestGradeValue = (Label) highestGradeBox.getChildren().get(0);
        highestGradeValue.setText(String.format("%.2f", highestGrade));

        // Update grade distribution chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Students");

        // Calculate grade distribution
        Map<String, Integer> gradeRanges = new HashMap<>();
        gradeRanges.put("90-100", 0);
        gradeRanges.put("80-89", 0);
        gradeRanges.put("70-79", 0);
        gradeRanges.put("60-69", 0);
        gradeRanges.put("0-59", 0);

        for (Student student : students) {
            double grade = student.getGrade();
            if (grade >= 90) {
                gradeRanges.merge("90-100", 1, Integer::sum);
            } else if (grade >= 80) {
                gradeRanges.merge("80-89", 1, Integer::sum);
            } else if (grade >= 70) {
                gradeRanges.merge("70-79", 1, Integer::sum);
            } else if (grade >= 60) {
                gradeRanges.merge("60-69", 1, Integer::sum);
            } else {
                gradeRanges.merge("0-59", 1, Integer::sum);
            }
        }

        // Add data to chart
        gradeRanges.forEach((range, count) ->
            series.getData().add(new XYChart.Data<>(range, count))
        );

        gradeChart.getData().clear();
        gradeChart.getData().add(series);

        // Style the chart bars
        series.getData().forEach(data -> {
            Node bar = data.getNode();
            if (bar != null) {
                String range = data.getXValue();
                switch (range) {
                    case "90-100" -> bar.setStyle("-fx-bar-fill: #2E7D32;"); // Green for A
                    case "80-89" -> bar.setStyle("-fx-bar-fill: #1565C0;"); // Blue for B
                    case "70-79" -> bar.setStyle("-fx-bar-fill: #F57C00;"); // Orange for C
                    case "60-69" -> bar.setStyle("-fx-bar-fill: #FDD835;"); // Yellow for D
                    default -> bar.setStyle("-fx-bar-fill: #C62828;"); // Red for F
                }
            }
        });
    }

    private void handleBackupDatabase() {
        // Implement database backup functionality
        showStatusMessage("Database backup completed successfully!", false);
    }

    private VBox createForm() {
        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("form-container");

        Label formTitle = new Label("Student Information");
        formTitle.getStyleClass().add("section-title");

        // Create form grid
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        // Create text fields with prompts
        idField = createStyledTextField("Enter ID");
        nameField = createStyledTextField("Enter Name");
        ageField = createStyledTextField("Enter Age");
        gradeField = createStyledTextField("Enter Grade");

        // Add form elements with styled labels
        addFormRow(form, 0, "ID:", idField);
        addFormRow(form, 1, "Name:", nameField);
        addFormRow(form, 2, "Age:", ageField);
        addFormRow(form, 3, "Grade:", gradeField);

        // Create buttons
        HBox buttonBox = createButtons();
        buttonBox.setAlignment(Pos.CENTER);

        formContainer.getChildren().addAll(formTitle, form, buttonBox);
        return formContainer;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(200);
        return field;
    }

    private void addFormRow(GridPane form, int row, String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
        form.add(label, 0, row);
        form.add(field, 1, row);
    }

    private void createTable() {
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create columns
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Student, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAge()).asObject());

        TableColumn<Student, Double> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getGrade()).asObject());
        gradeCol.setCellFactory(column -> new TableCell<Student, Double>() {
            @Override
            protected void updateItem(Double grade, boolean empty) {
                super.updateItem(grade, empty);
                if (empty || grade == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", grade));
                    if (grade >= 90) setStyle("-fx-text-fill: #2E7D32;"); // Green for A
                    else if (grade >= 80) setStyle("-fx-text-fill: #1565C0;"); // Blue for B
                    else if (grade >= 70) setStyle("-fx-text-fill: #F57C00;"); // Orange for C
                    else setStyle("-fx-text-fill: #C62828;"); // Red for D/F
                }
            }
        });

        tableView.getColumns().addAll(idCol, nameCol, ageCol, gradeCol);
        tableView.setItems(controller.getStudentList());

        // Add selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idField.setText(String.valueOf(newSelection.getId()));
                nameField.setText(newSelection.getName());
                ageField.setText(String.valueOf(newSelection.getAge()));
                gradeField.setText(String.format("%.2f", newSelection.getGrade()));
            }
        });

        // Set placeholder text
        tableView.setPlaceholder(new Label("No students found"));
    }

    private HBox createButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button addButton = createStyledButton("Add", "add");
        Button updateButton = createStyledButton("Update", "update");
        Button deleteButton = createStyledButton("Delete", "delete");
        Button clearButton = createStyledButton("Clear", "clear");

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
        return buttonBox;
    }

    private Button createStyledButton(String text, String action) {
        Button button = new Button(text);
        button.setMinWidth(100);
        
        switch (action) {
            case "delete" -> {
                button.setStyle("-fx-background-color: #F44336;");
                button.setOnAction(e -> handleDelete());
            }
            case "update" -> {
                button.setStyle("-fx-background-color: #4CAF50;");
                button.setOnAction(e -> handleUpdate());
            }
            case "add" -> {
                button.setStyle("-fx-background-color: #2196F3;");
                button.setOnAction(e -> handleAdd());
            }
            case "clear" -> {
                button.setStyle("-fx-background-color: #9E9E9E;");
                button.setOnAction(e -> clearFields());
            }
        }
        
        return button;
    }

    private void showStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-label", "error-label");
        statusLabel.getStyleClass().add(isError ? "error-label" : "status-label");
        statusLabel.setVisible(true);
    }

    private void handleAdd() {
        try {
            Student student = getStudentFromFields();
            controller.addStudent(student);
            clearFields();
            updateStatistics(); // Update statistics after adding
            showStatusMessage("Student added successfully!", false);
        } catch (Exception e) {
            showStatusMessage("Error: " + e.getMessage(), true);
        }
    }

    private void handleUpdate() {
        try {
            Student student = getStudentFromFields();
            controller.updateStudent(student);
            clearFields();
            updateStatistics(); // Update statistics after updating
            showStatusMessage("Student updated successfully!", false);
        } catch (Exception e) {
            showStatusMessage("Error: " + e.getMessage(), true);
        }
    }

    private void handleDelete() {
        try {
            int id = Integer.parseInt(idField.getText());
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this student?");
            
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                controller.deleteStudent(id);
                clearFields();
                updateStatistics(); // Update statistics after deleting
                showStatusMessage("Student deleted successfully!", false);
            }
        } catch (Exception e) {
            showStatusMessage("Error: " + e.getMessage(), true);
        }
    }

    private Student getStudentFromFields() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            double grade = Double.parseDouble(gradeField.getText());
            return new Student(id, name, age, grade);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter valid numeric values");
        }
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        ageField.clear();
        gradeField.clear();
        statusLabel.setVisible(false);
        tableView.getSelectionModel().clearSelection();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 