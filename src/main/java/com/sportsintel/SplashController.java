package com.sportsintel;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class SplashController {

    @FXML
    private ImageView logoImage;

    @FXML
    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm());
        logoImage.setImage(image);

        FadeTransition fadeLogo = new FadeTransition(Duration.millis(1.5), logoImage);
        fadeLogo.setFromValue(0.0);
        fadeLogo.setToValue(1.0);
        fadeLogo.play();

        PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
        delay.setOnFinished(event -> openHomeScreen());
        delay.play();
    }

    private void openHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeView.fxml"));
            Parent root = loader.load();

            root.setOpacity(0.0);

            Stage stage = (Stage) logoImage.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(1.2), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}