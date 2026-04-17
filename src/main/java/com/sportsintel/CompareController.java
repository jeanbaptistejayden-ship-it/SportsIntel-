package com.sportsintel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CompareController {
    private static final String API_BASE_URL = "http://127.0.0.1:8000";
    private static final Map<String, String> OPPONENT_TO_ABBR = createOpponentMap();

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
        try {
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

            JsonObject playerOneData = fetchPlayerData(playerOne, opponentAbbr);
            JsonObject playerTwoData = fetchPlayerData(playerTwo, opponentAbbr);

            SessionManager.setLatestCompare(new SessionManager.CompareResult(
                    playerOne,
                    playerTwo,
                    opponentName,
                    summaryAverage(playerOneData),
                    summaryAverage(playerTwoData),
                    averageFromGames(playerOneData.getAsJsonArray("games"), "ast"),
                    averageFromGames(playerTwoData.getAsJsonArray("games"), "ast"),
                    averageFromGames(playerOneData.getAsJsonArray("games"), "reb"),
                    averageFromGames(playerTwoData.getAsJsonArray("games"), "reb"),
                    averageFromGames(playerOneData.getAsJsonArray("games"), "fg_pct"),
                    averageFromGames(playerTwoData.getAsJsonArray("games"), "fg_pct"),
                    averageFromGames(playerOneData.getAsJsonArray("games"), "min"),
                    averageFromGames(playerTwoData.getAsJsonArray("games"), "min"),
                    summaryGamesPlayed(playerOneData),
                    summaryGamesPlayed(playerTwoData)
            ));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompareResultsView.fxml"));
            Parent root = loader.load();

            Scene currentScene = navLogo.getScene();
            currentScene.setRoot(root);

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open compare results page.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not retrieve comparison data from backend.");
        }
    }

    private JsonObject fetchPlayerData(String playerName, String opponentAbbr) throws Exception {
        int currentYear = LocalDate.now().getYear();
        String[] seasonsToTry = {
                formatSeason(currentYear - 1),
                formatSeason(currentYear - 2),
                formatSeason(currentYear - 3)
        };

        IllegalArgumentException lastError = null;
        for (String season : seasonsToTry) {
            String requestUrl = buildPlayerRequestUrl(playerName, season, opponentAbbr);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalArgumentException("Backend request failed: HTTP " + response.statusCode());
            }

            JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
            if (body.has("error")) {
                String error = body.get("error").getAsString();
                if (error.toLowerCase(Locale.ROOT).contains("no games found")) {
                    lastError = new IllegalArgumentException(error);
                    continue;
                }
                throw new IllegalArgumentException(error);
            }
            return body;
        }

        if (lastError != null) {
            throw lastError;
        }
        throw new IllegalArgumentException("No games found for that comparison.");
    }

    private String buildPlayerRequestUrl(String playerName, String season, String opponentAbbr) {
        StringBuilder url = new StringBuilder(API_BASE_URL)
                .append("/player/")
                .append(URLEncoder.encode(playerName, StandardCharsets.UTF_8).replace("+", "%20"));

        url.append("?season_type=both");
        url.append("&location=both");
        url.append("&stat=ppg");
        url.append("&season=").append(URLEncoder.encode(season, StandardCharsets.UTF_8));
        url.append("&opponent=").append(URLEncoder.encode(opponentAbbr, StandardCharsets.UTF_8));
        return url.toString();
    }

    private static String formatSeason(int startYear) {
        int endYear = startYear + 1;
        return startYear + "-" + String.valueOf(endYear).substring(2);
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
