package smpro.app;

import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.ListSelectionView;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.custom_nodes.MyPasswordField;
import smpro.app.utils.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class ConnectController implements Initializable {
    public ImageView sideImage;
    public Label titlel;
    public ComboBox<HashMap<String, Object>> yearcombo;
    public ComboBox<Locale> langcombo;
    public Label usernamel;
    public Label passl;
    public TextField usernamefield;
//    public TextField passfi8eld;
    public Label createnewl;
    public Button newProjectbtn;
    public Button cancelbtn;
    public Button confirmBtn;

    public MyPasswordField passfi8eld;


    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public ListProperty<HashMap<String, Object>> dbs = new SimpleListProperty<>();
    public Label authorLabel;
    public Label phoneAdvert;
    public Label whatsappAdvert;
    public GridPane loginGrid;
    public Label passHint;

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


        passfi8eld = new MyPasswordField(passHint);


        loginGrid.add(passfi8eld, 1, 1);
        passfi8eld.setPromptText("min char 6+");
        passfi8eld.setMinHeight(35);

        passfi8eld.addEventHandler(MouseEvent.MOUSE_RELEASED,passfi8eld::handleMouseReleased);
        passfi8eld.addEventHandler(MouseEvent.MOUSE_PRESSED,passfi8eld::handleMousePressed);

        passHint.setGraphic(ProjectUtils.createFontIcon(MaterialDesignL.LIGHTBULB, 8, Paint.valueOf("gray")));
        passHint.setStyle("-fx-text-fill: gray;-fx-border-width: 0;-fx-background-color: transparent;-fx-font-size: 13.5px");
        passHint.setVisible(false);
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

        for (TextField tf : new TextField[]{usernamefield, passfi8eld}) {
            System.out.println("removing error class");
            tf.textProperty().addListener((observableValue, eventHandler, t1) -> tf.getStyleClass().remove("error-textfield"));
        }


        newProjectbtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS, 50, Paint.valueOf("#242424")));
        newProjectbtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("create_newproject")));

        cancelbtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EXIT_TO_APP, 18, Paint.valueOf(Store.Colors.lightestGray)));

        usernamel.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT, 12, Paint.valueOf(Store.Colors.black)));
        passl.setGraphic(ProjectUtils.createFontIcon(MaterialDesignK.KEY, 12, Paint.valueOf(Store.Colors.black)));

        sideImage.setImage(ResourceUtil.getImageFromResource("images/welcomefinal.jpg", (int) sideImage.getFitWidth(), (int)
                sideImage.getFitHeight(), false));





        for (Label l : new Label[]{usernamErrl, passerrl}) {
            FontIcon warning = ProjectUtils.createFontIcon(MaterialDesignA.ALERT_CIRCLE, 10, Paint.valueOf(Store.Colors.red));
            l.setGraphic(warning);
            warning.setStrokeWidth(1);
            l.getStyleClass().addAll("text", "warning","text-bold");

        }

        cancelbtn.setTooltip(new Tooltip(Translator.getIntl("close_app")));


        usernamefield.requestFocus();

    }

    public void initActions() {
        cancelbtn.setOnAction(e->{
            thisStage.get().close();
            System.exit(0);
//            Platform.exit();
        });


        confirmBtn.setOnAction(e->{

            HashMap<String, Object> baseData = PgConnector.fetch("select * from base", PgConnector.connection.get()).get(0);

            String username = String.valueOf(baseData.get("root_username"));
            String password = String.valueOf(baseData.get("root_password"));

            //validation
            boolean isvalidUsername = username.equals(usernameProperty.get());
            boolean isvalidpass = password.equals(passProperty.get());


            if (!isvalidpass){
//                ProjectUtils.showFloatingTooltip(passerrl, thisStage.get(), passfi8eld, 0, vAdjustment);

                passfi8eld.getStyleClass().add("error-textfield");
                PopOver passwordErrpop = ProjectUtils.showPopover("", passerrl, PopOver.ArrowLocation.LEFT_CENTER, false,true);
                passwordErrpop.show(passfi8eld);

                Timeline timeline =  ProjectUtils.shakeX(passfi8eld, -10, 6);
                timeline.play();
            }


            if (!isvalidUsername){
                usernamefield.getStyleClass().add("error-textfield");

                PopOver usernameErrPop = ProjectUtils.showPopover("", usernamErrl, PopOver.ArrowLocation.BOTTOM_LEFT, false,true);
                usernameErrPop.show(usernamefield);

//                ProjectUtils.showFloatingTooltip(usernamErrl, thisStage.get(), usernamefield, 0, vAdjustment);
               Timeline timeline =  ProjectUtils.shakeX(usernamefield, -10, 6);
               timeline.play();

            }


            if (isvalidpass && isvalidUsername) {
                usernamefield.getStyleClass().remove("error-textfield");
                passfi8eld.getStyleClass().remove("error-textfield");

                Store.SessionStage.set(thisStage.get());

                HashMap<String, Object> authuser = new HashMap<>(Map.of(
                        "username", username,
                        "password", password
                ));
                Store.AuthUser.set(authuser);

                usernamefield.clear();
                passfi8eld.clear();


                thisStage.get().hide();

            } else {

            }

        });
    }



    public void createProject() {
    }





}
