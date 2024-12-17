package system.test.librarysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AccountsLogController {

    @FXML
    TextField usernameEntry;
    @FXML
    PasswordField passwordEntry;
    @FXML
    Button submitButton;
    @FXML
    Label LogLabel;

    private MainViewController mainViewController;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }


    public void submitButtonAction(ActionEvent event) throws IOException {
        if (!usernameEntry.getText().isBlank() && !passwordEntry.getText().isBlank()) {
            validate();
        } else {
            LogLabel.setText("Please enter username and password");
        }
    }

    public void validate() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM users WHERE username = '" + usernameEntry.getText() +  "' AND password = '" + passwordEntry.getText() + "' AND account_type = 'admin'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while (queryResult.next()) {
                int count = queryResult.getInt(1);
                if (count > 0) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("accountApproval.fxml"));
                    Parent root = loader.load();
                    BorderPane mainViewPane = mainViewController.mainViewPane;
                    mainViewPane.setRight(root);

                    Stage stage = (Stage) submitButton.getScene().getWindow();
                    stage.close();

                } else {
                    LogLabel.setText("Admin access only");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

