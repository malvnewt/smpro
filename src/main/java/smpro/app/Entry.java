package smpro.app;


import eu.hansolo.tilesfx.Demo;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.effect.Light;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import smpro.app.custom_titlebar.CaptionConfiguration;
import smpro.app.custom_titlebar.CustomCaption;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Entry extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("========= LAUNCHING SMPROV2 ==========");
        System.out.println("Testing Internationalisation");
//        System.out.println(Translator.getIntl("test_intl"));

        // resisize main window
        double screenHeight =  Screen.getPrimary().getBounds().getHeight();
        double screenWidth =  Screen.getPrimary().getBounds().getWidth();



        //check database connectivity and show error window if any
        try {
            SessionHandler.connectToProjectDb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




        URL mainurl = ResourceUtil.getAppResourceURL("views/entry-view.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(mainurl);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
        Parent root =fxmlLoader.load();
        Scene scene = new Scene(root, 320, 240);
        scene.getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        String title = String.format("%s %s\u2800 \u2800 \u2800 %s \u2800 \u2800%s %s %s", "SM\u2796PRO",Store.UnicodeSumnbol.tm,
                ProjectUtils.getFormatedDate(new Date().getTime(), DateFormat.getDateInstance(DateFormat.FULL,
                        Translator.getLocale())),Store.UnicodeSumnbol.leftSquarebracket,"SERVER SESSION",Store.UnicodeSumnbol.rightSquarebracket);
        stage.setTitle(title.toUpperCase()); // TODO :: SET UNDECORATED TITLE

        stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));


        stage.setScene(scene);
        stage.show();



        MenuBar menuBar = (MenuBar) fxmlLoader.getNamespace().get("menubar");
        menuBar.setStyle("-fx-border-width: 0 0 1px 0;-fx-border-color: "+Store.Colors.lightestGray);

        CustomCaption.useForStage(stage, new CaptionConfiguration().setCaptionHeight(40)
                .setIconColor(Color.web(Store.Colors.LightGray))
                .setIconHoverColor(Color.web(Store.Colors.White))
                .setControlBackgroundColor(Color.web(Store.Colors.transparent))
                        .setButtonHoverColor(Color.web("#aaaaaa30"))
                .setCaptionDragRegion(menuBar).
                useControls(true).
                setCloseButtonHoverColor(Color.web(Store.Colors.deepRed))
        );


        EntryController controller = fxmlLoader.getController();
        controller.thisStage.set(stage);


        stage.setMinHeight(0.8 * screenHeight);
        stage.setMinWidth(0.7 * screenWidth);
        stage.centerOnScreen();
        menuBar.requestFocus();




    }

    public static void main(String[] args) {
        launch();
    }
}




class SessionHandler {
    public static void  connectToProjectDb() throws SQLException {
        Connection baseconn;
        System.out.println("initialising sessionhandler");


        try {
             baseconn =  PgConnector.initConnect(PgConnector.baseDbname,PgConnector.dbHost);
            System.out.println(baseconn);
            PgConnector.baseConnection.set(baseconn);

            URL url = ResourceUtil.getAppResourceURL("views/others/connect.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
            Parent root =fxmlLoader.load();


            Stage stage = new Stage(StageStyle.UNDECORATED);

            Scene scene = new Scene(root);
            scene.getStylesheets().addAll(
                    ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
            );


            stage.setScene(scene);

//            new JMetro(Style.DARK).setScene(stage.getScene());
//



            String title = String.format("%s %s\u2800 \u2800 \u2800 %s \u2800 \u2800%s %s %s", "SM\u2796PRO",Store.UnicodeSumnbol.tm,
                    ProjectUtils.getFormatedDate(new Date().getTime(), DateFormat.getDateInstance(DateFormat.FULL,
                            Translator.getLocale())),Store.UnicodeSumnbol.leftSquarebracket,"DB CONNECTIVITY",Store.UnicodeSumnbol.rightSquarebracket);
            stage.setTitle(title.toUpperCase()); // TODO :: SET UNDECORATED TITLE
            stage.getIcons().add(ResourceUtil.getImageFromResource("images/admin.png", 50, 50));

            ConnectController controller = fxmlLoader.getController();
            controller.thisStage.set(stage);


            //get databases
            List<HashMap<String, Object>> dbObjects = PgConnector.fetch("select * from  databases order by id",PgConnector.baseConnection.get()
            );
            controller.dbs.set(FXCollections.observableList(dbObjects));

            stage.showAndWait();




        } catch (SQLException e) {
            System.err.println(e.getLocalizedMessage());

            //TODO PROJECT NOT CONFIGURED DIALOG AND SHUTDOWN

        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }





    }// main method

}




