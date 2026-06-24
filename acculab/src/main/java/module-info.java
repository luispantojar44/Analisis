module com.acculab {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires itextpdf;
    requires java.mail;

    opens com.acculab.controllers to javafx.fxml;
    opens com.acculab.models to javafx.base;
    
    exports com.acculab;
    exports com.acculab.controllers;
    exports com.acculab.models;
}
