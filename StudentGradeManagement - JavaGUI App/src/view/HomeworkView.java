package view;

import controller.HomeworkController;
import domain.Tema;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

public class HomeworkView {

    private HomeworkController ctrl;
    public TextField idTemaField, descriereField, deadlineField, predataField, saptCurentaField;
    public ComboBox<Integer> comboBox;
    private BorderPane borderPane;
    private TableView<Tema> tableView;

    public HomeworkView(HomeworkController ctrl) {
        this.ctrl = ctrl;
        initView();
    }

    public TableView<Tema> getTableView() {
        return tableView;
    }

    private void initView() {
        borderPane = new BorderPane();
        borderPane.setLeft(createLogo());
        borderPane.setCenter(createTableView());
        borderPane.setBottom(createTextFieldsAndButtons());
        borderPane.setBackground(Background.EMPTY);
    }

    private VBox createLogo() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_LEFT);

        Image imageBack = new Image("utils/logos/backIcon.png");
        ImageView imageViewBack = new ImageView(imageBack);
        imageViewBack.setFitWidth(70);
        imageViewBack.setFitHeight(70);

        Button btnBack = new Button("", imageViewBack);
        btnBack.setBackground(Background.EMPTY);
        btnBack.setOnAction(ctrl::handleBack);
        vBox.getChildren().add(btnBack);

        Image img = new Image("utils/logos/02535d2f-2011-4427-90aa-66f3ce8ba8a2.png");
        ImageView imageView = new ImageView(img);
        vBox.getChildren().add(imageView);

        vBox.setMargin(imageView, new Insets(250, 10, 10, 10));

        return vBox;
    }

    private VBox createTextFieldsAndButtons() {

        Image imageAdd = new Image("utils/logos/add.png");
        ImageView imageViewAdd = new ImageView(imageAdd);
        imageViewAdd.setFitWidth(70);
        imageViewAdd.setFitHeight(70);
        Button btnAdauga = new Button("", imageViewAdd);
        btnAdauga.setBackground(Background.EMPTY);
        btnAdauga.setOnAction(ctrl::handleAddHomework);

        Image imagePostpone = new Image("utils/logos/amana.png");
        ImageView imageViewPostpone = new ImageView(imagePostpone);
        imageViewPostpone.setFitWidth(200);
        imageViewPostpone.setFitHeight(80);
        Button btnAmana = new Button("", imageViewPostpone);
        btnAmana.setOnAction(ctrl::handlePostponeHomework);
        btnAmana.setBackground(Background.EMPTY);

        //id tema
        idTemaField = new TextField();
        idTemaField.setPromptText("id-ul temei");
        idTemaField.setFont(Font.font("Serif", FontPosture.ITALIC, 16));

        //descriereField
        descriereField = new TextField();
        descriereField.setPromptText("descriere");
        descriereField.setFont(Font.font("Serif", FontPosture.ITALIC, 16));


        //deadlineField
        deadlineField = new TextField();
        deadlineField.setPromptText("deadline");
        deadlineField.setFont(Font.font("Serif", FontPosture.ITALIC, 16));

        //saptamana in care a fost predataField
        predataField = new TextField();
        predataField.setPromptText("ultima saptamana de predare");
        predataField.setMinWidth(200);
        predataField.setFont(Font.font("Serif", FontPosture.ITALIC, 16));

        //saptamana curenta
        saptCurentaField = new TextField();
        saptCurentaField.setPromptText("Introduceti saptamana curenta");
        saptCurentaField.setMinWidth(300);
        saptCurentaField.setFont(Font.font("Serif", FontPosture.ITALIC, 16));


        //combo box for choosing the current week
        comboBox = new ComboBox<>();
        comboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
        comboBox.setPromptText("Alegeti saptamana curenta");
        comboBox.setStyle("-fx-font: 18px \"Serif\";");

        HBox layout = new HBox(10, comboBox, btnAmana);
        layout.setAlignment(Pos.CENTER);
        layout.setMargin(comboBox, new Insets(0, 0, 80, 0));
        layout.setMargin(btnAmana, new Insets(0, 0, 80, 0));
        layout.setSpacing(40);

        HBox hBox = new HBox(5, idTemaField, descriereField, deadlineField, predataField, btnAdauga);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setSpacing(10);
        hBox.setMargin(btnAdauga, new Insets(0, 0, 35, 60));
        hBox.setMargin(idTemaField, new Insets(30, 0, 0, 170));
        hBox.setMargin(descriereField, new Insets(30, 0, 0, 0));
        hBox.setMargin(deadlineField, new Insets(30, 0, 0, 0));
        hBox.setMargin(predataField, new Insets(30, 0, 0, 0));


        VBox finalLayout = new VBox(10, hBox, layout);

        return finalLayout;
    }


    public VBox createTableView() {
        tableView = new TableView<>();

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //initialize the names of the columns
        TableColumn<Tema, Integer> idTemaColumn = new TableColumn<>("ID Tema");
        idTemaColumn.setCellValueFactory(new PropertyValueFactory<>("idTema"));

        TableColumn<Tema, String> descriereColumn = new TableColumn<>("Descriere");
        descriereColumn.setCellValueFactory(new PropertyValueFactory<>("descriere"));

        TableColumn<Tema, Integer> deadlineColumn = new TableColumn<>("Deadline");
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        TableColumn<Tema, Integer> predataColumn = new TableColumn<>("Ultima sapt. de predare");
        predataColumn.setCellValueFactory(new PropertyValueFactory<>("predata"));


        //add columns
        tableView.getColumns().addAll(idTemaColumn, descriereColumn, deadlineColumn, predataColumn);

        tableView.setItems(ctrl.getModel()); //load data

        //add listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> {
                    ctrl.showHomeworkDetails(newValue);
                }));

        //add table to vBox

        tableView.setPrefSize(500, 600);

        VBox vBox = new VBox();
        vBox.getChildren().add(tableView);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setMargin(tableView, new Insets(0, 500, 0, 250));

        return vBox;
    }

    public BorderPane getView() {
        return borderPane;
    }

}
