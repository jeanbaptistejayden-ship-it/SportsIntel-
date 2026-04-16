package com.sportsintel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ResultsController {

    @FXML
    private ImageView navLogo;

    @FXML
    private ImageView playerImage;

    @FXML
    private HBox authButtons;

    @FXML
    private VBox profileBox;

    @FXML
    private VBox profileMenu;

    @FXML
    private Label profileNameLabel;

    @FXML
    private Label profileUsernameLabel;

    @FXML
    private Label seasonFilterLabel;

    @FXML
    private Label seasonTypeFilterLabel;

    @FXML
    private Label locationFilterLabel;

    @FXML
    private Label lastNFilterLabel;

    @FXML
    private Label playerSummaryLabel;

    @FXML
    private Label opponentSummaryLabel;

    @FXML
    private Label statSummaryLabel;

    @FXML
    private Label averageTitleLabel;

    @FXML
    private Label averageValueLabel;

    @FXML
    private Label averageUnitLabel;

    @FXML
    private Label gamesPlayedLabel;

    @FXML
    private Label highValueLabel;

    @FXML
    private Label lowValueLabel;

    @FXML
    public void initialize() {
        Image image = new Image(
                Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm()
        );

        if (navLogo != null) {
            navLogo.setImage(image);
        }
        applyLatestSearch();
        updateLoggedInUI();
    }

    private void applyLatestSearch() {
        SessionManager.SearchResult search = SessionManager.getLatestSearch();
        if (search == null) {
            return;
        }

        updateText(playerSummaryLabel, search.player());
        String opponent = (search.opponent() == null || search.opponent().isBlank()) ? "Any Opponent" : search.opponent();
        updateText(opponentSummaryLabel, "vs " + opponent);
        updateText(statSummaryLabel, search.stat());

        updateText(seasonFilterLabel, search.season());
        updateText(seasonTypeFilterLabel, search.seasonType());
        updateText(locationFilterLabel, search.location());
        updateText(lastNFilterLabel, search.lastN());

        updateText(averageTitleLabel, "vs " + opponent);
        updateText(averageValueLabel, String.format("%.1f", search.average()));
        updateText(averageUnitLabel, shortStatLabel(search.stat()) + " average");
        updateText(gamesPlayedLabel, String.valueOf(search.gamesPlayed()));
        updateText(highValueLabel, String.format("%.1f", search.high()));
        updateText(lowValueLabel, String.format("%.1f", search.low()));
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

            updateLoggedInUI();

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

            updateLoggedInUI();

        } catch (IOException e) {
            e.printStackTrace();
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
    private void handleCompareClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompareView.fxml"));
            Parent root = loader.load();

            Scene currentScene = navLogo.getScene();
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleProfileMenu() {
        boolean show = !profileMenu.isVisible();
        profileMenu.setVisible(show);
        profileMenu.setManaged(show);
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout();

        profileMenu.setVisible(false);
        profileMenu.setManaged(false);

        profileBox.setVisible(false);
        profileBox.setManaged(false);

        authButtons.setVisible(true);
        authButtons.setManaged(true);
    }

    private void updateLoggedInUI() {
        if (SessionManager.isLoggedIn()) {

            authButtons.setVisible(false);
            authButtons.setManaged(false);

            profileBox.setVisible(true);
            profileBox.setManaged(true);

            profileNameLabel.setText(SessionManager.getFullName());
            profileUsernameLabel.setText(SessionManager.getUsername());

        } else {

            authButtons.setVisible(true);
            authButtons.setManaged(true);

            profileBox.setVisible(false);
            profileBox.setManaged(false);

            profileMenu.setVisible(false);
            profileMenu.setManaged(false);
        }
    }

    private void updateText(Label label, String value) {
        if (label != null && value != null) {
            label.setText(value);
        }
    }

    private String shortStatLabel(String statLabel) {
        if (statLabel == null) {
            return "PPG";
        }
        if (statLabel.toLowerCase().contains("assist")) {
            return "APG";
        }
        if (statLabel.toLowerCase().contains("rebound")) {
            return "RPG";
        }
        return "PPG";
    }

    public void loadPlayerImage(String playerName) {
        if (playerName != null && playerName.equalsIgnoreCase("LeBron James")) {
            setPlayerImage("/lebron.png");
        } else {
            setPlayerImage("/lebron.png");
        }
    }

    private void setPlayerImage(String path) {
        try {
            Image image = new Image(
                    Objects.requireNonNull(getClass().getResource(path)).toExternalForm()
            );
            playerImage.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
