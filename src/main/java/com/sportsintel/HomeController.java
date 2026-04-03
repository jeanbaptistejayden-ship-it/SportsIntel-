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
}
