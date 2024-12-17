package system.test.librarysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class registerController {

    @FXML
    Button LoginNav;
    @FXML
    Button exitButton;
    @FXML
    Button registerButton;
    @FXML
    TextField usernameEntry;
    @FXML
    PasswordField passwordEntry;
    @FXML
    RadioButton adminType;
    @FXML
    RadioButton defaultType;
    @FXML
    Label registerLabel;

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void switchToLog(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) LoginNav.getScene().getWindow();
        Scene changeScene = new Scene(root);
        stage.setScene(changeScene);
        stage.show();

    }

    public void registerButtonAction(ActionEvent e) throws IOException, SQLException {
        if (!usernameEntry.getText().isBlank() && !passwordEntry.getText().isBlank()) {
            register();

        } else {
            registerLabel.setText("Please enter a username and password");
        }
    }

    public void register() throws SQLException {
        String Type = getAccountType();
        if (Type == null) {
            registerLabel.setText("select account type");
            return;
        }

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String registerDetails = "INSERT INTO register_requests (username, password , account_type)\n" +
                "values ('" + usernameEntry.getText() + "', '" + passwordEntry.getText() + "', '" + Type + "')";
        Statement statement = connectDB.createStatement();
        statement.executeUpdate(registerDetails);
        alertBox();
    }

    public String getAccountType() {
        String Type = null;
        if (defaultType.isSelected()) {
            Type = defaultType.getText().toLowerCase();
            System.out.println(Type);
        }
        else if(adminType.isSelected()) {
            Type = adminType.getText().toLowerCase();
            System.out.println(Type);
        }
        return Type;
    }

    private void alertBox() {
        Stage Window = new Stage();
        Window.initModality(Modality.APPLICATION_MODAL);
        Window.setTitle("registration successful");
        Window.setMinWidth(200);

        Label alertLabel = new Label();
        alertLabel.setText("Registration successful.\n" +
                "Account is awaiting approval.");
        Button closeButton = new Button();
        closeButton.setText("close");
        closeButton.setOnAction(e -> Window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(alertLabel, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 140 ,70);
        scene.getStylesheets().add(getClass().getResource("styleTest.css").toExternalForm());
        Window.setScene(scene);
        Window.showAndWait();
    }
}
