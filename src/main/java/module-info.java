module smpro.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
//    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    //atlantafx for theming
    requires atlantafx.base;

    opens smpro.app to javafx.fxml;
    exports smpro.app;
}