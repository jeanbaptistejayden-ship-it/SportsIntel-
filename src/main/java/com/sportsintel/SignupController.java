package com.sportsintel;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SignUpController {

    @FXML
    private ImageView signUpLogo;

    @FXML
    private TextField email_txt;

    @FXML
    private PasswordField pass_txt;

    @FXML
    private TextField phone_txt;

    @FXML
    private TextField disName_txt;

    @FXML
    private TextField first_name_txt;

    @FXML
    private TextField last_name_txt;

    @FXML
    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm());
        signUpLogo.setImage(image);
    }

    public boolean userSignup(){
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(getEmail_txt())
                .setEmailVerified(false)
                .setPassword(getPass_txt())
                .setPhoneNumber(getPhone_txt())
                .setDisplayName(getDisName_txt())
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = Main.fauth.createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public String getEmail_txt() {
        return email_txt.getText();
    }

    public String getPass_txt() {
        return pass_txt.getText();
    }

    public String getPhone_txt() {
        return phone_txt.getText();
    }

    public String getFirst_name_txt() {
        return first_name_txt.getText();
    }

    public String getLast_name_txt() {
        return last_name_txt.getText();
    }

    public String getFullName() {
        return "" + getFirst_name_txt() + getLast_name_txt();
    }
 n
    public String getDisName_txt() {
        return disName_txt.getText();
    }

    @FXML
    private void handleBackToHome(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleSignUpSubmit(ActionEvent event) {
        userSignup();
        SessionManager.login(getFullName(), "@ForeignStage");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}