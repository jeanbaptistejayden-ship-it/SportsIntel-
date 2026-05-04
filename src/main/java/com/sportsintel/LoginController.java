package com.sportsintel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class LoginController {
    private HomeController homeController;

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView loginLogo;

    @FXML
    public void initialize() {
        Image image = new Image(
                Objects.requireNonNull(getClass().getResource("/newlogo.png")).toExternalForm()
        );
        loginLogo.setImage(image);
    }

    @FXML
    private void handleBackToHome(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleLoginSubmit(ActionEvent event) {
        try {
            String email = emailField.getText();
            String password = passwordField.getText();

            Map<String, String> userData = FirebaseService.login(email, password);

            String uid = userData.get("uid");
            String fullName = userData.get("fullName");
            String realUsername = userData.get("username");

            SessionManager.login(uid, fullName, realUsername);

            if (homeController != null) {
                homeController.setLoggedInUser(
                        SessionManager.getFullName(),
                        SessionManager.getUsername()
                );
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Login");
            alert.setContentText("Email or password is incorrect.");

            alert.showAndWait();
        }
    }
}