package smpro.app.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.LightBase;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.net.URL;
import java.util.*;

public class AddPayeditemController implements Initializable {
    public Button cancelBtn;
    public Button confirmBtn;
    public Button closedlg;


    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public HBox dragArea;
    public TextField amountf;
    public HBox itemhb;

    CustomTextField itemsfield = new CustomTextField();

    public StringProperty itemProperty = new SimpleStringProperty("");
    public StringProperty amountProperty = new SimpleStringProperty("");
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closedlg.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOSE, 30, Paint.valueOf("transparent")));
        closedlg.setOnAction(e->thisStage.get().close());
        closedlg.addEventHandler(MouseEvent.MOUSE_EXITED, e->{
            closedlg.setStyle("-fx-background-color: transparent");
        });
        closedlg.addEventHandler(MouseEvent.MOUSE_ENTERED,e->{
            closedlg.setStyle("-fx-background-color: "+ Store.Colors.deepRed);
        });


        itemsfield.setMaxHeight(32);
        itemhb.getChildren().add(0, itemsfield);
        HBox.setHgrow(itemsfield,Priority.ALWAYS);
        itemsfield.setRight(ProjectUtils.createFontIcon(MaterialDesignT.TEXT,20,Paint.valueOf("lightgray")));
        itemsfield.setPromptText("eg Sports wear");


        cancelBtn.setOnAction(e->thisStage.get().close());

        amountProperty.bind(amountf.textProperty());
        itemProperty.bind(itemsfield.textProperty());

        amountf.textProperty().addListener((o, old, newval) -> amountf.setText(newval.replaceAll("\\D", "")));




    }


    public HashMap<String,String> getSalesMap() {
        if (!(itemProperty.get().isEmpty() || amountProperty.get().isEmpty())) {

        return new HashMap<>(Map.of("item", itemProperty.get(), "amount", amountProperty.get()));
        }
        return null;
    }

}
