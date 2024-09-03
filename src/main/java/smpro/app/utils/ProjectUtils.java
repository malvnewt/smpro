package smpro.app.utils;

import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;
import smpro.app.custom_titlebar.CaptionConfiguration;
import smpro.app.custom_titlebar.CustomCaption;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class ProjectUtils {




    public static FontIcon createFontIcon(Ikon code, int size, Paint color) {
        FontIcon icon = new FontIcon(code);
        icon.getStyleClass().removeAll("ikonli","ikonli-icon");
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
    public static void animateVContainer(VBox box, char sideInitial,  double end) {
        Timeline timeline;

        if (sideInitial == 'w') { //ANIMATE PANE WIDTH
            double currentWdith = box.getWidth();
            if (currentWdith > end) {
//                pane.setMinWidth(0);
                timeline = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(box.maxWidthProperty(), end, Interpolator.EASE_BOTH)));

            } else {
//                pane.setMaxWidth(Double.POSITIVE_INFINITY);
                timeline = new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(box.minWidthProperty(), end, Interpolator.EASE_BOTH)));

            }


        } else {// ANIMATE PANE HEIGHT
            double currentHeight = box.getHeight();
            if (currentHeight > end) {
                timeline = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(box.maxHeightProperty(), end, Interpolator.EASE_BOTH)));
            } else {
                timeline = new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(box.minHeightProperty(), end, Interpolator.EASE_BOTH)));
            }

        }


        timeline.play();

    }


    public static Timeline shakeY(Node node, double offset,int... cyclecount) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Timeline t = new Timeline(new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), 0, Interpolator.EASE_BOTH)), new KeyFrame(Duration.millis(100.0),
                new KeyValue(node.translateYProperty(), offset, Interpolator.EASE_BOTH)));
        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateY(0.0);
            }

        });
        if (cyclecount.length>0) t.setCycleCount(cyclecount[0]);

        return t;
    }

    public static Timeline shakeX(Node node, double offset,int... cyclecount) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Timeline t = new Timeline(new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), 0, Interpolator.EASE_BOTH)), new KeyFrame(Duration.millis(100.0),
                new KeyValue(node.translateXProperty(), offset, Interpolator.EASE_BOTH)));
        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateX(0.0);
            }

        });
        if (cyclecount.length>0) t.setCycleCount(cyclecount[0]);
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

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);
        stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
        stage.setResizable(false);

        SettingsController settingsController = fxmlLoader.getController();
        applyDialogCaption(stage, settingsController.dragbox);


        scene.getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );


        stage.setTitle(Translator.getIntl("app_settings").toUpperCase());
        stage.show();


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


    public static void positionFloatingStage(Stage parentStage,Stage contentStage, Node parent,double hoffset,double voffset,
            Pos... position
    ) {

        double parentMinx = parent.getLayoutBounds().getMinX();
        double parentMinY = parent.getLayoutBounds().getMinY();

        Point2D parentInScene = parent.localToScene(parentMinx, parentMinY);



        Pos pos = position.length > 0 ? position[0] : Pos.CENTER_RIGHT;
    if (pos==Pos.CENTER_RIGHT) {
                hoffset+=parent.getLayoutBounds().getWidth();
//                voffset += parent.getLayoutBounds().getHeight();

            } else{
                hoffset-=contentStage.getWidth();
//                voffset += parent.getLayoutBounds().getHeight();
            }




        contentStage.setX(parentInScene.getX() + parentStage.getX()+hoffset);
        contentStage.setY(parentInScene.getY() + parentStage.getY() + voffset);



    }

    public static void showFloatingTooltip( Node content,Stage parentStage,Node parent,double hoffset,double voffset ) {

        Tooltip tp = new Tooltip();
        tp.setGraphic(content);
        tp.setShowDelay(Duration.millis(100));

        // create floating nodestage4

        double parentMinx = parent.getLayoutBounds().getMinX();
        double parentMinY = parent.getLayoutBounds().getMinY();

        Point2D parentInScene = parent.localToScene(parentMinx, parentMinY);

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

        a.getDialogPane().setMaxWidth(320);
        a.getDialogPane().setStyle("-fx-wrap-text: true");
        a.getDialogPane().getStylesheets().add(ResourceUtil.getAppResourceURL("css/recaf/all.css").toExternalForm());


        switch (type) {
            case INFORMATION ->
                    a.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/info.png", 50, 50)));

            case WARNING ->
                    a.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/warning.png", 50, 50)));
            case CONFIRMATION -> a.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/question2.png", 50, 50)));
            case ERROR -> a.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/critical.png", 50, 50)));

            default ->
                    a.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/success.png", 50, 50)));
        }


        return a;



    }

    public static PopOver showPopover(String title, Node content, PopOver.ArrowLocation arrowLocation, boolean detachable,boolean autohide,double... maxheight) {
        PopOver p = new PopOver(content);
        p.setAnimated(true);
        p.setTitle(title);
        p.setArrowLocation(arrowLocation);
//        p.setAnchorLocation(anchorLocation);
        p.setDetachable(detachable);
        p.setAutoHide(autohide);
//        p.setStyle("-fx-background-color:#373737");

        if (maxheight.length>0)p.setMaxHeight(maxheight[0]);
        p.getStyleClass().add("MyPopover");

        p.setCloseButtonEnabled(true);
        return p;
    }



    public static TableColumn<HashMap<String,Object>, String> createTableColumn(String label,String dataKey,boolean... params) {

        // params = {setToUppercase, }

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
                            if (params.length > 0) {
                                boolean setupper = params[0];
                                if (setupper) itemText = itemText.toUpperCase();
                            }
                            setText(itemText);
//                            setPadding(new Insets(5, 5, 5, 10));
                            setTooltip(new Tooltip(itemText));

                            setPadding(new Insets(0,0,0,10));


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
    public static TableColumn<HashMap<String,Object>, String> createTableColumnWithGraphic(String label,String dataKey,
                                                                                Callback<Object,Node> graphicCallBack,boolean... params) {

        // params = {setToUppercase, }

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
                            if (params.length > 0) {
                                boolean setupper = params[0];
                                if (setupper) itemText = itemText.toUpperCase();
                            }
                            setText(itemText);
//                            setPadding(new Insets(5, 5, 5, 10));
                            setStyle("-fx-font-family: Consolas;-fx-font-size: 14px");

                            setTooltip(new Tooltip(itemText));

                            setGraphic(graphicCallBack.call(getItem()));
                            setPadding(new Insets(0,0,0,10));


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

    public static void setCustomCellFactory(TableColumn<HashMap<String, Object>, String> column,Callback<String,String> callback,Node... g) {

        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<HashMap<String, Object>, String> call(TableColumn<HashMap<String, Object>, String> hashMapStringTableColumn) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (!b) {
                            setText(callback.call(s));
                            if (g.length>0)setGraphic(g[0]);
                        }else {
                            setText(null);
                            setGraphic(null);

                        }
                    }
                };
            }
        });



    }

    public static TextInputDialog getTextDialog(Stage parent,String title,String headertext,String contentText,Node graphic) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.initModality(Modality.WINDOW_MODAL);
        textInputDialog.initOwner(parent);
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(headertext);
        textInputDialog.setContentText(contentText);

        textInputDialog.getDialogPane().getScene().getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        textInputDialog.getEditor().setMinWidth(280);

        textInputDialog.setGraphic(graphic);


        return textInputDialog;
    }

    public static String capitalize(String s) {
        StringBuilder out = new StringBuilder();
        try{
            List<String> parts = Arrays.stream(s.strip().replaceAll("  "," ").split(" ")).toList();

            for (String part : parts) {
                out.append(String.valueOf(part.charAt(0)).toUpperCase()).append(part.substring(1));
                out.append(" ");

            }
            return out.toString().strip();


        }catch (Exception err) {

            return s;
        }
    }

    public static void applyDialogCaption(Stage s,Node dragArea) {


        CustomCaption.useForStage(s,true, new CaptionConfiguration()
                .setCaptionHeight(35)
                .setIconColor(Color.web(Store.Colors.LightGray))
                .setIconHoverColor(Color.web(Store.Colors.White))
                .setControlBackgroundColor(Color.web(Store.Colors.transparent))
                .setButtonHoverColor(Color.web("#aaaaaa30"))
                .setCaptionDragRegion(dragArea).
                useControls(false).
                setCloseButtonHoverColor(Color.web(Store.Colors.deepRed))
        );
    }




}

