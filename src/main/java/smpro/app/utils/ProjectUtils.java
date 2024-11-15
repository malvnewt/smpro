package smpro.app.utils;

import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.WritableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;
import smpro.app.custom_titlebar.CaptionConfiguration;
import smpro.app.custom_titlebar.CustomCaption;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public static FontIcon createFontIconColored(Ikon code, int size, Paint color) {
        FontIcon icon = new FontIcon(code);
        icon.getStyleClass().removeAll("ikonli","ikonli-font-icon");
        icon.setFill(color);
        icon.setIconSize(size);
        return icon;
    }

    public static void animateScale(Node n, WritableValue<Double> p, double end) {
        Timeline timeline;
        timeline = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(p, end, Interpolator.EASE_BOTH)));
        timeline.play();

    }


    public static void animateScroll(ScrollPane pane, char sideInitial,  double end) {
        Timeline timeline;

        if (sideInitial == 'w') { //ANIMATE PANE WIDTH
            timeline = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(pane.hvalueProperty(), end, Interpolator.EASE_BOTH)));
        } else {// ANIMATE PANE HEIGHT
            timeline = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(pane.hvalueProperty(), end, Interpolator.EASE_BOTH)));



        }
        timeline.play();

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
//    public static Timeline scale(Node node,int... cyclecount) {
//        Objects.requireNonNull(node, "Node cannot be null!");
//
//        Timeline t = new Timeline(new KeyFrame(Duration.ZERO,
//                new KeyValue(node.scaleXProperty(), 0, Interpolator.EASE_BOTH)), new KeyFrame(Duration.millis(100.0),
//                new KeyValue(node.translateXProperty(), offset, Interpolator.EASE_BOTH)));
//        t.statusProperty().addListener((obs, old, val) -> {
//            if (val == Animation.Status.STOPPED) {
//                node.setTranslateX(0.0);
//            }
//
//        });
//        if (cyclecount.length>0) t.setCycleCount(cyclecount[0]);
//        return t;
//    }

    public static Tooltip createTooltip(String content) {
        Tooltip tp = new Tooltip(content);
        tp.setShowDelay(Duration.millis(200));
        tp.setShowDuration(Duration.seconds(30));

        return tp;
    }


    public static void openSettings(int tabindex,Stage parent) throws IOException {
        URL url = ResourceUtil.getAppResourceURL("views/settings.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
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
    public static String getFormatedDateTime(long epochdateTime, DateFormat formatter, Locale... locale) {
        Date date = new Date(epochdateTime);
        return formatter.format(date);

    }
    public static String getFormatedDate(LocalDate ld,DateTimeFormatter dtf){
        return ld.format(dtf);

    }

    public static HashMap<String, Object> getObject(String objId, String table) {
        return PgConnector.fetch(String.format("""
                select * from "%s" where id=%d  """, table, Integer.parseInt(objId)),PgConnector.getConnection()).get(0);

    }

    public static List<String> getUniqueValues(List<String> items) {
        List<String> out = new ArrayList<>();
        for (String string : items) {

            if (!out.contains(string)) out.add(string);
        }

        return out;
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
        p.setDetachable(detachable);
        p.setAutoHide(autohide);

        if (maxheight.length>0)p.setMaxHeight(maxheight[0]);
        p.getStyleClass().add("MyPopover");

        p.setCloseButtonEnabled(true);
        return p;
    }
    public static PopOver showInfoPopover(String title,String message,Node g, PopOver.ArrowLocation arrowLocation,float... maxheight) {
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);

        PopOver p = new PopOver(content);

        p.setAnimated(true);
        p.setTitle(title);
        p.setArrowLocation(arrowLocation);
        p.setDetachable(false);
        p.setAutoHide(true);
//        Label pl = new Label(message);
        TextArea ta = new TextArea(message);
        ta.setStyle("-fx-text-fill: gray;-fx-font-weight: bold");
        p.getStyleClass().add("MyPopover");
        p.setCloseButtonEnabled(true);

        if (g == null) {
            content.getChildren().add(ta);
        } else content.getChildren().addAll(g, ta);
        return p;
    }

    public static Tooltip createToolTip(String message, ImageView graphic) {

        VBox vb = new VBox();
        vb.setSpacing(5);
        vb.setPadding(new Insets(5));

        Label messageL = new Label(message);
        messageL.setStyle("-fx-font-weight: bold");
        messageL.setWrapText(true);

        vb.getChildren().add(messageL);
        vb.getChildren().add(graphic);

        Tooltip tp = new Tooltip();
        tp.setShowDelay(Duration.millis(100));
         tp.setGraphic(vb);
        return tp;

    }

    public static HBox createHspacer(double... space) {
        HBox hb = new HBox();
        if (space.length == 0) {

            HBox.setHgrow(hb, Priority.ALWAYS);
        } else {
            hb.setMinWidth(space[0]);
            hb.setMaxWidth(space[0]);
        }

        return hb;
    }
    public static VBox createVspacer(double... space) {
        VBox hb = new VBox();
        if (space.length == 0) {
            HBox.setHgrow(hb, Priority.ALWAYS);
        } else {
            hb.setMinHeight(space[0]);
            hb.setMaxHeight(space[0]);
        }

        return hb;
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

    public static String getShortPersonName(String string,int partcount) {
        List<String> parts = new ArrayList<>(Arrays.stream(string.strip().split(" ")).map(String::strip).toList());
        parts.removeIf(s -> s.isEmpty());

        StringBuilder builder = new StringBuilder();


        if (partcount > parts.size()) return string;

        for (String p : parts) {
            int pos = parts.indexOf(p);

            if (!p.isEmpty() && pos < partcount) builder.append(p).append(" ");
            if (!p.isEmpty() && pos == partcount) builder.append(p.charAt(0)).append(".");


        }
        return builder.toString();

    }

    public static String getTeacherForSubject(String subjectName,int classid) throws SQLException {
        String person = Translator.getIntl("unavailable");

        PreparedStatement ps = PgConnector.getConnection().prepareStatement("select * from permissions where classid=?");
        ps.setInt(1, classid);

        ResultSet res = ps.executeQuery();
        while (res.next()) {
            List<String> subjects = PgConnector.parsePgArray(res, "subjects");
            if (subjects.contains(subjectName)) {
                person = res.getString("teacher");
                break;
            }
        }

        return person;


    }

    public static Integer getSubjectCoefficient(HashMap<String, Object> cls, String subname) {
        HashMap<String, Object> subObj = PgConnector.getObjectFromKey("subject_name", subname, "subjects");
        assert subObj != null;
        return PgConnector.getNumberOrNull(subObj,"subject_coefficient").intValue();
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

    public static Label createErrLabel(String content) {
        Label l = new Label(content);
        l.setGraphic(createFontIcon(MaterialDesignA.ALERT_CIRCLE, 15, Paint.valueOf(Store.Colors.red)));
        l.getStyleClass().addAll("text", "danger", "text-bold");
        return l;
    }

    public static Label createInfoLabel(String content) {
        Label pl = new Label(content);
        pl.setStyle("-fx-text-fill: gray;-fx-font-weight: bold");
        return pl;
    }

    public static VBox getTablePlaceholder(int... size) {
        VBox placeholder = new VBox();
        VBox.setVgrow(placeholder,Priority.ALWAYS);
        placeholder.setAlignment(Pos.CENTER);
        Label l = new Label( Translator.getIntl("no_data").toUpperCase());
        l.setStyle("-fx-font-weight: bold;-fx-font-size: 14px;-fx-text-fill: lightgray;-fx-opacity: 0.75");

        int dim = size.length == 0 ? 40 : size[0];
        l.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/empty-glass.png", dim, dim, true)));
        placeholder.getChildren().add(l);

        return placeholder;
    }




}

