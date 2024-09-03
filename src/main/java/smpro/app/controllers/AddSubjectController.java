package smpro.app.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import javax.swing.tree.TreeNode;
import java.net.URL;
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
        categoryField.getItems().addAll(Store.SubjectCategories.SupportedCategories);
        categoryField.setValue(Store.SubjectCategories.SupportedCategories.get(0));
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
                        "values ('%s','%s','%s',%d,'%s','%s')", name,subjectCode, category, Integer.parseInt(coefficient), shortName, departmentHead);
                PgConnector.insert(insertsubject);


                Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
                        Translator.getIntl("data_updated"), ButtonType.OK);
                a.showAndWait();
                thisStage.get().close();

            }

        });



    }
}
