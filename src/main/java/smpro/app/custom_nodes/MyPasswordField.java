package smpro.app.custom_nodes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignE;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;

import java.beans.EventHandler;


public class MyPasswordField extends CustomPasswordField {

    StringProperty passwordProperty = new SimpleStringProperty();

    BooleanProperty showPassP = new SimpleBooleanProperty(false);

    FontIcon eyeVisisble = ProjectUtils.createFontIcon(MaterialDesignE.EYE, 15, Paint.valueOf(Store.Colors.lightestGray));

    public Button b = new Button("", eyeVisisble);

    private  final Label l;





    public MyPasswordField(Label l) {
        this.l = l;
        l.textProperty().bind(textProperty());


        //set right node
        b.setOnAction(e->showPassP.set(!showPassP.get()));
        setRight(b);
        b.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
        b.setCursor(Cursor.HAND);



        b.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        b.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);




    }

    public void handleMouseReleased(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.setOpacity(1);
        l.setVisible(false);

    }

    public void handleMousePressed(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.setOpacity(0.6);
        l.setVisible(true);


    }


}
