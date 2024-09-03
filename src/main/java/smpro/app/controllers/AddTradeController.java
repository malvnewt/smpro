package smpro.app.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Stack;

public class AddTradeController implements Initializable {
    public TextField tradeabrrfiled;
    public TextField trade_namefield;
    public Button cancelBtn;
    public Button confirmBtn;
    public Button closedlg;


    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public ImageView iconview;
    public HBox dragArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closedlg.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOSE, 30, Paint.valueOf("transparent")));
        closedlg.setOnAction(e->thisStage.get().close());
        closedlg.addEventHandler(MouseEvent.MOUSE_EXITED, e->{
            closedlg.setStyle("-fx-background-color: transparent");
        });
        closedlg.addEventHandler(MouseEvent.MOUSE_ENTERED,e->{
            closedlg.setStyle("-fx-background-color: "+ Store.Colors.deepRed);
        });

        trade_namefield.requestFocus();
        iconview.setImage(ResourceUtil.getImageFromResource("images/plus.png",50,50,true));




        cancelBtn.setOnAction(e->thisStage.get().close());

        confirmBtn.setOnAction(e->{
        String name = trade_namefield.getText();
        String abbr = tradeabrrfiled.getText();

        if (name.isEmpty() || abbr.isEmpty()) {
            thisStage.get().close();
        } else {
            String insert = String.format("insert into trades (trade_name,trade_abbreviation) values" +
                    "('%s','%s')", name, abbr);

            PgConnector.insert(insert);

            thisStage.get().close();

            Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
                    Translator.getIntl("data_updated"), ButtonType.OK);
            a.showAndWait();





        }
        });




    }
}
