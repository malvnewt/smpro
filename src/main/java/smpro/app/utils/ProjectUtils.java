package smpro.app.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import smpro.app.Entry;
import smpro.app.ResourceUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import static atlantafx.base.util.Animations.EASE;

public class ProjectUtils {




    public static FontIcon createFontIcon(Ikon code, int size, Paint color) {
        FontIcon icon = new FontIcon(code);
        icon.setFill(color);
        icon.setIconSize(size);
        return icon;
    }



    public static void animatePaneSide(AnchorPane pane, char sideInitial,  double end) {
        Timeline timeline;

        if (sideInitial == 'w') { //ANIMATE PANE WIDTH
            double currentWdith = pane.getWidth();
            if (currentWdith > end) {
//                pane.setMinWidth(0);
                timeline = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(pane.maxWidthProperty(), end, Interpolator.EASE_BOTH)));

            } else {
//                pane.setMaxWidth(Double.POSITIVE_INFINITY);
                timeline = new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(pane.minWidthProperty(), end, Interpolator.EASE_BOTH)));

            }


        } else {// ANIMATE PANE HEIGHT
            double currentHeight = pane.getHeight();
            if (currentHeight > end) {
                timeline = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(pane.maxHeightProperty(), end, Interpolator.EASE_BOTH)));
            } else {
                timeline = new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(pane.minHeightProperty(), end, Interpolator.EASE_BOTH)));
            }

        }


        timeline.play();

    }


    public static Timeline shakeY(Node node, double offset) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Timeline t = new Timeline(new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), 0, EASE)), new KeyFrame(Duration.millis(100.0),
                new KeyValue(node.translateYProperty(), offset, EASE)));
        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateY(0.0);
            }

        });
        return t;
    }

    public static Timeline shakeX(Node node, double offset) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Timeline t = new Timeline(new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), 0, EASE)), new KeyFrame(Duration.millis(100.0),
                new KeyValue(node.translateXProperty(), offset, EASE)));
        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateX(0.0);
            }

        });
        return t;
    }

    public static Tooltip createTooltip(String content) {
        Tooltip tp = new Tooltip(content);
        tp.setShowDelay(Duration.millis(200));

        return tp;
    }







}
