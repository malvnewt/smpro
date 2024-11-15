package smpro.app.custom_titlebar.internal;

import com.sun.jna.platform.win32.WinDef;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.jetbrains.annotations.Nullable;
import smpro.app.custom_titlebar.CaptionConfiguration;
import smpro.app.utils.Store;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ControlsController implements Initializable {


    public HBox root;

    public Button maximizeRestoreButton;

    public Button closeButton;

    public Button minimizeButton;

    private final List<Button> buttons = new ArrayList<>();

    private CaptionConfiguration config;

    public void applyConfig(CaptionConfiguration config) {
        this.config = config;
        for(Button button : buttons) {
            button.setTextFill(config.iconColor);
            button.setBackground(new Background(new BackgroundFill(config.controlBackgroundColor, null, null)));

        }

        root.setPrefHeight(config.captionHeight);
        root.setMaxHeight(config.captionHeight);
    }


    public void hoverButton(@Nullable CustomizedStage.CaptionButton hoveredButton) {
        Button button = hoveredButton != null ? switch (hoveredButton) {
            case CLOSE -> closeButton;
            case MAXIMIZE_RESTORE -> maximizeRestoreButton;
            case MINIMIZE -> minimizeButton;
        } : null;

        for(Button btn : buttons) {
            btn.setTextFill(config.iconColor);
            btn.setBackground(new Background(new BackgroundFill(config.controlBackgroundColor, null, null)));
        }


        if(button == null) return;

        Color bgColor = hoveredButton == CustomizedStage.CaptionButton.CLOSE ? config.closeButtonHoverColor: config.buttonHoverColor;

        button.setBackground(new Background(new BackgroundFill(bgColor, CornerRadii.EMPTY, Insets.EMPTY)));
        button.setTextFill(config.iconHoverColor);
    }

    public void onResize(WinDef.WPARAM wParam) {
        switch (wParam.intValue()) {
            case 2 /*SIZE_MAXIMIZED*/ -> maximizeRestoreButton.setText("\uE923");
            case 0 /*SIZE_RESTORED*/ -> maximizeRestoreButton.setText("\uE922");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttons.add(maximizeRestoreButton);
        buttons.add(closeButton);
        buttons.add(minimizeButton);

        String family = "Segoe Fluent Icons";
        Font font = Font.font(family, 10);
        if(!font.getFamily().equals(family))
            font = Font.font("Segoe MDL2 Assets", 10);

        for(Button b : buttons) {
            b.setFont(font);
            b.setBackground(new Background(new BackgroundFill(Color.web(Store.Colors.transparent), null, null)));

        }
    }
}
