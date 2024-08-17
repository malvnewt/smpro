package smpro.app;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.materialdesign2.*;
import org.postgresql.jdbc.PgArray;
import org.w3c.dom.events.Event;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void changeTab(int inex) {
        System.out.println("Changing settings tab");
        settingsTabpane.getSelectionModel().select(inex);
    }


    public void bindFields() {

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




}
