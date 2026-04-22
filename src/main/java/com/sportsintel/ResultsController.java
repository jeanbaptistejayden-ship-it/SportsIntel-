package com.sportsintel;

import javafx.application.Platform;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
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
    private Label trendSummaryLabel;
    @FXML
    private Label matchupStrengthSummaryLabel;
    @FXML
    private Label homeAwaySummaryLabel;
    @FXML
    private Label recentFormSummaryLabel;
    @FXML
    private Label lastFiveRow1DateLabel;
    @FXML
    private Label lastFiveRow1OpponentLabel;
    @FXML
    private Label lastFiveRow1PtsLabel;
    @FXML
    private Label lastFiveRow1RebLabel;
    @FXML
    private Label lastFiveRow1AstLabel;
    @FXML
    private Label lastFiveRow1MinLabel;
    @FXML
    private Label lastFiveRow1FgLabel;
    @FXML
    private Label lastFiveRow1ResultLabel;
    @FXML
    private Label lastFiveRow2DateLabel;
    @FXML
    private Label lastFiveRow2OpponentLabel;
    @FXML
    private Label lastFiveRow2PtsLabel;
    @FXML
    private Label lastFiveRow2RebLabel;
    @FXML
    private Label lastFiveRow2AstLabel;
    @FXML
    private Label lastFiveRow2MinLabel;
    @FXML
    private Label lastFiveRow2FgLabel;
    @FXML
    private Label lastFiveRow2ResultLabel;
    @FXML
    private Label lastFiveRow3DateLabel;
    @FXML
    private Label lastFiveRow3OpponentLabel;
    @FXML
    private Label lastFiveRow3PtsLabel;
    @FXML
    private Label lastFiveRow3RebLabel;
    @FXML
    private Label lastFiveRow3AstLabel;
    @FXML
    private Label lastFiveRow3MinLabel;
    @FXML
    private Label lastFiveRow3FgLabel;
    @FXML
    private Label lastFiveRow3ResultLabel;
    @FXML
    private Label lastFiveRow4DateLabel;
    @FXML
    private Label lastFiveRow4OpponentLabel;
    @FXML
    private Label lastFiveRow4PtsLabel;
    @FXML
    private Label lastFiveRow4RebLabel;
    @FXML
    private Label lastFiveRow4AstLabel;
    @FXML
    private Label lastFiveRow4MinLabel;
    @FXML
    private Label lastFiveRow4FgLabel;
    @FXML
    private Label lastFiveRow4ResultLabel;
    @FXML
    private Label lastFiveRow5DateLabel;
    @FXML
    private Label lastFiveRow5OpponentLabel;
    @FXML
    private Label lastFiveRow5PtsLabel;
    @FXML
    private Label lastFiveRow5RebLabel;
    @FXML
    private Label lastFiveRow5AstLabel;
    @FXML
    private Label lastFiveRow5MinLabel;
    @FXML
    private Label lastFiveRow5FgLabel;
    @FXML
    private Label lastFiveRow5ResultLabel;

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
        loadPlayerImage(search.playerImageUrl());
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
        updateText(averageUnitLabel, shortStat + " Average");

        updateText(baselineTitleLabel, "Current Season");
        updateText(baselineValueLabel, String.format("%.1f", search.seasonBaseline()));
        updateText(baselineUnitLabel, shortStat + " Average");

        updateText(lastFiveTitleLabel, "Last 5 Games");
        updateText(lastFiveValueLabel, String.format("%.1f", search.lastFiveAverage()));
        updateText(lastFiveUnitLabel, shortStat + " Recent");

        updateText(lastTenTitleLabel, "Last 10 Games");
        updateText(lastTenValueLabel, String.format("%.1f", search.lastTenAverage()));
        updateText(lastTenUnitLabel, shortStat + " L10");

        updateText(gamesPlayedLabel, String.valueOf(search.gamesPlayed()));
        updateText(highValueLabel, String.format("%.1f", search.high()));
        updateText(lowValueLabel, String.format("%.1f", search.low()));
        updateText(homeValueLabel, String.format("%.1f", search.homeAverage()));
        updateText(awayValueLabel, String.format("%.1f", search.awayAverage()));
        updateText(fgValueLabel, String.format("%.1f", search.fieldGoalPct()));
        updateText(astValueLabel, String.format("%.1f", search.assists()));
        updateText(rebValueLabel, String.format("%.1f", search.rebounds()));
        updateText(tovValueLabel, String.format("%.1f", search.turnovers()));
        updateText(minValueLabel, String.format("%.1f", search.minutes()));

        fillLastFiveTable(search.lastFiveVsOpponent());

        double trendDiff = search.lastFiveAverage() - search.seasonBaseline();
        String trendDirection = trendDiff >= 0 ? "upward" : "downward";
        updateText(
                trendSummaryLabel,
                String.format(
                        "Trending %s: last 5 games are %.1f %s versus selected baseline %.1f %s (Δ %.1f).",
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
                        "Career vs opponent: %.1f %s, %.1f APG, %.1f RPG across %d games.",
                        search.opponentCareerAverage(),
                        shortStat,
                        search.assists(),
                        search.rebounds(),
                        search.gamesPlayed()
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

        updateText(
                recentFormSummaryLabel,
                String.format(
                        "Career overview: %.1f %s, %.1f APG, %.1f RPG across %d games.",
                        search.careerAverage(),
                        shortStat,
                        search.careerAssists(),
                        search.careerRebounds(),
                        search.careerGames()
                )
        );
    }

    private void fillLastFiveTable(List<SessionManager.LastGameRow> rows) {
        SessionManager.LastGameRow[] normalized = new SessionManager.LastGameRow[5];
        for (int i = 0; i < normalized.length; i++) {
            if (rows != null && i < rows.size()) {
                normalized[i] = rows.get(i);
            } else {
                normalized[i] = new SessionManager.LastGameRow("-", "-", 0.0, 0.0, 0.0, 0.0, 0.0, "-");
            }
        }

        setRow(normalized[0], lastFiveRow1DateLabel, lastFiveRow1OpponentLabel, lastFiveRow1PtsLabel, lastFiveRow1RebLabel, lastFiveRow1AstLabel, lastFiveRow1MinLabel, lastFiveRow1FgLabel, lastFiveRow1ResultLabel);
        setRow(normalized[1], lastFiveRow2DateLabel, lastFiveRow2OpponentLabel, lastFiveRow2PtsLabel, lastFiveRow2RebLabel, lastFiveRow2AstLabel, lastFiveRow2MinLabel, lastFiveRow2FgLabel, lastFiveRow2ResultLabel);
        setRow(normalized[2], lastFiveRow3DateLabel, lastFiveRow3OpponentLabel, lastFiveRow3PtsLabel, lastFiveRow3RebLabel, lastFiveRow3AstLabel, lastFiveRow3MinLabel, lastFiveRow3FgLabel, lastFiveRow3ResultLabel);
        setRow(normalized[3], lastFiveRow4DateLabel, lastFiveRow4OpponentLabel, lastFiveRow4PtsLabel, lastFiveRow4RebLabel, lastFiveRow4AstLabel, lastFiveRow4MinLabel, lastFiveRow4FgLabel, lastFiveRow4ResultLabel);
        setRow(normalized[4], lastFiveRow5DateLabel, lastFiveRow5OpponentLabel, lastFiveRow5PtsLabel, lastFiveRow5RebLabel, lastFiveRow5AstLabel, lastFiveRow5MinLabel, lastFiveRow5FgLabel, lastFiveRow5ResultLabel);
    }

    private void setRow(
            SessionManager.LastGameRow row,
            Label dateLabel,
            Label opponentLabel,
            Label ptsLabel,
            Label rebLabel,
            Label astLabel,
            Label minLabel,
            Label fgLabel,
            Label resultLabel
    ) {
        updateText(dateLabel, row.date());
        updateText(opponentLabel, row.opponent());
        updateText(ptsLabel, String.format("%.0f", row.points()));
        updateText(rebLabel, String.format("%.0f", row.rebounds()));
        updateText(astLabel, String.format("%.0f", row.assists()));
        updateText(minLabel, String.format("%.1f", row.minutes()));
        updateText(fgLabel, String.format("%.1f%%", row.fieldGoalPct()));
        updateText(resultLabel, row.result());
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

    public void loadPlayerImage(String imageUrl) {
        try {
            System.out.println("Loading image: " + imageUrl);

            if (imageUrl == null || imageUrl.isBlank()) {
                return;
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                System.out.println("Image HTTP status: " + response.statusCode());
                return;
            }

            Image image = new Image(new ByteArrayInputStream(response.body()));

            Platform.runLater(() -> playerImage.setImage(image));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
