package smpro.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.net.URL;
import java.util.*;

public class ConnectController implements Initializable {
    public ImageView sideImage;
    public Label titlel;
    public ComboBox<HashMap<String,Object>> yearcombo;
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


    StringProperty usernamelProperty = new SimpleStringProperty(Translator.getIntl("login_username"));
    StringProperty passlProperty = new SimpleStringProperty(Translator.getIntl("login_pass"));
    StringProperty newprojectProperty = new SimpleStringProperty(Translator.getIntl("login_newproject"));
    StringProperty titleProperty = new SimpleStringProperty(Translator.getIntl("login_title"));

    StringProperty usernameProperty = new SimpleStringProperty("");
    StringProperty passProperty = new SimpleStringProperty("");



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindFields();
        initUi();

    }

    public void bindFields() {
        usernameProperty.bind(usernamefield.textProperty());
        passProperty.bind(passfi8eld.textProperty());
        createnewl.textProperty().bind(newprojectProperty);
        titlel.textProperty().bind(titleProperty);

        usernamel.textProperty().bind(usernamelProperty);
        passl.textProperty().bind(passlProperty);


        langcombo.valueProperty().addListener((observableValue, locale, newloc) -> {
            Translator.localeProperty.set(newloc);
            if (newloc.equals(Locale.ENGLISH)) {

                Translator.localeAltProperty.set(Locale.FRENCH);
            } else Translator.localeAltProperty.set(Locale.ENGLISH);

        });

        Store.currentProjectProperty.bind(yearcombo.valueProperty());


    }

    public void initUi() {
        newProjectbtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS,50, Paint.valueOf("#242424")));
//        confirmBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CONNECTION,50, Paint.valueOf("#242424")));
        cancelbtn.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/exit.png",16,16)));

        usernamel.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT,12,Paint.valueOf(Store.Colors.black)));
        passl.setGraphic(ProjectUtils.createFontIcon(MaterialDesignK.KEY,12,Paint.valueOf(Store.Colors.black)));

        sideImage.setImage(ResourceUtil.getImageFromResource("images/welcomecolored.jpg",(int) sideImage.getFitWidth(),(int)
                sideImage.getFitHeight(),false));
    }
}
