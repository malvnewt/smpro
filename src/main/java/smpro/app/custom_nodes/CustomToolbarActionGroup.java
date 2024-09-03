package smpro.app.custom_nodes;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import smpro.app.utils.ProjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class CustomToolbarActionGroup {

    List<HBox> actionItems = new ArrayList<>();

    private final Label title;



    public CustomToolbarActionGroup(String title) {
        Label titleLable = new Label(ProjectUtils.capitalize(title));
        titleLable.setStyle("-fx-font-weight: bold");
        this.title=titleLable;

    }


    public void addActions(String label, Node icon, Control control) {

        Label l = new Label(label);
        l.setStyle("-fx-opacity: 0.85");

        HBox container = new HBox(icon, l, control);
        container.setSpacing(8);
        container.setPadding(new Insets(5));
        container.setAlignment(Pos.CENTER_LEFT);

        actionItems.add(container);
    }


    public int getActionIndex(HBox hBox) {
        try {
            return actionItems.indexOf(hBox);
        } catch (Exception err) {
            return -1;
        }
    }

    public HBox getAction(int index) {
        try {
            return actionItems.get(index);
        } catch (Exception err) {
            return null;
        }

    }



    public Node build(int rowcount) {
        VBox groupContainer = new VBox();
        groupContainer.setSpacing(20);
        groupContainer.setPadding(new Insets(10));
        groupContainer.setStyle("-fx-border-width:0 2px 0 0;-fx-border-color: #44444480");

        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER_LEFT);

        for (HBox item : actionItems) {
            int row = actionItems.indexOf(item)/rowcount;
            int col = actionItems.size() % actionItems.indexOf(item);

            pane.add(item, col, row);

        }

        groupContainer.getChildren().addAll(pane, title);



        return groupContainer;

    }




}
