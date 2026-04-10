package com.sportsintel;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginController {

    @FXML
    private ImageView loginLogo;

    @FXML
    private TextField user_txt;

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
            UserRecord user = Main.fauth.getUserByEmail(getUser_txt());
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



    public String getUser_txt() {
        return user_txt.getText();
    }
}