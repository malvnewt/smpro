package smpro.app;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Entry extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("========= LAUNCHING SMPROV2 ==========");
        System.out.println("Testing Internationalisation");
        System.out.println(Translator.getIntl("test_intl"));

        // resisize main window
        double screenHeight =  Screen.getPrimary().getBounds().getHeight();
        double screenWidth =  Screen.getPrimary().getBounds().getWidth();



        // Set UI theme
        Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
//        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
//        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
//        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
//        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
//        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
//        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());

        //TODO :: check database connectivity and show error window if any

        //TODO show academic years and Language Locale window   ->

        URL mainurl = ResourceUtil.getAppResourceURL("views/entry-view.fxml");
        System.out.println("main view = " +mainurl);

        FXMLLoader fxmlLoader = new FXMLLoader(mainurl);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang"));
        Parent root =fxmlLoader.load();
        Scene scene = new Scene(root, 320, 240);
        scene.getStylesheets().add(ResourceUtil.getAppResourceURL("css/global.css").toExternalForm());


        stage.setTitle("Hello!"); // TODO :: SET UNDECORATED TITLE

        stage.setScene(scene);
        stage.show();

        stage.setMinHeight(0.8 * screenHeight);
        stage.setMinWidth(0.7 * screenWidth);
        stage.centerOnScreen();

        ModalPane mp = new ModalPane();
        mp.setContent(new Label("content"));
        mp.setDisplay(true);
        mp.show(root);
        mp.setPersistent(true);

    }

    public static void main(String[] args) {
        launch();
    }
}


class SessionHandler
{

}