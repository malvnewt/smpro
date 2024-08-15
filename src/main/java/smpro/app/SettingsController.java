package smpro.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public TabPane settingsTabpane;
    public TitledPane texttiled;


    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public Tab base_settings;
    public Button importImgbtn;
    public ImageView mainimgview;
    public ImageView secodimgivew;
    public Button savebaseBtn;
    public Button next1;
    public Tab addresstab;
    public Tab academicyeartab;
    public Tab sectionstab;
    public Tab tradestab;
    public Tab subjectstab;
    public Tab classestab;
    public Tab userstab;
    public Tab timetabletab;


    ObjectProperty<Image> logoProperty = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        settingsTabpane.getTabs().forEach(t->t.setClosable(false));


//        for (Tab t:new Tab[]{base_settings,})

        base_settings.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.STAR_SETTINGS,20, Paint.valueOf(Store.Colors.Gray)));
        addresstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CONTACTS,20, Paint.valueOf(Store.Colors.Gray)));
        userstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT,20, Paint.valueOf(Store.Colors.Gray)));
        academicyeartab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOCK,20, Paint.valueOf(Store.Colors.Gray)));
        classestab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SCHOOL,20, Paint.valueOf(Store.Colors.Gray)));
        sectionstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE,20, Paint.valueOf(Store.Colors.Gray)));
        timetabletab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TABLE,20, Paint.valueOf(Store.Colors.Gray)));
        subjectstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignB.BOOK,20, Paint.valueOf(Store.Colors.Gray)));
        tradestab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE,20, Paint.valueOf(Store.Colors.Gray)));

        buildBase();

    }

    public void changeTab(int inex) {
        System.out.println("Changing settings tab");
        settingsTabpane.getSelectionModel().select(inex);
    }

    /////////////////////////////////////////   BASE TAB
    public void importLogo() {

        Image img = new Image(ResourceUtil.getStystemFileStream(thisStage.get(),
                List.of(new FileChooser.ExtensionFilter("IMAGE FILE", "*.png", "*.jpg", "*.svg", "*jpeg"))));

        logoProperty.set(img);



    }


    public void buildBase() {
        mainimgview.imageProperty().bind(logoProperty);
        secodimgivew.imageProperty().bind(logoProperty);
        importImgbtn.setOnAction(e-> importLogo());

        importImgbtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignI.IMAGE_AREA,100, Paint.valueOf(Store.Colors.Gray)));

        // obtain and fill current values




    }

    public void saveBase() {

    }
    /////////////////////////////////////////




}
