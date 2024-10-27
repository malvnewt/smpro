package smpro.app.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.ImageCursor;
import javafx.scene.control.*;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class AddEmpController implements Initializable {
    public ImageView appIcon;
    public HBox dragArea;
    public Button closedlg;
    public TextField usernamef;
    public TextField address;
    public ComboBox<String> categorycombo;
    public TextField passf;
    public ComboBox<String> timefactorcombo;
    public TextField contact;
    public Label genderDispla;
    public HBox dptContainer;
    public ImageView displayview;
    public Button importImagbtn;
    public Button cancelBtn;
    public Button confirmBtn;
    public RadioButton male;
    public RadioButton female;
    public RadioButton nonbin;
    public Label contactl;
    public Label passl;
    public TextField namefield;
    public Label timel;
    public ComboBox<String> pronounscombo;
    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public ObjectProperty<Stage> mainstage = new SimpleObjectProperty<>();
    public Label empTitle;
    public Label usernamelabel;
    StringProperty imagePathP = new SimpleStringProperty("");


    //properties
    StringProperty genderP = new SimpleStringProperty();
    StringProperty nameP = new SimpleStringProperty();
    StringProperty categoryP = new SimpleStringProperty();
    StringProperty departmentP = new SimpleStringProperty();
    StringProperty timefP = new SimpleStringProperty();
    StringProperty addressP = new SimpleStringProperty();
    StringProperty contactP = new SimpleStringProperty();
    StringProperty usernameP = new SimpleStringProperty();
    StringProperty passP = new SimpleStringProperty();

    ObjectProperty<Image> imageP = new SimpleObjectProperty<>();

    SearchableComboBox<HashMap<String,Object>> departmentCombo = new SearchableComboBox<>();
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
        departmentCombo.setMaxHeight(32);

        initUi();
        bindFields();

    }

    public void initUi() {
        dptContainer.getChildren().add(0,departmentCombo);
        HBox.setHgrow(departmentCombo, Priority.ALWAYS);
        departmentCombo.setPromptText("...");


        List<HashMap<String,Object>> trades = PgConnector.fetch("select * from trades order by trade_name", PgConnector.getConnection())
                .stream().map(item -> {
                    Object name = PgConnector.getFielorBlank(item, "trade_name");
                    Object abbr = PgConnector.getFielorBlank(item, "trade_abbreviation");

                    return new HashMap<>(Map.of("subject_name", name,
                            "subject_abbreviation",abbr));
                }).toList();
        departmentCombo.getItems().addAll(trades);
        List<String> tradeNames = PgConnector.listHashAttrs(trades, "subject_names");


        departmentCombo.getItems().addAll(PgConnector.fetch("select * from subjects order by subject_name", PgConnector.getConnection()));
        departmentCombo.getStyleClass().addAll("dense");






        departmentCombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<HashMap<String, Object>> call(ListView<HashMap<String, Object>> hashMapListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(HashMap<String, Object> stringObjectHashMap, boolean b) {
                        super.updateItem(stringObjectHashMap, b);
                        if (!b) {
                            setText(ProjectUtils.capitalize(PgConnector.getFielorBlank(stringObjectHashMap, "subject_name")));
                            setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CIRCLE_MULTIPLE, 15, Paint.valueOf("lightgray")));
                        } else {
                            setText(null);
                            setGraphic(null);

                        }

                    }
                };
            }
        });

        departmentCombo.setButtonCell( new ListCell<>() {
            @Override
            protected void updateItem(HashMap<String, Object> stringObjectHashMap, boolean b) {
                super.updateItem(stringObjectHashMap, b);
                if (!b) {
                    String itemName = PgConnector.getFielorBlank(stringObjectHashMap, "subject_name");
                    setText(String.format("%s (%s)",itemName.toUpperCase(),
                           PgConnector.getFielorBlank(stringObjectHashMap, "subject_abbreviation")));
                    setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CIRCLE_MULTIPLE, 15, Paint.valueOf("lightgray")));

                    String foundIntrades = tradeNames.stream().filter(s->s.equalsIgnoreCase(itemName)).findAny().orElse(null);
                    if (!Objects.equals(null,foundIntrades))
                        setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CIRCLE_MULTIPLE, 15, Paint.valueOf(Store.Colors.green)));


                } else {
                    setText(null);
                    setGraphic(null);

                }

            }
        });

        passl.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignK.KEY, 15, Paint.valueOf("lightgray")));
        usernamelabel.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ACCOUNT, 15, Paint.valueOf("lightgray")));



        contactl.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PHONE, 15, Paint.valueOf("lightgray")));
        timel.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CLOCK_ALERT, 15, Paint.valueOf("lightgray")));

        contact.textProperty().addListener((observableValue, s, t1) -> contact.setText(t1.replaceAll("\\D", "")));
        usernamef.textProperty().addListener((observableValue, s, t1) -> usernamef.setText(t1.replaceAll(" ", "")));
        passf.textProperty().addListener((observableValue, s, t1) -> passf.setText(t1.replaceAll(" ", "")));

        importImagbtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignI.IMPORT, 15, Paint.valueOf("lightgray")));
        displayview.imageProperty().bind(imageP);

        imageP.set(ResourceUtil.getImageFromResource("images/44image.png", (int) displayview.getFitWidth(), (int) displayview.getFitHeight(), true));

        //prefixes
        pronounscombo.getItems().addAll(Store.Pronouns);
        categorycombo.getItems().addAll(Store.employeeCats);
        timefactorcombo.getItems().addAll(Store.empTime);

        for (ComboBox c : new ComboBox[]{categorycombo, timefactorcombo}) {

            c.setButtonCell( new ListCell<>() {
                @Override
                protected void updateItem(Object o, boolean b) {
                    super.updateItem(o, b);
                    if (!b) {
                        setText(Translator.getIntl(String.valueOf(o)).toUpperCase());
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            });

            c.setCellFactory(new Callback<ListView, ListCell>() {
                @Override
                public ListCell call(ListView listView) {
                    return   new ListCell<>() {
                        @Override
                        protected void updateItem(Object o, boolean b) {
                            super.updateItem(o, b);
                            if (!b) {
                                setText(Translator.getIntl(String.valueOf(o)).toUpperCase());
                            } else {
                                setText(null);
                                setGraphic(null);
                            }

                        }
                    };
                }
            });



        }


        pronounscombo.getSelectionModel().select(0);




    }

    public void bindFields() {
        male.setTooltip(new Tooltip(Translator.getIntl("male")));
        female.setTooltip(new Tooltip(Translator.getIntl("female")));
        nonbin.setTooltip(new Tooltip(Translator.getIntl("non_binary")));
        male.setId(Translator.getIntl("male"));
        female.setId(Translator.getIntl("female"));
        nonbin.setId(Translator.getIntl("non_binary"));



        List<RadioButton> genders = List.of(male, female, nonbin);
        for (RadioButton r : genders) {
            r.selectedProperty().addListener((observableValue, aBoolean, selected) -> {
                if (selected) {
                    List<RadioButton> others = genders.stream().filter(rb -> !Objects.equals(rb, r)).toList();
                    others.forEach(b -> b.setSelected(false));
                    genderP.set(r.getText());
                    genderDispla.setText(r.getId().toUpperCase());

                }
            });

        }

        male.setSelected(true);

        usernameP.bind(usernamef.textProperty());
        passP.bind(passf.textProperty());
        addressP.bind(address.textProperty());
        categoryP.bind(categorycombo.valueProperty());
        timefP.bind(timefactorcombo.valueProperty());
        contactP.bind(contact.textProperty());
        nameP.bind(namefield.textProperty());

        departmentCombo.valueProperty().addListener((observableValue, stringObjectHashMap, t1) -> {
            if (!Objects.equals(null, t1)) {
                departmentP.set(PgConnector.getFielorBlank(t1, "subject_name"));
            }
        } );


        importImagbtn.setOnAction(e->{
            String filepath  = ResourceUtil.getStystemFilePath(thisStage.get(),
                    List.of(new FileChooser.ExtensionFilter("IMAGE FILE", "*.png", "*.jpg", "*.svg", "*jpeg")));
            try {
                Image img = new Image(new FileInputStream(filepath), displayview.getFitWidth(), displayview.getFitHeight(), true, true);
                imageP.set(img);
                imagePathP.set(filepath);

            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        });

    }

    public void prepareUpdate(HashMap<String, Object> empData) throws SQLException {
//        importImagbtn.setDisable(true);
//        displayview.setEffect(new SepiaTone());
//        importImagbtn.setCursor(new ImageCursor(new Image(ResourceUtil.getResourceAsStream("images/not_allowed.png"), 50, 50,true,true)));
//        importImagbtn.setOnAction(e -> System.out.println("no image change"));

        empTitle.setText(Translator.getIntl("update_emp").toUpperCase());
        String fullname = PgConnector.getFielorBlank(empData, "first_lastname");
        String username = PgConnector.getFielorBlank(empData, "username");
        String addressT = PgConnector.getFielorBlank(empData, "address");
        String department = PgConnector.getFielorBlank(empData, "department");
        String timefactor = PgConnector.getFielorBlank(empData, "time_factor");
        String password = PgConnector.getFielorBlank(empData, "password");
        String category = PgConnector.getFielorBlank(empData, "employee_category");
        String gender = PgConnector.getFielorBlank(empData, "gender");
        String contactT = PgConnector.getFielorBlank(empData, "contact");

        String pronoun = fullname.substring(0, fullname.indexOf(" ")).strip();
        String names = fullname.substring( fullname.indexOf(" ")).strip();

        HashMap<String, Object> subjectObj = PgConnector.getObjectFromKey("subject_name", department.toLowerCase(), "subjects");

        pronounscombo.setValue(pronoun);
        namefield.setText(names);
        usernamef.setText(username);
        address.setText(addressT);
        departmentCombo.setValue(subjectObj);
        timefactorcombo.setValue(timefactor);
        passf.setText(password);
        categorycombo.setValue(category);
        contact.setText(contactT);

        if (gender.equalsIgnoreCase("m")) {
            male.setSelected(true);

        } else if (gender.equalsIgnoreCase("f")) {
            female.setSelected(true);

        } else {
            nonbin.setSelected(true);

        }

        // display current image
        PreparedStatement readImgStatement = PgConnector.getConnection().prepareStatement("select display_image from employees where id =?");
        readImgStatement.setInt(1, PgConnector.getNumberOrNull(empData, "id").intValue());

        InputStream is = PgConnector.readBinarydata(readImgStatement);

        imageP.set(Objects.equals(null,is) ?
                new Image(ResourceUtil.getResourceAsStream("images/44image.png")):new Image(is, displayview.getFitWidth(), displayview.getFitHeight(), true,true));




    }

    public void save(HashMap<String,Object>... updatedata) throws SQLException, IOException {
        // disable image change

        // disabled


        String name = nameP.get();
        String gender = genderP.get();
        String userame = usernameP.get();
        String passw = passP.get();
        String cat = categoryP.get();
        String timef = timefP.get();
        String department = departmentP.get();
        String addr = addressP.get();
        String contact_ = contactP.get();

        List<TextField> tfs = List.of(namefield, usernamef, passf, address, contact);
        List<ComboBox> combos = List.of(categorycombo,departmentCombo,timefactorcombo);

        for (TextField t : tfs) {
            if (t.getText().isEmpty() || t.getText() == null) {
                ProjectUtils.showPopover("", ProjectUtils.createErrLabel(Translator.getIntl("required")),
                        PopOver.ArrowLocation.LEFT_CENTER, false, true).show(t);
            }

        }
        for (ComboBox t : combos) {
            if (t.getValue() == null) {
                ProjectUtils.showPopover("", ProjectUtils.createErrLabel(Translator.getIntl("required")),
                        PopOver.ArrowLocation.LEFT_CENTER, false, true).show(t);
            }

        }

        //validate inputs
        if (name.isEmpty() || gender.isEmpty() || userame.isEmpty() ||
                passw.isEmpty() || cat.isEmpty() || timef.isEmpty() || department.isEmpty() || addr.isEmpty() || contact_.isEmpty()) return;


        String insertNew = "insert into employees (first_lastname,employee_category,department, time_factor,date_added,gender,username,password,address,contact)" +
                "values (?,?,?,?,?,?,?,?,?,?)";

        String update = "update employees set first_lastname=?,employee_category=?,department=?,time_factor=?,date_added=?,gender=?,username=?," +
                "password=?,address=?,contact=?  where id=? ";

        String insert = updatedata.length>0 ? update : insertNew;


        PreparedStatement ps = PgConnector.getConnection().prepareStatement(insert);

        ps.setString(1, String.format("%s %s", pronounscombo.getValue(), name).toLowerCase());
        ps.setString(2, cat);
        ps.setString(3, department);

        ps.setString(4, timef);
        ps.setLong(5, new Date().getTime());
        ps.setString(6, gender);
        ps.setString(7, userame);
        ps.setString(8, passw);
        ps.setString(9, addr);
        ps.setString(10, contact_);

        if (updatedata.length > 0) {
            ps.setInt(11,  PgConnector.getNumberOrNull(updatedata[0], "id").intValue());
        }
        ps.executeUpdate();

        //update with image
        if (updatedata.length == 0) {
            InputStream is = imagePathP.get().isEmpty() ? null : new FileInputStream(imagePathP.get());
            PreparedStatement updateimg = PgConnector.getConnection().prepareStatement("update employees set display_image=? where first_lastname=? ");

            updateimg.setBinaryStream(1, is, is==null ? 0: is.available());
            updateimg.setString(2,String.format("%s %s",pronounscombo.getValue(),name));
            updateimg.executeUpdate();
        } else {
            if (!imagePathP.get().isEmpty()) {

            InputStream is =  new FileInputStream(imagePathP.get());
            PreparedStatement updateimg = PgConnector.getConnection().prepareStatement("update employees set display_image=? where id=? ");

            updateimg.setBinaryStream(1, is, is==null ? 0: is.available());
            updateimg.setInt(2, PgConnector.getNumberOrNull(updatedata[0], "id").intValue());
            updateimg.executeUpdate();
            }


        }

        System.out.println("image upated");
        thisStage.get().close();


        Alert a = ProjectUtils.showAlert(mainstage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
        a.showAndWait();






    }



}
