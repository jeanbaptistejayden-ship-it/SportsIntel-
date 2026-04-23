package com.sportsintel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CompareController {
    private static final String API_BASE_URL = "http://127.0.0.1:8000";
    private static final Map<String, String> OPPONENT_TO_ABBR = createOpponentMap();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build();

    @FXML
    private ImageView navLogo;

    @FXML
    private ImageView mainLogo;

    @FXML
    private ComboBox<String> compareOpponentCombo;

    @FXML
    private TextField playerOneField;

    @FXML
    private TextField playerTwoField;

    @FXML
    private Button compareSubmitButton;

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
    private void handleCompareSubmit() {
        String playerOne = playerOneField != null ? playerOneField.getText().trim() : "";
        String playerTwo = playerTwoField != null ? playerTwoField.getText().trim() : "";
        String opponentName = compareOpponentCombo != null ? compareOpponentCombo.getValue() : null;
        String opponentAbbr = mapOpponent(opponentName);

        if (playerOne.isEmpty() || playerTwo.isEmpty()) {
            showError("Please enter both player names.");
            return;
        }
        if (opponentAbbr == null) {
            showError("Please select an opponent.");
            return;
        }

        setCompareLoading(true);

        CompletableFuture<JsonObject> playerOneFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchPlayerData(playerOne, opponentAbbr);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
        CompletableFuture<JsonObject> playerTwoFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchPlayerData(playerTwo, opponentAbbr);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });

        CompletableFuture.allOf(playerOneFuture, playerTwoFuture)
                .thenAccept(unused -> {
                    JsonObject playerOneData = playerOneFuture.join();
                    JsonObject playerTwoData = playerTwoFuture.join();

                    SessionManager.setLatestCompare(new SessionManager.CompareResult(
                            playerOne,
                            playerTwo,
                            opponentName,
                            summaryAverage(playerOneData),
                            summaryAverage(playerTwoData),
                            summaryAverageStat(playerOneData, "ast_average", "ast"),
                            summaryAverageStat(playerTwoData, "ast_average", "ast"),
                            summaryAverageStat(playerOneData, "reb_average", "reb"),
                            summaryAverageStat(playerTwoData, "reb_average", "reb"),
                            summaryAverageStat(playerOneData, "fg_pct_average", "fg_pct"),
                            summaryAverageStat(playerTwoData, "fg_pct_average", "fg_pct"),
                            summaryAverageStat(playerOneData, "min_average", "min"),
                            summaryAverageStat(playerTwoData, "min_average", "min"),
                            summaryGamesPlayed(playerOneData),
                            summaryGamesPlayed(playerTwoData)
                    ));

                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompareResultsView.fxml"));
                            Parent root = loader.load();
                            Scene currentScene = navLogo.getScene();
                            currentScene.setRoot(root);
                        } catch (IOException e) {
                            setCompareLoading(false);
                            showError("Could not open compare results page.");
                        }
                    });
                })
                .exceptionally(error -> {
                    Platform.runLater(() -> {
                        setCompareLoading(false);
                        showError(resolveCompareErrorMessage(error));
                    });
                    return null;
                });
    }

    private JsonObject fetchPlayerData(String playerName, String opponentAbbr) throws Exception {
        int seasonEndYear = currentSeasonStartYear();

        try {
            return requestPlayerData(buildSeasonRangeRequestUrl(playerName, opponentAbbr, seasonEndYear - 3, seasonEndYear));
        } catch (IllegalArgumentException firstError) {
            if (!isRecoverableCompareError(firstError)) {
                throw firstError;
            }
        }

        try {
            return requestPlayerData(buildSeasonRangeRequestUrl(playerName, opponentAbbr, seasonEndYear - 7, seasonEndYear));
        } catch (IllegalArgumentException secondError) {
            if (!isRecoverableCompareError(secondError)) {
                throw secondError;
            }
        }

        return requestPlayerData(buildCareerRequestUrl(playerName, opponentAbbr));
    }

    private JsonObject requestPlayerData(String requestUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .timeout(Duration.ofSeconds(45))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalArgumentException("Backend request failed: HTTP " + response.statusCode());
        }

        JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
        if (body.has("error")) {
            throw new IllegalArgumentException(body.get("error").getAsString());
        }
        return body;
    }

    private String buildCareerRequestUrl(String playerName, String opponentAbbr) {
        StringBuilder url = new StringBuilder(API_BASE_URL)
                .append("/player/")
                .append(URLEncoder.encode(playerName, StandardCharsets.UTF_8).replace("+", "%20"));

        url.append("?career=true");
        url.append("&season_type=regular");
        url.append("&location=both");
        url.append("&stat=ppg");
        url.append("&opponent=").append(URLEncoder.encode(opponentAbbr, StandardCharsets.UTF_8));
        url.append("&include_games=false");
        return url.toString();
    }

    private String buildSeasonRangeRequestUrl(String playerName, String opponentAbbr, int seasonStartYear, int seasonEndYear) {
        StringBuilder url = new StringBuilder(API_BASE_URL)
                .append("/player/")
                .append(URLEncoder.encode(playerName, StandardCharsets.UTF_8).replace("+", "%20"));

        url.append("?season_start=").append(seasonStartYear);
        url.append("&season_end=").append(seasonEndYear);
        url.append("&season_type=regular");
        url.append("&location=both");
        url.append("&stat=ppg");
        url.append("&opponent=").append(URLEncoder.encode(opponentAbbr, StandardCharsets.UTF_8));
        url.append("&include_games=false");
        return url.toString();
    }

    private boolean isRecoverableCompareError(IllegalArgumentException error) {
        String message = error.getMessage();
        if (message == null) {
            return false;
        }
        String normalized = message.toLowerCase();
        return normalized.contains("could not reach stats.nba.com")
                || normalized.contains("unexpected backend error while fetching player data")
                || normalized.contains("no games found for");
    }

    private int currentSeasonStartYear() {
        LocalDate now = LocalDate.now();
        return now.getMonthValue() >= 10 ? now.getYear() : now.getYear() - 1;
    }

    private String mapOpponent(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return OPPONENT_TO_ABBR.getOrDefault(value, null);
    }

    private double summaryAverage(JsonObject body) {
        return body.getAsJsonObject("summary").get("average").getAsDouble();
    }

    private int summaryGamesPlayed(JsonObject body) {
        return body.getAsJsonObject("summary").get("games_played").getAsInt();
    }

    private double summaryAverageStat(JsonObject body, String summaryKey, String fallbackGameKey) {
        JsonObject summary = body.getAsJsonObject("summary");
        if (summary.has(summaryKey) && !summary.get(summaryKey).isJsonNull()) {
            return summary.get(summaryKey).getAsDouble();
        }
        return averageFromGames(body.getAsJsonArray("games"), fallbackGameKey);
    }

    private double averageFromGames(JsonArray games, String statKey) {
        if (games == null || games.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        int count = 0;
        for (JsonElement game : games) {
            JsonObject gameObj = game.getAsJsonObject();
            if (gameObj.has(statKey) && !gameObj.get(statKey).isJsonNull()) {
                sum += gameObj.get(statKey).getAsDouble();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Comparison Error");
        alert.setHeaderText("Unable to compare players");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String resolveCompareErrorMessage(Throwable error) {
        Throwable current = error;
        while (current instanceof CompletionException && current.getCause() != null) {
            current = current.getCause();
        }
        if (current instanceof IllegalArgumentException && current.getMessage() != null && !current.getMessage().isBlank()) {
            return current.getMessage();
        }
        return "Could not retrieve comparison data from backend.";
    }

    private void setCompareLoading(boolean loading) {
        if (compareSubmitButton != null) {
            compareSubmitButton.setDisable(loading);
            compareSubmitButton.setText(loading ? "Comparing..." : "Compare Players");
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

    private static Map<String, String> createOpponentMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Atlanta Hawks", "ATL");
        map.put("Boston Celtics", "BOS");
        map.put("Brooklyn Nets", "BKN");
        map.put("Charlotte Hornets", "CHA");
        map.put("Chicago Bulls", "CHI");
        map.put("Cleveland Cavaliers", "CLE");
        map.put("Dallas Mavericks", "DAL");
        map.put("Denver Nuggets", "DEN");
        map.put("Detroit Pistons", "DET");
        map.put("Golden State Warriors", "GSW");
        map.put("Houston Rockets", "HOU");
        map.put("Indiana Pacers", "IND");
        map.put("Los Angeles Clippers", "LAC");
        map.put("Los Angeles Lakers", "LAL");
        map.put("Memphis Grizzlies", "MEM");
        map.put("Miami Heat", "MIA");
        map.put("Milwaukee Bucks", "MIL");
        map.put("Minnesota Timberwolves", "MIN");
        map.put("New Orleans Pelicans", "NOP");
        map.put("New York Knicks", "NYK");
        map.put("Oklahoma City Thunder", "OKC");
        map.put("Orlando Magic", "ORL");
        map.put("Philadelphia 76ers", "PHI");
        map.put("Phoenix Suns", "PHX");
        map.put("Portland Trail Blazers", "POR");
        map.put("Sacramento Kings", "SAC");
        map.put("San Antonio Spurs", "SAS");
        map.put("Toronto Raptors", "TOR");
        map.put("Utah Jazz", "UTA");
        map.put("Washington Wizards", "WAS");
        return map;
    }
}
