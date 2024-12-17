module system.test.librarysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;


    opens system.test.librarysystem to javafx.fxml;
    exports system.test.librarysystem;
}