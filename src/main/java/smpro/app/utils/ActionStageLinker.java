package smpro.app.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;
import smpro.app.controllers.AddEmpController;
import smpro.app.controllers.AddStudentController;
import smpro.app.controllers.GenericDialogController;
import smpro.app.controllers.SingleScoreViewController;
import smpro.app.controllers.SingleScoreViewController;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Handler;

import static smpro.app.utils.ProjectUtils.applyDialogCaption;

public class ActionStageLinker {


    public static AddStudentController openAddStudent(Stage parent) {
        URL url = ResourceUtil.getAppResourceURL("views/others/add-student.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
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
        controller.resetbtn.setDisable(true);



        applyDialogCaption(stage,controller.dragArea);


        scene.getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        stage.show();

        return controller;
    }
    public static AddEmpController openAddEmployee(Stage parent, HashMap<String,Object>... updateData) {
        System.out.println("adding/updateing employee");
        URL url = ResourceUtil.getAppResourceURL("views/others/add-staff.fxml");

        boolean isupdate = updateData.length > 0;

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
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

        AddEmpController controller = fxmlLoader.getController();
        controller.thisStage.set(stage);
        controller.mainstage.set(parent);
//        controller.resetbtn.setDisable(true);

        if (isupdate) {
            try {
                controller.prepareUpdate(updateData[0]);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        controller.cancelBtn.setOnAction(e -> stage.close());
        controller.confirmBtn.setOnAction(e -> {
            try {
                if (isupdate) {
                    controller.save(updateData[0]);
                } else {
                controller.save();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });



        applyDialogCaption(stage,controller.dragArea);


        scene.getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        stage.showAndWait();

        return controller;
    }
    public static GenericDialogController  openGenericDialog(Stage parent) {
        URL url = ResourceUtil.getAppResourceURL("views/dialog-view.fxml");


        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
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


        GenericDialogController controller = fxmlLoader.getController();
        controller.thisStage.set(stage);


        applyDialogCaption(stage,controller.dragbox);

        scene.getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        stage.show();
        return controller;
    }
    public static SingleScoreViewController openSingleScoreDialog(Stage parent,HashMap<String,Object> sobj) throws SQLException {
        URL url = ResourceUtil.getAppResourceURL("views/others/singleStudentScoreView.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
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

        SingleScoreViewController controller = fxmlLoader.getController();
        controller.thisStage.set(stage);
        controller.preparedUpdate(sobj);
        controller.studentObjP.set(sobj);

        applyDialogCaption(stage,controller.dragArea);

        scene.getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        stage.show();
        return controller;
    }


}
