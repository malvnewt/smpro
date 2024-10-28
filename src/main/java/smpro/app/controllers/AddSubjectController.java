package smpro.app.controllers;

import com.google.gson.Gson;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignE;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.postgresql.util.PGobject;
import smpro.app.ResourceUtil;
import smpro.app.utils.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
    public Button classcoefBtn;

    ObjectProperty<HashMap<Integer, Integer>> configuredCoefP = new SimpleObjectProperty<>();
    BooleanProperty isupdate = new SimpleBooleanProperty(false);


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

        confirmBtn.setOnAction(e-> {
            try {
                save();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        addSubjectcatBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS,32,Paint.valueOf(Store.Colors.LightGray)));
        addSubjectcatBtn.setOnAction(this::addSubjectCat);

        classcoefBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EYE_PLUS,32,Paint.valueOf(Store.Colors.LightGray)));
        classcoefBtn.setOnAction(e->handleConfigureClassCoef());

        bindFields();


    }

    public void bindFields() {
        classcoefBtn.setDisable(true);
        coefField.textProperty().addListener((observableValue, s, newval) -> {
            if (newval.isEmpty() || Objects.equals(newval, null)) {
                classcoefBtn.setDisable(true);
            }else classcoefBtn.setDisable(false);
        });
    }


    public void handleConfigureClassCoef() {
        System.out.println("current loaded config "+configuredCoefP.get());
        URL url = ResourceUtil.getAppResourceURL("views/others/verticalRectWindow.fxml");
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
        stage.initOwner(thisStage.get());
        stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
        stage.setResizable(false);

        VerticalRectController configController = fxmlLoader.getController();

        if (!isupdate.get() || Objects.equals(null,configuredCoefP.get())) {
            configController.initConfigureClassCoefs(Integer.parseInt(coefField.getText()), namefield.getText());
        } else {
            configController.initConfigureClassCoefs(Integer.parseInt(coefField.getText()), namefield.getText(),configuredCoefP.get());
        }

        PopOver p = ProjectUtils.showPopover("", root, PopOver.ArrowLocation.LEFT_CENTER, false, true);
        p.show(classcoefBtn);

        configController.confirmBtn.setOnAction(e -> {
            configuredCoefP.set(configController.classSubjectCoefMap);
            p.hide();
        });
        configController.closetP.setOnAction(e-> p.hide());
        p.setOnHiding(e-> System.out.println("configured coefficients "+configuredCoefP.get()));


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
        isupdate.set(true);

        String sname = PgConnector.getFielorBlank(selected, "subject_name");
        subjectCodefield.setText(PgConnector.getFielorBlank(selected,"subject_code"));
        namefield.setText(sname);
        abbrfield.setText(PgConnector.getFielorBlank(selected,"subject_abbreviation"));
        coefField.setText(PgConnector.getFielorBlank(selected,"subject_coefficient"));
        headField.setText(PgConnector.getFielorBlank(selected,"department_head"));
        categoryField.setValue(PgConnector.getFielorBlank(selected,"subject_category"));

        try {
            PreparedStatement ps = PgConnector.getConnection().prepareStatement("select * from subjects where subject_name=?");
            ps.setString(1, sname);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                PGobject pGobject = (PGobject) rs.getObject("coef_configs");
//                HashMap<String, String> coefStringmap = PgConnector.getMapFromJsonB(pGobject);
                if (!Objects.equals(null, pGobject)) {
                    HashMap<String, String> foundCoefMap = PgConnector.parsePgMapString(pGobject.getValue(),"\\D");

                    HashMap<Integer, Integer> intMap = new HashMap<>();
                    for (String k : foundCoefMap.keySet()) {
                        if (!intMap.containsKey(Integer.parseInt(k))) {
                            intMap.put(Integer.parseInt(k), Integer.parseInt(foundCoefMap.get(k)));
                        }
                    }

                    configuredCoefP.set(intMap);


                }


            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void save() throws SQLException {
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

            //validate
            List<HashMap<String, Object>> check = PgConnector.fetch(String.format("select * from subjects where subject_name='%s' or subject_code='%s'", name.toLowerCase(),
                    subjectCode), PgConnector.getConnection());
            if (!check.isEmpty()) {
                Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.ERROR, "INSERTION FAILED", "ERR",
                        Translator.getIntl("subject_name_code_exist"), ButtonType.OK);
                a.showAndWait();
                return;
            }

            PgConnector.insert(insertsubject);

            //update with coeff config
            if (!Objects.equals(null, configuredCoefP.get())) {
                PreparedStatement updateConfigStatement = PgConnector.getConnection().prepareStatement("update subjects set coef_configs=? where subject_name=?");

                updateConfigStatement.setObject(1,PgConnector.getJsonbObject(configuredCoefP.get()));
                updateConfigStatement.setString(2, name.toLowerCase());
                updateConfigStatement.executeUpdate();
            }


            Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.INFORMATION, "INSERTION SUCCESS", "INFO",
                    Translator.getIntl("data_updated"), ButtonType.OK);
            a.showAndWait();
            thisStage.get().close();

        }


    }

    public void update(HashMap<String,Object> s) throws SQLException {
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

            //update with coeff config
            if (!Objects.equals(null, configuredCoefP.get())) {
                PreparedStatement updateConfigStatement = PgConnector.getConnection().prepareStatement("update subjects set coef_configs=? where subject_name=?");

                updateConfigStatement.setObject(1,PgConnector.getJsonbObject(configuredCoefP.get()));
                updateConfigStatement.setString(2, name.toLowerCase());
                updateConfigStatement.executeUpdate();
            }



            Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
                    Translator.getIntl("data_updated"), ButtonType.OK);
            a.showAndWait();
            thisStage.get().close();

        }

    }


}
