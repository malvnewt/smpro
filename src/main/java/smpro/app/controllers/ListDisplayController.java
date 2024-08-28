package smpro.app.controllers;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListDisplayController implements Initializable {
    public Label title;
//    public Label searchLabel;
//    public TextField searchfield;
    public Button toffleselectAllBtn;
    public Button viewSelectedBtn;
//    public Button returnBtn;
    public Button confirmBtn;
//    public ListView<CheckBox> lv;
    public VBox itemcvb;


    public BooleanProperty viewSelectedP = new SimpleBooleanProperty(false);
    public BooleanProperty selectAllP = new SimpleBooleanProperty(false);

    public List<CheckBox> dataItems = new ArrayList<>();
    public Button closetP;
    public HBox searchcontainer;

    CustomTextField searchfield = new CustomTextField();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchfield.setRight(ProjectUtils.createFontIcon(MaterialDesignS.SELECT_SEARCH,15,Paint.valueOf("gray")));
        searchfield.setPromptText(Translator.getIntl("type_"));
        searchcontainer.getChildren().add(searchfield);
        searchcontainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchfield, Priority.ALWAYS);

        closetP.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOSE,20,Paint.valueOf("gray")));


        initUi();

    }

    public void initUi() {
//        returnBtn.setVisible(false);
        closetP.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
        closetP.getStyleClass().add("danger");
        closetP.setCursor(Cursor.HAND);


        viewSelectedBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EYE, 50, Paint.valueOf(Store.Colors.lightestGray)));
        toffleselectAllBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SELECT_GROUP, 50, Paint.valueOf(Store.Colors.lightestGray)));


        toffleselectAllBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("toggle_selectall")));
        viewSelectedBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("toggle_show_selections")));

        viewSelectedBtn.setOnAction(e->viewSelectedP.set(!viewSelectedP.get()));
        toffleselectAllBtn.setOnAction(e->selectAllP.set(!selectAllP.get()));




        itemcvb.setPadding(new Insets(5));
        itemcvb.setSpacing(5);





        viewSelectedP.addListener((observableValue, aBoolean, istrue) -> {
            if (istrue) {
                viewSelectedBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EYE_OFF, 50, Paint.valueOf(Store.Colors.lightestGray)));
                // show selected items
                itemcvb.getChildren().clear();
                itemcvb.getChildren().addAll(dataItems.stream().filter(CheckBox::isSelected).toList());


            }else {
                viewSelectedBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EYE, 50, Paint.valueOf(Store.Colors.lightestGray)));
                itemcvb.getChildren().clear();
                itemcvb.getChildren().addAll(dataItems.stream().toList());

            }

        });
        selectAllP.addListener((observableValue, aBoolean, istrue) -> {
            if (istrue) {
                toffleselectAllBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SELECT, 50, Paint.valueOf(Store.Colors.lightestGray)));
                //select all
                dataItems.forEach(cb -> cb.setSelected(true));

            }else {
                toffleselectAllBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SELECT_GROUP, 50, Paint.valueOf(Store.Colors.lightestGray)));
                dataItems.forEach(cb -> cb.setSelected(false));


            }

        });


        searchfield.textProperty().addListener((o,old,value)->{
            List<CheckBox> foundItems = dataItems.stream().filter(item -> item.getId().toLowerCase().contains(value.toLowerCase())).toList();
            itemcvb.getChildren().clear();
            itemcvb.getChildren().addAll(foundItems);
        });




    }


    public void loadDataItems(List<String> data,List<String> selectedOptions) {

        for (String item : data) {
            CheckBox cb = new CheckBox(item.toUpperCase());
            cb.setId(item);
            cb.setStyle("-fx-font-size: 14px;-fx-font-weight: bold;");
            if (selectedOptions.contains(item))cb.setSelected(true);

            dataItems.add(cb);
        }

        itemcvb.getChildren().addAll(dataItems);

    }





}
