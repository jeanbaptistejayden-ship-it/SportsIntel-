package com.sportsintel;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class HelpController {

    @FXML
    private void handleClose(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
