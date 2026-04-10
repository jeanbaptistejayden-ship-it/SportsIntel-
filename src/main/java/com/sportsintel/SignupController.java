package com.sportsintel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class SignupController {

        @FXML
        private ImageView signupLogo;

        @FXML
        public void initialize() {
            Image image = new Image(
                    Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm()
            );
            signupLogo.setImage(image);
        }

        @FXML
        private void handleBackToHome(ActionEvent event) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }
