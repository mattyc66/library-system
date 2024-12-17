package system.test.librarysystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoansController implements Initializable {
    @FXML
    private TableView<LoansSearchModel> loanTable;
    @FXML
    private TableColumn<LoansSearchModel, Integer> LoanID;
    @FXML
    private TableColumn<LoansSearchModel, String> itemName;
    @FXML
    private TableColumn<LoansSearchModel, String> Customer;
    @FXML
    private TableColumn<LoansSearchModel, String> productType;
    @FXML
    private TableColumn<LoansSearchModel, String> lateFee;
    @FXML
    private TableColumn<LoansSearchModel, String> returnDate;

    @FXML
    Button backButton;
    @FXML
    TextField loanSearch;
    @FXML
    Label loanLabel;
    @FXML
    CheckBox toggleTest;
    @FXML
    DatePicker customDate;


    @FXML
    private void BackButtonAction(ActionEvent event) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();


    }
    @FXML
    private void returnButtonAction() throws Exception {
        LoansSearchModel selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) {
            loanLabel.setText("Select an item from the table");
        } else {
            loanLabel.setText("");
            returnItem();
        }
    }


    ObservableList<LoansSearchModel> LoansSearchModelObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resource){
        customDate.setVisible(false);
        toggleTest.setOnAction(this::testCheck);
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String LoansViewQuery = "SELECT ID, item_name, customer, product_type, return_date, late_fee FROM loans;";

        try{

            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(LoansViewQuery);

            while (queryOutput.next()){

                Integer queryloanID = queryOutput.getInt("ID");
                String queryitem_name = queryOutput.getString("item_name");
                String querycustomer = queryOutput.getString("customer");
                String queryproduct_type = queryOutput.getString("product_type");
                String queryreturn_date = queryOutput.getString("return_date");
                Integer querylate_fee = queryOutput.getInt("late_fee");

                LoansSearchModelObservableList.add(new LoansSearchModel(queryloanID, queryitem_name, querycustomer, queryproduct_type, queryreturn_date, querylate_fee));
            }

            LoanID.setCellValueFactory(new PropertyValueFactory<>("ID"));
            itemName.setCellValueFactory(new PropertyValueFactory<>("item_name"));
            Customer.setCellValueFactory(new PropertyValueFactory<>("customer"));
            productType.setCellValueFactory(new PropertyValueFactory<>("product_type"));
            lateFee.setCellValueFactory(new PropertyValueFactory<>("late_fee"));
            returnDate.setCellValueFactory(new PropertyValueFactory<>("return_date"));

            loanTable.setItems(LoansSearchModelObservableList);

            FilteredList<LoansSearchModel> filteredData = new FilteredList<>(LoansSearchModelObservableList, b -> true);

            loanSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(loansSearchModel -> {

                    if (newValue.isEmpty() || newValue == null) {
                        return true;
                    }

                    String searchKeyword = newValue.toLowerCase();

                    if (loansSearchModel.getItem_name().toLowerCase().indexOf(searchKeyword) > -1) {
                        return true;
                    } else if (loansSearchModel.getCustomer().toLowerCase().indexOf(searchKeyword) > -1) {
                        return true;
                    } else
                        return false;
                });
            });

            SortedList<LoansSearchModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(loanTable.comparatorProperty());

            loanTable.setItems(sortedData);


        } catch (SQLException e) {
            Logger.getLogger(LoansController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();

        }

    }

    public void testCheck(ActionEvent event) {
        //toggleTest.setOnAction(e -> {
        boolean isChecked = toggleTest.isSelected();
        customDate.setVisible(isChecked);
    }


    private void returnItem() throws  SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();

        LoansSearchModel selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        String returnDate = selectedLoan.getReturn_date();
        Integer lateFee = selectedLoan.getLate_fee();
        String removeQuery = "DELETE FROM loans WHERE ID =" + selectedLoan.getID();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentDate;
        if (toggleTest.isSelected() && customDate.getValue() != null) {
            currentDate = customDate.getValue();
        } else {
            currentDate = LocalDate.now();
        }
        LocalDate loanReturnDate = LocalDate.parse(returnDate, formatter);
        boolean Late = loanReturnDate.isBefore(currentDate);

        String productType = selectedLoan.getProduct_type();
        if (productType.equals("book")) {
            String returnBook = "UPDATE books SET stock = stock + 1 WHERE Name = '" + selectedLoan.getItem_name() + "';";
            statement.executeUpdate(returnBook);
        } else {
            String returnDVD = "UPDATE dvd SET stock = stock + 1 WHERE Name = '" + selectedLoan.getItem_name() + "';";
            statement.executeUpdate(returnDVD);
        }
        statement.executeUpdate(removeQuery);
        refreshTable();
        if (Late) {
            int lateFeeAmount = 0;
            try {
                lateFeeAmount = lateFee;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            alertBox(Late, lateFeeAmount);
        } else {
            alertBox(Late, 0);
        }
    }



    @FXML
    private void refreshTable() {
        LoansSearchModelObservableList.clear();
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String LoansViewQuery = "SELECT ID, item_name, customer, product_type, return_date, late_fee FROM loans;";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(LoansViewQuery);
            while (queryOutput.next()){
                Integer queryloanID = queryOutput.getInt("ID");
                String queryitem_name = queryOutput.getString("item_name");
                String querycustomer = queryOutput.getString("customer");
                String queryproduct_type = queryOutput.getString("product_type");
                String queryreturn_date = queryOutput.getString("return_date");
                Integer querylate_fee = queryOutput.getInt("late_fee");
                LoansSearchModelObservableList.add(new LoansSearchModel(queryloanID, queryitem_name, querycustomer, queryproduct_type, queryreturn_date, querylate_fee));
            }
        } catch (SQLException e) {
            Logger.getLogger(LoansController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }

        loanTable.refresh();
    }

    private void alertBox(Boolean Late, Integer lateFeeAmount){
        Stage Window = new Stage();
        Window.initModality(Modality.APPLICATION_MODAL);
        Window.setTitle("item returned");
        Window.setMinWidth(200);

        VBox layout = new VBox(10);
        Label alertLabel = new Label();
        alertLabel.setText("Item returned successfully");
        layout.getChildren().add(alertLabel);

        if (Late) {
            Label lateFee = new Label("A late fee has been applied: £"+ lateFeeAmount);
            layout.getChildren().add(lateFee);
        }
        Button closeButton = new Button();
        closeButton.setText("close");
        closeButton.setOnAction(e -> Window.close());
        layout.getChildren().add(closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 140 ,70);
        scene.getStylesheets().add(getClass().getResource("styleTest.css").toExternalForm());
        Window.setScene(scene);
        Window.showAndWait();
    }
}
