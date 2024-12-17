package system.test.librarysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainViewController {

    @FXML
    Button exitButton;
    @FXML
    Button LoanButton;
    @FXML
    Button homeButton;
    @FXML
    public BorderPane mainViewPane;
    @FXML
    public TabPane mainViewTabs;

    private Stage stage;
    private Scene scene;


    public void switchToLoans(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Loans.fxml"));
        Node loansView = loader.load();
        mainViewPane.setRight(loansView);
    }

    @FXML
    private void ExitButtonAction(ActionEvent event) throws Exception{
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void HomeButtonAction(ActionEvent event) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }


    public void signOutAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void switchToAccounts(ActionEvent event) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("accountsLog.fxml"));
        Parent root = loader.load();

        AccountsLogController accountsLogController = loader.getController();
        accountsLogController.setMainViewController(this);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }


}
