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
    private TextField username_txt;

    @FXML
    private Label user_error;

    @FXML
    private PasswordField pass_txt;

    private String fullName;


    @FXML
    public void initialize() {
        Image image = new Image(
                Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm()
        );
        loginLogo.setImage(image);
    }

    public boolean verifyUser() throws FirebaseAuthException {
        boolean verified = false;
        System.out.println(AcessFBData.getUserList().size());

            for (int i = 0; i < AcessFBData.getUserList().size(); i++) {
                if (AcessFBData.getUserList().get(i).getUsername().equals(username_txt.getText())) {
                    fullName = AcessFBData.getUserList().get(i).getFullName();
                    verified = true;
                } else {
                    System.out.println("Not veridied");
                   // user_error.setVisible(true);
                }
            }
            return verified;
            //String url = user.getPassword()
    }

    @FXML
    private void handleBackToHome(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleLoginSubmit(ActionEvent event) throws FirebaseAuthException {
        if(verifyUser()) {
            SessionManager.login(fullName, "@ForeignStage");
            if (homeController != null) {
                homeController.setLoggedInUser(
                        SessionManager.getFullName(),
                        SessionManager.getUsername()
                );
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    public String getUsername_txt() {
        return username_txt.getText();
    }
}