package smpro.app.controllers;

import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.*;

public class AddStudentController implements Initializable {

    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();



    public ImageView appIcon;
    public HBox dragArea;
    public Button closedlg;
    public Button cancelBtn;
    public Button confirmBtn;
    public TextField fnamefield;
    public TextField fparentfiled;
    public TextField lparentfield;
    public TextField lnamefield;
    public DatePicker dobpicker;
    public ComboBox<String> sectioncombo;
    public RadioButton maleradio;
    public RadioButton femaleradio;
    public RadioButton nonbinradio;
    public HBox contact1container;
    public HBox contact2container;
    public TextField placefield;
    public CheckBox repeatercb;
    public TextField addressfield;
    public Button importBtn;
    public ImageView imageview;
    public ImageView imaggrauview;
    public VBox tradecontainer;
    public Button resetbtn;
    public HBox classContainer;
    public Label dobDisplay;
    public Button undoImage;


    //properties
    StringProperty fnameP = new SimpleStringProperty();
    StringProperty lnameP = new SimpleStringProperty();
    StringProperty placeP = new SimpleStringProperty();
    StringProperty tradeP = new SimpleStringProperty();
    StringProperty sectionP = new SimpleStringProperty();
    ObjectProperty<HashMap<String,Object>> classP = new SimpleObjectProperty<>();
    StringProperty fparentP = new SimpleStringProperty();
    StringProperty lParentP = new SimpleStringProperty();
    StringProperty addressP = new SimpleStringProperty();
    StringProperty fcontactP = new SimpleStringProperty();
    StringProperty lcontactP = new SimpleStringProperty();
    LongProperty dobP = new SimpleLongProperty();
    StringProperty genderP = new SimpleStringProperty("M");


    BooleanProperty repeaterP = new SimpleBooleanProperty(false);

    ObjectProperty<Image> imageP = new SimpleObjectProperty<>();
    StringProperty imagePathP = new SimpleStringProperty();



    //create custom fields
    CustomTextField fcontactfield = new CustomTextField();
    CustomTextField lcontactfield = new CustomTextField();

    SearchableComboBox<HashMap<String,Object>> classSearch = new SearchableComboBox<>();
    SearchableComboBox<String> tradeSearch = new SearchableComboBox<>();


    ToggleGroup genderGroup = new ToggleGroup();


    List<HashMap<String, Object>> classes = PgConnector.fetch("select * from classes order by level,classname", PgConnector.getConnection());
    List<HashMap<String, Object>> trades = PgConnector.fetch("select * from trades order by trade_name", PgConnector.getConnection());


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


        initUi();
        bindFields();
    }





    public void initUi() {
        genderGroup.getToggles().addAll(maleradio, femaleradio, nonbinradio);


        undoImage.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignU.UNDO, 20, Paint.valueOf("#dddddd")));
        undoImage.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("remove_image")));

        fcontactfield.setRight(ProjectUtils.createFontIcon(MaterialDesignP.PHONE, 12, Paint.valueOf("gray")));
        fcontactfield.setRight(ProjectUtils.createFontIcon(MaterialDesignP.PHONE, 12, Paint.valueOf("gray")));
        lcontactfield.setRight(ProjectUtils.createFontIcon(MaterialDesignP.PHONE, 12, Paint.valueOf("gray")));
        contact1container.getChildren().add(fcontactfield);
        contact2container.getChildren().add(lcontactfield);


        tradeSearch.getItems().addAll(PgConnector.listHashAttrs(trades, "trade_name"));
        tradecontainer.getChildren().add(tradeSearch);

//        classSearch.getItems().addAll(classes);
        classSearch.setMaxHeight(32);
        classSearch.setMinWidth(267);
        classSearch.setPlaceholder(new Label(Translator.getIntl("empty")));
        classSearch.setPromptText(Translator.getIntl("select"));

        classSearch.setCellFactory(new Callback<>() {
            @Override
            public ListCell<HashMap<String, Object>> call(ListView<HashMap<String, Object>> hashMapListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(HashMap<String, Object> o, boolean b) {
                        super.updateItem(o, b);
                        if (!b) {
                            String t = String.format("%s %s [ %s ] ", PgConnector.getFielorBlank(o, "class_abbreviation").toUpperCase(),
                                    Store.UnicodeSumnbol.blank, ProjectUtils.capitalize(PgConnector.getFielorBlank(o, "classname")));
                            setText(t);
                            setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_SMALL, 15, Paint.valueOf("gray")));

                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
            }
        });
        classSearch.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(HashMap<String, Object> o, boolean b) {
                super.updateItem(o, b);
                if (!b) {
                    String t = String.format("%s %s [ %s ] ", PgConnector.getFielorBlank(o, "class_abbreviation").toUpperCase(),
                            Store.UnicodeSumnbol.blank, ProjectUtils.capitalize(PgConnector.getFielorBlank(o, "classname")));
                    setText(t);
                    setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_SMALL, 15, Paint.valueOf("gray")));

                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
        classContainer.getChildren().add(classSearch);


        sectioncombo.getItems().addAll(PgConnector.listHashAttrs(PgConnector.
                fetch("select * from sections order by section_name", PgConnector.getConnection()), "section_name"));
        sectioncombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (!b) {
                            setText(ProjectUtils.capitalize(s));
                            setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_SMALL, 15, Paint.valueOf("gray")));
                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
            }
        });
        tradeSearch.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (!b) {
                            setText(ProjectUtils.capitalize(s));
                            setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_SMALL, 15, Paint.valueOf("gray")));
                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
            }
        });
        sectioncombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String s, boolean b) {
                super.updateItem(s, b);
                if (!b) {
                    setText(ProjectUtils.capitalize(s));
                    setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_SMALL, 15, Paint.valueOf("gray")));
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });

        tradeSearch.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String s, boolean b) {
                super.updateItem(s, b);
                if (!b) {
                    setText(ProjectUtils.capitalize(s));
                    setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_SMALL, 15, Paint.valueOf("gray")));
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });

        importBtn.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/44image.png", 50, 50)));

        classSearch.getStyleClass().add("alt-icon");
        tradeSearch.getStyleClass().add("alt-icon");
        sectioncombo.getStyleClass().add("alt-icon");


        for (CustomTextField t : new CustomTextField[]{fcontactfield, lcontactfield}) {
            t.textProperty().addListener((observableValue, s, t1) -> t.setText(t1.replaceAll("\\D", "")));
        }

        maleradio.setGraphic(ProjectUtils.createFontIcon(MaterialDesignG.GENDER_MALE, 15, Paint.valueOf("gray")));
        femaleradio.setGraphic(ProjectUtils.createFontIcon(MaterialDesignG.GENDER_FEMALE, 15, Paint.valueOf("gray")));
        nonbinradio.setGraphic(ProjectUtils.createFontIcon(MaterialDesignG.GENDER_MALE_FEMALE_VARIANT, 15, Paint.valueOf("gray")));
        resetbtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignB.BRUSH, 15, Paint.valueOf("gray")));



    }

    public void bindFields() {

        undoImage.setOnAction(e -> {
            imageP.set(null);
            imagePathP.set("");

        });

        fnameP.bind(fnamefield.textProperty());
        lnameP.bind(lnamefield.textProperty());
        tradeP.bind(tradeSearch.valueProperty());
        sectionP.bind(sectioncombo.valueProperty());

        fparentP.bind(fparentfiled.textProperty());
        lParentP.bind(lparentfield.textProperty());
        fcontactP.bind(fcontactfield.textProperty());
        lcontactP.bind(lcontactfield.textProperty());
        addressP.bind(addressfield.textProperty());
        placeP.bind(placefield.textProperty());

        repeaterP.bind(repeatercb.selectedProperty());

        classP.bind(classSearch.valueProperty());

        dobpicker.valueProperty().addListener((observableValue, localDate, newdate) -> {
            dobP.set(newdate.toEpochDay()*Store.EPOCK_DAY_MILLISECS);
            String formated = ProjectUtils.getFormatedDate(dobP.get(), DateFormat.getDateInstance(0, Translator.getLocale()));
            dobDisplay.setText(formated);


        });


        imaggrauview.imageProperty().bind(imageview.imageProperty());

        imageP.addListener((observableValue, image, newimg) -> {
            imageview.setImage(newimg);
//            if (!Objects.equals(newimg, null)) {
//                undoImage.setVisible(true);
//
//            } else {
//                undoImage.setVisible(false);
//
//            }
        });

        undoImage.visibleProperty().bind(imageP.isNotNull());


        sectioncombo.valueProperty().addListener((observableValue, s, newSection) -> {
            //get classes
            List<HashMap<String, Object>> foundClasses = classes.stream().filter(c->Objects.equals(PgConnector.getFielorBlank(c,"section"),newSection)).toList();
            classSearch.getItems().clear();
            classSearch.getItems().addAll(foundClasses);
        });


        //
        importBtn.setOnAction(e->{
          String filepath =   ResourceUtil.getStystemFilePath(thisStage.get(), List.of(
                    new FileChooser.ExtensionFilter("IMAGE FILE", "*.png", "*.jpg")
            ));
            imagePathP.set(filepath);


            FileInputStream fs;
            try {
                 fs = new FileInputStream(filepath);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            imageP.set(new Image(fs, imageview.getFitWidth(), imageview.getFitHeight(), true, true));
        });


        cancelBtn.setOnAction(e -> thisStage.get().close());
        confirmBtn.setOnAction(e -> {
            try {
                saveStudent();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        resetbtn.setOnAction(e -> resetFields());


    }


    public void resetFields() {
        fnamefield.clear();
        lnamefield.clear();
        fparentfiled.clear();
        lparentfield.clear();
        fcontactfield.clear();
        lcontactfield.clear();
        addressfield.clear();
        placefield.clear();

        imageP.set(null);
        imagePathP.set("");


    }

    public void prepareUpdate(HashMap<String, Object> studentData) throws SQLException {
        System.out.println(studentData);


        String fname = PgConnector.getFielorBlank(studentData, "firstname");
        String lname = PgConnector.getFielorBlank(studentData, "lastname");
        String gender = PgConnector.getFielorBlank(studentData, "gender");
        String address = PgConnector.getFielorBlank(studentData, "address");
        String fparent = PgConnector.getFielorBlank(studentData, "parent_one");
        String lparent = PgConnector.getFielorBlank(studentData, "parent_two");
        String fcontact = PgConnector.getFielorBlank(studentData, "contact_one");
        String lcontact = PgConnector.getFielorBlank(studentData, "contact_two");

//        String matricule = PgConnector.getFielorBlank(studentData, "matricule");
        String trade = PgConnector.getFielorBlank(studentData, "trade");
        boolean repeater = (boolean) studentData.get("repeater");

        String birthplace = PgConnector.getFielorBlank(studentData, "place_of_birth");
        String section = PgConnector.getFielorBlank(studentData, "section");
        long dob = PgConnector.getNumberOrNull(studentData, "date_of_birth").longValue();

        int classid = PgConnector.getNumberOrNull(studentData, "classid").intValue();
        int studentid = PgConnector.getNumberOrNull(studentData, "id").intValue();

        fnamefield.setText(fname);
        lnamefield.setText(lname);
        addressfield.setText(address);
        fparentfiled.setText(fparent);
        lparentfield.setText(lparent);
        fcontactfield.setText(fcontact);
        lcontactfield.setText(lcontact);
        placefield.setText(birthplace);

        sectioncombo.setValue(section);
        repeatercb.setSelected(repeater);
        tradeSearch.setValue(trade);

        if (gender.strip().equalsIgnoreCase("M")) {
            maleradio.setSelected(true);

        } else if (gender.strip().equalsIgnoreCase("F")) {
            femaleradio.setSelected(true);
        } else nonbinradio.setSelected(true);

        dobpicker.setValue(LocalDate.ofEpochDay(dob/Store.EPOCK_DAY_MILLISECS));

        // getclass
        HashMap<String, Object> classobj = PgConnector.getObjectFromId(classid, "classes");
        classSearch.setValue(classobj);


        //get image
        PreparedStatement imagePs = PgConnector.getConnection().prepareStatement("select image from students where id=?");
        imagePs.setInt(1, studentid);

        InputStream is = PgConnector.readBinarydata(imagePs);


        if (!(is == null)) {
            imageview.setImage(new Image(is, imageview.getFitWidth(), imageview.getFitHeight(), true, true));
//            imaggrauview.setImage(new Image(isgray, imaggrauview.getFitWidth(), imaggrauview.getFitHeight(), true, true));
        }





    }

    public boolean saveStudent() throws SQLException {

        // final binding
        List<RadioButton> genderRadios = List.of(femaleradio, maleradio, nonbinradio);
        for (RadioButton r : genderRadios) {
            if (r.isSelected()) {
                genderP.set(r.getId());
                break;
            }


        }

        boolean isvalid= true;

        //validate
        List<TextField> tfield = List.of(fnamefield, lnamefield, addressfield, placefield);
        for (TextField f : tfield) {
            if (f.getText().isEmpty()) {
                Label content = new Label(Translator.getIntl("required"));
                content.getStyleClass().addAll("text", "danger","text-bold");

                FontIcon warning = ProjectUtils.createFontIcon(MaterialDesignA.ALERT_CIRCLE, 10, Paint.valueOf(Store.Colors.red));
                warning.setStrokeWidth(1);
                content.setGraphic(warning);

                PopOver p = ProjectUtils.showPopover("", content, PopOver.ArrowLocation.LEFT_CENTER, false, true);
                p.show(f);
                isvalid=false;
            }
        }

        List<ComboBox<?>> combos = List.of(sectioncombo, classSearch, tradeSearch);
        for (ComboBox<?> c : combos) {

            if (c.getValue() == null) {
                Label content = new Label(Translator.getIntl("required"));
                content.getStyleClass().addAll("text", "danger","text-bold");

                FontIcon warning = ProjectUtils.createFontIcon(MaterialDesignA.ALERT_CIRCLE, 10, Paint.valueOf(Store.Colors.red));
                warning.setStrokeWidth(1);
                content.setGraphic(warning);
                ProjectUtils.showPopover("", content, PopOver.ArrowLocation.LEFT_CENTER, false, true).show(c);
                isvalid=false;


            }
        }

        if (dobpicker.getValue() == null) {
            Label content = new Label(Translator.getIntl("required"));
            content.getStyleClass().addAll("text", "danger","text-bold");
            FontIcon warning = ProjectUtils.createFontIcon(MaterialDesignA.ALERT_CIRCLE, 10, Paint.valueOf(Store.Colors.red));
            warning.setStrokeWidth(1);
            content.setGraphic(warning);
            ProjectUtils.showPopover("", content, PopOver.ArrowLocation.LEFT_CENTER, false, true).show(dobpicker);
            isvalid=false;


        }

        if (!isvalid)return false;

        //save valide form
        String q = """
                insert into students\s
                (
                classid,section,trade,
                firstname,lastname,
                place_of_birth,address,
                
                admission_date,date_of_birth,
                
                parent_one,contact_one,
                parent_two,contact_two,
                
                gender,repeater,
                matricule,image
                                
                )
                
                values  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                                
                """;






        try (PreparedStatement insert = PgConnector.getConnection().prepareStatement(q)) {

            insert.setInt(1, PgConnector.getNumberOrNull(classP.get(), "id").intValue());
            insert.setString(2, sectionP.get());
            insert.setString(3, tradeP.get());

            insert.setString(4, fnameP.get());
            insert.setString(5, lnameP.get());
            insert.setString(6, placeP.get());
            insert.setString(7, addressP.get());

            insert.setLong(8, new Date().getTime());
            insert.setLong(9, dobP.get());

            insert.setString(10, fparentP.get());
            insert.setString(11, fcontactP.get());
            insert.setString(12, lParentP.get());
            insert.setString(13, lcontactP.get());

            insert.setString(14, genderP.get());
            insert.setBoolean(15, repeaterP.get());
            insert.setString(16, genMatricule(fnameP.get(),lnameP.get()));

            if (!Objects.equals(null, imageP.get())) {
                FileInputStream is = new FileInputStream(imagePathP.get());

                insert.setBinaryStream(17, is, is.available());


            } else {
                insert.setBinaryStream(17, null, 0);


            }

            // insert
            System.out.println(insert);

            insert.executeUpdate();



        } catch (Exception err) {
            err.printStackTrace();
        }



        Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
                Translator.getIntl("data_updated"), ButtonType.OK);
        a.showAndWait();
        resetFields();
        return false;
    }
    public boolean updateStudent(int studentid,Callback<Void,Void> cback) throws SQLException {

        // final binding
        List<RadioButton> genderRadios = List.of(femaleradio, maleradio, nonbinradio);
        for (RadioButton r : genderRadios) {
            if (r.isSelected()) {
                genderP.set(r.getId());
                break;
            }


        }

        boolean isvalid= true;

        //validate
        List<TextField> tfield = List.of(fnamefield, lnamefield, addressfield, placefield);
        for (TextField f : tfield) {
            if (f.getText().isEmpty()) {
                Label content = new Label(Translator.getIntl("required"));
                content.getStyleClass().addAll("text", "danger","text-bold");

                FontIcon warning = ProjectUtils.createFontIcon(MaterialDesignA.ALERT_CIRCLE, 10, Paint.valueOf(Store.Colors.red));
                warning.setStrokeWidth(1);
                content.setGraphic(warning);

                PopOver p = ProjectUtils.showPopover("", content, PopOver.ArrowLocation.LEFT_CENTER, false, true);
                p.show(f);
                isvalid=false;
            }
        }

        List<ComboBox<?>> combos = List.of(sectioncombo, classSearch, tradeSearch);
        for (ComboBox<?> c : combos) {

            if (c.getValue() == null) {
                Label content = new Label(Translator.getIntl("required"));
                content.getStyleClass().addAll("text", "danger","text-bold");

                FontIcon warning = ProjectUtils.createFontIcon(MaterialDesignA.ALERT_CIRCLE, 10, Paint.valueOf(Store.Colors.red));
                warning.setStrokeWidth(1);
                content.setGraphic(warning);
                ProjectUtils.showPopover("", content, PopOver.ArrowLocation.LEFT_CENTER, false, true).show(c);
                isvalid=false;


            }
        }

        if (dobpicker.getValue() == null) {
            Label content = new Label(Translator.getIntl("required"));
            content.getStyleClass().addAll("text", "danger","text-bold");
            FontIcon warning = ProjectUtils.createFontIcon(MaterialDesignA.ALERT_CIRCLE, 10, Paint.valueOf(Store.Colors.red));
            warning.setStrokeWidth(1);
            content.setGraphic(warning);
            ProjectUtils.showPopover("", content, PopOver.ArrowLocation.LEFT_CENTER, false, true).show(dobpicker);
            isvalid=false;


        }

        if (!isvalid)return false;

        //update valide form
        String q = """
                update students\s
                
                set classid=?, section=?,trade=?,firstname=?,lastname=?,place_of_birth=?,address=?,
                admission_date=?,date_of_birth=?,parent_one=?,contact_one=?,
                parent_two=?,contact_two=?,gender=?,repeater=?
                
                where id=?
                
                """;






        try (PreparedStatement insert = PgConnector.getConnection().prepareStatement(q)) {

            insert.setInt(1, PgConnector.getNumberOrNull(classP.get(), "id").intValue());
            insert.setString(2, sectionP.get());
            insert.setString(3, tradeP.get());

            insert.setString(4, fnameP.get());
            insert.setString(5, lnameP.get());
            insert.setString(6, placeP.get());
            insert.setString(7, addressP.get());

            insert.setLong(8, new Date().getTime());
            insert.setLong(9, dobP.get());

            insert.setString(10, fparentP.get());
            insert.setString(11, fcontactP.get());
            insert.setString(12, lParentP.get());
            insert.setString(13, lcontactP.get());

            insert.setString(14, genderP.get());
            insert.setBoolean(15, repeaterP.get());
//            insert.setString(16, genMatricule(fnameP.get(),lnameP.get()));



            insert.setInt(16, studentid);
            insert.executeUpdate();

            if (!Objects.equals(null, imageP.get())) {
                FileInputStream is = new FileInputStream(imagePathP.get());

                PreparedStatement imgps = PgConnector.getConnection().prepareStatement("update students set image=? where id=?");
                imgps.setBinaryStream(1, is, is.available());
                imgps.setInt(2, studentid);

                imgps.executeUpdate();

            }




        } catch (Exception err) {
            err.printStackTrace();
        }



        Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
                Translator.getIntl("data_updated"), ButtonType.OK);
        a.showAndWait();
        cback.call(null);
        return true;
    }


    public static String genMatricule(String fname, String lname) {
        int charsize = 8;
        String chars = "1AB2C3D4E5FGH8IJKLM4N9OPQ2RST6";

        StringBuilder builder = new StringBuilder();

        try {
            builder.append(fname.charAt(0));
            builder.append(lname.charAt(0));

            for (int i = 0; i < charsize-2; i++) {
                int randow = (int) (Math.random() * charsize);
                builder.append(chars.charAt(randow));

            }

        } catch (Exception e) {
            e.printStackTrace();

            for (int i = 0; i < charsize; i++) {
                int randow = (int) (Math.random() * charsize);
                builder.append(chars.charAt(randow));

            }


        }

        //check exist
        List<?> found = PgConnector.fetch(String.format("select * from students where matricule='%s'", builder.toString().toUpperCase()), PgConnector.getConnection());
        if (!found.isEmpty()) {
          return  genMatricule(fname, lname);
        }

        return builder.toString().toUpperCase();

    }







}
