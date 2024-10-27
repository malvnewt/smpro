package smpro.app.custom_nodes;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class CustomToolbarActionGroup {

    List<HBox> actionItems = new ArrayList<>();




    public CustomToolbarActionGroup() { }


    public void addActions(String label, Node icon, Control control) {

        Label l = new Label(label);
        l.setStyle("-fx-opacity: 0.85");
        l.setMinWidth(100);
        l.setMaxWidth(100);
        l.setTextOverrun(OverrunStyle.ELLIPSIS);
        l.setTooltip(ProjectUtils.createTooltip(label.toUpperCase()));
        control.setTooltip(ProjectUtils.createTooltip(label.toUpperCase()));
        control.setPadding(new Insets(4));

        HBox container =icon==null ? new HBox( l, control) :new HBox(icon, l, control);
        container.setSpacing(2);
        container.setPadding(new Insets(1));
        container.setAlignment(Pos.CENTER_LEFT);


        //feedback
        control.addEventHandler(MouseEvent.MOUSE_CLICKED,e->ProjectUtils.shakeX(control, -5).play());


        actionItems.add(container);
    }



    public Node build(int rowcount) {
        VBox groupContainer = new VBox();
        groupContainer.setSpacing(3);
        groupContainer.setPadding(new Insets(0,4,0,4));
        groupContainer.setStyle("-fx-border-width:0 2px 0 0;-fx-border-color: #44444480");

        GridPane pane = new GridPane();
        pane.setHgap(5);
//        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER_LEFT);

        int maxcolcount = Math.ceilDiv(actionItems.size(), rowcount);


        for (HBox item : actionItems) {
            int row = actionItems.indexOf(item)/maxcolcount;
            int col = actionItems.indexOf(item) % maxcolcount;


            pane.add(item, col, row);

        }

        groupContainer.getChildren().addAll(pane);
        groupContainer.setAlignment(Pos.CENTER);



        return groupContainer;

    }




}
