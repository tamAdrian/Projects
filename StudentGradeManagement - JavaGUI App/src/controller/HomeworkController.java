package controller;

import domain.Tema;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import service.HomeworkService;
import utils.HomeworkChangeEvent;
import utils.Observer;
import utils.WarningBox;
import validator.ValidationException;
import view.HomeworkView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HomeworkController implements Observer<HomeworkChangeEvent> {

    private HomeworkService service;
    private ObservableList<Tema> model; //getAll from inFileRepository
    private HomeworkView view;


    private Stage primaryStage;
    private Scene mainMenuScene;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setMainMenuScene(Scene mainMenuScene) {
        this.mainMenuScene = mainMenuScene;
    }

    public HomeworkController(HomeworkService service) {

        this.service = service;

        List<Tema> list = StreamSupport.stream(service.allHomeworks().spliterator(), false)
                .collect(Collectors.toList());
        model = FXCollections.observableArrayList(list);

        service.addObserver(this);
    }

    public ObservableList<Tema> getModel() {
        return model;
    }

    public void setModel(ObservableList<Tema> model) {
        this.model = model;
    }

    public HomeworkView getView() {
        return view;
    }

    public void setView(HomeworkView view) {
        this.view = view;
    }

    @Override
    public void update(HomeworkChangeEvent event) {
        model.setAll(StreamSupport.stream(service.allHomeworks().spliterator(), false)
                .collect(Collectors.toList()));
    }

    /**
     * extract homework from text fields (from view)
     * @return a homework
     */
    public Tema extractHomework(){

        String idTema = view.idTemaField.getText();
        String descriere = view.descriereField.getText() ;
        String deadline =  view.deadlineField.getText();
        String predata =  view.predataField.getText();

        Tema tema = new Tema(Integer.parseInt(idTema), descriere, Integer.parseInt(deadline), Integer.parseInt(predata));
        return tema;
    }

    public void showHomeworkDetails(Tema value){
        if(value == null){
            view.idTemaField.setText("");
            view.descriereField.setText("");
            view.deadlineField.setText("");
            view.predataField.setText("");

        }
        else{
            view.idTemaField.setText(value.getID().toString());
            view.descriereField.setText(value.getDescriere());
            view.deadlineField.setText(value.getDeadline().toString());
            view.predataField.setText(value.getPredata().toString());
        }
    }

    public void handleAddHomework(ActionEvent actionEvent){


        WarningBox warningBox = new WarningBox();
        try
        {
            Tema t = extractHomework();
            Tema saved = service.addHomework(t);
            if(saved == null){
                warningBox.warningMessage(Alert.AlertType.INFORMATION, "Salvare cu succes", " Tema a fost adaugata!");
                showHomeworkDetails(null);
            }
            else{
                warningBox.warningMessage(Alert.AlertType.INFORMATION, "ATTENTION", "Tema a fost deja adaugata!");
            }
        }
        catch (ValidationException e){
                warningBox.warningMessage(Alert.AlertType.ERROR, "ERROR", e.getMessage());
        }
        catch(NumberFormatException e){
            warningBox.warningMessage(Alert.AlertType.ERROR, "ERROR", "Introduceti o valoare intreaga!");
        }
        catch(RuntimeException e){
            warningBox.warningMessage(Alert.AlertType.ERROR, "ERROR", e.getMessage());
        }


    }

    public void handlePostponeHomework(ActionEvent actionEvent){

        WarningBox warningBox = new WarningBox();

        try{
            Tema t = extractHomework();
            Integer saptCurenta = view.comboBox.getValue();
            if(service.postponeHomework(saptCurenta, t.getID()) == null){
                warningBox.warningMessage(Alert.AlertType.INFORMATION, "", "Tema a fost amanata cu succes!");
            }

        }
        catch(ValidationException e){
            warningBox.warningMessage(Alert.AlertType.ERROR, "ERROR", e.getMessage());

        }
        catch(RuntimeException e){
            warningBox.warningMessage(Alert.AlertType.ERROR, "ERROR", "Selectati saptamana curenta !");
    }
    }

    public void handleBack(ActionEvent actionEvent){
        primaryStage.setScene(mainMenuScene);
    }

}
