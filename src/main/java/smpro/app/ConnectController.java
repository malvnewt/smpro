package smpro.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ConnectController implements Initializable {
    public ImageView sideImage;
    public Label titlel;
    public ComboBox<HashMap<String, Object>> yearcombo;
    public ComboBox<Locale> langcombo;
    public Label usernamel;
    public Label passl;
    public TextField usernamefield;
    public TextField passfi8eld;
    public Label createnewl;
    public Button newProjectbtn;
    public Button cancelbtn;
    public Button confirmBtn;


    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public ListProperty<HashMap<String, Object>> dbs = new SimpleListProperty<>();
    public Label authorLabel;
    public Label phoneAdvert;
    public Label whatsappAdvert;

    StringProperty usernamelProperty = new SimpleStringProperty(Translator.getIntl("login_username"));
    StringProperty passlProperty = new SimpleStringProperty(Translator.getIntl("login_pass"));
    StringProperty newprojectProperty = new SimpleStringProperty(Translator.getIntl("login_newproject"));
    StringProperty titleProperty = new SimpleStringProperty(Translator.getIntl("login_title"));

    StringProperty usernameProperty = new SimpleStringProperty("");
    StringProperty passProperty = new SimpleStringProperty("");

    Label usernamErrl = new Label(Translator.getIntl("invalid_username"));
    Label passerrl = new Label(Translator.getIntl("invalid_pass"));

    int vAdjustment= -35;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindFields();
        initUi();
        initActions();



    }

    public void bindFields() {
        usernameProperty.bind(usernamefield.textProperty());
        passProperty.bind(passfi8eld.textProperty());
        createnewl.textProperty().bind(newprojectProperty);
        titlel.textProperty().bind(titleProperty);

        usernamel.textProperty().bind(usernamelProperty);
        passl.textProperty().bind(passlProperty);


        langcombo.getItems().addAll(Translator.supportedLocales);
        langcombo.valueProperty().addListener((observableValue, locale, newloc) -> {
            Translator.localeProperty.set(newloc);
            if (newloc.equals(Locale.ENGLISH)) {
                Translator.localeAltProperty.set(Locale.FRENCH);
            } else Translator.localeAltProperty.set(Locale.ENGLISH);

        });

        langcombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Locale> call(ListView<Locale> localeListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Locale locale, boolean empty) {
                        super.updateItem(locale, empty);

                        if (!empty) {
                            setText(locale.getDisplayName().toUpperCase());
                            String localeImg = String.format("images/lang/%s.png", locale.toLanguageTag());
                            System.out.println(localeImg);

                            ImageView g = new ImageView(ResourceUtil.getImageFromResource(localeImg, 18, 18, true));
                            setGraphic(g);

                        }

                    }
                };
            }
        });

        langcombo.setButtonCell( new ListCell<>() {
            @Override
            protected void updateItem(Locale locale, boolean empty) {
                super.updateItem(locale, empty);

                if (!empty) {
                    setText(locale.getDisplayName().toUpperCase());
                    String localeImg = String.format("images/lang/%s.png", locale.toLanguageTag());
                    System.out.println(localeImg);

                    ImageView g = new ImageView(ResourceUtil.getImageFromResource(localeImg, 18, 18, true));
                    setGraphic(g);

                }

            }
        });

        langcombo.setValue(Translator.getLocale());


        yearcombo.itemsProperty().bind(dbs);

        yearcombo.valueProperty().addListener((observableValue, stringObjectHashMap, project) -> {
            Store.currentProjectProperty.set(project);

            String name = String.valueOf(project.get("name"));

            try {
                PgConnector.switchDbConnection(name);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        yearcombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<HashMap<String, Object>> call(ListView<HashMap<String, Object>> hashMapListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(HashMap<String, Object> item, boolean b) {
                        super.updateItem(item, b);
                        if (!b) {
                            String label = String.format("%s %s/%s", item.get("name"), item.get("start"), item.get("end"));
                            setText(label.toUpperCase());

                            setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE, 15, Paint.valueOf(Store.Colors.black)));
                        }

                    }
                };
            }

        });

        yearcombo.setButtonCell( new ListCell<>(){
            @Override
            protected void updateItem(HashMap<String, Object> item, boolean b) {
                super.updateItem(item, b);
                if (!b) {
                    String label = String.format("%s %s/%s", item.get("name"), item.get("start"), item.get("end"));
                    setText(label.toUpperCase());

                    setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE, 15, Paint.valueOf(Store.Colors.black)));
                }

            }
        });

        dbs.addListener((observableValue, hashMaps, newvalue) -> {
            if (!newvalue.isEmpty()) {
                confirmBtn.setDisable(false);
                yearcombo.setValue(newvalue.get(newvalue.size() - 1));
            } else {
                yearcombo.setPlaceholder(new Label(Translator.getIntl("no_database")));
                confirmBtn.setDisable(true);
            }
        });


    }

    public void initUi() {
        authorLabel.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CODE_TAGS, 10, Paint.valueOf("#242424")));
        phoneAdvert.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PHONE_INCOMING, 10, Paint.valueOf("#242424")));
        whatsappAdvert.setGraphic(ProjectUtils.createFontIcon(MaterialDesignW.WHATSAPP, 10, Paint.valueOf("#242424")));


        newProjectbtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS, 50, Paint.valueOf("#242424")));
        newProjectbtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("create_newproject")));

        cancelbtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EXIT_TO_APP, 18, Paint.valueOf(Store.Colors.lightestGray)));

        usernamel.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT, 12, Paint.valueOf(Store.Colors.black)));
        passl.setGraphic(ProjectUtils.createFontIcon(MaterialDesignK.KEY, 12, Paint.valueOf(Store.Colors.black)));

        sideImage.setImage(ResourceUtil.getImageFromResource("images/welcomefinal.jpg", (int) sideImage.getFitWidth(), (int)
                sideImage.getFitHeight(), false));




        usernamErrl.setStyle("-fx-text-fill: orange");
        passerrl.setStyle("-fx-text-fill: orange");

        cancelbtn.setTooltip(new Tooltip(Translator.getIntl("close_app")));

    }

    public void initActions() {
        cancelbtn.setOnAction(e->{
            thisStage.get().close();
            Platform.exit();
        });


        confirmBtn.setOnAction(e->{

            HashMap<String, Object> baseData = PgConnector.fetch("select * from base", PgConnector.connection.get()).get(0);

            String username = String.valueOf(baseData.get("root_username"));
            String password = String.valueOf(baseData.get("root_password"));

            //validation
            boolean isvalidUsername = username.equals(usernameProperty.get());
            boolean isvalidpass = password.equals(passProperty.get());


            if (!isvalidpass)
                ProjectUtils.showFloatingTooltip(passerrl, thisStage.get(), passfi8eld, 0, vAdjustment);


            if (!isvalidUsername)
                ProjectUtils.showFloatingTooltip(usernamErrl, thisStage.get(), usernamefield, 0, vAdjustment);


            if (isvalidpass && isvalidUsername) {
                thisStage.get().close();

            }

        });
    }



    public void createProject() {
    }





}
