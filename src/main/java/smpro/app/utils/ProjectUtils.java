package smpro.app.utils;

import eu.hansolo.tilesfx.addons.Switch;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        stage.setResizable(false);

//        new JMetro(Style.DARK).setScene(scene);



        stage.setTitle(Translator.getIntl("app_settings").toUpperCase());
        stage.show();

        SettingsController  settingsController = fxmlLoader.getController();
        settingsController.changeTab(tabindex);
        settingsController.thisStage.set(stage);







    }

    public static String getFormatedDate(long epochTime, DateFormat formatter, Locale... locale) {
        Date date = new Date(epochTime);
        return formatter.format(date);

    }
    public static String getFormatedDate(LocalDate ld,DateTimeFormatter dtf){
        return ld.format(dtf);

    }





    public static void clickShakeHandler(MouseEvent event,boolean... isVertical) {
        Node source = (Node) event.getSource();

        if (isVertical.length > 0) {
            ProjectUtils.shakeY(source, -5).play();
        } else {

        ProjectUtils.shakeX(source, -5).play();
        }


    }

    public static Stage showFloatingNode(
            Node content,Stage parentStage,
            Node parent,double hoffset,double voffset,
            boolean movable,
            Pos... position
    ) {
        // create floating nodestage4
        Stage s = new Stage(StageStyle.UNDECORATED);


        VBox vb = new VBox(content);
        vb.setPadding(new Insets(5));
        vb.setStyle("-fx-background-color: " + Store.Colors.black);


        if (movable) {
            vb.setCursor(Cursor.MOVE);

            //apply move login

        }


        s.setScene(new Scene(vb));
        s.show();
        s.sizeToScene();



        double parentMinx = parent.getLayoutBounds().getMinX();
        double parentMinY = parent.getLayoutBounds().getMinY();

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        double parentStageWidth = parentStage.getWidth();
        double parentStageHeight = parentStage.getHeight();



        Point2D parentInScene = parent.localToScene(parentMinx, parentMinY);


        Pos pos = position.length > 0 ? position[0] : Pos.CENTER_RIGHT;
    if (pos==Pos.CENTER_RIGHT) {
                hoffset+=parent.getLayoutBounds().getWidth();
//                voffset += parent.getLayoutBounds().getHeight();

            } else if (pos==Pos.CENTER_LEFT) {
                hoffset-=parentStageWidth;
//                voffset += parent.getLayoutBounds().getHeight();
            }




        s.setX(parentInScene.getX() + (bounds.getWidth()/2)-(parentStageWidth/2)+hoffset);
        s.setY(parentInScene.getY() + (bounds.getHeight()/2)-(parentStageHeight/2)+voffset);

        return s;



    }

    public static void showFloatingTooltip(
            Node content,Stage parentStage,
            Node parent,double hoffset,double voffset


    ) {

        Tooltip tp = new Tooltip();
        tp.setGraphic(content);
        tp.setShowDelay(Duration.millis(100));
//        tp.setStyle("-fx-background-color:"+ Store.Colors.black);

        // create floating nodestage4

        double parentMinx = parent.getLayoutBounds().getMinX();
        double parentMinY = parent.getLayoutBounds().getMinY();

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        double parentStageWidth = parentStage.getWidth();
        double parentStageHeight = parentStage.getHeight();



        Point2D parentInScene = parent.localToScene(parentMinx, parentMinY);


//        double x = parentInScene.getX() + (bounds.getWidth()/2)-(parentStageWidth/2) + parent.getLayoutBounds().getWidth()+hoffset;
//        double y = parentInScene.getY() + (bounds.getHeight()/2)-(parentStageHeight/2)+voffset;
        double x = parentInScene.getX() +  parentStage.getX() + parent.getLayoutBounds().getWidth() +hoffset;
        double y = parentInScene.getY() + parentStage.getY() + parent.getLayoutBounds().getHeight()  +voffset;

        tp.show(parent,x,y);

        tp.setAutoHide(true);
        parent.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> tp.hide());



    }


    public static Alert showAlert(Stage parent, Alert.AlertType type,String header, String title, String message, ButtonType... buttons) {

        Alert a = new Alert(type,message,buttons);
        a.setTitle(title);
//        a.setContentText(message);
        a.setHeaderText(header);
        a.initOwner(parent);
        a.initModality(Modality.WINDOW_MODAL);
//        a.initStyle(StageStyle.UNDECORATED);

//        a.getDialogPane().getScene().setFill(Paint.valueOf(Store.Colors.lightestGray));


        switch (type) {
            case INFORMATION ->
                    a.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/info.png", 50, 50)));

            case WARNING ->
                    a.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/warning.png", 50, 50)));
            case CONFIRMATION ->
                    a.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/question2.png", 50, 50)));

            default ->
                    a.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/success.png", 50, 50)));
        }


        return a;

    }




    /// tabpane animation

    public static Timeline tabTranslation(Pane rootpane, double from, double to) {
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rootpane.translateXProperty(), from)),
                new KeyFrame(Duration.millis(200), new KeyValue(rootpane.translateXProperty(), to))
        );
        t.setCycleCount(1);
        t.setDelay(Duration.ZERO);

        return t;
    }


    public static TableColumn<HashMap<String,Object>, String> createTableColumn(String label,String dataKey) {

        TableColumn<HashMap<String,Object>, String> col = new TableColumn<>(label);

        col.setCellFactory(new Callback<>() {
            @Override
            public TableCell<HashMap<String,Object>, String> call(TableColumn<HashMap<String,Object>, String> colData) {
                return new TableCell<>(){
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);

                        if (!b) {
                            String itemText = String.valueOf(getItem());
                            setText(itemText);
//                            setPadding(new Insets(5, 5, 5, 10));
                            setStyle("-fx-font-family: Consolas;-fx-font-size: 14px");

                            setTooltip(new Tooltip(itemText));

                        }else {
                            setText(null);
                            setGraphic(null);

                        }
                    }
                };

            }
        });

        col.setCellValueFactory(coldataitem -> {
          HashMap<String,Object> dataitem =   coldataitem.getValue();
            return new SimpleStringProperty(String.valueOf(dataitem.get(dataKey)));

        });


        return col;


    }


    public static TextInputDialog getTextDialog(Stage parent,String title,String headertext,String contentText,Node graphic) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.initModality(Modality.WINDOW_MODAL);
        textInputDialog.initOwner(parent);
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(headertext);
        textInputDialog.setContentText(contentText);

        textInputDialog.getEditor().setMinWidth(300);

        textInputDialog.setGraphic(graphic);


        return textInputDialog;
    }


    public static Label createErrorLabel(String errText) {
        return new ErrorLabel(errText);
    }






}


class ErrorLabel extends Label {
    private final String labelText;

    ErrorLabel(String labelText) {
        this.labelText = labelText;
        this.setStyle("-fx-text-fill: orange;-fx-font-weight: bold;-fx-padding: 5px");
    }
}
