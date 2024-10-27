package smpro.app.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import smpro.app.ResourceUtil;
import smpro.app.utils.DocumentBase;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Handler;

public class GenericDialogController implements Initializable {
    public VBox content;
    public ImageView appIcon;
    public HBox dragbox;
    public Label dlgTitle;
    public Button closedlg;

    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public Button closeBtn;
    public Button confirmBtn;
    public HBox optionalTools;


    public GenericDialogController() { }

    @Override

    public void initialize(URL url, ResourceBundle resourceBundle) {

        appIcon.setImage(ResourceUtil.getImageFromResource("images/logo-server.png", (int) appIcon.getFitWidth(), (int) appIcon.getFitHeight(), true));
        closedlg.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOSE, 30, Paint.valueOf("transparent")));
        closedlg.setOnAction(e -> thisStage.get().close());
        closedlg.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            closedlg.setStyle("-fx-background-color: transparent");
        });
        closedlg.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            closedlg.setStyle("-fx-background-color: " + Store.Colors.deepRed);
        });

        closeBtn.setOnAction(e -> thisStage.get().close());

    }

    public void printClassLists(List<String> classids, List<String> fields) {
        String filepath = ResourceUtil.getSaveStystemFilePath(thisStage.get(),
                List.of(new FileChooser.ExtensionFilter("PDF FILE", "*.pdf")));
        System.out.println(filepath);

        DocumentBase documentBase = new DocumentBase(filepath);
        thisStage.get().close();
        documentBase.buildClassList(classids, fields);


    }



    public void printPersonalMarksheet(List<HashMap<String, Object>> classes, List<String> subjects, List<Integer> counts,String instructor) {
        String filepath = ResourceUtil.getSaveStystemFilePath(thisStage.get(),
                List.of(new FileChooser.ExtensionFilter("PDF FILE", "*.pdf")));
        System.out.println(filepath);

        DocumentBase documentBase = new DocumentBase(filepath);
//        thisStage.get().close();
        documentBase.buildMarkSheets(classes, subjects, counts,instructor);


    }
    public void printMarkSheets(List<HashMap<String, Object>> classes, List<String> subjects, List<Integer> counts) {
        String filepath = ResourceUtil.getSaveStystemFilePath(thisStage.get(),
                List.of(new FileChooser.ExtensionFilter("PDF FILE", "*.pdf")));
        System.out.println(filepath);

        DocumentBase documentBase = new DocumentBase(filepath);
        thisStage.get().close();
        documentBase.buildMarkSheets(classes, subjects, counts);


    }
}
