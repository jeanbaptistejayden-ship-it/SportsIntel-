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
import java.util.Objects;

public class ResultsController {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

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
    private VBox lastFiveRowsBox;

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

        String opponent = isBlank(search.opponent()) ? "Any Opponent" : search.opponent();
        String statLabel = defaultString(search.stat(), "Points Per Game");
        String shortStat = shortStatLabel(statLabel);

        updateText(playerSummaryLabel, defaultString(search.player(), "Unknown Player"));
        updateText(opponentSummaryLabel, "vs " + opponent);
        updateText(statSummaryLabel, statLabel);

        updateText(seasonFilterLabel, defaultString(search.season(), "Unknown"));
        updateText(seasonTypeFilterLabel, prettifySeasonType(search.seasonType()));
        updateText(locationFilterLabel, prettifyLocation(search.location()));
        updateText(lastNFilterLabel, defaultString(search.lastN(), "All"));

        updateText(averageTitleLabel, "vs " + opponent);
        updateText(averageValueLabel, formatOneDecimal(search.average()));
        updateText(averageUnitLabel, shortStat + " Average");

        updateText(baselineTitleLabel, "Career Average");
        updateText(baselineValueLabel, formatOneDecimal(search.seasonBaseline()));
        updateText(baselineUnitLabel, shortStat + " Average");

        updateText(lastFiveTitleLabel, "Last 5 Games");
        updateText(lastFiveValueLabel, formatOneDecimal(search.lastFiveAverage()));
        updateText(lastFiveUnitLabel, shortStat + " Recent");

        updateText(lastTenTitleLabel, "Last 10 Games");
        updateText(lastTenValueLabel, formatOneDecimal(search.lastTenAverage()));
        updateText(lastTenUnitLabel, shortStat + " Recent");

        updateText(gamesPlayedLabel, String.valueOf(search.gamesPlayed()));
        updateText(highValueLabel, formatOneDecimal(search.high()));
        updateText(lowValueLabel, formatOneDecimal(search.low()));
        updateText(homeValueLabel, formatOneDecimal(search.homeAverage()));
        updateText(awayValueLabel, formatOneDecimal(search.awayAverage()));
        updateText(fgValueLabel, formatOneDecimal(search.pointsAverage()));
        updateText(astValueLabel, formatOneDecimal(search.assists()));
        updateText(rebValueLabel, formatOneDecimal(search.rebounds()));
        updateText(tovValueLabel, formatOneDecimal(search.turnovers()));
        updateText(minValueLabel, formatOneDecimal(search.minutes()));

        updateText(bestMatchupTeamLabel, defaultString(search.bestGameOpponent(), "N/A"));
        updateText(bestMatchupValueLabel, formatOneDecimal(search.bestGameValue()) + " " + shortStat);
        updateText(toughestMatchupTeamLabel, defaultString(search.toughestGameOpponent(), "N/A"));
        updateText(toughestMatchupValueLabel, formatOneDecimal(search.toughestGameValue()) + " " + shortStat);

        double trendDiff = search.lastFiveAverage() - search.seasonBaseline();
        String trendDirection = trendDiff >= 0 ? "upward" : "downward";
        updateText(
                trendSummaryLabel,
                String.format(
                        "Trending %s: last 5 games are %.1f %s versus average %.1f %s (Δ %.1f).",
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
                        "In %d games vs %s, %s averages %.1f PPG, %.1f APG, %.1f RPG in %.1f MPG.",
                        search.careerVsOpponentGamesPlayed(),
                        opponent,
                        defaultString(search.player(), "The player"),
                        search.careerVsOpponentPpg(),
                        search.careerVsOpponentApg(),
                        search.careerVsOpponentRpg(),
                        search.careerVsOpponentMpg()
                )
        );

        updateText(
                recentFormSummaryLabel,
                String.format(
                        "Career Averages: %.1f PPG, %.1f APG, %.1f RPG, %.1f MPG, %.1f FG%%, %.1f 3P%%, %.1f TOPG, %.1f BPG, %.1f SPG.",
                        search.careerPpg(),
                        search.careerApg(),
                        search.careerRpg(),
                        search.careerMpg(),
                        search.careerFgPct(),
                        search.careerThreePtPct(),
                        search.careerTopg(),
                        search.careerBpg(),
                        search.careerSpg()
                )
        );

        loadPlayerImage(search.playerImageUrl());
        renderRecentGames(search.recentGamesVsOpponent());
    }

    @FXML
    private void handleHelpClick() {
        openModal("/HelpView.fxml", "Help & Support", 900, 700, true);
    }

    @FXML
    private void handleLoginClick() {
        openModal("/LoginView.fxml", "Login", 480, 700, false);
        updateLoggedInUI();
    }

    @FXML
    private void handleSignUpClick() {
        openModal("/SignUpView.fxml", "Sign Up", 520, 920, false);
        updateLoggedInUI();
    }

    @FXML
    private void handleHomeClick() {
        try {
            navigateTo("/HomeView.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCompareClick() {
        try {
            navigateTo("/CompareView.fxml");
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
        if (label != null) {
            label.setText(value == null ? "" : value);
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

    private String prettifySeasonType(String value) {
        if (value == null) {
            return "Both";
        }
        if (value.equalsIgnoreCase("regular")) {
            return "Regular Season";
        }
        if (value.equalsIgnoreCase("playoffs")) {
            return "Playoffs";
        }
        return "Both";
    }

    private String prettifyLocation(String value) {
        if (value == null) {
            return "Both";
        }
        if (value.equalsIgnoreCase("home")) {
            return "Home";
        }
        if (value.equalsIgnoreCase("away")) {
            return "Away";
        }
        return "Both";
    }

    private String defaultString(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String formatOneDecimal(double value) {
        return String.format("%.1f", value);
    }

    public void loadPlayerImage(String imageUrl) {
        try {
            if (isBlank(imageUrl)) {
                return;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
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

    private void navigateTo(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Scene currentScene = navLogo.getScene();
        currentScene.setRoot(root);
    }

    private void openModal(String fxmlPath, String title, int width, int height, boolean resizable) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(resizable);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderRecentGames(java.util.List<SessionManager.RecentGame> games) {
        if (lastFiveRowsBox == null) {
            return;
        }

        lastFiveRowsBox.getChildren().clear();

        for (SessionManager.RecentGame game : games) {
            HBox row = new HBox();
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setSpacing(0);
            row.getStyleClass().add("last-five-data-row");

            row.getChildren().add(makeTableCell(game.date(), 160, "last-five-cell"));
            row.getChildren().add(makeTableCell(game.opponent(), 120, "last-five-cell"));
            row.getChildren().add(makeTableCell(String.format("%.1f", game.points()), 90, "last-five-cell", "centered-cell", "value-blue"));
            row.getChildren().add(makeTableCell(String.format("%.1f", game.rebounds()), 90, "last-five-cell", "centered-cell"));
            row.getChildren().add(makeTableCell(String.format("%.1f", game.assists()), 90, "last-five-cell", "centered-cell"));
            row.getChildren().add(makeTableCell(String.format("%.1f", game.minutes()), 90, "last-five-cell", "centered-cell"));
            row.getChildren().add(makeTableCell(String.format("%.1f%%", game.fieldGoalPct()), 110, "last-five-cell", "centered-cell"));
            row.getChildren().add(makeTableCell(String.format("%.1f%%", game.threePointPct()), 110, "last-five-cell", "centered-cell"));

            lastFiveRowsBox.getChildren().add(row);
        }
    }

    private Label makeTableCell(String text, double width, String... styleClasses) {
        Label label = new Label(text);

        label.setMinWidth(width);
        label.setPrefWidth(width);
        label.setMaxWidth(width);

        if (width == 160 || width == 120) {
            label.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        } else {
            label.setAlignment(javafx.geometry.Pos.CENTER);
        }

        label.getStyleClass().addAll(styleClasses);
        return label;
    }
}