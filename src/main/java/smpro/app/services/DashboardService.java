package smpro.app.services;

import eu.hansolo.fx.charts.AxisType;
import eu.hansolo.fx.charts.Position;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.tilesfx.Demo;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.chart.SmoothedChart;
import eu.hansolo.tilesfx.chart.TilesFXSeries;
import eu.hansolo.tilesfx.skins.BarChartItem;
import eu.hansolo.tilesfx.tools.Helper;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.EntryController;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;
import smpro.app.controllers.AddSubjectController;
import smpro.app.custom_nodes.CustomToolbarActionGroup;
import smpro.app.utils.*;

import java.io.IOException;
import java.net.URL;
import java.security.cert.TrustAnchor;
import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.util.*;

public class DashboardService {

    Button registerBtn;
    Button registerTeacher;
    Button registerClass;
    Button addSubject;
    Button makePayment;
    CustomTextField searchStudent;
    Button refreshBtn;


    double MINHEIGHT = 300d;



    private final EntryController entryController;


   public List<Node> toolbarItems = new ArrayList<>();

    public ObjectProperty<Stage> mainStage = new SimpleObjectProperty<>();

    DoubleProperty timesXP = new SimpleDoubleProperty(0);


    public DashboardService(Stage mainStage,EntryController controller) {
        this.entryController = controller;
        this.mainStage.set(mainStage);

        initUi();

    }

    public void initUi() {
        // date and locale containers
        Label datel = new Label(ProjectUtils.getFormatedDate(new Date().getTime(), DateFormat.getDateInstance(0, Translator.getLocale())));
        datel.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CALENDAR, 40, Paint.valueOf(Store.Colors.green)));
        entryController.dateTile.getChildren().add(datel);



        entryController.langlable.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TRANSLATE, 40, Paint.valueOf(Store.Colors.green)));
        entryController.langlable.setText(Translator.getLocale().getDisplayLanguage(Translator.getLocale()));


//        ///////////////////////////  create charts
        Platform.runLater(this::buildCountsDisplay);
        Platform.runLater(this::buildCalendarTile);
        Platform.runLater(this::buildStudentchart);
        Platform.runLater(this::buildHoursChart);
        Platform.runLater(this::buildKTermInfoTile);
        Platform.runLater(this::buildTradesChart);
        Platform.runLater(this::buildRecentAddsView);






    }

    public List<Node> buildToolbarOptions(){
        //register student group
        CustomToolbarActionGroup registerGrou = new CustomToolbarActionGroup();
        registerBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/plus_green.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE, true)));
        registerGrou.addActions(ProjectUtils.capitalize(Translator.getIntl("register_student")), null, registerBtn);



        CustomToolbarActionGroup paymentgrouup = new CustomToolbarActionGroup();

        makePayment= new Button("",new ImageView(ResourceUtil.getImageFromResource("images/dollar.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        paymentgrouup.addActions(ProjectUtils.capitalize(Translator.getIntl("collect_fees")),
                null,makePayment);

        CustomToolbarActionGroup searchGroup = new CustomToolbarActionGroup();
        searchStudent = new CustomTextField();
        searchStudent.setMaxWidth(200);
        searchStudent.setPromptText(Translator.getIntl("search_student"));
        searchStudent.setRight(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT,15, Paint.valueOf("gray")));
        searchGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("search")),
                new ImageView(ResourceUtil.getImageFromResource("images/search.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)),searchStudent);





        //////////////////////////////////
        CustomToolbarActionGroup groupTwo = new CustomToolbarActionGroup();

        registerTeacher = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/add_user.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        groupTwo.addActions(ProjectUtils.capitalize(Translator.getIntl("new_staff")),
                null,registerTeacher);
        ////////////////////////////////





        //////////////////////////////
        registerClass= new Button("",new ImageView(ResourceUtil.getImageFromResource("images/plus.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        groupTwo.addActions(ProjectUtils.capitalize(Translator.getIntl("new_class")),
                null,registerClass);

        addSubject= new Button("",new ImageView(ResourceUtil.getImageFromResource("images/newdoc.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        groupTwo.addActions(ProjectUtils.capitalize(Translator.getIntl("new_sub")),
                null,addSubject);



        //refresh gorup
        CustomToolbarActionGroup refreshgroup = new CustomToolbarActionGroup();


        refreshBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/refresh.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE+15, true)));
        refreshgroup.addActions(ProjectUtils.capitalize(Translator.getIntl("refresh_stats")),
                null,refreshBtn);
        refreshBtn.setStyle("-fx-background-color: transparent");




        for (Button b : new Button[]{registerBtn, registerClass,refreshBtn,addSubject,makePayment,registerTeacher}) {
            b.setCursor(Cursor.HAND);
            b.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
            b.getStyleClass().add("toolbar-btn");


        }



        toolbarItems.add(registerGrou.build(1));
        toolbarItems.add(paymentgrouup.build(1));
        toolbarItems.add(searchGroup.build(1));
        toolbarItems.add(groupTwo.build(2));
        toolbarItems.add(refreshgroup.build(1));

        return toolbarItems;


    }

    public void buildCountsDisplay(){
        entryController.countsDisplayHb.getChildren().clear();

        List<HashMap<String, String>> items = new ArrayList<>();
        items.add(new HashMap<>(Map.of("title","students","image", "images/menu_icons/students3.png")));
        items.add(new HashMap<>(Map.of("title","classes","image", "images/menu_icons/class_board.png")));
        items.add(new HashMap<>(Map.of("title","sections","image", "images/section_home.png")));
        items.add(new HashMap<>(Map.of("title","employees","image", "images/menu_icons/teachers.png")));
        items.add(new HashMap<>(Map.of("title","subjects","image", "images/book.png")));


        for (HashMap<String, String> item : items) {
            String title = item.get("title");
            int count = PgConnector.fetch(String.format("""
                    select * from "%s"  """, title), PgConnector.getConnection()).size();

            Node sideNode;

            String filepath = item.get("image");

            if (filepath.contains(".svg")) {
                ImageView view = new ImageView();
                view.setFitHeight(50);
                view.setFitWidth(50);
                view.setImage(SVGLoader.load(ResourceUtil.getAppResourceURL(filepath)).toImage());

                sideNode =view;
                System.out.println(sideNode);
            } else {
             sideNode = new ImageView(ResourceUtil.getImageFromResource(filepath, 50, 50, true));
            }




            String titleString = title.equalsIgnoreCase("subjects") ? "departments_long" : title;
            Label titlelable = new Label(Translator.getIntl(titleString).toUpperCase());
            titlelable.setStyle("-fx-font-weight: bold;-fx-font-size: 14");

            Label countlable = new Label(String.valueOf(count));
            countlable.setStyle("-fx-font-weight: bold;-fx-font-size: 25;-fx-text-fill: "+ (count==0 ? Store.Colors.red:"white"));

            GridPane pane = new GridPane();
            pane.add(titlelable, 0, 0, 2, 1);
            pane.add(countlable,0,1);
//            pane.add(sideNode,2,0,1,2);

//            pane.setAlignment(Pos.CENTER_LEFT);
            GridPane.setHalignment(sideNode, HPos.RIGHT);
            GridPane.setHalignment(countlable, HPos.LEFT);
            GridPane.setHalignment(titlelable, HPos.LEFT);

            pane.setHgap(50);
            pane.setVgap(5);
            pane.setPadding(new Insets(10));



            HBox container = new HBox(pane,ProjectUtils.createHspacer(),sideNode);
            container.setSpacing(20);
            container.setPadding(new Insets(10));

            container.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(container, Priority.ALWAYS);
            HBox.setHgrow(pane, Priority.ALWAYS);



            container.setStyle("-fx-background-color: linear-gradient(to right,#dddddd20 60%,#dddddd65);-fx-background-radius: 10px ");
//            container.setEffect(new Lighting());

            entryController.countsDisplayHb.getChildren().add(container);


        }

        entryController.countsDisplayHb.setSpacing(20);
        entryController.countsDisplayHb.setAlignment(Pos.CENTER_LEFT);
        entryController.countsDisplayHb.setPadding(new Insets(10));



    }

    public void buildTradesChart() {
        Tile donutChartTile;

        List<ChartData> data = new ArrayList<>();
        List<Color> colors = List.of(
                Tile.BLUE, Tile.GREEN, Tile.DARK_BLUE,
                Tile.MAGENTA, Tile.YELLOW_ORANGE, Tile.ORANGE, Tile.PINK,
                Tile.YELLOW, Tile.LIGHT_GREEN, Tile.RED, Color.web("#a3d1ff70"),
                Color.web("#eeffaa25"),Color.web("#eeffaa50"),Color.web("#eeffaa75"),
                Color.web("#aaaaff25"),Color.web("#aaaaff50"),Color.web("#aaaaff75"),
                Color.web("#fafaaa25"),Color.web("#fafaaa60"),Color.web("#fafaaa90"),
                Color.web("#dfadce25"),Color.web("#dfadce60"),Color.web("#dfadce90"),
                Color.web("#caefba25"),Color.web("#caefba60"),Color.web("#caefba60")
        );


        List<HashMap<String,Object>> allTrades = PgConnector.fetch("select * from trades order by trade_name", PgConnector.getConnection());
        for (HashMap<String,Object> t : allTrades) {
            List<?> studentsfount = PgConnector.fetch(String.format("select * from students where trade='%s'", PgConnector.getFielorBlank(t,"trade_name")), PgConnector.getConnection());

            int index = allTrades.indexOf(t);



            Color color = index==0 ?  colors.get(index) : colors.get(index % colors.size());
            System.out.println(color);

            data.add(new ChartData(PgConnector.getFielorBlank(t,"trade_abbreviation").toUpperCase(), studentsfount.size(),color));
        }

        donutChartTile = TileBuilder.create().skinType(Tile.SkinType.DONUT_CHART).prefSize(150.0, 150.0).
                title(Translator.getIntl("trades")).text(Translator.getIntl("trade_chart_bottom")).textVisible(true).chartData(data).build();

        donutChartTile.setAnimated(true);
        entryController.tradeView.getChildren().clear();
        entryController.tradeView.getChildren().add(donutChartTile);
        donutChartTile.setMinHeight(MINHEIGHT);




    }

    public void buildStudentchart() {

        //        student distribution chart

        List<HashMap<String,Object>> classdata = PgConnector.fetch("select * from classes order by level,classname",PgConnector.getConnection());
        List<String> classAbbrs = PgConnector.listHashAttrs(classdata,"class_abbreviation");

        List<Number> studentCounts = new ArrayList<>();
        for (HashMap<String, Object> item : classdata) {
            String cid = PgConnector.getFielorBlank(item, "id");
            List<?> students = PgConnector.fetch(String.format("select * from students where classid=%d", Integer.parseInt(cid)), PgConnector.getConnection());
            studentCounts.add(students.size());
        }

        Tile studentChartTile;
        XYChart.Series<String, Number> studentSeries = new XYChart.Series();
        studentSeries.setName(Translator.getIntl("class_count"));

        for (HashMap<String, Object> citem : classdata) {
            int cindex = classdata.indexOf(citem);
            int scount = studentCounts.get(cindex).intValue();
            String cname = classAbbrs.get(cindex);
            cname = cname.length() > 5 ? cname.substring(0, 5) :cname;

            studentSeries.getData().add(new XYChart.Data(cname.toUpperCase(), scount,Tile.BLUE));
        }

        studentChartTile = TileBuilder.create().skinType(Tile.SkinType.SMOOTHED_CHART).prefSize(150.0, 150.0)
                .title(Translator.getIntl("student_dist")).chartType(Tile.ChartType.AREA).smoothing(true).tooltipTimeout(1000.0).tilesFxSeries(
                        new TilesFXSeries<>(studentSeries, Tile.BLUE)).averageVisible(true)
                .build();

        studentChartTile.setMinHeight(MINHEIGHT);
        studentChartTile.setAnimated(true);

        entryController.studentClassoverview.getChildren().clear();
        entryController.studentClassoverview.getChildren().add(studentChartTile);


    }

    public void buildHoursChart() {
        entryController.nextTimes.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ARROW_LEFT_THICK,40,Paint.valueOf("#dddddd99")));
        entryController.prevTimes.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ARROW_RIGHT_THICK,40,Paint.valueOf("#dddddd99")));
        entryController.dashboardPeriodTime.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CLOCK,20,Paint.valueOf("#dddddd")));

        List<HashMap<String, Object>> clsdata = PgConnector.fetch("select * from classes order by level,classname", PgConnector.getConnection());


        double timeItemMinwidth = 170;

        //scroll actions
            ScrollPane containerScroll =  entryController.timeItemsScrollpane;
            HBox containerhb = entryController.dashboradtimeItemshb;
        entryController.prevTimes.setOnAction(e->{
            double scrollhbPercent =  (timeItemMinwidth*5) / containerScroll.getWidth();

            double fivedelta = (5d / clsdata.size());

            double endvalue = containerScroll.getHvalue() + fivedelta;
            System.out.println(endvalue);
            System.out.println(containerScroll.getHvalue());
            ProjectUtils.animateScroll(containerScroll,'w',endvalue);

        });

        entryController.nextTimes.setOnAction(e->{
//            ScrollPane containerScroll =  entryController.timeItemsScrollpane;
            double fivedelta = (5d / clsdata.size());
            double endvalue = containerScroll.getHvalue() -fivedelta;

            ProjectUtils.animateScroll(containerScroll,'w',endvalue);

        });

        entryController.dashboradtimeItemshb.setOnMousePressed(event -> timesXP.set(event.getX()));

        entryController.dashboradtimeItemshb.setOnMouseDragged(event -> {
            System.out.println("mouse dragged");
            double newx = event.getX();
            double deltax =entryController.timeItemsScrollpane.getHvalue()+ (timesXP.get()-newx) / (entryController.timeItemsScrollpane.getWidth());
            entryController.timeItemsScrollpane.setHvalue( deltax);


        });

        //hour combno
        entryController.dashboadPeriodCombo.getItems().clear();
        entryController.dashboadPeriodCombo.getItems().addAll(Store.supportedPeriods);
        entryController.dashboadPeriodCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Integer integer, boolean b) {
                super.updateItem(integer, b);
                if (!b) {
                    setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CIRCLE_MULTIPLE_OUTLINE, 20, Paint.valueOf("lightgray")));
                    setText(String.format("%s %s %d", Translator.getIntl("hour"), Store.UnicodeSumnbol.blank, integer));
                }else {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-font-weight: bold");
                }
            }
        });
        entryController.dashboadPeriodCombo.setValue(1);
        entryController.dashboadPeriodCombo.valueProperty().addListener((observableValue, integer, newPeriod) -> System.out.println("getting periods id = " + newPeriod));

        //TODO refill hours hbox with correct periods

        entryController.nextTimes.setCursor(Cursor.HAND);
        entryController.prevTimes.setCursor(Cursor.HAND);


        String demoTeacher = "Mr Ngwa Marvin Newton";
        String demoSub = "Chemistry";

        entryController.dashboradtimeItemshb.getChildren().clear();

        for (HashMap<String, Object> clsObj : clsdata) {
            String cname = PgConnector.getFielorBlank(clsObj, "class_abbreviation");
            Label classL = new Label(cname.toUpperCase());
            Label teacherL = new Label(ProjectUtils.capitalize(ProjectUtils.getShortPersonName(demoTeacher, 2)));
            Label subjectL = new Label(demoSub.toUpperCase());

            GridPane.setHgrow(teacherL,Priority.ALWAYS);
            classL.setMinWidth(35);
            subjectL.setMinWidth(80);
//            teacherL.setMinWidth(35);

            for (Label l:new Label[]{classL,subjectL,teacherL})
                l.setTooltip(ProjectUtils.createTooltip(l.getText().toUpperCase()));

            teacherL.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ACCOUNT, 12, Paint.valueOf(Store.Colors.LightGray)));
            subjectL.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignB.BOOK_OPEN, 12, Paint.valueOf(Store.Colors.LightGray)));

            classL.setStyle("-fx-font-weight: bold;-fx-font-size: 16");
            subjectL.setStyle("-fx-font-weight: bold;-fx-font-size: 12");
            teacherL.setStyle("-fx-font-weight: bold;-fx-font-size: 12;-fx-opacity: 0.75");

            GridPane itemPane = new GridPane();
            itemPane.setHgap(15);
            itemPane.setVgap(8);
            itemPane.setMinWidth(timeItemMinwidth);

            itemPane.add(classL, 0, 0);
            itemPane.add(subjectL, 1, 0);
            itemPane.add(teacherL, 0, 1,2,1);
            itemPane.setPadding(new Insets(8));

            GridPane.setHalignment(teacherL, HPos.CENTER);
            itemPane.setStyle("-fx-background-color:#44444480;-fx-background-radius: 10px ");

            entryController.dashboradtimeItemshb.getChildren().add(itemPane);


        }





    }

    public void buildCalendarTile() {

        ZonedDateTime now = ZonedDateTime.now();
        List<ChartData> calendarData = new ArrayList<>(10);
        calendarData.add(new ChartData("DATE", now.toInstant()));

        Tile calendarTile = TileBuilder.create().skinType(Tile.SkinType.CALENDAR).prefSize(150.0, 150.0).
                chartData(calendarData).minHeight(220).build();
        calendarTile.setAnimated(true);

        entryController.recently_added.getChildren().clear();
        entryController.recently_added.getChildren().add(calendarTile);

    }

    public void buildKTermInfoTile() {
        HashMap<String, Object> base = PgConnector.fetch("select * from base",PgConnector.getConnection()).get(0);
        int currentTerm = PgConnector.getNumberOrNull(base, "current_term").intValue();



        Tile termFlipTile = TileBuilder.create().skinType(Tile.SkinType.FLIP).prefSize(150.0, 150.0).textVisible(true)
                .characters(String.valueOf(currentTerm),String.valueOf(currentTerm)).flipTimeInMS(5000L).text(Translator.getIntl("current_term").toUpperCase()).animated(true).build();


        entryController.termdisplayview.getChildren().clear();
        entryController.termdisplayview.getChildren().add(termFlipTile);

        entryController.editTermibtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PENCIL_OUTLINE, 20, Paint.valueOf(Store.Colors.LightGray)));
        entryController.editTermibtn.setOnAction(e->{

        });


    }

    public void buildRecentAddsView() {

        List<HashMap<String, Object>> recentStudents = PgConnector.fetch("select * from students  order by id desc limit 10", PgConnector.getConnection());

        entryController.dashboardRecentLv.getItems().clear();
        entryController.dashboardRecentLv.getItems().addAll(recentStudents);
        entryController.dashboardRecentLv.getStyleClass().addAll("dense", "bordered");

        entryController.dashboardRecentLv.setCellFactory(hashMapListView -> new ListCell<>(){
            @Override
            protected void updateItem(HashMap<String,Object> item, boolean b) {
                super.updateItem(item, b);

                if (!b) {
                    String fname = PgConnector.getFielorBlank(item, "firstname");
                    String lname = PgConnector.getFielorBlank(item, "lastname");

                    long dateEpochTime = PgConnector.getNumberOrNull(item, "admission_date").longValue();
                    String dateString = ProjectUtils.getFormatedDate(dateEpochTime, DateFormat.getDateInstance(0, Translator.getLocale()));

                    String text = String.format("%s %s - %s %s  %s ", ProjectUtils.capitalize(fname), ProjectUtils.capitalize(lname), Store.UnicodeSumnbol.blank, Translator.getIntl("added_on"), ProjectUtils.capitalize(dateString));
                    setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ACCOUNT,18,Paint.valueOf("#dddddd60")));
                    setText(text);

                    setTooltip(ProjectUtils.createTooltip(text));

                } else {
                    setText(null);
                    setGraphic(null);

                } ;
            }
        });




    }

    public void bindFields() {

        registerBtn.setOnAction(e->{
            System.out.println(mainStage.get());
            ActionStageLinker.openAddStudent(mainStage.get());
        });

        addSubject.setOnAction(e-> addSubjectHandler());

        registerClass.setOnAction(e -> addClassHandler());

        refreshBtn.setOnAction(e->refreshStatsHandler());



    }


    public void addSubjectHandler() {

            URL url = ResourceUtil.getAppResourceURL("views/others/add-subject.fxml");

            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Scene scene = new Scene(root);

            scene.getStylesheets().addAll(
                    ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainStage.get());
            stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
            stage.setResizable(false);

            AddSubjectController addSubjectController = fxmlLoader.getController();
            addSubjectController.thisStage.set(stage);

            ProjectUtils.applyDialogCaption(stage,addSubjectController.dragArea);

            stage.setTitle("PROMPT");
            stage.show();



//            Alert a = ProjectUtils.showAlert(mainStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
//            Translator.getIntl("data_updated"), ButtonType.OK);
//            a.showAndWait();




    }

    public void addClassHandler() {
        SettingsController settingsController = new SettingsController();
        settingsController.openClassWindow(false,mainStage.get());

    }

    public void addTeacherHandler() {

    }

    public void refreshStatsHandler() {

        for (VBox container:new VBox[]{
                entryController.studentClassoverview,entryController.recently_added,
                entryController.termdisplayview,entryController.tradeView,entryController.termdisplayview,

        })container.getChildren().clear();
        entryController.dashboradtimeItemshb.getChildren().clear();
        entryController.countsDisplayHb.getChildren().clear();

        Platform.runLater(this::buildCountsDisplay);
        Platform.runLater(this::buildCalendarTile);
        Platform.runLater(this::buildStudentchart);
        Platform.runLater(this::buildTradesChart);
        Platform.runLater(this::buildHoursChart);
        Platform.runLater(this::buildKTermInfoTile);
        Platform.runLater(this::buildRecentAddsView);

    }







}
