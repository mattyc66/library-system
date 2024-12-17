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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookController implements Initializable {

    @FXML
    protected TableView<ProductSearchModel> productTable;
    @FXML
    private TableColumn<ProductSearchModel, Integer> ID;
    @FXML
    private TableColumn<ProductSearchModel, String> Name;
    @FXML
    private TableColumn<ProductSearchModel, String> Creator;
    @FXML
    private TextField productSearch;
    @FXML
    Button viewButton;
    @FXML
    Button printButton;
    @FXML
    Label bookLabel;

    @FXML
    private void printButtonAction(ActionEvent e) throws IOException, SQLException {
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            bookLabel.setText("Select an item from the table");
        } else {
            bookLabel.setText("");
            printDetails();
        }
    }

    private String LoanCount;
    private String EarliestDate;

    private void getLoanDetails() throws SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        ProductSearchModel selectedProduct = productTable.getSelectionModel().getSelectedItem();
        Statement statement = connectDB.createStatement();
        String Name = selectedProduct.getName();

        String loanCountQuery = "SELECT COUNT(*) AS loan_count FROM loans WHERE item_name = '" + Name + "'";
        ResultSet loanCountResult = statement.executeQuery(loanCountQuery);
        if (loanCountResult.next()) {
            int loanCount = loanCountResult.getInt("loan_count");
            LoanCount = String.valueOf(loanCount);
            if (loanCount > 0) {
                String earliestDateQuery = "SELECT MIN(return_date) AS earliest_date FROM loans WHERE item_name = '" + Name + "'";
                ResultSet earliestDateResult = statement.executeQuery(earliestDateQuery);
                if (earliestDateResult.next()) {
                    EarliestDate = earliestDateResult.getString("earliest_date");
                }
            } else {
                EarliestDate = null;
            }
        }
    }

    private void printDetails() throws IOException, SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        ProductSearchModel selectedProduct = productTable.getSelectionModel().getSelectedItem();
        Statement statement = connectDB.createStatement();

        String details = "SELECT * FROM books WHERE ID = " + selectedProduct.getID() + " ";
        ResultSet Details = statement.executeQuery(details);

        while (Details.next()) {
            String Type = Details.getString("type");
            String Name = Details.getString("Name");
            String Creator = Details.getString("Creator");
            String lateFee = Details.getString("late_fee");
            String stock = Details.getString("stock");

            String content = "Type: " + Type + "\nName: " + Name + "\nCreator: " + Creator + "\nlate fee: £" + lateFee + "\nStock: " + stock;
            getLoanDetails();

            String LoanDetails = "current loans: " + LoanCount;
            if (LoanCount != null && Integer.parseInt(LoanCount) > 0) {
                LoanDetails += "\nearliest return: " + EarliestDate;
            }

            String fileName = Name + " product details";
            String downloadsFolder = System.getProperty("user.home") + "/Downloads";
            String filePath = downloadsFolder + "/" + fileName;
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content + "\n\n" + LoanDetails);
            writer.close();
            alertBox();
        }
    }
    private void alertBox() {
        Stage Window = new Stage();
        Window.initModality(Modality.APPLICATION_MODAL);
        Window.setTitle("item loaned");
        Window.setMinWidth(200);

        Label alertLabel = new Label();
        alertLabel.setText("details printed to text file \nlocated in downloads file");
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


    @FXML
    public void switchToItemView() throws Exception {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("item-view-page.fxml"));
        Parent root = loader.load();
        BorderPane mainViewPane = (BorderPane) viewButton.getScene().getRoot();


        ProductSearchModel selectedProduct = productTable.getSelectionModel().getSelectedItem();
        String info = (selectedProduct.getID() + " - " + selectedProduct.getName());
        String imageQuery = ("SELECT image_path FROM books WHERE ID = " + selectedProduct.getID());
        Statement statement = connectDB.createStatement();
        ResultSet imageQ = statement.executeQuery(imageQuery);
        ItemViewController itemviewcontroller = loader.getController();
        if (imageQ.next()) {
            String imagePath = imageQ.getString(1);
            itemviewcontroller.recive(info, imagePath);
        }

        String getStock = "SELECT *, stock FROM books WHERE ID =" + selectedProduct.getID();
        ResultSet stockAmount = statement.executeQuery(getStock);
        if (stockAmount.next()) {
            int stock = stockAmount.getInt("stock");
            String stockString = String.valueOf(stock);
            itemviewcontroller.setStock(stockString);
        }

        String ID = String.valueOf(selectedProduct.getID());
        String Name = selectedProduct.getName();
        String Creator = selectedProduct.getCreator();
        itemviewcontroller.populateListView(ID, Name, Creator);
        int selectedID = selectedProduct.getID();

        String getItemQuery = "SELECT * FROM books WHERE ID =" + selectedID;
        ResultSet result = statement.executeQuery(getItemQuery);
        itemviewcontroller.setResult(result);

        mainViewPane.setRight(root);

    }


    ObservableList<ProductSearchModel> productSearchModelObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resource){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String productViewQuery = "SELECT ID, Name, Creator FROM books;";

        try{

            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(productViewQuery);

            while (queryOutput.next()){

                Integer queryID = queryOutput.getInt("ID");
                String queryName = queryOutput.getString("Name");
                String queryCreator = queryOutput.getString("Creator");

                productSearchModelObservableList.add(new ProductSearchModel(queryID, queryName, queryCreator, null));
            }

            ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
            Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
            Creator.setCellValueFactory(new PropertyValueFactory<>("Creator"));

            productTable.setItems(productSearchModelObservableList);

            FilteredList<ProductSearchModel> filteredData = new FilteredList<>(productSearchModelObservableList, b -> true);

            productSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(productSearchModel -> {


                    if (newValue.isEmpty() || newValue == null) {
                        return true;
                    }

                    String searchKeyword = newValue.toLowerCase();

                    if (productSearchModel.getName().toLowerCase().indexOf(searchKeyword) > -1) {
                        return true;
                    } else if (productSearchModel.getCreator().toLowerCase().indexOf(searchKeyword) > -1) {
                        return true;
                    } else
                        return false;

                });
            });

            SortedList<ProductSearchModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(productTable.comparatorProperty());
            productTable.setItems(sortedData);

        } catch (SQLException e) {
            Logger.getLogger(BookController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();

        }

    }

}