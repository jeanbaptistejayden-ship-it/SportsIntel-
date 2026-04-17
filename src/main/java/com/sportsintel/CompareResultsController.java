package com.sportsintel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CompareResultsController {

    @FXML
    private ImageView navLogo;

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
    private Label comparisonSubtitleLabel;

    @FXML
    private Label playerOneNameLabel;

    @FXML
    private Label playerTwoNameLabel;

    @FXML
    private Label pointsLeftLabel;

    @FXML
    private Label pointsRightLabel;

    @FXML
    private Label pointsDiffLabel;
    @FXML
    private StackPane pointsLeftBox;
    @FXML
    private StackPane pointsRightBox;
    @FXML
    private Label pointsLeftArrow;
    @FXML
    private Label pointsRightArrow;

    @FXML
    private Label assistsLeftLabel;

    @FXML
    private Label assistsRightLabel;

    @FXML
    private Label assistsDiffLabel;
    @FXML
    private StackPane assistsLeftBox;
    @FXML
    private StackPane assistsRightBox;
    @FXML
    private Label assistsLeftArrow;
    @FXML
    private Label assistsRightArrow;

    @FXML
    private Label reboundsLeftLabel;

    @FXML
    private Label reboundsRightLabel;

    @FXML
    private Label reboundsDiffLabel;
    @FXML
    private StackPane reboundsLeftBox;
    @FXML
    private StackPane reboundsRightBox;
    @FXML
    private Label reboundsLeftArrow;
    @FXML
    private Label reboundsRightArrow;

    @FXML
    private Label fgPctLeftLabel;

    @FXML
    private Label fgPctRightLabel;

    @FXML
    private Label fgPctDiffLabel;
    @FXML
    private StackPane fgPctLeftBox;
    @FXML
    private StackPane fgPctRightBox;
    @FXML
    private Label fgPctLeftArrow;
    @FXML
    private Label fgPctRightArrow;

    @FXML
    private Label minutesLeftLabel;

    @FXML
    private Label minutesRightLabel;

    @FXML
    private Label minutesDiffLabel;
    @FXML
    private StackPane minutesLeftBox;
    @FXML
    private StackPane minutesRightBox;
    @FXML
    private Label minutesLeftArrow;
    @FXML
    private Label minutesRightArrow;

    @FXML
    private Label gamesLeftLabel;

    @FXML
    private Label gamesRightLabel;
    @FXML
    private StackPane gamesLeftBox;
    @FXML
    private StackPane gamesRightBox;

    @FXML
    private Label analysisPlayerOneLabel;

    @FXML
    private Label analysisPlayerTwoLabel;

    @FXML
    private Label analysisPlayerOneTextLabel;

    @FXML
    private Label analysisPlayerTwoTextLabel;

    @FXML
    public void initialize() {
        Image image = new Image(
                Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm()
        );

        if (navLogo != null) {
            navLogo.setImage(image);
        }

        bindCompareData();
        updateLoggedInUI();
    }

    private void bindCompareData() {
        SessionManager.CompareResult compare = SessionManager.getLatestCompare();
        if (compare == null) {
            return;
        }

        if (comparisonSubtitleLabel != null) {
            comparisonSubtitleLabel.setText("vs " + compare.opponent());
        }

        if (playerOneNameLabel != null) {
            playerOneNameLabel.setText(compare.playerOne());
        }
        if (playerTwoNameLabel != null) {
            playerTwoNameLabel.setText(compare.playerTwo());
        }

        if (pointsLeftLabel != null) {
            pointsLeftLabel.setText(formatOneDecimal(compare.playerOnePoints()));
        }
        if (pointsRightLabel != null) {
            pointsRightLabel.setText(formatOneDecimal(compare.playerTwoPoints()));
        }
        if (pointsDiffLabel != null) {
            pointsDiffLabel.setText("Difference: " + formatOneDecimal(Math.abs(compare.playerOnePoints() - compare.playerTwoPoints())));
        }
        applyWinLoseStyles(pointsLeftBox, pointsRightBox, pointsLeftArrow, pointsRightArrow, compare.playerOnePoints(), compare.playerTwoPoints(), true);

        if (assistsLeftLabel != null) {
            assistsLeftLabel.setText(formatOneDecimal(compare.playerOneAssists()));
        }
        if (assistsRightLabel != null) {
            assistsRightLabel.setText(formatOneDecimal(compare.playerTwoAssists()));
        }
        if (assistsDiffLabel != null) {
            assistsDiffLabel.setText("Difference: " + formatOneDecimal(Math.abs(compare.playerOneAssists() - compare.playerTwoAssists())));
        }
        applyWinLoseStyles(assistsLeftBox, assistsRightBox, assistsLeftArrow, assistsRightArrow, compare.playerOneAssists(), compare.playerTwoAssists(), true);

        if (reboundsLeftLabel != null) {
            reboundsLeftLabel.setText(formatOneDecimal(compare.playerOneRebounds()));
        }
        if (reboundsRightLabel != null) {
            reboundsRightLabel.setText(formatOneDecimal(compare.playerTwoRebounds()));
        }
        if (reboundsDiffLabel != null) {
            reboundsDiffLabel.setText("Difference: " + formatOneDecimal(Math.abs(compare.playerOneRebounds() - compare.playerTwoRebounds())));
        }
        applyWinLoseStyles(reboundsLeftBox, reboundsRightBox, reboundsLeftArrow, reboundsRightArrow, compare.playerOneRebounds(), compare.playerTwoRebounds(), true);

        if (fgPctLeftLabel != null) {
            fgPctLeftLabel.setText(formatPercent(compare.playerOneFgPct()));
        }
        if (fgPctRightLabel != null) {
            fgPctRightLabel.setText(formatPercent(compare.playerTwoFgPct()));
        }
        if (fgPctDiffLabel != null) {
            fgPctDiffLabel.setText("Difference: " + formatPercent(Math.abs(compare.playerOneFgPct() - compare.playerTwoFgPct())));
        }
        applyWinLoseStyles(fgPctLeftBox, fgPctRightBox, fgPctLeftArrow, fgPctRightArrow, compare.playerOneFgPct(), compare.playerTwoFgPct(), true);

        if (minutesLeftLabel != null) {
            minutesLeftLabel.setText(formatOneDecimal(compare.playerOneMinutes()));
        }
        if (minutesRightLabel != null) {
            minutesRightLabel.setText(formatOneDecimal(compare.playerTwoMinutes()));
        }
        if (minutesDiffLabel != null) {
            minutesDiffLabel.setText("Difference: " + formatOneDecimal(Math.abs(compare.playerOneMinutes() - compare.playerTwoMinutes())));
        }
        applyWinLoseStyles(minutesLeftBox, minutesRightBox, minutesLeftArrow, minutesRightArrow, compare.playerOneMinutes(), compare.playerTwoMinutes(), true);

        if (gamesLeftLabel != null) {
            gamesLeftLabel.setText(String.valueOf(compare.playerOneGames()));
        }
        if (gamesRightLabel != null) {
            gamesRightLabel.setText(String.valueOf(compare.playerTwoGames()));
        }
        applyWinLoseStyles(gamesLeftBox, gamesRightBox, null, null, compare.playerOneGames(), compare.playerTwoGames(), true);

        if (analysisPlayerOneLabel != null) {
            analysisPlayerOneLabel.setText(compare.playerOne());
        }
        if (analysisPlayerTwoLabel != null) {
            analysisPlayerTwoLabel.setText(compare.playerTwo());
        }
        if (analysisPlayerOneTextLabel != null) {
            analysisPlayerOneTextLabel.setText(compare.playerOne() + " averages " + formatOneDecimal(compare.playerOnePoints())
                    + " PPG vs " + compare.opponent() + " across " + compare.playerOneGames() + " games.");
        }
        if (analysisPlayerTwoTextLabel != null) {
            analysisPlayerTwoTextLabel.setText(compare.playerTwo() + " averages " + formatOneDecimal(compare.playerTwoPoints())
                    + " PPG vs " + compare.opponent() + " across " + compare.playerTwoGames() + " games.");
        }
    }

    private String formatOneDecimal(double value) {
        return String.format("%.1f", value);
    }

    private String formatPercent(double value) {
        return String.format("%.1f%%", value * 100.0);
    }

    private void applyWinLoseStyles(
            StackPane leftBox,
            StackPane rightBox,
            Label leftArrow,
            Label rightArrow,
            double leftValue,
            double rightValue,
            boolean higherIsBetter
    ) {
        if (leftBox == null || rightBox == null) {
            return;
        }

        leftBox.getStyleClass().removeAll("stat-win", "stat-lose");
        rightBox.getStyleClass().removeAll("stat-win", "stat-lose");

        if (leftArrow != null) {
            leftArrow.setVisible(false);
            leftArrow.setManaged(false);
        }
        if (rightArrow != null) {
            rightArrow.setVisible(false);
            rightArrow.setManaged(false);
        }

        if (Double.compare(leftValue, rightValue) == 0) {
            return;
        }

        boolean leftWins = higherIsBetter ? leftValue > rightValue : leftValue < rightValue;
        if (leftWins) {
            leftBox.getStyleClass().add("stat-win");
            rightBox.getStyleClass().add("stat-lose");
            if (leftArrow != null) {
                leftArrow.setVisible(true);
                leftArrow.setManaged(true);
            }
        } else {
            leftBox.getStyleClass().add("stat-lose");
            rightBox.getStyleClass().add("stat-win");
            if (rightArrow != null) {
                rightArrow.setVisible(true);
                rightArrow.setManaged(true);
            }
        }
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

    @FXML
    private void toggleProfileMenu() {
        boolean show = !profileMenu.isVisible();
        profileMenu.setVisible(show);
        profileMenu.setManaged(show);

        if (show) {
            profileMenu.toFront();
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout();
        updateLoggedInUI();
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
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm()
            );

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
    private void handleNewComparison() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompareView.fxml"));
            Parent root = loader.load();

            Scene currentScene = navLogo.getScene();
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
