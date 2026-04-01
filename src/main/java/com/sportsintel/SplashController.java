package com.sportsintel;

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
        Image image = new Image(
                Objects.requireNonNull(
                        getClass().getResource("/resources/logo.png")
                ).toExternalForm()
        );
        logoImage.setImage(image);

        PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
        delay.setOnFinished(event -> openHomeScreen());
        delay.play();
    }

    private void openHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoImage.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm()
            );

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}