package utils;

import javafx.scene.control.Alert;

public class WarningBox {


    public WarningBox() {}

    public void warningMessage(Alert.AlertType type, String header, String mesaj){

        Alert message=new Alert(type);
        message.setHeight(200);
        message.setWidth(500);
        message.setHeaderText(header);
        message.setContentText(mesaj);
        message.showAndWait();

    }
}
