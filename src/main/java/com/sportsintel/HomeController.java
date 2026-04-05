package com.sportsintel;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HomeController {

    @FXML
    private ImageView navLogo;

    @FXML
    private ImageView mainLogo;

    @FXML
    private ComboBox<String> seasonTypeCombo;

    @FXML
    private ComboBox<String> locationCombo;

    @FXML
    private ComboBox<String> sportCombo;

    @FXML
    private ComboBox<String> opponentCombo;

    @FXML
    private ComboBox<String> statisticCombo;

    @FXML
    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm());
        navLogo.setImage(image);
        mainLogo.setImage(image);

        if (seasonTypeCombo != null) {
            seasonTypeCombo.getItems().addAll("Regular Season", "Playoffs", "Both");
            seasonTypeCombo.setValue("Both");
        }

        if (locationCombo != null) {
            locationCombo.getItems().addAll("Home", "Away", "Both");
            locationCombo.setValue("Both");
        }

        if (statisticCombo != null) {
            statisticCombo.getItems().addAll("Points Per Game", "Assists Per Game", "Rebounds Per Game");
        }

        if (sportCombo != null) {
            sportCombo.getItems().addAll("Basketball", "Baseball (Coming Soon)", "Football (Coming Soon)", "Soccer (Coming Soon)");
        }

        if (opponentCombo != null) {
            opponentCombo.getItems().addAll("Atlanta Hawks",
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
                    "Washington Wizards");
        }
    }

    @FXML
    private void handleHelpClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HelpView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm()
            );

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
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm()
            );

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
}
