package com.sportsintel;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class HomeController {

    @FXML
    private ImageView navLogo;

    @FXML
    private ComboBox<String> seasonTypeCombo;

    @FXML
    private ComboBox<String> locationCombo;

    @FXML
    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm());
        navLogo.setImage(image);

        if (seasonTypeCombo != null) {
            seasonTypeCombo.getItems().addAll("Regular Season", "Playoffs", "Both");
            seasonTypeCombo.setValue("Both");
        }

        if (locationCombo != null) {
            locationCombo.getItems().addAll("Home", "Away", "Both");
            locationCombo.setValue("Both");
        }
    }
}
