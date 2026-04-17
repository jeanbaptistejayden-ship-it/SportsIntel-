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
    private Label baselineTitleLabel;
    @FXML
    private Label baselineValueLabel;
    @FXML
    private Label baselineUnitLabel;
    @FXML
    private Label lastFiveTitleLabel;
    @FXML
    private Label lastFiveValueLabel;
    @FXML
    private Label lastFiveUnitLabel;
    @FXML
    private Label lastTenTitleLabel;
    @FXML
    private Label lastTenValueLabel;
    @FXML
    private Label lastTenUnitLabel;

    @FXML
    private Label gamesPlayedLabel;

    @FXML
    private Label highValueLabel;

    @FXML
    private Label lowValueLabel;
    @FXML
    private Label fgValueLabel;
    @FXML
    private Label minValueLabel;
    @FXML
    private Label astValueLabel;
    @FXML
    private Label rebValueLabel;
    @FXML
    private Label tovValueLabel;
    @FXML
    private Label homeValueLabel;
    @FXML
    private Label awayValueLabel;
    @FXML
    private Label bestMatchupTeamLabel;
    @FXML
    private Label bestMatchupValueLabel;
    @FXML
    private Label toughestMatchupTeamLabel;
    @FXML
    private Label toughestMatchupValueLabel;
    @FXML
    private Label trendSummaryLabel;
    @FXML
    private Label matchupStrengthSummaryLabel;
    @FXML
    private Label homeAwaySummaryLabel;
    @FXML
    private Label recentFormSummaryLabel;

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
        String shortStat = shortStatLabel(search.stat());
        updateText(averageUnitLabel, shortStat + " average");

        updateText(baselineTitleLabel, "Season Baseline");
        updateText(baselineValueLabel, String.format("%.1f", search.seasonBaseline()));
        updateText(baselineUnitLabel, shortStat + " baseline");

        updateText(lastFiveTitleLabel, "Last 5 Games");
        updateText(lastFiveValueLabel, String.format("%.1f", search.lastFiveAverage()));
        updateText(lastFiveUnitLabel, shortStat + " recent");

        updateText(lastTenTitleLabel, "Last 10 Games");
        updateText(lastTenValueLabel, String.format("%.1f", search.lastTenAverage()));
        updateText(lastTenUnitLabel, shortStat + " L10");

        updateText(gamesPlayedLabel, String.valueOf(search.gamesPlayed()));
        updateText(highValueLabel, String.format("%.1f", search.high()));
        updateText(lowValueLabel, String.format("%.1f", search.low()));
        updateText(fgValueLabel, String.format("%.1f", search.fieldGoalPct()));
        updateText(minValueLabel, String.format("%.1f", search.minutes()));
        updateText(astValueLabel, String.format("%.1f", search.assists()));
        updateText(rebValueLabel, String.format("%.1f", search.rebounds()));
        updateText(tovValueLabel, String.format("%.1f", search.turnovers()));
        updateText(homeValueLabel, String.format("%.1f", search.homeAverage()));
        updateText(awayValueLabel, String.format("%.1f", search.awayAverage()));

        updateText(bestMatchupTeamLabel, search.bestGameOpponent());
        updateText(bestMatchupValueLabel, String.format("%.1f %s", search.bestGameValue(), shortStat));
        updateText(toughestMatchupTeamLabel, search.toughestGameOpponent());
        updateText(toughestMatchupValueLabel, String.format("%.1f %s", search.toughestGameValue(), shortStat));

        double trendDiff = search.lastFiveAverage() - search.seasonBaseline();
        String trendDirection = trendDiff >= 0 ? "upward" : "downward";
        updateText(
                trendSummaryLabel,
                String.format(
                        "Trending %s: last 5 games are %.1f %s versus baseline %.1f %s (Δ %.1f).",
                        trendDirection,
                        search.lastFiveAverage(),
                        shortStat,
                        search.seasonBaseline(),
                        shortStat,
                        trendDiff
                )
        );

        updateText(
                matchupStrengthSummaryLabel,
                String.format(
                        "Best single-game output came vs %s (%.1f %s); toughest was vs %s (%.1f %s).",
                        search.bestGameOpponent(),
                        search.bestGameValue(),
                        shortStat,
                        search.toughestGameOpponent(),
                        search.toughestGameValue(),
                        shortStat
                )
        );

        double homeAwayDelta = search.homeAverage() - search.awayAverage();
        String locationEdge = homeAwayDelta >= 0 ? "home" : "away";
        updateText(
                homeAwaySummaryLabel,
                String.format(
                        "Split favors %s: home %.1f vs away %.1f %s (Δ %.1f).",
                        locationEdge,
                        search.homeAverage(),
                        search.awayAverage(),
                        shortStat,
                        homeAwayDelta
                )
        );

        double recentFormDelta = search.lastFiveAverage() - search.lastTenAverage();
        String formDirection = recentFormDelta >= 0 ? "positive momentum" : "cooling off";
        updateText(
                recentFormSummaryLabel,
                String.format(
                        "Recent form shows %s (last 5: %.1f, last 10: %.1f %s).",
                        formDirection,
                        search.lastFiveAverage(),
                        search.lastTenAverage(),
                        shortStat
                )
        );
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
        String normalized = statLabel.toLowerCase();
        if (normalized.contains("assist")) {
            return "APG";
        }
        if (normalized.contains("rebound")) {
            return "RPG";
        }
        if (normalized.contains("field goal")) {
            return "FG%";
        }
        if (normalized.contains("3pt")) {
            return "3PT%";
        }
        if (normalized.contains("free throw")) {
            return "FT%";
        }
        if (normalized.contains("steal")) {
            return "SPG";
        }
        if (normalized.contains("block")) {
            return "BPG";
        }
        if (normalized.contains("turnover")) {
            return "TOPG";
        }
        if (normalized.contains("minute")) {
            return "MPG";
        }
        if (normalized.contains("plus/minus")) {
            return "+/-";
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
