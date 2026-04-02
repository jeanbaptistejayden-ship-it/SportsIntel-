package com.sportsintel;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class HomeController {

    @FXML
    private ImageView navLogo;

    @FXML
    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm());
        navLogo.setImage(image);
    }
}
