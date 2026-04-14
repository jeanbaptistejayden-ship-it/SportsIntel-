package com.sportsintel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeController {
    private static final String API_BASE_URL = "http://127.0.0.1:8000";
    private static final Map<String, String> OPPONENT_TO_ABBR = createOpponentMap();

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
    private TextField seasonStartField;

    @FXML
    private TextField seasonEndField;

    @FXML
    private TextField lastNGamesField;

    @FXML
    private TextField playerNameField;

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
            statisticCombo.getItems().addAll("Points Per Game",
                    "Assists Per Game",
                    "Rebounds Per Game",
                    "Field Goal Percentage",
                    "3pt Throw Percentage",
                    "Free Throw Percentage",
                    "Steals Per Game",
                    "Blocks Per Game",
                    "Turnovers Per Game",
                    "Minutes Per Game",
                    "Plus/Minus");
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

        if (SessionManager.isLoggedIn()) {
            setLoggedInUser(
                    SessionManager.getFullName(),
                    SessionManager.getUsername()
            );
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

            LoginController loginController = loader.getController();
            loginController.setHomeController(this);

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

            updateLoggedInUI();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSearchClick() {
        try {
            String playerName = playerNameField != null ? playerNameField.getText().trim() : "";
            if (playerName.isEmpty()) {
                showError("Please enter a player name.");
                return;
            }

            String selectedSport = sportCombo != null ? sportCombo.getValue() : "Basketball";
            if (selectedSport != null && !selectedSport.startsWith("Basketball")) {
                showError("Only Basketball is supported right now.");
                return;
            }

            String seasonType = mapSeasonType(seasonTypeCombo != null ? seasonTypeCombo.getValue() : null);
            String location = mapLocation(locationCombo != null ? locationCombo.getValue() : null);
            String stat = mapStat(statisticCombo != null ? statisticCombo.getValue() : null);
            String season = buildSeasonParam();
            String opponent = mapOpponent(opponentCombo != null ? opponentCombo.getValue() : null);
            Integer lastN = parseLastN();

            String requestUrl = buildPlayerRequestUrl(playerName, season, seasonType, location, opponent, lastN, stat);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                showError("Backend request failed: HTTP " + response.statusCode());
                return;
            }

            JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
            if (body.has("error")) {
                showError(body.get("error").getAsString());
                return;
            }

            JsonObject summary = body.getAsJsonObject("summary");
            JsonObject meta = body.getAsJsonObject("meta");

            SessionManager.setLatestSearch(new SessionManager.SearchResult(
                    summary.get("player").getAsString(),
                    opponentCombo != null ? opponentCombo.getValue() : "Any Opponent",
                    formatStatLabel(summary.get("stat").getAsString()),
                    meta.get("season").getAsString(),
                    meta.get("season_type").getAsString(),
                    meta.get("location").getAsString(),
                    meta.get("last_n").isJsonNull() ? "All" : meta.get("last_n").getAsString(),
                    summary.get("games_played").getAsInt(),
                    summary.get("average").getAsDouble(),
                    summary.get("high").getAsDouble(),
                    summary.get("low").getAsDouble()
            ));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResultsView.fxml"));
            Parent root = loader.load();

            Scene currentScene = navLogo.getScene();
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open results page.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not retrieve player data from backend.");
        }
    }

    public void setLoggedInUser(String fullName, String username) {
        authButtons.setVisible(false);
        authButtons.setManaged(false);

        profileBox.setVisible(true);
        profileBox.setManaged(true);

        profileNameLabel.setText(fullName);
        profileUsernameLabel.setText(username);

        profileMenu.setVisible(false);
        profileMenu.setManaged(false);
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

    private String buildSeasonParam() {
        if (seasonStartField == null) {
            return null;
        }
        String rawStart = seasonStartField.getText() == null ? "" : seasonStartField.getText().trim();
        if (rawStart.isEmpty()) {
            return null;
        }

        int startYear;
        try {
            startYear = Integer.parseInt(rawStart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Season start year must be a number.");
        }

        int endYear = startYear + 1;
        return startYear + "-" + String.valueOf(endYear).substring(2);
    }

    private Integer parseLastN() {
        if (lastNGamesField == null) {
            return null;
        }
        String value = lastNGamesField.getText() == null ? "" : lastNGamesField.getText().trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                throw new IllegalArgumentException("Last N Games must be a positive number.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Last N Games must be a number.");
        }
    }

    private String mapSeasonType(String value) {
        if (value == null || value.equalsIgnoreCase("Both")) {
            return "both";
        }
        if (value.equalsIgnoreCase("Regular Season")) {
            return "regular";
        }
        if (value.equalsIgnoreCase("Playoffs")) {
            return "playoffs";
        }
        return "both";
    }

    private String mapLocation(String value) {
        if (value == null || value.equalsIgnoreCase("Both")) {
            return "both";
        }
        if (value.equalsIgnoreCase("Home")) {
            return "home";
        }
        if (value.equalsIgnoreCase("Away")) {
            return "away";
        }
        return "both";
    }

    private String mapStat(String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("Points Per Game")) {
            return "ppg";
        }
        if (value.equalsIgnoreCase("Assists Per Game")) {
            return "apg";
        }
        if (value.equalsIgnoreCase("Rebounds Per Game")) {
            return "rpg";
        }
        throw new IllegalArgumentException("Only Points, Assists, and Rebounds are supported right now.");
    }

    private String mapOpponent(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return OPPONENT_TO_ABBR.getOrDefault(value, null);
    }

    private String buildPlayerRequestUrl(
            String playerName,
            String season,
            String seasonType,
            String location,
            String opponent,
            Integer lastN,
            String stat
    ) {
        StringBuilder url = new StringBuilder(API_BASE_URL)
                .append("/player/")
                .append(URLEncoder.encode(playerName, StandardCharsets.UTF_8).replace("+", "%20"));

        url.append("?season_type=").append(URLEncoder.encode(seasonType, StandardCharsets.UTF_8));
        url.append("&location=").append(URLEncoder.encode(location, StandardCharsets.UTF_8));
        url.append("&stat=").append(URLEncoder.encode(stat, StandardCharsets.UTF_8));

        if (season != null) {
            url.append("&season=").append(URLEncoder.encode(season, StandardCharsets.UTF_8));
        }
        if (opponent != null) {
            url.append("&opponent=").append(URLEncoder.encode(opponent, StandardCharsets.UTF_8));
        }
        if (lastN != null) {
            url.append("&last_n=").append(lastN);
        }
        return url.toString();
    }

    private String formatStatLabel(String stat) {
        if ("ast".equalsIgnoreCase(stat)) {
            return "Assists Per Game";
        }
        if ("reb".equalsIgnoreCase(stat)) {
            return "Rebounds Per Game";
        }
        return "Points Per Game";
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Search Error");
        alert.setHeaderText("Unable to run search");
        alert.setContentText(message);
        alert.showAndWait();
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
}
