package controller;

import domain.NotaDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import service.GradeService;
import utils.GradeChangeEvent;
import utils.Observer;
import utils.WarningBox;

import java.util.List;

public class FilterController implements Observer<GradeChangeEvent> {

    public TableView tableView;
    public TableColumn tableColumnStudent;
    public TableColumn tableColumnLaborator;
    public TableColumn tableColumnNota;
    public TextField textFieldNume;
    public TextField textFieldHomework;
    public ComboBox comboBoxLaborator;
    public ComboBox comboBoxGrupa;
    public Button buttonFiltreNote;
    public Button buttonAverageStudent;
    public TextField textFieldAverageStudent;
    private Stage primaryStage;
    private Scene mainMenuScene;
    private GradeService gradeService;

    private ObservableList<NotaDTO> model = FXCollections.observableArrayList();
    private ObservableList<String> modelHomeworkNumber = FXCollections.observableArrayList();
    private ObservableList<String> modelGroupNumber = FXCollections.observableArrayList();

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setMainMenuScene(Scene mainMenuScene) {
        this.mainMenuScene = mainMenuScene;
    }

    public void setGradeService(GradeService gradeService) {

        this.gradeService = gradeService;

        List<NotaDTO> notaDTOList = gradeService.getAllNoteDTO();
        model.setAll(notaDTOList);

        List<String> homeworkNumberList = gradeService.getAllHomeworkNumber();
        modelHomeworkNumber.setAll(homeworkNumberList);

        List<String> groupNumberList = gradeService.getAllGroupNumber();
        modelGroupNumber.setAll(groupNumberList);

        gradeService.addObserver(this);
    }

    public void initialize() {

        createTableView();
        tableView.setItems(model);
        comboBoxGrupa.setItems(modelGroupNumber);
        comboBoxLaborator.setItems(modelHomeworkNumber);

        textFieldHomework.textProperty().addListener(o -> handleFilterByNameAndHomework());
        textFieldNume.textProperty().addListener(o -> handleFilterByNameAndHomework());

    }

    public void handleBack() {
        this.primaryStage.setScene(mainMenuScene);
    }

    private void createTableView() {

        tableColumnStudent.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("numeStudent"));
        tableColumnLaborator.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("numeTema"));
        tableColumnNota.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("valoareNota"));

    }

    @Override
    public void update(GradeChangeEvent gradeChangeEvent) {

        List<NotaDTO> notaDTOList = gradeService.getAllNoteDTO();
        List<String> homeworkNumberList = gradeService.getAllHomeworkNumber();
        List<String> groupNumberList = gradeService.getAllGroupNumber();

        model.setAll(notaDTOList);
        modelGroupNumber.setAll(groupNumberList);
        modelHomeworkNumber.setAll(homeworkNumberList);

    }


    public void handleFilterByNameAndHomework() {

        if (textFieldNume.getText().isEmpty() && textFieldHomework.getText().isEmpty()) {
            List<NotaDTO> notaDTOList = gradeService.getAllNoteDTO();
            model.setAll(notaDTOList);

        } else {
            String numeStudent = textFieldNume.getText();
            String idTema = textFieldHomework.getText();
            List<NotaDTO> list = gradeService.filterByStudentNameAndHomework(numeStudent, idTema);
            model.setAll(list);
        }

    }

    public void handleFiltreNote() {

        String laborator = (String) comboBoxLaborator.getSelectionModel().getSelectedItem();
        String grupa = (String) comboBoxGrupa.getSelectionModel().getSelectedItem();

        String errors = "";
        if (laborator == null) {
            errors += "Selectati numarul laboratorului !\n";
        }
        if (grupa == null) {
            errors += "Selectati numarul grupei !\n";
        }

        if (errors.length() > 0) {
            WarningBox warningBox = new WarningBox();
            warningBox.warningMessage(Alert.AlertType.ERROR, "ERROR", errors);
        } else {
            List<NotaDTO> notaDTOList = gradeService.filterByHomeworkNumberAndGroup(laborator, grupa);
            model.setAll(notaDTOList);
        }
    }

    public void handleAverageStudent() {

        if (tableView.getSelectionModel().getSelectedItem() == null) {
            WarningBox warningBox = new WarningBox();
            warningBox.warningMessage(Alert.AlertType.ERROR, "ERROR", "SELECTATI UN STUDENT DIN TABEL!");
        } else {
            NotaDTO notaDTO = (NotaDTO) tableView.getSelectionModel().getSelectedItem();
            Float medie = gradeService.averageForStudent(notaDTO.getNumeStudent());
            textFieldAverageStudent.setText(medie.toString());
        }

    }
}
