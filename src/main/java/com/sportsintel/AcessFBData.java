package com.sportsintel;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class AcessFBData {
    private static boolean key;
    private static User person;
    private static ObservableList<User> userList= FXCollections.observableArrayList();
    public static ObservableList<User> getUserList() {return userList;}
    private static int count = 0;
    private String playerName = null;
    private String stat = null;
    private String opponentName = null;
    private String sport = null;


    public static void addUserData(String user, String name, String email, String password) {

        DocumentReference docRef = Main.fstore.collection("References").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Username", user);
        data.put("Full Name", name);
        data.put("Email", email);
        data.put("Password", password);
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);



    }

    public static void addSearchData(ArrayList<String> history, String user) throws ExecutionException, InterruptedException {
        Map<String, Object> userSearchData= new HashMap<>();
        count = readSearchCount(user);
        if(count >= 5){
            count = 0;
            setSearchCount(userSearchData, count);
        }
        else{
            count = count + 1;
            setSearchCount(userSearchData, count);
        }
        String docName = "History" + count;
        ArrayList<String> searchHistory = new ArrayList<String>();
        Collections.addAll(searchHistory, history.get(0),  history.get(1),  history.get(2),  history.get(3));
        userSearchData.put(docName, searchHistory);
        userSearchData.put("Count", count);

        ApiFuture<WriteResult> userSearch =
                Main.fstore.collection("SearchHistory").document(user).set(userSearchData, SetOptions.merge());

    }



    public static void readUserInfo() {
        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = Main.fstore.collection("References").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;

        try {
            documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                System.out.println("Outing....");
                for (QueryDocumentSnapshot document : documents) {
                    person = new User(String.valueOf(document.getData().get("Username"))    ,
                            document.getData().get("Full Name").toString(),
                            document.getData().get("Email").toString(),
                            document.getData().get("Password").toString())


                    ;
                    userList.add(person);
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println("ERROR");
        }
    }

    public static ArrayList<String> readSearchData(String user, int count) throws ExecutionException, InterruptedException {
        DocumentReference docRef = Main.fstore.collection("SearchHistory").document(user);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        ArrayList<String> userData = null;
        DocumentSnapshot doc = future.get();
        String docName = "History" + count;
        String data = String.valueOf(doc.get(docName));

        if (doc.exists() && !data.isEmpty()){
            System.out.println("Outing Search History....");
            //System.out.println(data);
            String trimmed = data.substring(1, data.length() - 1);
            userData = new ArrayList<>(
                    Arrays.asList(trimmed.split(",")));
            //System.out.println(userData);
            //System.out.println( String.valueOf(doc.get("Count")));


            return userData;
            //ArrayList<String> historydata = null;
           // historydata.add((String) doc.get("History1"));
           // System.out.println(historydata);
        }
        else{
            return null;
        }
    }

    public static int readSearchCount(String user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = Main.fstore.collection("SearchHistory").document(user);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot doc = future.get();

        if (doc.exists()){
            String seachCount = String.valueOf(doc.get("Count"));
            count = Integer.parseInt(seachCount);
            return count;
        }
        else{
            return -1;
        }
    }

    public static void setSearchCount(Map<String, Object> userSearchData, int count) throws ExecutionException, InterruptedException {
        userSearchData.put("Count", count);

    }

    public static String userDataToString(ArrayList<String> userData){

        String playerName = userData.get(0);
        String stat = userData.get(1);
        String opponentName = userData.get(2);
        String sport = userData.get(3);

        String output = "Player: " + playerName + " Opponent: " + opponentName
                + " Stat: " + stat + " Sport: " + sport;

        return output;

    }


}
