package com.sportsintel;

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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    private HomeController homeController;

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
    @FXML
    private ImageView loginLogo;

    @FXML
    private TextField email_txt;

    @FXML
    private Label user_error;

    @FXML
    private PasswordField pass_txt;

    @FXML
    public void initialize() {
        Image image = new Image(
                Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm()
        );
        loginLogo.setImage(image);
    }

    public void verifyUser() throws FirebaseAuthException {
        try {
            UserRecord user = Main.fauth.getUserByEmail(getEmail_txt());
            //String url = user.getPassword();

                if (!(user == null)) {
                    System.out.println("User verified");
                }
            }  catch (Exception e){
                user_error.setVisible(true);
            }
     }

    @FXML
    private void handleBackToHome(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleLoginSubmit(ActionEvent event) {
        SessionManager.login("Dan Gron", "@ForeignStage");

        if (homeController != null) {
            homeController.setLoggedInUser(
                    SessionManager.getFullName(),
                    SessionManager.getUsername()
            );
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public String getEmail_txt() {
        return email_txt.getText();
    }
}