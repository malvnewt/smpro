package smpro.app.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddSubjectController implements Initializable {
    public ImageView iconview;
    public TextField namefield;
    public TextField abbrfield;
    public TextField coefField;
    public TextField headField;
    public ComboBox<String> categoryField;
    public Button cancelBtn;
    public Button confirmBtn;


    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public TextField subjectCodefield;
    public HBox dragArea;
    public ImageView appIcon;
    public Button closedlg;
    public Button addSubjectcatBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appIcon.setImage(ResourceUtil.getImageFromResource("images/plus.png", (int) appIcon.getFitWidth(), (int) appIcon.getFitHeight(), true));
        closedlg.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOSE, 30, Paint.valueOf("transparent")));
        closedlg.setOnAction(e->thisStage.get().close());
        closedlg.addEventHandler(MouseEvent.MOUSE_EXITED, e->{
            closedlg.setStyle("-fx-background-color: transparent");
        });
        closedlg.addEventHandler(MouseEvent.MOUSE_ENTERED,e->{
            closedlg.setStyle("-fx-background-color: "+Store.Colors.deepRed);
        });

//        iconview.setImage(ResourceUtil.getImageFromResource("images/newdoc.png",50,50,true));
        coefField.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("can_be_changed_perclass")));

        // restrict coeff input\
        coefField.textProperty().addListener(((observableValue, s, t1) -> coefField.setText(t1.replaceAll("\\D", ""))));

        //validation remover
        for (TextField t : new TextField[]{namefield, coefField, headField, subjectCodefield,abbrfield}) {
            t.textProperty().addListener((a,b,c)-> {
                if (!c.isEmpty()) {
                    t.getStyleClass().remove("error-textfield");
                }
            });

        }


        //populate category
        categoryField.getItems().addAll(Store.DbsubjectCategories);
        categoryField.setValue(Store.DbsubjectCategories.get(0));
        categoryField.valueProperty().addListener((a,b,c)->{
            if (!c.isEmpty())categoryField.getStyleClass().remove("error-textfield");
        });


        categoryField.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (!b) {
                            setText(s.toUpperCase());
                            setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE, 15, Paint.valueOf(Store.Colors.lightestGray)));
                        }
                    }
                };
            }
        });

        categoryField.setButtonCell( new ListCell<>() {
            @Override
            protected void updateItem(String s, boolean b) {
                super.updateItem(s, b);
                if (!b) {
                    setText(s.toUpperCase());
                    setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE, 15, Paint.valueOf(Store.Colors.lightestGray)));
                }
            }
        });

        cancelBtn.setOnAction(e -> thisStage.get().close());

        confirmBtn.setOnAction(e->{
            String name = namefield.getText();
            String shortName = abbrfield.getText();
            String coefficient = coefField.getText();
            String departmentHead = headField.getText();
            String category = categoryField.getValue();
            String subjectCode = subjectCodefield.getText();

            //validate
            if (name.isEmpty()) namefield.getStyleClass().add("error-textfield");
            if (shortName.isEmpty())abbrfield.getStyleClass().add("error-textfield");
            if (coefficient.isEmpty()) coefField.getStyleClass().add("error-textfield");
            if (category.isEmpty())categoryField.getStyleClass().add("error-textfield");
            if (subjectCode.isEmpty()) subjectCodefield.getStyleClass().add("error-textfield");


            if (!(name.isEmpty() || shortName.isEmpty() || coefficient.isEmpty() || category.isEmpty())) {
                String insertsubject = String.format("insert into subjects (subject_name,subject_code,subject_category,subject_coefficient,subject_abbreviation,department_head)" +
                        "values ('%s','%s','%s',%d,'%s','%s')", name.toLowerCase(),subjectCode, category, Integer.parseInt(coefficient), shortName, departmentHead);
                PgConnector.insert(insertsubject);


                Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
                        Translator.getIntl("data_updated"), ButtonType.OK);
                a.showAndWait();
                thisStage.get().close();

            }

        });

        addSubjectcatBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS,32,Paint.valueOf(Store.Colors.LightGray)));
        addSubjectcatBtn.setOnAction(this::addSubjectCat);



    }

    public void addSubjectCat(Event e) {
        TextInputDialog tf = ProjectUtils.getTextDialog(
                thisStage.get(), "PROMPT", Translator.getIntl("add_subject_cat"), Translator.getIntl("type_name"),
                new ImageView(ResourceUtil.getImageFromResource("images/plus.png", 50, 50, true)));
        tf.showAndWait().ifPresent(string -> {
            PgConnector.insert(String.format("insert into subject_categories" +
                    " (category_name) values ('%s')", string.toLowerCase()));

            Store.DbsubjectCategories.clear();
            Store.DbsubjectCategories.addAll(PgConnector.listHashAttrs(PgConnector.fetch("select * from subject_categories order by category_name",
                    PgConnector.getConnection()), "category_name"));
            categoryField.getItems().clear();
            categoryField.getItems().addAll(Store.DbsubjectCategories);
            categoryField.setValue(string);

        });


    }

    public void prepareUpdate(HashMap<String, Object> selected) {
        subjectCodefield.setText(PgConnector.getFielorBlank(selected,"subject_code"));
        namefield.setText(PgConnector.getFielorBlank(selected,"subject_name"));
        abbrfield.setText(PgConnector.getFielorBlank(selected,"subject_abbreviation"));
        coefField.setText(PgConnector.getFielorBlank(selected,"subject_coefficient"));
        headField.setText(PgConnector.getFielorBlank(selected,"department_head"));
        categoryField.setValue(PgConnector.getFielorBlank(selected,"subject_category"));
    }

    public void update(HashMap<String,Object> s) {
        int sid = PgConnector.getNumberOrNull(s,"id").intValue();
        String name = namefield.getText();
        String shortName = abbrfield.getText();
        String coefficient = coefField.getText();
        String departmentHead = headField.getText();
        String category = categoryField.getValue();
        String subjectCode = subjectCodefield.getText();

        //validate
        if (name.isEmpty()) namefield.getStyleClass().add("error-textfield");
        if (shortName.isEmpty())abbrfield.getStyleClass().add("error-textfield");
        if (coefficient.isEmpty()) coefField.getStyleClass().add("error-textfield");
        if (category.isEmpty())categoryField.getStyleClass().add("error-textfield");
        if (subjectCode.isEmpty()) subjectCodefield.getStyleClass().add("error-textfield");


        if (!(name.isEmpty() || shortName.isEmpty() || coefficient.isEmpty() || category.isEmpty())) {
            String insertsubject =
                    String.format("update subjects set subject_name='%s',subject_code='%s',subject_category='%s',subject_coefficient=%d," +
                                    "subject_abbreviation='%s',department_head='%s'" +
                    "where id=%d",
                    name.toLowerCase(),subjectCode, category, Integer.parseInt(coefficient), shortName, departmentHead,sid);
            PgConnector.update(insertsubject);
            Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
                    Translator.getIntl("data_updated"), ButtonType.OK);
            a.showAndWait();
            thisStage.get().close();

        }

    }
}
