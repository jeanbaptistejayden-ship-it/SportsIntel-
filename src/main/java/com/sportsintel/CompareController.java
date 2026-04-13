package com.sportsintel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CompareController {

    @FXML
    private ImageView navLogo;

    @FXML
    private ImageView mainLogo;

    @FXML
    private ComboBox<String> compareOpponentCombo;

    @FXML
    public void initialize() {
        Image image = new Image(
                Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm()
        );

        if (navLogo != null) {
            navLogo.setImage(image);
        }

        if (mainLogo != null) {
            mainLogo.setImage(image);
        }

        if (compareOpponentCombo != null) {
            compareOpponentCombo.getItems().addAll(
                    "Atlanta Hawks",
                    "Boston Celtics",
                    "Brooklyn Nets",
                    "Charlotte Hornets",
                    "Chicago Bulls",
                    "Cleveland Cavaliers",
                    "Dallas Mavericks",
                    "Denver Nuggets",
                    "Detroit Pistons",
                    "Golden State Warriors",
                    "Houston Rockets",
                    "Indiana Pacers",
                    "Los Angeles Clippers",
                    "Los Angeles Lakers",
                    "Memphis Grizzlies",
                    "Miami Heat",
                    "Milwaukee Bucks",
                    "Minnesota Timberwolves",
                    "New Orleans Pelicans",
                    "New York Knicks",
                    "Oklahoma City Thunder",
                    "Orlando Magic",
                    "Philadelphia 76ers",
                    "Phoenix Suns",
                    "Portland Trail Blazers",
                    "Sacramento Kings",
                    "San Antonio Spurs",
                    "Toronto Raptors",
                    "Utah Jazz",
                    "Washington Wizards"
            );
        }
    }

    @FXML
    private void handleHomeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeView.fxml"));
            Parent root = loader.load();

            Scene currentScene = navLogo.getScene();
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHelpClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HelpView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

            Stage helpStage = new Stage();
            helpStage.setTitle("Help & Support");
            helpStage.setScene(scene);
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 480, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(scene);
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.setResizable(false);
            loginStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUpClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUpView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 520, 920);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

            Stage signUpStage = new Stage();
            signUpStage.setTitle("Sign Up");
            signUpStage.setScene(scene);
            signUpStage.initModality(Modality.APPLICATION_MODAL);
            signUpStage.setResizable(false);
            signUpStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}