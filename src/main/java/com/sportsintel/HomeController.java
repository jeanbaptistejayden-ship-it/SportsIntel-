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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HomeController {
    private static final String API_BASE_URL = "http://127.0.0.1:8000";
    private static final int MAX_SEASON_START_YEAR = 2025;
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
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

        initializeCombos();
        updateLoggedInUI();
    }

    private void initializeCombos() {
        if (seasonTypeCombo != null) {
            seasonTypeCombo.getItems().setAll("Regular Season", "Playoffs", "Both");
            seasonTypeCombo.setValue("Both");
        }

        if (locationCombo != null) {
            locationCombo.getItems().setAll("Home", "Away", "Both");
            locationCombo.setValue("Both");
        }

        if (statisticCombo != null) {
            statisticCombo.getItems().setAll(
                    "Points Per Game",
                    "Assists Per Game",
                    "Rebounds Per Game",
                    "Field Goal Percentage",
                    "3pt Percentage",
                    "Free Throw Percentage",
                    "Steals Per Game",
                    "Blocks Per Game",
                    "Turnovers Per Game",
                    "Minutes Per Game",
                    "Plus/Minus"
            );
        }

        if (sportCombo != null) {
            sportCombo.getItems().setAll(
                    "Basketball",
                    "Baseball (Coming Soon)",
                    "Football (Coming Soon)",
                    "Soccer (Coming Soon)"
            );
        }

        if (opponentCombo != null) {
            opponentCombo.getItems().setAll(
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
    }

    @FXML
    private void handleHelpClick() {
        openModal("/HelpView.fxml", "Help & Support", 900, 700, true);
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

            updateLoggedInUI();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open login page.");
        }
    }

    @FXML
    private void handleSignUpClick() {
        openModal("/SignUpView.fxml", "Sign Up", 520, 920, false);
        updateLoggedInUI();
    }

    @FXML
    private void handleSearchClick() {
        try {
            String playerName = getTrimmedText(playerNameField);
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
            String seasonStart = buildSeasonStartParam();
            String seasonEnd = buildSeasonEndParam(seasonStart);
            String opponent = mapOpponent(opponentCombo != null ? opponentCombo.getValue() : null);
            Integer lastN = parseLastN();

            String requestUrl = buildPlayerRequestUrl(
                    playerName,
                    seasonStart,
                    seasonEnd,
                    seasonType,
                    location,
                    opponent,
                    lastN,
                    stat
            );
            System.out.println("Request URL: " + requestUrl);

            JsonObject body = sendJsonRequest(requestUrl);
            JsonObject summary = requireObject(body, "summary");
            JsonObject meta = requireObject(body, "meta");
            JsonArray games = requireArray(body, "games");
            JsonObject careerVsOpponentSummary = requireObject(body, "career_vs_opponent_summary");
            JsonObject careerOverviewSummary = requireObject(body, "career_overview_summary");
            JsonArray recentVsOpponentGames = requireArray(body, "recent_vs_opponent_games");

            String selectedStatKey = getJsonString(summary, "stat", "pts");

            String playerImageUrl = getJsonString(summary, "player_image", "");
            String displayedSeason = (seasonStart == null && seasonEnd == null)
                    ? "No Season Range"
                    : getJsonString(meta, "season_range", getJsonString(meta, "season", "Unknown"));
            String displayedSeasonType = getJsonString(meta, "season_type", "both");
            String displayedLocation = getJsonString(meta, "location", "both");
            String displayedLastN = getNullableJsonString(meta, "last_n", "All");
            String displayedPlayer = getJsonString(summary, "player", playerName);
            String displayedStatLabel = formatStatLabel(selectedStatKey);
            String displayedOpponent = (opponentCombo != null
                    && opponentCombo.getValue() != null
                    && !opponentCombo.getValue().isBlank())
                    ? opponentCombo.getValue()
                    : "Any Opponent";

            double filteredAverageForSelectedStat = getAverageForSelectedStat(games, selectedStatKey);
            double careerAverageForSelectedStat = getCareerAverageForSelectedStat(careerOverviewSummary, selectedStatKey);
            double recentFiveAverageForSelectedStat = getRecentAverageForSelectedStat(games, selectedStatKey, 5);
            double recentTenAverageForSelectedStat = getRecentAverageForSelectedStat(games, selectedStatKey, 10);

            double pointsAverage = averageOf(games, "pts");
            double fieldGoalPctAverage = averageOf(games, "fg_pct");
            double minutesAverage = averageOf(games, "min");
            double assistsAverage = averageOf(games, "ast");
            double reboundsAverage = averageOf(games, "reb");
            double turnoversAverage = averageOf(games, "tov");

            double homeAverageForSelectedStat = averageByLocation(games, true, selectedStatKey);
            double awayAverageForSelectedStat = averageByLocation(games, false, selectedStatKey);

            SessionManager.setLatestSearch(new SessionManager.SearchResult(
                    displayedPlayer,
                    playerImageUrl,
                    displayedOpponent,
                    displayedStatLabel,
                    displayedSeason,
                    displayedSeasonType,
                    displayedLocation,
                    displayedLastN,

                    getJsonInt(summary, "games_played", 0),
                    filteredAverageForSelectedStat,
                    getJsonDouble(summary, "high", 0.0),
                    getJsonDouble(summary, "low", 0.0),

                    careerAverageForSelectedStat,
                    recentFiveAverageForSelectedStat,
                    recentTenAverageForSelectedStat,

                    pointsAverage,
                    fieldGoalPctAverage,
                    minutesAverage,
                    assistsAverage,
                    reboundsAverage,
                    turnoversAverage,
                    homeAverageForSelectedStat,
                    awayAverageForSelectedStat,

                    readGameOpponent(summary, "high_game"),
                    readGameValue(summary, "high_game"),
                    readGameOpponent(summary, "low_game"),
                    readGameValue(summary, "low_game"),

                    getJsonInt(careerVsOpponentSummary, "games_played", 0),
                    getJsonDouble(careerVsOpponentSummary, "ppg", 0.0),
                    getJsonDouble(careerVsOpponentSummary, "apg", 0.0),
                    getJsonDouble(careerVsOpponentSummary, "rpg", 0.0),
                    getJsonDouble(careerVsOpponentSummary, "mpg", 0.0),

                    getJsonDouble(careerOverviewSummary, "ppg", 0.0),
                    getJsonDouble(careerOverviewSummary, "apg", 0.0),
                    getJsonDouble(careerOverviewSummary, "rpg", 0.0),
                    getJsonDouble(careerOverviewSummary, "mpg", 0.0),
                    getJsonDouble(careerOverviewSummary, "fg_pct", 0.0),
                    getJsonDouble(careerOverviewSummary, "fg3_pct", 0.0),
                    getJsonDouble(careerOverviewSummary, "tov", 0.0),
                    getJsonDouble(careerOverviewSummary, "blk", 0.0),
                    getJsonDouble(careerOverviewSummary, "stl", 0.0),

                    parseRecentGames(recentVsOpponentGames)
            ));

            navigateTo("/ResultsView.fxml");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open results page.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not retrieve player data from backend: " + e.getMessage());
        }
    }

    @FXML
    private void handleCompareClick() {
        try {
            navigateTo("/CompareView.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open compare page.");
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

    @FXML
    private void handleResetClick() {
        if (seasonStartField != null) {
            seasonStartField.clear();
        }
        if (seasonEndField != null) {
            seasonEndField.clear();
        }
        if (lastNGamesField != null) {
            lastNGamesField.clear();
        }
        if (playerNameField != null) {
            playerNameField.clear();
        }
        if (seasonTypeCombo != null) {
            seasonTypeCombo.setValue("Both");
        }
        if (locationCombo != null) {
            locationCombo.setValue("Both");
        }
        if (sportCombo != null) {
        }
        if (opponentCombo != null) {
            opponentCombo.getSelectionModel().clearSelection();
            opponentCombo.setValue(null);
        }
        if (statisticCombo != null) {
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

    private JsonObject sendJsonRequest(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response body: " + response.body());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Backend request failed: HTTP " + response.statusCode());
        }

        JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
        if (body.has("error") && !body.get("error").isJsonNull()) {
            throw new IllegalStateException(body.get("error").getAsString());
        }

        return body;
    }

    private JsonObject requireObject(JsonObject parent, String key) {
        if (parent == null || !parent.has(key) || parent.get(key) == null || parent.get(key).isJsonNull()) {
            throw new IllegalStateException("Missing object in backend response: " + key);
        }
        return parent.getAsJsonObject(key);
    }

    private JsonArray requireArray(JsonObject parent, String key) {
        if (parent == null || !parent.has(key) || parent.get(key) == null || parent.get(key).isJsonNull()) {
            return new JsonArray();
        }
        return parent.getAsJsonArray(key);
    }

    private String getJsonString(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key) == null || object.get(key).isJsonNull()) {
            return fallback;
        }
        return object.get(key).getAsString();
    }

    private String getNullableJsonString(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key) == null || object.get(key).isJsonNull()) {
            return fallback;
        }
        String value = object.get(key).getAsString();
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private double getJsonDouble(JsonObject object, String key, double fallback) {
        if (object == null || !object.has(key) || object.get(key) == null || object.get(key).isJsonNull()) {
            return fallback;
        }
        return object.get(key).getAsDouble();
    }

    private int getJsonInt(JsonObject object, String key, int fallback) {
        if (object == null || !object.has(key) || object.get(key) == null || object.get(key).isJsonNull()) {
            return fallback;
        }
        return object.get(key).getAsInt();
    }

    private String getTrimmedText(TextField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    private String buildSeasonStartParam() {
        String rawStart = getTrimmedText(seasonStartField);
        if (rawStart.isEmpty()) {
            return null;
        }

        int startYear;
        try {
            startYear = Integer.parseInt(rawStart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Season start year must be a number.");
        }

        if (startYear > MAX_SEASON_START_YEAR) {
            throw new IllegalArgumentException("Season start year cannot be later than 2025.");
        }

        return toSeasonString(startYear);
    }

    private String buildSeasonEndParam(String seasonStart) {
        String rawEnd = getTrimmedText(seasonEndField);
        if (rawEnd.isEmpty()) {
            return seasonStart;
        }

        int endYear;
        try {
            endYear = Integer.parseInt(rawEnd);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Season end year must be a number.");
        }

        if (endYear > MAX_SEASON_START_YEAR) {
            throw new IllegalArgumentException("Season end year cannot be later than 2025.");
        }

        if (seasonStart != null) {
            int startYear = Integer.parseInt(seasonStart.substring(0, 4));
            if (endYear < startYear) {
                throw new IllegalArgumentException("Season end year must be greater than or equal to season start year.");
            }
        }

        return toSeasonString(endYear);
    }

    private String toSeasonString(int startYear) {
        return startYear + "-" + String.valueOf(startYear + 1).substring(2);
    }

    private Integer parseLastN() {
        String value = getTrimmedText(lastNGamesField);
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
        if (value.equalsIgnoreCase("Field Goal Percentage")) {
            return "field_goal_percentage";
        }
        if (value.equalsIgnoreCase("3pt Percentage")) {
            return "three_point_percentage";
        }
        if (value.equalsIgnoreCase("Free Throw Percentage")) {
            return "free_throw_percentage";
        }
        if (value.equalsIgnoreCase("Steals Per Game")) {
            return "steals";
        }
        if (value.equalsIgnoreCase("Blocks Per Game")) {
            return "blocks";
        }
        if (value.equalsIgnoreCase("Turnovers Per Game")) {
            return "turnovers";
        }
        if (value.equalsIgnoreCase("Minutes Per Game")) {
            return "minutes";
        }
        if (value.equalsIgnoreCase("Plus/Minus")) {
            return "plus_minus";
        }
        throw new IllegalArgumentException("Invalid statistic selection.");
    }

    private String mapOpponent(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return OPPONENT_TO_ABBR.getOrDefault(value, null);
    }

    private String buildPlayerRequestUrl(
            String playerName,
            String seasonStart,
            String seasonEnd,
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

        if (seasonStart != null) {
            url.append("&season_start=").append(URLEncoder.encode(seasonStart, StandardCharsets.UTF_8));
        }
        if (seasonEnd != null) {
            url.append("&season_end=").append(URLEncoder.encode(seasonEnd, StandardCharsets.UTF_8));
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
        if ("fg_pct".equalsIgnoreCase(stat)) {
            return "Field Goal Percentage";
        }
        if ("fg3_pct".equalsIgnoreCase(stat)) {
            return "3pt Percentage";
        }
        if ("ft_pct".equalsIgnoreCase(stat)) {
            return "Free Throw Percentage";
        }
        if ("stl".equalsIgnoreCase(stat)) {
            return "Steals Per Game";
        }
        if ("blk".equalsIgnoreCase(stat)) {
            return "Blocks Per Game";
        }
        if ("tov".equalsIgnoreCase(stat)) {
            return "Turnovers Per Game";
        }
        if ("min".equalsIgnoreCase(stat)) {
            return "Minutes Per Game";
        }
        if ("plus_minus".equalsIgnoreCase(stat)) {
            return "Plus/Minus";
        }
        return "Points Per Game";
    }

    private double getCareerAverageForSelectedStat(JsonObject careerOverviewSummary, String selectedStatKey) {
        return switch (selectedStatKey.toLowerCase()) {
            case "pts" -> getJsonDouble(careerOverviewSummary, "ppg", 0.0);
            case "ast" -> getJsonDouble(careerOverviewSummary, "apg", 0.0);
            case "reb" -> getJsonDouble(careerOverviewSummary, "rpg", 0.0);
            case "min" -> getJsonDouble(careerOverviewSummary, "mpg", 0.0);
            case "fg_pct" -> getJsonDouble(careerOverviewSummary, "fg_pct", 0.0);
            case "fg3_pct" -> getJsonDouble(careerOverviewSummary, "fg3_pct", 0.0);
            case "tov" -> getJsonDouble(careerOverviewSummary, "tov", 0.0);
            case "blk" -> getJsonDouble(careerOverviewSummary, "blk", 0.0);
            case "stl" -> getJsonDouble(careerOverviewSummary, "stl", 0.0);
            default -> 0.0;
        };
    }

    private double getAverageForSelectedStat(JsonArray games, String selectedStatKey) {
        return averageOf(games, selectedStatKey);
    }

    private double getRecentAverageForSelectedStat(JsonArray games, String selectedStatKey, int n) {
        return averageOfLastN(games, selectedStatKey, n);
    }

    private double averageOf(JsonArray games, String statKey) {
        if (games == null || games.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        int count = 0;
        for (JsonElement gameElement : games) {
            JsonObject game = gameElement.getAsJsonObject();
            if (game.has(statKey) && !game.get(statKey).isJsonNull()) {
                sum += game.get(statKey).getAsDouble();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    private double averageByLocation(JsonArray games, boolean home, String statKey) {
        if (games == null || games.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        int count = 0;
        for (JsonElement gameElement : games) {
            JsonObject game = gameElement.getAsJsonObject();
            boolean isHome = game.has("home") && !game.get("home").isJsonNull() && game.get("home").getAsBoolean();
            if (isHome == home && game.has(statKey) && !game.get(statKey).isJsonNull()) {
                sum += game.get(statKey).getAsDouble();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    private double averageOfLastN(JsonArray games, String statKey, int lastN) {
        if (games == null || games.isEmpty()) {
            return 0.0;
        }

        List<JsonObject> sortedGames = new ArrayList<>();
        for (JsonElement gameElement : games) {
            sortedGames.add(gameElement.getAsJsonObject());
        }
        sortedGames.sort(Comparator.comparing(this::parseGameDate).reversed());

        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < sortedGames.size() && count < lastN; i++) {
            JsonObject game = sortedGames.get(i);
            if (game.has(statKey) && !game.get(statKey).isJsonNull()) {
                sum += game.get(statKey).getAsDouble();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    private LocalDate parseGameDate(JsonObject game) {
        if (game == null || !game.has("date") || game.get("date").isJsonNull()) {
            return LocalDate.MIN;
        }

        String dateText = game.get("date").getAsString();
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US));
        } catch (DateTimeParseException e) {
            return LocalDate.MIN;
        }
    }

    private String readGameOpponent(JsonObject summary, String gameKey) {
        if (!summary.has(gameKey) || summary.get(gameKey).isJsonNull()) {
            return "N/A";
        }

        JsonObject game = summary.getAsJsonObject(gameKey);
        if (!game.has("opponent") || game.get("opponent").isJsonNull()) {
            return "N/A";
        }

        return game.get("opponent").getAsString();
    }

    private double readGameValue(JsonObject summary, String gameKey) {
        if (!summary.has(gameKey) || summary.get(gameKey).isJsonNull()) {
            return 0.0;
        }

        JsonObject game = summary.getAsJsonObject(gameKey);
        if (!game.has("value") || game.get("value").isJsonNull()) {
            return 0.0;
        }

        return game.get("value").getAsDouble();
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
            showError("Could not open " + title + ".");
        }
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

    private List<SessionManager.RecentGame> parseRecentGames(JsonArray games) {
        List<SessionManager.RecentGame> rows = new ArrayList<>();

        for (JsonElement element : games) {
            JsonObject game = element.getAsJsonObject();

            rows.add(new SessionManager.RecentGame(
                    getJsonString(game, "date", ""),
                    getJsonString(game, "opponent", ""),
                    getJsonDouble(game, "pts", 0.0),
                    getJsonDouble(game, "reb", 0.0),
                    getJsonDouble(game, "ast", 0.0),
                    getJsonDouble(game, "min", 0.0),
                    getJsonDouble(game, "fg_pct", 0.0),
                    getJsonDouble(game, "fg3_pct", 0.0)
            ));
        }

        return rows;
    }
}