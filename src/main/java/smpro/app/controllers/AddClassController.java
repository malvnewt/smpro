package smpro.app.controllers;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class AddClassController implements Initializable {
    public Button saveClassBtn;
    public Button cancelBtn;
    public ComboBox<HashMap<String,Object>> formatsCombo;
    public TilePane gdPreview;
    public TilePane gcPreview;
    public TilePane gbPreview;
    public TilePane gaPreview;
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
    public ComboBox<Integer> levelcombo;
    public BooleanProperty isUpdate = new SimpleBooleanProperty(false);
    public ImageView iconView;
    public TitledPane groupAtpane;
    public VBox logoContainer;


    List<Button> editBtns  = new ArrayList<>();
    List<TextField> editFields = new ArrayList<>();
    List<ListView<String>> editLview = new ArrayList<>();


    /// Properties
    StringProperty classnameP = new SimpleStringProperty();
    StringProperty shortNameP = new SimpleStringProperty();
    StringProperty classMasterP = new SimpleStringProperty();
    StringProperty sectionP = new SimpleStringProperty();

    IntegerProperty cycleP = new SimpleIntegerProperty(1);
    IntegerProperty levelP = new SimpleIntegerProperty(1);



    ListProperty<String> catAsubs = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    ListProperty<String> catBsubs = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    ListProperty<String> catCsubs = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    ListProperty<String> catDsubs = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    ListProperty<String> compulsorySubsP = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    StringProperty catAlabel = new SimpleStringProperty();
    StringProperty catBlabel = new SimpleStringProperty();
    StringProperty catClabel = new SimpleStringProperty();
    StringProperty catDlabel = new SimpleStringProperty();

    List<StringProperty> labelProperties = List.of(catAlabel, catBlabel, catClabel, catDlabel);
    List<ListProperty<String>> categorySubjectsProperties = List.of(catAsubs, catBsubs, catCsubs, catDsubs);


    List<String> dbSubjects = PgConnector.listHashAttrs(PgConnector.
            fetch("select distinct subject_name,id from subjects order by subject_name",
                    PgConnector.getConnection()), "subject_name");







    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editBtns.addAll(List.of(groupAbtn, groupBbtn, groupCbtn, groupDbtn));
        editFields.addAll(List.of(gAlabel, gbLabel, gclable, gdLabel));
        editLview.addAll(List.of(galv, gblv, gclv, gdlv));



        bindFields();
        bindActions();
        try {
            initUi();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
        //bind group labels
        for (StringProperty labelP:labelProperties)labelP.bind(editFields.get(labelProperties.indexOf(labelP)).textProperty());

        //validation for group name fields
        editFields.forEach(f-> f.textProperty().addListener((o, old, newval)->{
            if (!newval.isEmpty())f.getStyleClass().remove("error-textfield");

        }));

        //bind subjectCategories and preview
        List<TilePane> previewTiles = List.of(gaPreview, gbPreview, gcPreview, gdPreview);

        previewTiles.forEach(tilePane -> {
            tilePane.setHgap(2);
            tilePane.setVgap(2);
            tilePane.setAlignment(Pos.TOP_LEFT);
        });

        for (ListProperty<String> categorySubsP : categorySubjectsProperties) {
            int index = categorySubjectsProperties.indexOf(categorySubsP);
            ListView<String> lv = editLview.get(index);
            TilePane tilePane = previewTiles.get(index);

            categorySubsP.bind(lv.itemsProperty());

            lv.itemsProperty().addListener((observableValue, strings, newItems) -> {
                categorySubsP.set(newItems);
                tilePane.getChildren().clear();

                newItems.forEach(s->{
                    Label l = new Label(ProjectUtils.capitalize(s));
                    l.getStyleClass().add("section-title");
                    l.setStyle("-fx-font-weight: " +
                            "bold;-fx-text-fill: #242424;-fx-font-family: Consolas;-fx-font-size: 10px;" +
                            "-fx-background-color: transparent");
                    l.setTooltip(ProjectUtils.createTooltip(s.toUpperCase()));
                    l.setEffect(new Lighting());
                    tilePane.getChildren().add(l);
                });
            });



        }

        compulsorySubsP.bind(compulsorylv.itemsProperty());



        //bind main class fields
        shortNameP.bind(shortnamefield.textProperty());
        classnameP.bind(classnamefiled.textProperty());
        classMasterP.bind(classmastercombo.valueProperty());
        cycleP.bind(cyclecombo.valueProperty());
        levelP.bind(levelcombo.valueProperty());
        sectionP.bind(sectioncombo.valueProperty());

        //bind preview



    }
    public void initUi() throws SQLException {

        //place logo on preview
        PreparedStatement ps = PgConnector.getConnection().prepareStatement("select logo_bytes from base where id=1");

        InputStream logoStream = PgConnector.readBinarydata(ps);

        if (Objects.equals(null, logoStream)) {
            logoContainer.getChildren().clear();
            Label l = new Label("LOGO");
            l.getStyleClass().add("smpro-large-sub");
            l.setStyle("-fx-text-fill: #242424;-fx-font-weight: bold");
            logoContainer.getChildren().add(l);
            logoContainer.setAlignment(Pos.CENTER);
        } else {
            Image img = new Image(logoStream, previewImageview.getFitWidth(), previewImageview.getFitWidth(), true, true);
            previewImageview.setImage(img);

        }



        //graphics
        for (Button b:editBtns){
            int index = editBtns.indexOf(b);

            b.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS, 15, Paint.valueOf(Store.Colors.lightestGray)));
            b.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("set_subjects")));

            b.setOnAction(e-> {
                try {
                    changeCategorySubjects(index);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        compulsoryBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS, 15, Paint.valueOf(Store.Colors.lightestGray)));
        compulsoryBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("compulsory_btn_tp")));

        iconView.setImage(ResourceUtil.getImageFromResource("images/plus.png", 50, 50, true));


        // add cycles
        cyclecombo.getItems().addAll(Store.supportedCycles);
        levelcombo.getItems().addAll(Store.supportedLevels);

        List<String> sectionStrings = PgConnector.listHashAttrs(PgConnector.
                fetch("select * from sections order by section_name", PgConnector.getConnection()), "section_name");
        sectioncombo.getItems().addAll(sectionStrings);






    }


    public void prepareUpdate() {

    }

    public void loadClassSettings() {

    }

    public void changeCategorySubjects(int index) throws IOException {
        ListView<String> lv = editLview.get(index);
        TextField textField = editFields.get(index);



        String groupName = textField.getText();
        String popupTitle = groupName.isEmpty() ? Translator.getIntl("select_group_subjects") :
                Translator.getIntl("select_group_subjects") + String.format(" [ %s ]", groupName);

        URL url = ResourceUtil.getAppResourceURL("views/others/list-popup.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang"));
        Parent root =fxmlLoader.load();
        Scene scene = new Scene(root);

        ListDisplayController listDisplayController = fxmlLoader.getController();
        listDisplayController.title.setText(popupTitle);

        listDisplayController.loadDataItems(dbSubjects, new ArrayList<>());


        Stage stage = new Stage();
        stage.setTitle("PROMPT");
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(thisStage.get());
        stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
        stage.setResizable(false);

        stage.show();
        ProjectUtils.positionFloatingStage(thisStage.get(), stage, lv, 20, 0);






    }

    public void save() {

    }

    public void update() {

    }


}
