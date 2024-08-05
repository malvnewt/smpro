package smpro.app.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class CustomNodes {

    public static HBox getLabelledInput(String lkey, String iputId, String... inputPlaceholder) {
        String placeholder = inputPlaceholder.length > 0 ? inputPlaceholder[0] : "";

        TextField f = new TextField();
        f.setId(iputId);


        Label l = new Label();
        l.setText(Translator.getIntl(lkey));


        HBox container = new HBox(l,f);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(4));
        container.setSpacing(10);

        return container;
    }





}


