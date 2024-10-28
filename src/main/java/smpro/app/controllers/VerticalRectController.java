package smpro.app.controllers;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
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
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.net.URL;
import java.util.*;

public class VerticalRectController implements Initializable {
    public Label title;
    public Button toffleselectAllBtn;
    public Button viewSelectedBtn;
    public Button confirmBtn;
    public VBox itemcvb;


    public List<Node> dataItems = new ArrayList<>();
    public Button closetP;
    public HBox searchcontainer;

    CustomTextField searchfield = new CustomTextField();

    ///////////////    coef config variables
    public HashMap<Integer, Integer> classSubjectCoefMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        searchfield.setRight(ProjectUtils.createFontIcon(MaterialDesignF.FILTER,18,Paint.valueOf("gray")));
//        searchfield.setPromptText(Translator.getIntl("type_"));
//        searchcontainer.getChildren().add(searchfield);
//        searchcontainer.setAlignment(Pos.CENTER_LEFT);
//        HBox.setHgrow(searchfield, Priority.ALWAYS);

        closetP.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOSE,20,Paint.valueOf("gray")));
        closetP.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
        closetP.getStyleClass().add("danger");
        closetP.setCursor(Cursor.HAND);


        itemcvb.setPadding(new Insets(5));
        itemcvb.setSpacing(4);


    }


    ///////////////////////////////////////////////////////////////////////////////
    public void initConfigureClassCoefs(int generatCoef,String subjectName,HashMap<Integer,Integer>...availableConfig) {
        title.setText(String.format("%s [%s]", Translator.getIntl("coefficients"), ProjectUtils.capitalize(subjectName)));
        viewSelectedBtn.setVisible(false);
        toffleselectAllBtn.setVisible(false);
        searchcontainer.setVisible(false);

        List<HashMap<String, Object>> classes = PgConnector.fetch("select * from classes order by level,classname", PgConnector.getConnection());

        if (availableConfig.length > 0) {
            classSubjectCoefMap.putAll(availableConfig[0]);
        }

        for (HashMap<String, Object> cls : classes) {
            String cname = PgConnector.getFielorBlank(cls, "classname");
            int cid = PgConnector.getNumberOrNull(cls, "id").intValue();

            if (availableConfig.length == 0) {
                if (classSubjectCoefMap.containsKey(cid)) {
                    classSubjectCoefMap.replace(cid, generatCoef);
                }else  classSubjectCoefMap.put(cid, generatCoef);
            }

            Label clabel = new Label(cname.toUpperCase());
            clabel.getStyleClass().add("text-bold");

            TextField cf = new TextField(String.valueOf(classSubjectCoefMap.get(cid)));
            cf.textProperty().addListener(((observableValue, s, t1) -> {
                if (!cf.getText().matches("\\D")) {
                    cf.setText(t1.replaceAll("\\D", ""));
                }
                if (!cf.getText().isEmpty()) {
                classSubjectCoefMap.replace(cid, Integer.parseInt(cf.getText()));
                }

            }));

            cf.setMinWidth(100);
            cf.setMaxWidth(100);

            HBox itemhb = new HBox(clabel,ProjectUtils.createHspacer(), cf);
            itemhb.setId(String.valueOf(cid));
            itemhb.setSpacing(5);
            itemhb.setAlignment(Pos.CENTER_LEFT);

            itemcvb.getChildren().add(itemhb);

        }



    }


    ///////////////////////////////////////////////////////////////////////////////





}
