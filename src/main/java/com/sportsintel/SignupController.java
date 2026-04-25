package com.sportsintel;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SignUpController {
    @FXML
    private Label input_error_lbl;
    @FXML
    private Label last_name_lbl;
    @FXML
    private Label phone_lbl;
    @FXML
    private Label email_lbl;
    @FXML
    private Label user_lbl;
    @FXML
    private Label pass_lbl;
    @FXML
    private Label confirm_pass_lbl;
    @FXML
    private PasswordField confirm_pass_txt;
    @FXML
    private Label first_name_lbl;

    @FXML
    private ImageView signUpLogo;

    @FXML
    private TextField email_txt;

    @FXML
    private PasswordField pass_txt;

    @FXML
    private TextField phone_txt;

    @FXML
    private TextField user_txt;

    @FXML
    private TextField first_name_txt;

    @FXML
    private TextField last_name_txt;

    @FXML
    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm());
        input_error_lbl.setVisible(false);
        signUpLogo.setImage(image);
    }

    public void setTextRed(Label label){
        label.setTextFill(Color.RED);
    }
    public void setTextBlack(Label label){
        label.setTextFill(Color.BLACK);
    }


    public int verifyUserInput(){
        int count = 0;
        if (first_name_txt.getText().isEmpty()){
            setTextRed(first_name_lbl);
            count = 1;
            input_error_lbl.setVisible(true);

        }
        else{
            setTextBlack(first_name_lbl);
        }
        if (last_name_txt.getText().isEmpty()){
            setTextRed(last_name_lbl);
            count = 1;
            input_error_lbl.setVisible(true);

        }
        else{
            setTextBlack(last_name_lbl);
        }
        if (email_txt.getText().isEmpty()){
            setTextRed(email_lbl);
            count = 1;
        }
        else{
            setTextBlack(email_lbl);
        }
        if (!verifyPhone(phone_txt.getText())){
                setTextRed(phone_lbl);
                count = 1;
        }
        else{
            setTextBlack(phone_lbl);
        }
        if (user_txt.getText().isEmpty()){
            setTextRed(user_lbl);
            count = 1;
        }
        else{
            setTextBlack(user_lbl);
        }
        if (verifyPassword()){

            setTextBlack(pass_lbl);
        }
        else{
            setTextRed(pass_lbl);
            count = 1;
        }
        if (confirm_pass_txt.getText().isEmpty()){
            setTextRed(confirm_pass_lbl);
            count = 1;
        }
        else{
            setTextBlack(confirm_pass_lbl);
        }
        return count;
    }

    public boolean verifyPhone(String phone){

        if (phone.length()==12 & phone.startsWith("+")){
                return true;
        }
        else {
            return false;
        }
    }

    public boolean verifyUniqueness() throws FirebaseAuthException {
        boolean unique = true;
        String email = email_txt.getText();
        String phone = phone_txt.getText();
        String user = user_txt.getText();
        try {
            if (Main.fauth.getUserByEmail(email).getEmail() == null) {
                System.out.println("Email already in use");

            }
        }
        catch (FirebaseAuthException e){
            unique = false;
        }
        try{
                if (Main.fauth.getUserByPhoneNumber(phone).getPhoneNumber() == null) {
                    System.out.println("Phone number already in use");
                }
            }
        catch (FirebaseAuthException e){
            unique = false;
        }

        return unique;
    }

    public boolean verifyPassword(){
        boolean verified = true;
        if(getPass_txt().isEmpty()){
            verified = false;
        }
        if(getPass_txt().length()<8){
            verified = false;
        }
        if(!getPass_txt().equals(getConfirm_pass_txt())){
            verified = false;
        }

        return verified;

    }

    public void userSignup(){
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                        .setEmail(getEmail_txt())
                        .setEmailVerified(false)
                        .setPassword(getPass_txt())
                        .setPhoneNumber(getPhone_txt())
                        .setDisplayName(getUser_txt())
                        .setDisabled(false);
            UserRecord userRecord;
            try {
                userRecord = Main.fauth.createUser(request);

                System.out.println("Successfully created new user: " + userRecord.getUid());


            } catch (FirebaseAuthException ex) {
                System.out.println("FAIL");
                System.out.println(getPass_txt());

            }
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
        }


    public String getEmail_txt() {
        return email_txt.getText();
    }

    public String getPass_txt() {
        return pass_txt.getText();
    }

    public String getConfirm_pass_txt() {
        return confirm_pass_txt.getText();
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

    public String getUser_txt() {
        return user_txt.getText();
    }

    @FXML
    private void handleBackToHome(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleSignUpSubmit(ActionEvent event) throws FirebaseAuthException {
        if(verifyUserInput()==0) {
            userSignup();

            input_error_lbl.setVisible(false);
            AcessFBData.addData(getUser_txt(), getFullName(), getEmail_txt(), getPass_txt());
            AcessFBData.readFirebase();
            SessionManager.login(getFullName(), "@ForeignStage");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
        else{
            input_error_lbl.setVisible(true);
        }
    }
}