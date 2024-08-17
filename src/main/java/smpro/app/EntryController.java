package smpro.app;

import atlantafx.base.controls.*;
import atlantafx.base.util.Animations;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.cell.ImageGridCell;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class EntryController implements Initializable {


    public VBox windowTopvb;
    public MenuBar menubar;
    public ToolBar maintoolbar;
    public SplitPane mainsp;
    public AnchorPane menupane;
    public AnchorPane contentpane;
    public Label setings_label;
    public Button settingsBtn;
    public Button menuBtn;
    public HBox menuexpanhb;
    public Label expane_menu;
    public VBox menuitemsVb;
    public HBox settingsContainer;



    public  ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();





    List<String> featureNames = List.of(
            "dashboard", "students", "teachers", "classes", "human_resource",
            "score_records", "timetable", "dicipline_conduct", "suiveillance", "reports_transcripts",
            "announcement", "library"
    );

    public HashMap<String, String> features = new HashMap<>(Map.of(
            "dashboard","dashboard1.png",
            "students","students.png",
            "teachers","teachers.png",
            "classes","class.png",
            "score_records","marksheet.png",
            "timetable","timetable.png",
            "human_resource","hr.png",
            "reports_transcripts","reports2.png",
            "announcement","messages2.png"
    ));


    List<HBox> menuContainers = new ArrayList<>();


    // PROPERTY BINDINGS
    BooleanProperty menuIsExpanded = new SimpleBooleanProperty(false);

    ObjectProperty<Node> selectedFeatureProperty = new SimpleObjectProperty<>();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        features.put("dicipline_conduct", "discipline1.png");
        features.put("library", "library2.png");
        features.put("suiveillance", "camera3.png");

        configureUi();
    }


    public void configureUi() {
//        ProjectUtils.animatePaneSide(menupane,'w', Store.MENU_COLLAPSE_WIDTH);
        menupane.setMaxWidth(Store.MENU_COLLAPSE_WIDTH);



        // set menu and setting icons
        menuBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MENU, 70, Paint.valueOf("gray")));

        menuBtn.setOnAction(e -> {
            ProjectUtils.shakeX(menuBtn, -5).play();

            if (menuIsExpanded.get()) {
                menupane.setMinWidth(Store.MENU_COLLAPSE_WIDTH);
                menuBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MENU, 70, Paint.valueOf("gray")));

                ProjectUtils.animatePaneSide(menupane, 'w', Store.MENU_COLLAPSE_WIDTH);
            } else {
                menupane.setMaxWidth(Store.MENU_EXPAND_WIDTH);
                ProjectUtils.animatePaneSide(menupane, 'w', Store.MENU_EXPAND_WIDTH);
                menuBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MENU_OPEN, 70, Paint.valueOf("gray")));

            }
            menuIsExpanded.set(!menuIsExpanded.get());

        });

        settingsBtn.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/menu_icons/setting2.png", 50, 50)));

        settingsBtn.setOnAction(e -> {

            try {
                ProjectUtils.openSettings(0,thisStage.get());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ProjectUtils.shakeX(settingsContainer, -5).play();

        });


        // seetings and menu containers

        for (HBox h : new HBox[]{menuexpanhb, settingsContainer}) {
            h.addEventHandler(MouseEvent.MOUSE_ENTERED, this::handleMouseEnter);
            h.addEventHandler(MouseEvent.MOUSE_EXITED, this::handleMouseLeave);
        }
          menuexpanhb.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
          settingsContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleSettingsPressed);

        //////////////////////
        insertMenuItems();
        selectedFeatureProperty.addListener((observableValue, oldnode, newnode) -> {
            newnode.setStyle("-fx-background-color: "+Store.Colors.LightGray);

            if (!Objects.equals(null,oldnode))oldnode.setStyle("-fx-background-color: transparent");


        });


    }


    public void insertMenuItems() {
        menuitemsVb.setSpacing(5);

        for (String featureName : featureNames) {

            int index = featureNames.indexOf(featureName);

            Label fLabel = new Label(Translator.getIntl(featureName).toUpperCase());
            fLabel.setStyle("-fx-font-weight: bold;-fx-font-family: Consolas");

            Button fButton = new Button();
            fButton.setStyle("-fx-border-width: 0;-fx-background-color: transparent");
            fButton.setMinSize(50, 50);
            fButton.setMaxSize(50, 50);


            String filename = features.get(featureName);

            fButton.setGraphic(new
                    ImageView(ResourceUtil.getImageFromResource("images/menu_icons/" + filename, 50, 50)));
            fButton.setTooltip(ProjectUtils.createTooltip(Translator.getIntl(featureName).toUpperCase()));
            fButton.setCursor(Cursor.HAND);



            HBox hb = new HBox(fButton,fLabel);
            hb.setSpacing(20);
            hb.setAlignment(Pos.CENTER_LEFT);
            hb.setPadding(new Insets(5));
            hb.setId(featureName);

//            hb.setEffect(dropshadowEffect);

            hb.addEventHandler(MouseEvent.MOUSE_ENTERED,this::handleMouseEnter);
            hb.addEventHandler(MouseEvent.MOUSE_EXITED,this::handleMouseLeave);

            hb.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                ProjectUtils.shakeX(hb, -5).play();
                selectedFeatureProperty.set(hb);
            });

            fButton.setOnAction(e-> {
                ProjectUtils.shakeX(hb, -5).play();
                selectedFeatureProperty.set(hb);

            });

            if (index==0){
                selectedFeatureProperty.set(hb);
                hb.setStyle("-fx-background-color: "+Store.Colors.selectionBg);
            }

            menuitemsVb.getChildren().add(hb);






        }




    }



    public void handleMouseEnter(MouseEvent event) {
        HBox source = (HBox) event.getSource();
        source.setStyle("-fx-background-color: "+Store.Colors.hoverbg);

    }

    public void handleMouseLeave(MouseEvent event) {
        HBox source = (HBox) event.getSource();

        if (!Objects.equals(source.getId(), selectedFeatureProperty.get().getId())) source.setStyle("-fx-background-color: transparent");
        if (Objects.equals(source.getId(), selectedFeatureProperty.get().getId())) source.setStyle("-fx-background-color: "+Store.Colors.selectionBg);

    }

    public void handleMousePressed(MouseEvent event) {
        HBox source = (HBox) event.getSource();

        ProjectUtils.shakeX(source, -5).play();


        menupane.setMinWidth(Store.MENU_COLLAPSE_WIDTH);
        ProjectUtils.animatePaneSide(menupane, 'w', Store.MENU_COLLAPSE_WIDTH);
        menuBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MENU, 70, Paint.valueOf("gray")));
        menuIsExpanded.set(false);

    }


    public void handleSettingsPressed(MouseEvent event) {
//        Animations.pulse(source).play();

        try {
            ProjectUtils.openSettings(0,thisStage.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ProjectUtils.shakeX(settingsContainer, -5).play();


    }


    public void refreshApp() {

    }


}