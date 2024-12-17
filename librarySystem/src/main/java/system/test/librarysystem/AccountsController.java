package system.test.librarysystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountsController implements Initializable {

    @FXML
    TableView<accountsSearchModel> accountTable;
    @FXML
    TableColumn<accountsSearchModel, Integer> ID;
    @FXML
    TableColumn<accountsSearchModel, String> Username;
    @FXML
    TableColumn<accountsSearchModel, String> Account_type;
    @FXML
    TextField userSearch;
    @FXML
    Button approveButton;
    @FXML
    Button denyButton;

    public void approveAction(ActionEvent event) throws Exception {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();

        accountsSearchModel selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        String passGet = "SELECT password FROM register_requests WHERE ID = '" + selectedAccount.getID() + "'";

        try {
            ResultSet pass = statement.executeQuery(passGet);

            while (pass.next()) {
                String password = pass.getString("password");
                String remove = "DELETE FROM register_requests WHERE ID = '" + selectedAccount.getID() + "'";
                String add = "INSERT INTO users (username, password, account_type)\n" +
                        "VALUES ('" + selectedAccount.getUsername() + "', '" + password + "', '" + selectedAccount.getAccount_type() + "')";
                Statement updateStatement = connectDB.createStatement();
                updateStatement.executeUpdate(add);
                updateStatement.executeUpdate(remove);
                alertBox("account has been approved");
                refreshTable();
            }
            pass.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            statement.close();
        }
    }

    public void denyAction(ActionEvent event) throws Exception {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();
        accountsSearchModel selectedAccount = accountTable.getSelectionModel().getSelectedItem();

        String denyQuery =  "DELETE FROM register_requests WHERE ID = '" + selectedAccount.getID() + "'";
        statement.executeUpdate(denyQuery);
        alertBox("account has been denied");
        refreshTable();
    }


    ObservableList<accountsSearchModel> accountsSearchModelObservableList = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resource) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String accountViewQuery = "SELECT ID, username, account_type FROM register_requests";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(accountViewQuery);

            while (queryOutput.next()) {

                Integer queryID = queryOutput.getInt("ID");
                String queryUsername = queryOutput.getString("Username");
                String queryAccountType = queryOutput.getString("Account_type");

                accountsSearchModelObservableList.add(new accountsSearchModel(queryID, queryUsername, queryAccountType));
            }
            ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
            Username.setCellValueFactory(new PropertyValueFactory<>("Username"));
            Account_type.setCellValueFactory(new PropertyValueFactory<>("Account_type"));

            accountTable.setItems(accountsSearchModelObservableList);
            FilteredList<accountsSearchModel> filteredData = new FilteredList<>(accountsSearchModelObservableList, b -> true);
            userSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(accountsSearchModel -> {

                    if (newValue.isEmpty() || newValue == null) {
                        return true;
                    }

                    String searchKeyword = newValue.toLowerCase();
                    if (accountsSearchModel.getUsername().toLowerCase().indexOf(searchKeyword) > -1) {
                        return true;
                    } else if (accountsSearchModel.getAccount_type().toLowerCase().indexOf(searchKeyword) > -1) {
                        return true;
                    } else
                        return false;

                });
            });

            SortedList<accountsSearchModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(accountTable.comparatorProperty());
            accountTable.setItems(sortedData);

        } catch (SQLException e) {
            Logger.getLogger(AccountsController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshTable() {
        accountsSearchModelObservableList.clear();
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String accountViewQuery = "SELECT ID, username, account_type FROM register_requests";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(accountViewQuery);
            while (queryOutput.next()){
                Integer queryID = queryOutput.getInt("ID");
                String queryUsername = queryOutput.getString("Username");
                String queryAccountType = queryOutput.getString("Account_type");
                accountsSearchModelObservableList.add(new accountsSearchModel(queryID, queryUsername, queryAccountType));
            }
        } catch (SQLException e) {
            Logger.getLogger(LoansController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
        accountTable.refresh();
    }

    private void alertBox(String message) {
        Stage Window = new Stage();
        Window.initModality(Modality.APPLICATION_MODAL);
        Window.setTitle("item loaned");
        Window.setMinWidth(200);

        Label alertLabel = new Label();
        alertLabel.setText(message);
        Button closeButton = new Button();
        closeButton.setText("close");
        closeButton.setOnAction(e -> Window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(alertLabel, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 140, 70);
        scene.getStylesheets().add(getClass().getResource("styleTest.css").toExternalForm());
        Window.setScene(scene);
        Window.showAndWait();
    }

}
