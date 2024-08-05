package smpro.app;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import smpro.app.utils.ProjectUtils;

import java.io.IOException;
import java.net.URL;

public class Entry extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("========= LAUNCHING SMPROV2 ==========");

        // Set UI theme
        Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
//        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());

        //TODO :: check database connectivity and show error window if any

        //TODO show academic years and Language Locale window   ->

        URL mainurl = ProjectUtils.getAppResource("views/entry-view.fxml");
        System.out.println("main view = " +mainurl);

        FXMLLoader fxmlLoader = new FXMLLoader(mainurl);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        scene.getStylesheets().add(ResourceUtil.getAppResource("css/global.css"));


        stage.setTitle("Hello!"); // TODO :: SET UNDECORATED TITLE

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


class SessionHandler
{

}