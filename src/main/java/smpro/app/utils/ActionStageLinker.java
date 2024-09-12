package smpro.app.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;
import smpro.app.controllers.AddStudentController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static smpro.app.utils.ProjectUtils.applyDialogCaption;

public class ActionStageLinker {


    public static AddStudentController openAddStudent(Stage parent) {
        URL url = ResourceUtil.getAppResourceURL("views/others/add-student.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);
        stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
        stage.setResizable(false);

        AddStudentController controller = fxmlLoader.getController();
        controller.thisStage.set(stage);



        applyDialogCaption(stage,controller.dragArea);


        scene.getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        stage.show();

        return controller;
    }
}
