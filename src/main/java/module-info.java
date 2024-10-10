module smpro.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.base;
    requires javafx.base;
    requires java.desktop;
    requires javafx.graphics;

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



    //postgres connection
    requires java.sql;
    requires org.postgresql.jdbc;

    //custom caption
//    requires lombok;
    requires org.jetbrains.annotations;
    requires com.sun.jna.platform;
    requires com.sun.jna;
    requires lombok;


   // ITEXT 7 FOR PDF
    requires com.itextpdf.barcodes;
    requires com.itextpdf.io;
    requires com.itextpdf.kernel;
    requires com.itextpdf.layout;


    opens smpro.app to javafx.fxml;
    opens smpro.app.controllers to javafx.fxml;
    exports smpro.app.custom_titlebar.internal.structs to com.sun.jna;
    exports  smpro.app.custom_titlebar.internal to  javafx.fxml;
    exports smpro.app;


}