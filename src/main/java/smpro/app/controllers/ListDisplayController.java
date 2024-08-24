package smpro.app.controllers;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListDisplayController implements Initializable {
    public Label title;
    public TextField searchfield;
    public Button toffleselectAllBtn;
    public Button viewSelectedBtn;
    public Button returnBtn;
    public Button confirmBtn;

    public ObjectProperty<Stage> PopupStageProperty = new SimpleObjectProperty<>();
    public ListView<CheckBox> lv;

    public BooleanProperty viewSelectedP = new SimpleBooleanProperty(false);
    public BooleanProperty selectAllP = new SimpleBooleanProperty(false);

    public List<CheckBox> dataItems = new SimpleListProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initUi();

    }

    public void initUi() {

        returnBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ARROW_TOP_LEFT_THICK, 50, Paint.valueOf(Store.Colors.lightestGray)));
        viewSelectedBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EYE, 50, Paint.valueOf(Store.Colors.lightestGray)));
        toffleselectAllBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SELECT_GROUP, 50, Paint.valueOf(Store.Colors.lightestGray)));

        toffleselectAllBtn.setTooltip(ProjectUtils.createTooltip("toggle_selectall"));
        viewSelectedBtn.setTooltip(ProjectUtils.createTooltip("toggle_show_selections"));

        viewSelectedBtn.setOnAction(e->viewSelectedP.set(!viewSelectedP.get()));
        toffleselectAllBtn.setOnAction(e->selectAllP.set(!selectAllP.get()));

        returnBtn.setOnAction(e -> PopupStageProperty.get().close());




        lv.getStyleClass().addAll("striped", "dense","bordered");
        lv.setCellFactory(new Callback<>() {
            @Override
            public ListCell<CheckBox> call(ListView<CheckBox> checkBoxListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(CheckBox checkBox, boolean b) {
                        super.updateItem(checkBox, b);
                        if (!b) {
                            setText(checkBox.getText().toUpperCase());

                        } else {
                            setGraphic(null);
                            setText(null);

                        }
                    }
                };
            }
        });



        viewSelectedP.addListener((observableValue, aBoolean, istrue) -> {
            if (istrue) {
                viewSelectedBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EYE_OFF, 50, Paint.valueOf(Store.Colors.lightestGray)));
                // show selected items
                lv.getItems().clear();
                lv.getItems().addAll(dataItems.stream().filter(CheckBox::isSelected).sorted().toList());


            }else {
                viewSelectedBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EYE, 50, Paint.valueOf(Store.Colors.lightestGray)));
                lv.getItems().clear();
                lv.getItems().addAll(dataItems.stream().sorted().toList());

            }

        });
        selectAllP.addListener((observableValue, aBoolean, istrue) -> {
            if (istrue) {
                toffleselectAllBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SELECT_GROUP, 50, Paint.valueOf(Store.Colors.lightestGray)));
                //select all
                dataItems.forEach(cb -> cb.setSelected(true));

            }else {
                viewSelectedBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SELECT, 50, Paint.valueOf(Store.Colors.lightestGray)));
                dataItems.forEach(cb -> cb.setSelected(false));


            }

        });




    }


    public void loadDataItems(List<String> data,List<String> selectedOptions) {
        for (String item : data) {
            CheckBox cb = new CheckBox(item.toUpperCase());
            cb.setId(item);
            if (selectedOptions.contains(item))cb.setSelected(true);
            dataItems.add(cb);
        }

        lv.getItems().addAll(dataItems);

    }





}
