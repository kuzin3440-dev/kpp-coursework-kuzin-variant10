package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;

public class MainApp extends Application {
    private final HttpChargingServiceClient api = new HttpChargingServiceClient();

    private final TableView<Station> stationTable = new TableView<>();
    private final TableView<ChargingModule> moduleTable = new TableView<>();
    private final TableView<Incident> incidentTable = new TableView<>();
    private final TableView<RepairTask> taskTable = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Станції", buildStationsTab()),
                new Tab("Зарядні модулі", buildModulesTab()),
                new Tab("Інциденти", buildIncidentsTab()),
                new Tab("Ремонтні завдання", buildTasksTab())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));

        Scene scene = new Scene(tabs, 1020, 660);
        stage.setTitle("Обслуговування зарядних станцій електромобілів (Клієнт)");
        stage.setScene(scene);
        stage.show();

        refreshAll();
    }

    private VBox buildStationsTab() {
        TableColumn<Station, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Station, String> colCode = new TableColumn<>("Код");
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Station, String> colName = new TableColumn<>("Назва");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Station, String> colCity = new TableColumn<>("Місто");
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));

        stationTable.getColumns().setAll(List.of(colId, colCode, colName, colCity));
        stationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField tfCode = new TextField();
        TextField tfName = new TextField();
        TextField tfCity = new TextField();

        stationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                tfCode.setText(sel.getCode());
                tfName.setText(sel.getName());
                tfCity.setText(sel.getCity());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Код:"), tfCode);
        form.addRow(1, new Label("Назва:"), tfName);
        form.addRow(2, new Label("Місто:"), tfCity);
        GridPane.setHgrow(tfCode, Priority.ALWAYS);
        GridPane.setHgrow(tfName, Priority.ALWAYS);
        GridPane.setHgrow(tfCity, Priority.ALWAYS);

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addStation(new Station(null, tfCode.getText(), tfName.getText(), tfCity.getText()));
            refreshStations();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            Station sel = stationTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                throw new IllegalArgumentException("Виберіть запис у таблиці");
            }
            api.updateStation(new Station(sel.getId(), tfCode.getText(), tfName.getText(), tfCity.getText()));
            refreshStations();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            Station sel = stationTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                throw new IllegalArgumentException("Виберіть запис у таблиці");
            }
            api.deleteStation(sel.getId());
            refreshStations();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> refreshStations());

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnRefresh);
        VBox box = new VBox(12, stationTable, form, buttons);
        box.setPadding(new Insets(12));
        VBox.setVgrow(stationTable, Priority.ALWAYS);
        return box;
    }

    private VBox buildModulesTab() {
        TableColumn<ChargingModule, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ChargingModule, Long> colStId = new TableColumn<>("Станція (ID)");
        colStId.setCellValueFactory(new PropertyValueFactory<>("stationId"));

        TableColumn<ChargingModule, String> colCode = new TableColumn<>("Код модуля");
        colCode.setCellValueFactory(new PropertyValueFactory<>("moduleCode"));

        TableColumn<ChargingModule, String> colType = new TableColumn<>("Конектор");
        colType.setCellValueFactory(new PropertyValueFactory<>("connectorType"));

        TableColumn<ChargingModule, Double> colPower = new TableColumn<>("Потужність (кВт)");
        colPower.setCellValueFactory(new PropertyValueFactory<>("powerKw"));

        TableColumn<ChargingModule, Double> colLoad = new TableColumn<>("Завантаженість F1");
        colLoad.setCellValueFactory(new PropertyValueFactory<>("loadScore"));

        TableColumn<ChargingModule, Double> colDeficit = new TableColumn<>("Дефіцит F3");
        colDeficit.setCellValueFactory(new PropertyValueFactory<>("deficitScore"));

        moduleTable.getColumns().setAll(List.of(colId, colStId, colCode, colType, colPower, colLoad, colDeficit));
        moduleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField tfStId = new TextField();
        TextField tfCode = new TextField();
        TextField tfType = new TextField();
        TextField tfPower = new TextField();
        TextField tfLoad = new TextField();
        TextField tfDeficit = new TextField();

        moduleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                tfStId.setText(String.valueOf(sel.getStationId()));
                tfCode.setText(sel.getModuleCode());
                tfType.setText(sel.getConnectorType());
                tfPower.setText(String.valueOf(sel.getPowerKw()));
                tfLoad.setText(String.valueOf(sel.getLoadScore()));
                tfDeficit.setText(String.valueOf(sel.getDeficitScore()));
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Станція (ID):"), tfStId, new Label("Код модуля:"), tfCode);
        form.addRow(1, new Label("Тип конектора:"), tfType, new Label("Потужність (кВт):"), tfPower);
        form.addRow(2, new Label("Завантаженість F1:"), tfLoad, new Label("Дефіцит F3:"), tfDeficit);

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            ChargingModule m = new ChargingModule(
                    null,
                    Long.parseLong(tfStId.getText().trim()),
                    tfCode.getText().trim(),
                    tfType.getText().trim(),
                    Double.parseDouble(tfPower.getText().trim()),
                    Double.parseDouble(tfLoad.getText().trim()),
                    Double.parseDouble(tfDeficit.getText().trim())
            );
            api.addModule(m);
            refreshModules();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            ChargingModule sel = moduleTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                throw new IllegalArgumentException("Виберіть запис у таблиці");
            }
            ChargingModule m = new ChargingModule(
                    sel.getId(),
                    Long.parseLong(tfStId.getText().trim()),
                    tfCode.getText().trim(),
                    tfType.getText().trim(),
                    Double.parseDouble(tfPower.getText().trim()),
                    Double.parseDouble(tfLoad.getText().trim()),
                    Double.parseDouble(tfDeficit.getText().trim())
            );
            api.updateModule(m);
            refreshModules();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            ChargingModule sel = moduleTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                throw new IllegalArgumentException("Виберіть запис у таблиці");
            }
            api.deleteModule(sel.getId());
            refreshModules();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> refreshModules());

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        Label lblStatus = new Label("CSV не імпортовано");
        Button btnImportCsv = new Button("Імпортувати CSV");

        btnImportCsv.setOnAction(e -> {
            Path csvPath = Path.of("sample-data", "stations_modules.csv");
            ImportStationsModulesTask task = new ImportStationsModulesTask(csvPath, api);

            btnImportCsv.setDisable(true);
            progressBar.progressProperty().unbind();
            lblStatus.textProperty().unbind();

            progressBar.progressProperty().bind(task.progressProperty());
            lblStatus.textProperty().bind(task.messageProperty());

            task.setOnSucceeded(ev -> {
                progressBar.progressProperty().unbind();
                lblStatus.textProperty().unbind();
                progressBar.setProgress(1.0);
                lblStatus.setText("Готово. Імпортовано: " + task.getValue());
                btnImportCsv.setDisable(false);
                refreshAll();
            });

            task.setOnFailed(ev -> {
                progressBar.progressProperty().unbind();
                lblStatus.textProperty().unbind();
                progressBar.setProgress(0);
                Throwable ex = task.getException();
                String msg = (ex != null && ex.getMessage() != null) ? ex.getMessage() : "Помилка CSV-імпорту";
                lblStatus.setText("Помилка імпорту");
                btnImportCsv.setDisable(false);
                showError(msg);
                refreshAll();
            });

            task.setOnCancelled(ev -> {
                progressBar.progressProperty().unbind();
                lblStatus.textProperty().unbind();
                lblStatus.setText("Імпорт скасовано; частина записів могла бути збережена");
                btnImportCsv.setDisable(false);
                refreshAll();
            });

            Thread thread = new Thread(task, "csv-import");
            thread.setDaemon(true);
            thread.start();
        });

        HBox buttons = new HBox(10, progressBar, lblStatus, btnImportCsv, btnAdd, btnUpdate, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER_LEFT);
        VBox box = new VBox(12, moduleTable, form, buttons);
        box.setPadding(new Insets(12));
        VBox.setVgrow(moduleTable, Priority.ALWAYS);
        return box;
    }

    private VBox buildIncidentsTab() {
        TableColumn<Incident, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Incident, Long> colModId = new TableColumn<>("Модуль (ID)");
        colModId.setCellValueFactory(new PropertyValueFactory<>("moduleId"));

        TableColumn<Incident, String> colDesc = new TableColumn<>("Опис несправності");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Incident, Double> colF1 = new TableColumn<>("F1");
        colF1.setCellValueFactory(new PropertyValueFactory<>("f1"));

        TableColumn<Incident, Double> colF2 = new TableColumn<>("F2");
        colF2.setCellValueFactory(new PropertyValueFactory<>("f2"));

        TableColumn<Incident, Double> colF3 = new TableColumn<>("F3");
        colF3.setCellValueFactory(new PropertyValueFactory<>("f3"));

        TableColumn<Incident, Double> colPriority = new TableColumn<>("Інтегральний показник");
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priorityIndex"));

        TableColumn<Incident, IncidentState> colState = new TableColumn<>("Стан");
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));

        incidentTable.getColumns().setAll(List.of(colId, colModId, colDesc, colF1, colF2, colF3, colPriority, colState));
        incidentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField tfModId = new TextField();
        TextField tfDesc = new TextField();
        TextField tfF2 = new TextField();
        ComboBox<IncidentState> cbState = new ComboBox<>(FXCollections.observableArrayList(IncidentState.values()));
        cbState.setValue(IncidentState.RECORDED);

        incidentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                tfModId.setText(String.valueOf(sel.getModuleId()));
                tfDesc.setText(sel.getDescription());
                tfF2.setText(String.valueOf(sel.getF2()));
                cbState.setValue(sel.getState());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Модуль (ID):"), tfModId, new Label("Тяжкість F2 (0..100):"), tfF2);
        form.addRow(1, new Label("Опис інциденту:"), tfDesc, new Label("Новий стан:"), cbState);

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addIncident(
                    Long.parseLong(tfModId.getText().trim()),
                    tfDesc.getText().trim(),
                    Double.parseDouble(tfF2.getText().trim())
            );
            refreshIncidents();
        }));

        Button btnUpdate = new Button("Змінити дані");
        btnUpdate.setOnAction(e -> safe(() -> {
            Incident sel = incidentTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                throw new IllegalArgumentException("Виберіть інцидент у таблиці");
            }
            api.updateIncident(
                    sel.getId(),
                    Long.parseLong(tfModId.getText().trim()),
                    tfDesc.getText().trim(),
                    Double.parseDouble(tfF2.getText().trim())
            );
            refreshIncidents();
        }));

        Button btnState = new Button("Змінити стан");
        btnState.setOnAction(e -> safe(() -> {
            Incident sel = incidentTable.getSelectionModel().getSelectedItem();
            if (sel == null || cbState.getValue() == null) {
                throw new IllegalArgumentException("Виберіть інцидент і бажаний стан");
            }
            api.changeIncidentState(sel.getId(), cbState.getValue());
            refreshIncidents();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            Incident sel = incidentTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                throw new IllegalArgumentException("Виберіть інцидент у таблиці");
            }
            api.deleteIncident(sel.getId());
            refreshIncidents();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> refreshIncidents());

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnState, btnDelete, btnRefresh);
        VBox box = new VBox(12, incidentTable, form, buttons);
        box.setPadding(new Insets(12));
        VBox.setVgrow(incidentTable, Priority.ALWAYS);
        return box;
    }

    private VBox buildTasksTab() {
        TableColumn<RepairTask, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<RepairTask, Long> colIncId = new TableColumn<>("Інцидент (ID)");
        colIncId.setCellValueFactory(new PropertyValueFactory<>("incidentId"));

        TableColumn<RepairTask, String> colDesc = new TableColumn<>("Опис робіт");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<RepairTask, String> colDate = new TableColumn<>("Запланована дата");
        colDate.setCellValueFactory(new PropertyValueFactory<>("plannedDate"));

        taskTable.getColumns().setAll(List.of(colId, colIncId, colDesc, colDate));
        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField tfIncId = new TextField();
        TextField tfDesc = new TextField();
        TextField tfDate = new TextField();

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                tfIncId.setText(String.valueOf(sel.getIncidentId()));
                tfDesc.setText(sel.getDescription());
                tfDate.setText(sel.getPlannedDate());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Інцидент (ID):"), tfIncId);
        form.addRow(1, new Label("Опис робіт:"), tfDesc);
        form.addRow(2, new Label("Запланована дата:"), tfDate);
        GridPane.setHgrow(tfDesc, Priority.ALWAYS);

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addTask(new RepairTask(
                    null,
                    Long.parseLong(tfIncId.getText().trim()),
                    tfDesc.getText().trim(),
                    tfDate.getText().trim()
            ));
            refreshTasks();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            RepairTask sel = taskTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                throw new IllegalArgumentException("Виберіть завдання у таблиці");
            }
            api.updateTask(new RepairTask(
                    sel.getId(),
                    Long.parseLong(tfIncId.getText().trim()),
                    tfDesc.getText().trim(),
                    tfDate.getText().trim()
            ));
            refreshTasks();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            RepairTask sel = taskTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                throw new IllegalArgumentException("Виберіть завдання у таблиці");
            }
            api.deleteTask(sel.getId());
            refreshTasks();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> refreshTasks());

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnRefresh);
        VBox box = new VBox(12, taskTable, form, buttons);
        box.setPadding(new Insets(12));
        VBox.setVgrow(taskTable, Priority.ALWAYS);
        return box;
    }

    private void refreshStations() {
        safe(() -> stationTable.setItems(FXCollections.observableArrayList(api.getStations())));
    }

    private void refreshModules() {
        safe(() -> moduleTable.setItems(FXCollections.observableArrayList(api.getModules())));
    }

    private void refreshIncidents() {
        safe(() -> incidentTable.setItems(FXCollections.observableArrayList(api.getIncidents())));
    }

    private void refreshTasks() {
        safe(() -> taskTable.setItems(FXCollections.observableArrayList(api.getTasks())));
    }

    private void refreshAll() {
        refreshStations();
        refreshModules();
        refreshIncidents();
        refreshTasks();
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }

    private void safe(ThrowingAction action) {
        try {
            action.run();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(message != null ? message : "Невідома помилка");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
