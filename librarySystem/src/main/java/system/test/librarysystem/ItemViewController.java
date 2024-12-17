package system.test.librarysystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ItemViewController {

    @FXML
    Button BackButton;

    @FXML
    Label Details;
    @FXML
    public ImageView imageTest;
    @FXML
    TextField customerName;
    @FXML
    Button LoanProductButton;
    @FXML
    Label nameLabel;


    @FXML
    private void BackButtonAction(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) BackButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

    public void recive(String info, String imagePath) {
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        imageTest.setImage(image);
    }

    @FXML
    private ListView<String> itemView;


    public void populateListView(String ID, String Name, String Creator) {
        String details = "ID: " + ID + "\nName: " + Name + "\nCreator: " + Creator + "\nStock: " + stock;

        if ("0".equals(stock)) {
            details += "\nStatus: Unavailable";
        }
        ObservableList<String> detailsList = FXCollections.observableArrayList(details);
        itemView.setItems(detailsList);
    }

    public void rePopulateListView(String ID, String Name, String Creator, String updatedStock) {
        itemView.getItems().clear();
        String details = "ID: " + ID + "\nName: " + Name + "\nCreator: " + Creator + "\nStock: " + updatedStock;
        if ("0".equals(updatedStock)) {
            details += "\nStatus: Unavailable";
        }
        ObservableList<String> detailsList = FXCollections.observableArrayList(details);
        itemView.setItems(detailsList);
    }


    private String stock;

    public void setStock(String stock) {
        this.stock = stock;
    }

    private ResultSet result;

    public void setResult(ResultSet result) {
        this.result = result;
    }


    @FXML
    private void LoanItemButton(ActionEvent event) throws Exception {
        if (!customerName.getText().isBlank()) {
            LoanItemAction();
        } else {
            nameLabel.setText("Please enter Customer name");
        }
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

    public void LoanItemAction() throws SQLException {
        if (result.next()) {
            int stock = result.getInt("stock");
            String productType = result.getString("type");

            if (stock == 0) {
                alertBox("Item is out of stock");
            } else {
                DatabaseConnection connectNow = new DatabaseConnection();
                Connection connectDB = connectNow.getConnection();
                LocalDate currentDate = LocalDate.now();
                LocalDate loanDate = currentDate.plusDays(30);
                String formattedDate = loanDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                Statement statement = connectDB.createStatement();

                String loanInsert = "INSERT INTO loans (item_name, customer, product_type, return_date, late_fee, image_path, creator)\n" +
                        "VALUES ('" + result.getString("Name") + "','" + customerName.getText() + "','" + productType + "','" + formattedDate + "','" + result.getString("late_fee") + "', '" + result.getString("image_path") + "', '" + result.getString("creator") + "')";
                statement.executeUpdate(loanInsert);

                String updateStock;
                if (productType.equals("book")) {
                    updateStock = "UPDATE books SET stock = stock - 1 WHERE ID = " + result.getString("ID") + ";";
                } else {
                    updateStock = "UPDATE dvd SET stock = stock - 1 WHERE ID = " + result.getString("ID") + ";";
                }
                statement.executeUpdate(updateStock);
                alertBox("Item loaned successfully");

                String ID = result.getString("ID");
                String Name = result.getString("Name");
                String Creator = result.getString("creator");
                String updatedStock = Integer.toString(stock - 1);
                rePopulateListView(ID, Name, Creator, updatedStock);
            }
        }
    }
}








