package com.sportsintel;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.util.Map;

public class FirebaseService {

    private static Firestore db;

    public static void initialize() throws Exception {
        InputStream serviceAccount =
                FirebaseService.class.getResourceAsStream("/firebase-service-account.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        db = FirestoreClient.getFirestore();
    }

    public static void createUser(String uid, String fullName, String username, String email) throws Exception {
        db.collection("users").document(uid).set(Map.of(
                "fullName", fullName,
                "username", username,
                "email", email
        )).get();
    }

    public static void saveSearch(String uid, String player, String opponent, String stat, String season) throws Exception {
        db.collection("users")
                .document(uid)
                .collection("searchHistory")
                .add(Map.of(
                        "player", player,
                        "opponent", opponent,
                        "stat", stat,
                        "season", season,
                        "timestamp", FieldValue.serverTimestamp()
                )).get();
    }

    public static String signUp(String email, String password, String fullName, String username) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(fullName);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        createUser(uid, fullName, username, email);

        return uid;
    }

    public static void saveSearchHistory(String uid, SessionManager.SearchResult search) throws Exception {
        db.collection("users")
                .document(uid)
                .collection("searchHistory")
                .add(Map.ofEntries(
                        Map.entry("player", search.player()),
                        Map.entry("opponent", search.opponent()),
                        Map.entry("stat", search.stat()),
                        Map.entry("season", search.season()),
                        Map.entry("seasonType", search.seasonType()),
                        Map.entry("location", search.location()),
                        Map.entry("lastN", search.lastN()),
                        Map.entry("gamesPlayed", search.gamesPlayed()),
                        Map.entry("average", search.average()),
                        Map.entry("high", search.high()),
                        Map.entry("low", search.low()),
                        Map.entry("type", "single"),
                        Map.entry("timestamp", FieldValue.serverTimestamp())
                ))
                .get();
    }

    public static List<Map<String, Object>> getSearchHistory(String uid) throws Exception {
        QuerySnapshot snapshot = db.collection("users")
                .document(uid)
                .collection("searchHistory")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .get();

        List<Map<String, Object>> history = new ArrayList<>();

        for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
            history.add(document.getData());
        }

        return history;
    }
    private static final String API_KEY = "AIzaSyALDoopTC-SN8x_0N5dhISA0N6wqiNgbW8";

    public static Map<String, String> login(String email, String password) throws Exception {
        URL url = new URL(
                "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY
        );

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String json = new Gson().toJson(Map.of(
                "email", email,
                "password", password,
                "returnSecureToken", true
        ));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        Map<String, Object> response = new Gson().fromJson(
                new String(conn.getInputStream().readAllBytes()),
                Map.class
        );

        String uid = (String) response.get("localId");

        DocumentSnapshot userDoc = db.collection("users").document(uid).get().get();

        String fullName = userDoc.getString("fullName");
        String username = userDoc.getString("username");

        return Map.of(
                "uid", uid,
                "fullName", fullName,
                "username", username
        );
    }

    public static void saveCompareHistory(String uid, SessionManager.CompareResult compare) throws Exception {
        db.collection("users")
                .document(uid)
                .collection("searchHistory")
                .add(Map.ofEntries(
                        Map.entry("type", "compare"),
                        Map.entry("playerOne", compare.playerOne()),
                        Map.entry("playerTwo", compare.playerTwo()),
                        Map.entry("opponent", compare.opponent()),
                        Map.entry("playerOnePoints", compare.playerOnePoints()),
                        Map.entry("playerTwoPoints", compare.playerTwoPoints()),
                        Map.entry("playerOneAssists", compare.playerOneAssists()),
                        Map.entry("playerTwoAssists", compare.playerTwoAssists()),
                        Map.entry("playerOneRebounds", compare.playerOneRebounds()),
                        Map.entry("playerTwoRebounds", compare.playerTwoRebounds()),
                        Map.entry("timestamp", FieldValue.serverTimestamp())
                ))
                .get();
    }
}