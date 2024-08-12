module smpro.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.base;
    requires javafx.base;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
//    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;

    //icons
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;

    //charts
    requires com.github.hervegirod;
    requires eu.hansolo;


    //atlantafx for theming
    requires atlantafx.base;

    opens smpro.app to javafx.fxml;
    exports smpro.app;
}