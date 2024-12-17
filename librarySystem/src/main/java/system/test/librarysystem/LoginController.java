package system.test.librarysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;



public class LoginController {

    @FXML
    private Label loginLabel;
    @FXML
    private TextField usernameEntry;
    @FXML
    private PasswordField passwordEntry;
    @FXML
    Button loginButton;
    @FXML
    Button exitButton;
    @FXML
    Button registerNav;


    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void switchToRegister(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) registerNav.getScene().getWindow();
        Scene changeScene = new Scene(root);
        stage.setScene(changeScene);
        stage.show();
    }


    public void loginButtonAction(ActionEvent e) throws IOException {
        if (!usernameEntry.getText().isBlank() && !passwordEntry.getText().isBlank()) {
            validateLogin();
        } else {
            loginLabel.setText("Please enter username and password");
        }
    }

    public void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String verifyLogin = "SELECT username, password, account_type FROM users WHERE username = '" + usernameEntry.getText() +  "' AND password = '" + passwordEntry.getText() + "'";

        try {

            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while(queryResult.next()) {
                if (queryResult.getInt(1) ==1 ) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
                    Parent root = loader.load();
                    root.getStylesheets().add(getClass().getResource("styleTest.css").toExternalForm());
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                    return;
                }
            }
            loginLabel.setText("Invalid login");
        } catch (Exception e) {

        }
    }
}

