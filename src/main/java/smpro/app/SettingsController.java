package smpro.app;

import atlantafx.base.util.Animations;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.materialdesign2.*;
import org.postgresql.jdbc.PgArray;
import org.w3c.dom.events.Event;
import smpro.app.controllers.AddSubjectController;
import smpro.app.controllers.AddTradeController;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import javax.swing.tree.TreeNode;
import java.io.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class SettingsController implements Initializable {
    public TabPane settingsTabpane;
    public TitledPane texttiled;


    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public Tab base_settings;
    public Button importImgbtn;
    public ImageView mainimgview;
    public ImageView secodimgivew;
    public Button savebaseBtn;
    public Tab addresstab;
    public Tab academicyeartab;
    public Tab sectionstab;
    public Tab tradestab;
    public Tab subjectstab;
    public Tab classestab;
    public Tab userstab;
    public Tab timetabletab;
    public ComboBox<String> secotorCombo;
    public TextField mottofield;
    public ComboBox<String> institutionCombo;
    public TextField principalfield;
    public TextField abbrAltfield;
    public TextField namealtField;
    public TextField abbfield;
    public TextField schoolnamefield;
    public Button nextBtnBase;
    public TextField regionfield;
    public TextField divisionfield;
    public TextField townfield;
    public TextField addressField;
    public TextField lineonef;
    public TextField linetwof;
    public TextField emailf;
    public Label usernamel;
    public Label passl;
    public TextField passfield;
    public TextField usernamefield;
    public Button saveAdressBtn;
    public Button nextAdressTab;
    public TextField poboxfield;
    public Button togglePass;
    public Label townl;
    public Label addressl;
    public Label phonel;
    public Label emall;
    public AnchorPane timetablePane;
    public AnchorPane adminPane;
    public AnchorPane classesPane;
    public AnchorPane subjectsPane;
    public AnchorPane tradePane;
    public AnchorPane sectionsPane;
    public AnchorPane academicPane;
    public AnchorPane addressPane;
    public AnchorPane basePane;
    public TextField yearFrom;
    public TextField yearTo;
    public DatePicker datet2;
    public DatePicker datet3;
    public DatePicker datetNext;
    public Label datedisplay1;
    public Label datedisplay2;
    public Label datedisplay3;
    public Label term3Warning;
    public Button saveAcademicYearbtn;
    public Button nextAcayearBtn;
    public Button addsectionBtn;
    public Button removeSectionBtn;
    public TableView<HashMap<String,Object>> sectionstable;
    public Button savesectionsBtn;
    public Button nextSections;
    public TableView<HashMap<String,Object>> tradesTable;
    public Button addTradeBtn;
    public Button removeTradeBtn;
    public Button savesectionsBtn1;
    public Button nextTradesBtn;
    public TableView<HashMap<String,Object>> subjectsTable;
    public Button addSubjectBtn;
    public Button removeSubjectBtn;
    public Button nextSubjects;


    ObjectProperty<Image> logoProperty = new SimpleObjectProperty<>();


    //base tab properties
    StringProperty nameProperty = new SimpleStringProperty();
    StringProperty abbrPty = new SimpleStringProperty();
    StringProperty namealtpty = new SimpleStringProperty();
    StringProperty abbraltpty = new SimpleStringProperty();
    StringProperty mottopty = new SimpleStringProperty();
    StringProperty principty = new SimpleStringProperty();
    StringProperty sectionpty = new SimpleStringProperty();
    StringProperty typepty = new SimpleStringProperty();


    //address tab properties
    StringProperty regionP = new SimpleStringProperty();
    StringProperty divisionP = new SimpleStringProperty();
    StringProperty lineoneP = new SimpleStringProperty();
    StringProperty linetwoP = new SimpleStringProperty();
    StringProperty townP = new SimpleStringProperty();
    StringProperty addressP = new SimpleStringProperty();
    StringProperty emailP = new SimpleStringProperty();
    StringProperty poboxP = new SimpleStringProperty();

    StringProperty usernameP = new SimpleStringProperty();
    StringProperty passP = new SimpleStringProperty();
    BooleanProperty showPassP = new SimpleBooleanProperty(false);

    StringProperty logoPathProprty = new SimpleStringProperty();


    List<Pane> tabRootPanes = new ArrayList<>();
    ObjectProperty<Tab> currentTabProperty = new SimpleObjectProperty<>(base_settings);
    IntegerProperty currentTabIndexProperty = new SimpleIntegerProperty(0);



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabRootPanes.addAll(List.of(basePane, addressPane, academicPane,
                sectionsPane, tradePane, subjectsPane, classesPane, addressPane, timetablePane));
        settingsTabpane.getTabs().forEach(t->t.setClosable(false));

        bindFields();

        // base tab init
        base_settings.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.STAR_SETTINGS,20, Paint.valueOf(Store.Colors.Gray)));
        addresstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CONTACTS,20, Paint.valueOf(Store.Colors.Gray)));
        userstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT,20, Paint.valueOf(Store.Colors.Gray)));
        academicyeartab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOCK,20, Paint.valueOf(Store.Colors.Gray)));
        classestab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SCHOOL,20, Paint.valueOf(Store.Colors.Gray)));
        sectionstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE,20, Paint.valueOf(Store.Colors.Gray)));
        timetabletab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TABLE,20, Paint.valueOf(Store.Colors.Gray)));
        subjectstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignB.BOOK,20, Paint.valueOf(Store.Colors.Gray)));
        tradestab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE,20, Paint.valueOf(Store.Colors.Gray)));





        try {
            buildBase();
            buildAddress();
            buildAcademicYear();
            buildSections();
            buildTrades();
            buildSubjects();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void changeTab(int inex) {
        System.out.println("Changing settings tab");
        settingsTabpane.getSelectionModel().select(inex);
        currentTabIndexProperty.set(inex);

    }


    public void bindFields() {
        settingsTabpane.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldindex, newindex) -> {


            Pane oldPane = tabRootPanes.get(oldindex.intValue());
            Pane newPane = tabRootPanes.get(newindex.intValue());

            oldPane.setOpacity(0);

            FadeTransition tout = new FadeTransition();
            tout.setFromValue(1);
            tout.setToValue(0);
            tout.setDuration(Duration.millis(300));
            tout.setInterpolator(Interpolator.EASE_BOTH);
            tout.setNode(oldPane);

            FadeTransition t = new FadeTransition();
            t.setFromValue(0);
            t.setToValue(1);
            t.setDuration(Duration.millis(300));
            t.setInterpolator(Interpolator.EASE_BOTH);
            t.setNode(newPane);

            tout.playFromStart();
            t.playFromStart();


        });

        // bind base fields
        nameProperty.bind(schoolnamefield.textProperty());
        abbrPty.bind(abbfield.textProperty());
        namealtpty.bind(namealtField.textProperty());
        abbraltpty.bind(abbrAltfield.textProperty());
        mottopty.bind(mottofield.textProperty());
        principty.bind(principalfield.textProperty());

        sectionpty.bind(secotorCombo.valueProperty());
        typepty.bind(institutionCombo.valueProperty());

        for (TextField f : new TextField[]{principalfield, mottofield, abbrAltfield, abbfield, namealtField, schoolnamefield}) {
            f.textProperty().addListener(((observableValue, s, t1) -> {
                String filteredText = t1.replaceAll("'","");
                f.setText(filteredText);

            }));
        }

        // bind address fields
        usernameP.bind(usernamefield.textProperty());
        passP.bind(passfield.textProperty());

        regionP.bind(regionfield.textProperty());
        divisionP.bind(divisionfield.textProperty());
        lineoneP.bind(lineonef.textProperty());
        linetwoP.bind(linetwof.textProperty());
        emailP.bind(emailf.textProperty());
        poboxP.bind(poboxfield.textProperty());
        addressP.bind(addressField.textProperty());
        townP.bind(townfield.textProperty());


    }

    /////////////////////////////////////////   BASE TAB
    public void importLogo() throws FileNotFoundException {
        InputStream stream;

            String filepath  = ResourceUtil.getStystemFilePath(thisStage.get(),
                    List.of(new FileChooser.ExtensionFilter("IMAGE FILE", "*.png", "*.jpg", "*.svg", "*jpeg")));
        System.out.println(filepath);

            stream = new FileInputStream(filepath);


        try {
            System.out.println("logo size "+stream.available());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (Objects.equals(null,stream)) return;

        Image img = new Image(stream);

        logoProperty.set(img);
        logoPathProprty.set(filepath);




    }

/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
    public void buildBase() throws SQLException {
        mainimgview.imageProperty().bind(logoProperty);
        secodimgivew.imageProperty().bind(logoProperty);
        importImgbtn.setOnAction(e-> {
            try {
                importLogo();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        importImgbtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignI.IMAGE_AREA,100, Paint.valueOf(Store.Colors.Gray)));


        //populate combos

        secotorCombo.getItems().addAll(Store.Sectors.supportedSectors);
        institutionCombo.getItems().addAll(Store.Insitutions.supportedInstitutions);

        for (ComboBox<String> c : new ComboBox[]{secotorCombo, institutionCombo}) {

            c.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(String s, boolean b) {
                    super.updateItem(s, b);
                    if (!b) {
                        setText(Translator.getIntl(s).toUpperCase());
                        setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE, 15, Paint.valueOf(Store.Colors.Gray)));
                    }
                }
            });

            c.setCellFactory(new Callback<>() {
                @Override
                public ListCell<String> call(ListView<String> stringListView) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(String s, boolean b) {
                            super.updateItem(s, b);
                            if (!b) {
                                setText(Translator.getIntl(s).toUpperCase());
                                setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE, 15, Paint.valueOf(Store.Colors.Gray)));
                            }
                        }
                    };
                }
            });

        }



        // obtain and fill current values
        HashMap<String, Object> basedata = PgConnector.fetch("select * from base",PgConnector.getConnection()).get(0);

        String school = PgConnector.getFielorBlank(basedata,"school_name");
        String schoolalt = PgConnector.getFielorBlank(basedata,"schoolname_alt");
        String schoolabbr = PgConnector.getFielorBlank(basedata,"school_abbr");
        String schoolabbrAlt =  PgConnector.getFielorBlank(basedata,"schoolabbr_alt");
        String motto = PgConnector.getFielorBlank(basedata,"motto");
        String principal =PgConnector.getFielorBlank(basedata,"principal");

        String institutionType = String.valueOf(basedata.get("type"));
        String sector = String.valueOf(basedata.get("sector"));

        institutionCombo.setValue(institutionType);
        secotorCombo.setValue(sector);

        namealtField.setText(schoolalt);
        abbfield.setText(schoolabbr.toUpperCase());

        schoolnamefield.setText(school);
        abbrAltfield.setText(schoolabbrAlt.toUpperCase());

        mottofield.setText(motto);
        principalfield.setText(principal);

        // load logo
        PreparedStatement ps = PgConnector.connection.get().prepareStatement("select logo_bytes from base where id=1");
        InputStream is = PgConnector.readBinarydata(ps);
        try {
            System.out.println("obtained stream "+is.available());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logoProperty.set(new Image(is));


        //attach handlers

        savebaseBtn.setOnAction(e->{
            try {
                saveBase(e);
            } catch (SQLException | IOException ex) {
                System.err.println(ex.getLocalizedMessage());
                throw new RuntimeException(ex);
            }
        });

        nextBtnBase.setOnAction(e -> changeTab(1));











    }

    public void buildAddress() {
        usernamel.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT, 12, Paint.valueOf(Store.Colors.black)));
        passl.setGraphic(ProjectUtils.createFontIcon(MaterialDesignK.KEY, 12, Paint.valueOf(Store.Colors.black)));

        phonel.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PHONE, 12, Paint.valueOf(Store.Colors.black)));
        emall.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EMAIL, 12, Paint.valueOf(Store.Colors.black)));
//        addressl.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MAP, 12, Paint.valueOf(Store.Colors.black)));
//        townl.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CITY, 12, Paint.valueOf(Store.Colors.black)));

        for (TextField t : new TextField[]{poboxfield, linetwof, lineonef}) {
            t.textProperty().addListener((observableValue, s, t1) -> t.setText(t1.replaceAll("\\D", "")));
        }

        for (TextField f : new TextField[]{regionfield, divisionfield, emailf, townfield, addressField, usernamefield,passfield}) {
            f.textProperty().addListener(((observableValue, s, t1) -> {
                String filteredText = t1.replaceAll("'","");
                f.setText(filteredText);

            }));
        }


        // obtain and fill address values
        HashMap<String, Object> basedata = PgConnector.fetch("select * from base",PgConnector.getConnection()).get(0);

        String region = PgConnector.getFielorBlank(basedata,"region");
        String division = PgConnector.getFielorBlank(basedata,"division");
        String lineone = PgConnector.getFielorBlank(basedata,"lineone");
        String linetwo =  PgConnector.getFielorBlank(basedata,"linetwo");
        String address = PgConnector.getFielorBlank(basedata,"address");
        String email =PgConnector.getFielorBlank(basedata,"email");
        String town =PgConnector.getFielorBlank(basedata,"town_city");
        String pobox =PgConnector.getFielorBlank(basedata,"pobox");

        String username =PgConnector.getFielorBlank(basedata,"root_username");
        String passw =PgConnector.getFielorBlank(basedata,"root_password");


        regionfield.setText(region);
        divisionfield.setText(division);
        lineonef.setText(lineone);
        linetwof.setText(linetwo);
        addressField.setText(address);
        emailf.setText(email);
        townfield.setText(town);
        poboxfield.setText(pobox);

        usernamefield.setText(username);
        passfield.setText(passw);


        // add action handlers
        saveAdressBtn.setOnAction(e -> saveAddress());
        nextAdressTab.setOnAction(e -> changeTab(2));

    }


    public void buildAcademicYear() {
        String t3warning = String.format("\"%s\" %s %s", Translator.getIntl("nextYearresume"),Store.UnicodeSumnbol.blank, Translator.getIntl("t3warning"));
        term3Warning.setText(t3warning);

        term3Warning.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/warning.png", 40, 40, true)));


        for (TextField t : new TextField[]{yearFrom, yearTo}) {
            t.textProperty().addListener((observableValue, s, t1) -> t.setText(t1.replaceAll("\\D", "")));
        }


        HashMap<String, Object> basedata = PgConnector.fetch("select * from base", PgConnector.getConnection()).get(0);
        List<HashMap<String, Object>> terms = PgConnector.fetch("select * from terms order by term", PgConnector.getConnection());

        HashMap<String, Object> t1 = terms.get(0);
        HashMap<String, Object> t2 = terms.get(1);
        HashMap<String, Object> t3 = terms.get(2);

        String currentAcayear = PgConnector.getFielorBlank(basedata, "academic_year");

        Number t1Resume = PgConnector.getNumberOrNull(t1, "resume_date");
        Number t2Resume = PgConnector.getNumberOrNull(t2, "resume_date");
        Number t3Resume = PgConnector.getNumberOrNull(t3, "resume_date");

        //fill current values
        yearFrom.setText(Objects.equals(currentAcayear, "") ? "" : currentAcayear.split("/")[0]);
        yearTo.setText(Objects.equals(currentAcayear, "") ? "" : currentAcayear.split("/")[1]);

        datet2.valueProperty().addListener(((observableValue, localDate, t11) -> datedisplay1.setText(ProjectUtils.getFormatedDate(datet2.getValue(),DateTimeFormatter.ofPattern(Store.baseDateFormat,Translator.getLocale())))));
        datet3.valueProperty().addListener(((observableValue, localDate, t11) -> datedisplay2.setText(ProjectUtils.getFormatedDate(datet3.getValue(),DateTimeFormatter.ofPattern(Store.baseDateFormat,Translator.getLocale())))));
        datetNext.valueProperty().addListener(((observableValue, localDate, t11) -> datedisplay3.setText(ProjectUtils.getFormatedDate(datetNext.getValue(),DateTimeFormatter.ofPattern(Store.baseDateFormat,Translator.getLocale())))));


        datet2.setValue(LocalDate.ofEpochDay(t2Resume.longValue()));
        datet3.setValue(LocalDate.ofEpochDay(t3Resume.longValue()));
        datetNext.setValue(LocalDate.ofEpochDay(t1Resume.longValue()));

        nextAcayearBtn.setOnAction(e -> changeTab(3));
        saveAcademicYearbtn.setOnAction(e->saveAcademicYear());







    }

    public void buildSections() {
        addsectionBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS, 50, Paint.valueOf(Store.Colors.black)));
        removeSectionBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TRASH_CAN, 50, Paint.valueOf(Store.Colors.red)));


        TableColumn<HashMap<String, Object>, String> sectionnamecol =
                ProjectUtils.createTableColumn(Translator.getIntl("section").toUpperCase(), "section_name",true);

        TableColumn<HashMap<String, Object>, String> sectionidcol = ProjectUtils.createTableColumn("ID", "id" );


        List<TableColumn<HashMap<String, Object>, String>> sectioncols = List.of(sectionidcol, sectionnamecol);
        sectionnamecol.setMinWidth(400);
        sectionidcol.setMinWidth(80);

        sectionstable.getColumns().addAll(sectioncols);
        sectionnamecol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TEXT,15,Paint.valueOf(Store.Colors.lightestGray)));
        sectionidcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignN.NUMERIC_0_BOX,15,Paint.valueOf(Store.Colors.lightestGray)));



        // fetch section data
        List<HashMap<String, Object>> sections = PgConnector.fetch("select * from sections order by id", PgConnector.getConnection());
        sectionstable.getItems().addAll(sections);

//        sectionsPane.getStylesheets().add(JMetroStyleClass.BACKGROUND);
//        sectionstable.getStylesheets().addAll(JMetroStyleClass.ALTERNATING_ROW_COLORS, JMetroStyleClass.BACKGROUND, JMetroStyleClass.TABLE_GRID_LINES);

        addsectionBtn.setOnAction(e->{
            TextInputDialog textInputDialog = ProjectUtils.getTextDialog(
                    thisStage.get(), "PROMPT", Translator.getIntl("new_section"), Translator.getIntl("type_name"),
                    new ImageView(ResourceUtil.getImageFromResource("images/plus.png", 50, 50, true))
            );

            Optional<String> optionalS  = textInputDialog.showAndWait();

            optionalS.ifPresent(newval->{

                if (!newval.isEmpty()) {

                //perform insertion
                String insert = String.format("insert into sections (section_name) values ('%s')", newval);
                PgConnector.insert(insert);

                Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
                a.showAndWait();

                //update sections table items
                sectionstable.getItems().clear();
                sectionstable.setItems(FXCollections.observableList(PgConnector.fetch("select * from sections", PgConnector.getConnection())));
                }

            });



        });



        removeSectionBtn.setOnAction(e->{
           HashMap<String,Object> selectedSection =  sectionstable.getSelectionModel().getSelectedItem();
            if (!Objects.equals(null, selectedSection)) {
                Number sectionid = PgConnector.getNumberOrNull(selectedSection, "id");

                String delete = String.format("delete from sections where id=%d", sectionid.intValue());
                PgConnector.update(delete);

                Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "DELETE SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
                a.showAndWait();
                //update sections table items
                sectionstable.getItems().clear();
                sectionstable.setItems(FXCollections.observableList(PgConnector.fetch("select * from sections", PgConnector.getConnection())));


            }

        });

        nextSections.setOnAction(e -> changeTab(4));




    }
    public void buildTrades() {
        addTradeBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS, 50, Paint.valueOf(Store.Colors.black)));
        removeTradeBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TRASH_CAN, 50, Paint.valueOf(Store.Colors.red)));


        TableColumn<HashMap<String, Object>, String> tradenamecol = ProjectUtils.createTableColumn(Translator.getIntl("trade").toUpperCase(), "trade_name");
        TableColumn<HashMap<String, Object>, String> tradeAbbrcol = ProjectUtils.createTableColumn(Translator.getIntl("abbreviation").toUpperCase(), "trade_abbreviation",true);
        TableColumn<HashMap<String, Object>, String> tradeidcol = ProjectUtils.createTableColumn("ID", "id");

        List<TableColumn<HashMap<String, Object>, String>> tradecols = List.of(tradeidcol, tradenamecol,tradeAbbrcol);
        tradenamecol.setMinWidth(300);
        tradeAbbrcol.setMinWidth(200);
        tradeidcol.setMinWidth(80);

        tradenamecol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TEXT,15,Paint.valueOf(Store.Colors.lightestGray)));
        tradeAbbrcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TEXT,15,Paint.valueOf(Store.Colors.lightestGray)));
        tradeidcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignN.NUMERIC_0_BOX,15,Paint.valueOf(Store.Colors.lightestGray)));


        tradesTable.getColumns().addAll(tradecols);

        // fetch section data
        List<HashMap<String, Object>> trades = PgConnector.fetch("select * from trades order by trade_name", PgConnector.getConnection());
        tradesTable.getItems().addAll(trades);

        addTradeBtn.setOnAction(e->{


            URL url = ResourceUtil.getAppResourceURL("views/others/two-inputs.fxml");

            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Scene scene = new Scene(root);

            scene.getStylesheets().addAll(
                    ResourceUtil.getAppResourceURL("css/global.css").toExternalForm(),
                    ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(thisStage.get());
            stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
            stage.setResizable(false);

            AddTradeController addTradeController = fxmlLoader.getController();
            addTradeController.thisStage.set(stage);

            stage.setTitle("PROMPT");
            stage.showAndWait();

            tradesTable.getItems().clear();
            tradesTable.setItems(FXCollections.observableList(PgConnector.fetch("select * from trades order by trade_name", PgConnector.getConnection())));




        });



        removeTradeBtn.setOnAction(e->{
           HashMap<String,Object> selectedTrade =  tradesTable.getSelectionModel().getSelectedItem();
            if (!Objects.equals(null, selectedTrade)) {
                Number tradeid = PgConnector.getNumberOrNull(selectedTrade, "id");

                String delete = String.format("delete from trades where id=%d", tradeid.intValue());
                PgConnector.update(delete);

                Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "DELETE SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
                a.showAndWait();
                //update sections table items
                tradesTable.getItems().clear();
                tradesTable.setItems(FXCollections.observableList(PgConnector.fetch("select * from trades order by trade_name", PgConnector.getConnection())));


            }

        });

        nextTradesBtn.setOnAction(e -> changeTab(5));






    }

    public void buildSubjects() {
        addSubjectBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS, 50, Paint.valueOf(Store.Colors.black)));
        removeSubjectBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TRASH_CAN, 50, Paint.valueOf(Store.Colors.red)));



        TableColumn<HashMap<String, Object>, String> subjectnamecol = ProjectUtils.createTableColumn(Translator.getIntl("subject").toUpperCase(), "subject_name");
        TableColumn<HashMap<String, Object>, String> categoryCol = ProjectUtils.createTableColumn(Translator.getIntl("category").toUpperCase(), "subject_category",true);
        TableColumn<HashMap<String, Object>, String> subjectidcol = ProjectUtils.createTableColumn("ID", "id");
        TableColumn<HashMap<String, Object>, String> subjectAbbrcol = ProjectUtils.createTableColumn(Translator.getIntl("abbreviation").toUpperCase(), "subject_abbreviation",true);
        TableColumn<HashMap<String, Object>, String> subjectcoefcol = ProjectUtils.createTableColumn("COEFF", "subject_coefficient");
        TableColumn<HashMap<String, Object>, String> departmentHeadcol = ProjectUtils.createTableColumn(Translator.getIntl("department_head").toUpperCase(), "department_head");


        List<TableColumn<HashMap<String, Object>, String>> subjectCols = List.of(subjectnamecol,subjectAbbrcol,categoryCol,subjectcoefcol,departmentHeadcol);

        for (TableColumn<HashMap<String, Object>, String> col:new TableColumn[]{subjectnamecol,categoryCol,departmentHeadcol})
            col.setMinWidth(150);
        subjectcoefcol.setMinWidth(80);
        subjectAbbrcol.setMinWidth(80);

        subjectnamecol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TEXT,15,Paint.valueOf(Store.Colors.lightestGray)));
        subjectAbbrcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TEXT,15,Paint.valueOf(Store.Colors.lightestGray)));
        categoryCol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TEXT,15,Paint.valueOf(Store.Colors.lightestGray)));
        departmentHeadcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT,15,Paint.valueOf(Store.Colors.lightestGray)));
        subjectcoefcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignN.NUMERIC,15,Paint.valueOf(Store.Colors.lightestGray)));




        subjectsTable.getColumns().addAll(subjectCols);

        // fetch section data
        List<HashMap<String, Object>> subjects = PgConnector.fetch("select * from subjects order by subject_name", PgConnector.getConnection());
        subjectsTable.getItems().addAll(subjects);

        addSubjectBtn.setOnAction(e->{

            URL url = ResourceUtil.getAppResourceURL("views/others/add-subject.fxml");

            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Scene scene = new Scene(root);

            scene.getStylesheets().addAll(
                    ResourceUtil.getAppResourceURL("css/global.css").toExternalForm(),
                    ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(thisStage.get());
            stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
            stage.setResizable(false);

            AddSubjectController addSubjectController = fxmlLoader.getController();
            addSubjectController.thisStage.set(stage);

            stage.setTitle("PROMPT");
            stage.showAndWait();

            subjectsTable.getItems().clear();
            subjectsTable.setItems(FXCollections.observableList(PgConnector.fetch("select * from subjects order by subject_name", PgConnector.getConnection())));

//            Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
//            Translator.getIntl("data_updated"), ButtonType.OK);
//            a.showAndWait();



        });



        removeSubjectBtn.setOnAction(e->{
           HashMap<String,Object> selectedSubject =  subjectsTable.getSelectionModel().getSelectedItem();
            if (!Objects.equals(null, selectedSubject)) {
                Number subjectid = PgConnector.getNumberOrNull(selectedSubject, "id");

                String delete = String.format("delete from subjects where id=%d", subjectid.intValue());
                PgConnector.update(delete);

                Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "DELETE SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
                a.showAndWait();
                //update sections table items
                subjectsTable.getItems().clear();
                subjectsTable.setItems(FXCollections.observableList(PgConnector.fetch("select * from subjects order  by subject_name",
                        PgConnector.getConnection())));


            }

        });

        nextSubjects.setOnAction(e -> changeTab(6));






    }
    /////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
    public void saveBase(ActionEvent e) throws SQLException, IOException {

        //update fields
        String update = String.format("""
                update base set
                school_name='%s',
                school_abbr='%s',
                schoolname_alt='%s',
                schoolabbr_alt='%s',
                motto='%s',
                principal='%s',
                sector='%s',
                type='%s'
                """, nameProperty.get(), abbrPty.get(), namealtpty.get(), abbraltpty.get(), mottopty.get(), principty.get(), sectionpty.get(), typepty.get());
        PgConnector.update(update);

        //update logo
        if (!logoPathProprty.isNull().get()) {
            FileInputStream fstream = new FileInputStream(logoPathProprty.get());
            System.out.println("about to insert image with byte size = "+fstream.available());
            PreparedStatement ps = PgConnector.connection.get().prepareStatement("""
                update base set logo_bytes=? where id=1
                """);


            ps.setBinaryStream(1, fstream,fstream.available());
           boolean imageInserted =  PgConnector.insertBinaryData(ps);

           if (imageInserted)logoPathProprty.set(null);
        }


        Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
        a.showAndWait();


    }

    /////////////////////////////////////////
    public void saveAddress() {
        boolean invalidUser = usernameP.get().length() < 3;
        boolean invalidpass= passP.get().length() < 6;

        if (invalidUser) {
            Label errusername = new Label(Translator.getIntl("min_len_user"));
            errusername.setStyle("-fx-text-fill: orange;-fx-font-weight: bold");
            ProjectUtils.showFloatingTooltip(errusername,thisStage.get(),usernamefield,0,0);
        }

        if (invalidpass) {
            Label errpass = new Label(Translator.getIntl("min_len_pass"));
            errpass.setStyle("-fx-text-fill: orange;-fx-font-weight: bold");
            ProjectUtils.showFloatingTooltip(errpass,thisStage.get(),passfield,0,0);
        }

        if (invalidpass | invalidUser) return;

        //update fields
        String update = String.format("""
                update base set
                region='%s',
                division='%s',
                lineone='%s',
                linetwo='%s',
                town_city='%s',
                email='%s',
                address='%s',
                pobox='%s',
                root_username='%s',
                root_password='%s'
                """, regionP.get(), divisionP.get(), lineoneP.get(), linetwoP.get(), townP.get(), emailP.get(), addressP.get(), poboxP.get(),usernameP.get(),passP.get());
        PgConnector.update(update);

        Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
        a.showAndWait();

    }


    //////////////////////////////////////

    public void saveAcademicYear() {
        String fromYear = yearFrom.getText();
        String toYEar = yearTo.getText();

        boolean validFrom = true;
        boolean validto = true;


        if (Objects.equals(fromYear, "") || fromYear == null) {
            Label errYearfroml = new Label(Translator.getIntl("invalid_year"));
            errYearfroml.setStyle("-fx-text-fill: orange;-fx-font-weight: bold");
            ProjectUtils.showFloatingTooltip(errYearfroml, thisStage.get(), yearFrom, -150, 0);
            validFrom = false;

        }

        if (Objects.equals(toYEar, "") || toYEar == null) {
            Label errYearfroml = new Label(Translator.getIntl("invalid_year"));
            errYearfroml.setStyle("-fx-text-fill: orange;-fx-font-weight: bold");
            ProjectUtils.showFloatingTooltip(errYearfroml, thisStage.get(), yearTo, -150, 0);
            validto = false;

        }

        if (!validFrom || !validto) return;

        List<DatePicker> nextDatePickers = List.of(datetNext, datet2, datet3);
        for (int i = 1; i <= 3; i++) {
            long termdate = nextDatePickers.get(i - 1).getValue().toEpochDay();
            String update = String.format("""
                    update terms set resume_date=%d where term=%d""", termdate, i);
            PgConnector.update(update);

        }

        //update acayear
        String year = fromYear + "/" + toYEar;
        String updateYear = String.format("update base set academic_year='%s'", year);
        PgConnector.update(updateYear);




        Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
        a.showAndWait();

    }




    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////



}
