package controller;

import domain.Nota;
import domain.NotaDTO;
import domain.Student;
import domain.Tema;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.stage.Stage;
import service.GradeService;
import utils.GradeChangeEvent;
import utils.Observer;
import utils.WarningBox;
import validator.ValidationException;

import java.util.List;
import java.util.Optional;

public class GradeController implements Observer<GradeChangeEvent> {

    public ComboBox comboBoxHomework;
    public TableView tableViewGrade;
    public TableColumn tableColumnStudent;
    public TableColumn tableColumnTema;
    public TableColumn tableColumnValoare;
    public ComboBox comboBoxSaptCurenta;
    public TextField textFieldCautaNume;
    public ListView listViewStudents;
    public TextArea textAreaFeedback;
    public TextField textFieldSaptMotivate;
    public TextField textFieldValNota;
    public Button buttonBack;
    public Button btnAdaugaNota;
    private GradeService gradeService;

    private ObservableList<NotaDTO> model = FXCollections.observableArrayList();
    private ObservableList<String> comboBoxListModel = FXCollections.observableArrayList();

    private Stage primaryStage;
    private Scene mainMenuStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setMainMenuStage(Scene mainMenuStage) {
        this.mainMenuStage = mainMenuStage;
    }

    public void setGradeService(GradeService gradeService) {
        this.gradeService = gradeService;
        gradeService.addObserver(this);
        initModels();
    }

    @FXML
    public void initialize() {

        createTableView();
        tableViewGrade.setBackground(Background.EMPTY);
        tableViewGrade.setItems(model);
        comboBoxHomework.setItems(comboBoxListModel);

        textFieldCautaNume.textProperty().addListener(o -> handleCautareNume());
        textFieldSaptMotivate.textProperty().addListener(o -> handleSelectareTema());
    }


    private void initModels() {

        //set list view model
        List<NotaDTO> gradeList = gradeService.getAllNoteDTO();
        model.setAll(gradeList);

        //set homework combo box model
        List<String> gradesId = gradeService.returnListOfIds();
        comboBoxListModel.setAll(gradesId);

        //set current week
        comboBoxSaptCurenta.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);

        //set default value for homework
        comboBoxHomework.getSelectionModel().selectFirst();

        gradeService.addObserver(this);
    }

    @Override
    public void update(GradeChangeEvent gradeChangeEvent) {

        List<NotaDTO> gradeList = gradeService.getAllNoteDTO();
        model.setAll(gradeList);
        tableViewGrade.setItems(model);

        List<String> gradesId = gradeService.returnListOfIds();
        comboBoxListModel.setAll(gradesId);

    }

    private void validareNota() {

        String errors = "";

        String idTemaString = comboBoxHomework.getSelectionModel().getSelectedItem().toString();
        if (!idTemaString.matches("[1-9]")) {
            errors += "Selectati o tema. \n";
        }

        if (listViewStudents.getSelectionModel().getSelectedItem() == null) {
            errors += "Selectati un student.\n";
        }
        try {
            Float valoare = Float.parseFloat(textFieldValNota.getText().toString());
        } catch (NumberFormatException e) {
            errors += "Nota trebuie sa fie cuprinsa intre 1 si 10.\n";
        }

        if (comboBoxSaptCurenta.getSelectionModel().getSelectedItem() == null) {
            errors += "Alegeti saptamana curenta.\n";
        }
        if (errors.length() > 0) {
            throw new ValidationException(errors);
        }

    }

    @FXML
    private void handleSelectareTema() {

        Object object = comboBoxHomework.getSelectionModel().getSelectedItem();
        Object saptCurenta = comboBoxSaptCurenta.getSelectionModel().getSelectedItem();

        if (object != null && saptCurenta != null) {

            Tema tema = gradeService.getHomeworkService().findHomework(Integer.parseInt(object.toString()));
            Integer saptamanaCurenta = Integer.parseInt(saptCurenta.toString());

            Integer depunctare = saptamanaCurenta - tema.getDeadline();

            if (depunctare > 0) {

                textFieldSaptMotivate.setEditable(true);
                textFieldSaptMotivate.setPromptText("Sapt motivate !");
                String motivare = textFieldSaptMotivate.getText();

                if (!motivare.equals("")) {
                    depunctare = depunctare - Integer.parseInt(motivare);
                    if (depunctare > 2) {
                        textAreaFeedback.setText("Tema nu mai poate fi predata !");
                    } else {
                        if (depunctare > 0) {
                            textAreaFeedback.setText("Nota a fost diminuata cu " + depunctare * 2.5 + " puncte!");

                        } else
                            textAreaFeedback.setText("Nu aveti alte penalizari !");
                    }
                } else {
                    if (depunctare > 2) {
                        textAreaFeedback.setText("Tema nu mai poate fi predata !");
                    } else {
                        textAreaFeedback.setText("Nota a fost diminuata cu " + depunctare * 2.5 + " puncte!");

                    }
                }
            } else {
                textFieldSaptMotivate.setEditable(false);
                textFieldSaptMotivate.setPromptText("");
                textAreaFeedback.setText("");
            }

        }
    }

    private void createTableView() {

        tableColumnStudent.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("numeStudent"));
        tableColumnTema.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("numeTema"));
        tableColumnValoare.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("valoareNota"));

    }

    public void handleCautareNume() {

        ObservableList<Student> observableList = FXCollections.observableArrayList();

        if (textFieldCautaNume.getText().equals("")) {
            listViewStudents.setItems(observableList);
        } else {
            String name = textFieldCautaNume.getText();
            List<Student> studentList = gradeService.findStudentByName(name);
            observableList.setAll(studentList);
            listViewStudents.setItems(observableList);
        }

    }


    public void handleMotivare() {
        String motivare = textFieldSaptMotivate.getText();
    }

    public void handleAdaugaNota() {

        try {
            validareNota();
            Integer idTema = Integer.parseInt(comboBoxHomework.getSelectionModel().getSelectedItem().toString());
            Student student = (Student) listViewStudents.getSelectionModel().getSelectedItem();
            Float valoare = Float.parseFloat(textFieldValNota.getText());
            Integer saptamanaCurenta = Integer.parseInt(comboBoxSaptCurenta.getSelectionModel().getSelectedItem().toString());
            String feedback = textAreaFeedback.getText();

            Nota nota = new Nota(student.getID(), idTema, valoare, saptamanaCurenta);

            String motivare = textFieldSaptMotivate.getText();
            Integer saptMotivate = 0;
            if (motivare.matches("[1-14]")) {
                saptMotivate = Integer.parseInt(motivare);
            }

            Integer deadline = gradeService.getHomeworkService().findHomework(idTema).getDeadline();

            Float depunctare = valoare - gradeService.calculeazaDepunctare(valoare, deadline, saptamanaCurenta, saptMotivate);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            String contentText = "TEMA: " + idTema +
                    "\n STUDENTUL: " + student.getNume() +
                    "\n NOTA: " + valoare;

            if (depunctare != 0) {
                contentText += "\n DEPUNCTARE: " + depunctare;
            }
            if (saptMotivate != 0) {
                contentText += "\n MOTIVARE: " + saptMotivate;
            }

            alert.setContentText(contentText);
            Optional<ButtonType> action = alert.showAndWait();

            if (action.get() == ButtonType.OK) {

                gradeService.addGrade(nota, saptMotivate, feedback);

                textFieldSaptMotivate.setText("");
                textFieldValNota.setText("");
                textAreaFeedback.setText("");
            }

        } catch (ValidationException e) {
            WarningBox warningBox = new WarningBox();
            warningBox.warningMessage(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    public void handleBack() {
        this.primaryStage.setScene(mainMenuStage);
    }
}
