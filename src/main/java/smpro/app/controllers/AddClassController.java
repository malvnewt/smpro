package smpro.app.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddClassController implements Initializable {
    public Button saveClassBtn;
    public Button cancelBtn;
    public ComboBox<HashMap<String,Object>> formatsCombo;
    public AnchorPane gdPreview;
    public AnchorPane gcPreview;
    public AnchorPane gbPreview;
    public AnchorPane gaPreview;
    public ImageView previewImageview;
    public ListView<String> compulsorylv;
    public Button compulsoryBtn;
    public ListView<String> gdlv;
    public Button groupDbtn;
    public TextField gdLabel;
    public ListView<String> gclv;
    public Button groupCbtn;
    public TextField gclable;
    public ListView<String> gblv;
    public Button groupBbtn;
    public TextField gbLabel;
    public ListView<String> galv;
    public Button groupAbtn;
    public TextField gAlabel;
    public ComboBox<String> sectioncombo;
    public ComboBox<Integer> cyclecombo;
    public ComboBox<String> classmastercombo;
    public TextField shortnamefield;
    public TextField classnamefiled;


    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    BooleanProperty isUpdate = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        bindFields();
        bindActions();
        initUi();

        isUpdate.addListener((o,old,newv)->{
            if (newv)prepareUpdate();
        });
        
    }


    public void bindActions() {
        cancelBtn.setOnAction(e->thisStage.get().close());

        saveClassBtn.setOnAction(e->{
            if (isUpdate.get()) {
                save();
            }else update();
        });


    }

    public void bindFields() {

    }
    public void initUi() {

    }


    public void prepareUpdate() {

    }

    public void loadClassSettings() {

    }

    public void save() {

    }

    public void update() {

    }


}
