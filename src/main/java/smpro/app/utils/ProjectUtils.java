package smpro.app.utils;

import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import smpro.app.Entry;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

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
    public static void animateTabOut(AnchorPane pane, char sideInitial,  double end) {
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


    public static void openSettings(int tabindex,Stage parent) throws IOException {
        URL url = ResourceUtil.getAppResourceURL("views/settings.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang"));
        Parent root =fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ResourceUtil.getAppResourceURL("css/global.css").toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);
        stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));

//        new JMetro(Style.LIGHT).setScene(scene);



        stage.setTitle(Translator.getIntl("app_settings").toUpperCase());
        stage.show();

        SettingsController  settingsController = fxmlLoader.getController();
        settingsController.changeTab(tabindex);
        settingsController.thisStage.set(stage);







    }

    public static String getFormatedDate(long epochTime, DateFormat formatter) {
        Date date = new Date(epochTime);
        return formatter.format(date);

    }



    public static void clickShakeHandler(MouseEvent event,boolean... isVertical) {
        Node source = (Node) event.getSource();

        if (isVertical.length > 0) {
            ProjectUtils.shakeY(source, -5).play();
        } else {

        ProjectUtils.shakeX(source, -5).play();
        }


    }







}
