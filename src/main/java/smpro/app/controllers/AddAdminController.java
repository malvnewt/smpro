package smpro.app.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TransferQueue;

public class AddAdminController implements Initializable {


    public Label title;
    public HBox dragArea;
    public Button closedlg;
    public TextField usernamefield;
    public TextField fullnamefiled;
    public ImageView iconview;
    public TextField passwordfield;
    public Button cancelBtn;
    public Button confirmBtn;
    List<String> featureNames = Store.appFeatures;


   public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        configureUi();
        configureActions();


    }

    private void configureActions() {
        cancelBtn.setOnAction(e -> thisStage.get().close());

        confirmBtn.setOnAction(e->{
            String fullname = fullnamefiled.getText();
            String username = usernamefield.getText();
            String password = passwordfield.getText();

            List<HashMap<String, Object>> admins = PgConnector.fetch(String.format("select * from users where username='%s'",username), PgConnector.getConnection());

            for (TextField f : new TextField[]{usernamefield, passwordfield, fullnamefiled}) {
                if (f.getText().isEmpty()) {
                    f.getStyleClass().add("error-textfield");
                }
            }

            boolean isinvalid = fullname.isEmpty() || password.isEmpty() || username.isEmpty();
            if (isinvalid)return;

            if (!admins.isEmpty()) {
                Label l = new Label(String.format("%s '%s' %s ", Translator.getIntl("username"), username, Translator.getIntl("already_exist")));
                l.getStyleClass().addAll("danger", "text-bold", "text");



                PopOver userpopup = ProjectUtils.showPopover("", l, PopOver.ArrowLocation.BOTTOM_LEFT, false, true);
                userpopup.show(usernamefield);
                return;
            }

            String insert = String.format("insert into users (fullname,username,password) values ('%s','%s','%s')", fullname, username, password);

            PgConnector.insert(insert);

            thisStage.get().close();


        });

    }


    public void configureUi() {
        iconview.setImage(ResourceUtil.getImageFromResource("images/plus.png", 50, 50, true));
        title.setText(Translator.getIntl("add_new_user"));

        closedlg.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOSE, 30, Paint.valueOf("transparent")));
        closedlg.setOnAction(e->thisStage.get().close());
        closedlg.addEventHandler(MouseEvent.MOUSE_EXITED, e->{
            closedlg.setStyle("-fx-background-color: transparent");
        });
        closedlg.addEventHandler(MouseEvent.MOUSE_ENTERED,e->{
            closedlg.setStyle("-fx-background-color: "+ Store.Colors.deepRed);
        });


        for (TextField f : new TextField[]{usernamefield, passwordfield, fullnamefiled}) {
            f.textProperty().addListener((o,old,newv)->{
                if (!newv.isEmpty()) f.getStyleClass().remove("error-textfield");
            });

        }

        fullnamefiled.requestFocus();

        title.setText(Translator.getIntl("add_new_admin"));



    }

}
