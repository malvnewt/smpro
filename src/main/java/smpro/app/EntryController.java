package smpro.app;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.StatusBar;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.services.DashboardService;
import smpro.app.services.EmployeeService;
import smpro.app.services.MarksheetService;
import smpro.app.services.StudentClassService;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

public class EntryController implements Initializable {


    public VBox windowTopvb;
    public MenuBar menubar;
    public ToolBar maintoolbar;
    public SplitPane mainsp;
    public AnchorPane menupane;
    public AnchorPane contentpane;
    public Label setings_label;
    public Button settingsBtn;
    public Button menuBtn;
    public HBox menuexpanhb;
    public Label expane_menu;
    public VBox menuitemsVb;
    public HBox settingsContainer;



    public  ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public BorderPane borderPane;
    public Menu graphicMenu;
    public MenuItem graphicCloseMEnu;
    public Menu titleMenuelement;
    public MenuItem titlemenuitem;
    public Tab dashboardTab;
    public Tab teacherstab;
    public Tab studentTab;
    public Tab marksheettab;
    public Tab timetabletab;
    public Tab hrtab;
    public Tab reportstab;
    public Tab messagestab;
    public Tab diciplinetab;
    public Tab libtab;
    public Tab suveillancetab;
    public TabPane mainContentTabpane;
    public ScrollPane sidemenuScrollpane;
    public SplitPane studentsSplitPane;
    public VBox studentSectionsContainer;
    public VBox studentsTableContainer;
    public AnchorPane studentTreePane;
    public HBox datelocalehb;
    public VBox dateTile;
    public HBox localehb;
    public Label currentLocLabel;
//    public ComboBox<Locale> localescombo;
    public HBox countsDisplayHb;
    public VBox studentClassoverview;
    public VBox monthly_finance;
    public VBox perclass_finance;
    public VBox recently_added;
    public VBox evaluationProgres;
    public Label langlable;
    public VBox tradeView;
    public Button prevTimes;
    public HBox dashboradtimeItemshb;
    public Button nextTimes;
    public HBox timetableview;
    public Label dashboardPeriodTime;
    public ComboBox<Integer> dashboadPeriodCombo;
    public ScrollPane timeItemsScrollpane;
    public VBox termdisplayview;
    public Button editTermibtn;
    public ListView<HashMap<String,Object>> dashboardRecentLv;
    public AnchorPane dashboardPane;
    public ImageView empview;
    public Label employeeNames;
    public Label empUsername;
    public Label empPass;
    public Label empdepartment;
    public VBox allocationsVb;
    public Button opentimtableBtn;
    public Button print_marksheets;
    public GridPane detailsGrid;
    public VBox empTablecontainer;
    public Button collapseStaffDetails;
    public AnchorPane empdetailspane;
    public AnchorPane emptablepane;
    public SplitPane empSplitpane;
    public Button assign_subject;
    public VBox examsLeftvb;
    public VBox examsclasstreeContainer;
    public VBox examsFilterContainer;
    public ComboBox<Integer> examsTermconbo;
    public ComboBox<Integer> examsevalcombo;
    public Button examCopyscoresbtn;
    public Button examAddBtn;
    public Button examAssignBtn;
    public Button examimportBtn;
    public Button examdeleteBtn;
    public VBox examsTableContainer;
    public HBox examsConfirmContainer;
    public Label examsStatsLabel;
    public Button examsSaveMarksBtn;
    public AnchorPane examleftpane;
    public VBox examsubContainer;
    public Label employeeDepartmentLabel;


    List<String> featureNames = Store.appFeatures;

    public HashMap<String, String> features = new HashMap<>(Map.of(
            "dashboard","dashboard3.png",
//            "students","students.png",
            "teachers","teachers.png",
            "classes_menu","students3.png",
            "score_records","marksheet.png",
            "timetable","timetable2.png",
            "human_resource","hr4.png",
            "reports_transcripts","reports3.png",
            "announcement","messages2.png"
    ));

    List<Tab> contentTabs = new ArrayList<>();

    List<HBox> menuContainers = new ArrayList<>();


    // PROPERTY BINDINGS
    BooleanProperty menuIsExpanded = new SimpleBooleanProperty(false);



    ObjectProperty<Node> selectedFeatureProperty = new SimpleObjectProperty<>();


    StatusBar statusBar = new StatusBar();




    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    ////////////////   SERVICE INSTANCE VARIABLES ///////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////

    StudentClassService studentClassService;


    ////////////////////////    tab callbacks
    HashMap<Integer, List<Node>> featureToolbarMap = new HashMap<> ();

    HashMap<Integer, Callback<Object, Void>> tabToViewCallbackMap = new HashMap<>(
            Map.of(
                    0, o -> {
                        try {
                            initDashboarService();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }


                        return null;
                    },
                    1, o -> {
                        try {
                            initEmployeeService();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    },
                    2, o -> {
                        try {
                            initStudentService();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }


                        return null;
                    },
                    3, o -> {
                        try {
                            initMarksheetService();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }


                        return null;
                    }


            )
    );


    HashMap<Integer, Boolean> builtViewsMap = new HashMap<> ();




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        features.put("dicipline_conduct", "discipline1.png");
        features.put("library", "library2.png");
        features.put("suiveillance", "camera3.png");

        maintoolbar.setPadding(new Insets(0,10,5,10));

        contentTabs.addAll(List.of(dashboardTab, teacherstab, studentTab, hrtab, marksheettab,
                timetabletab, reportstab, messagestab, diciplinetab, libtab, suveillancetab)
        );

        configureUi();
        configureStatusBar();
        handleSidemenuSelection();


        // init dashboard as auto selected tab
        try {
            initDashboarService();
            maintoolbar.getItems().addAll(featureToolbarMap.get(0));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


    public void configureUi() {
//        ProjectUtils.animatePaneSide(menupane,'w', Store.MENU_COLLAPSE_WIDTH);
        menupane.setMaxWidth(Store.MENU_COLLAPSE_WIDTH);
        employeeDepartmentLabel.setText("Dept\u0020\u0020 \u27A1 \u0020");

        graphicMenu.setText("");
        graphicMenu.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/logo-server.png",30,30,true)));

        //menutitle
        HashMap<String, Object> base = PgConnector.fetch("select * from base", PgConnector.getConnection()).get(0);

        String mainTitle = String.format("%s %s %s %s%s",
                PgConnector.getFielorBlank(base, "school_name").toUpperCase(),
                Store.UnicodeSumnbol.blank,
                PgConnector.getFielorBlank(base, "academic_year").toUpperCase(),
                Store.UnicodeSumnbol.blank,
                Store.UnicodeSumnbol.blank
//                ProjectUtils.getFormatedDate(new Date().getTime(), DateFormat.getDateInstance(0, Translator.getLocale()))

        );
        titleMenuelement.setText(mainTitle);
        titleMenuelement.setStyle("-fx-text-fill:lightgray;-fx-font-weight:bold");
        titleMenuelement.setDisable(true);


        graphicCloseMEnu.setGraphic(ProjectUtils.createFontIcon(MaterialDesignE.EXIT_TO_APP,15,Paint.valueOf(Store.Colors.Gray)));
        graphicCloseMEnu.setAccelerator(new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_DOWN,KeyCombination.SHIFT_DOWN));
        graphicCloseMEnu.setOnAction(e->{System.exit(0);});
        graphicCloseMEnu.setStyle("-fx-text-fill:gray;opacity:0.8;");


//        graphicMenu.setDisable(true);



        // set menu and setting icons
        menuBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MENU, 70, Paint.valueOf("gray")));

        menuBtn.setOnAction(e -> {
            ProjectUtils.shakeX(menuBtn, -5).play();

            if (menuIsExpanded.get()) {
                menupane.setMinWidth(Store.MENU_COLLAPSE_WIDTH);
                menuBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MENU, 70, Paint.valueOf("gray")));

                ProjectUtils.animatePaneSide(menupane, 'w', Store.MENU_COLLAPSE_WIDTH);
            } else {
                menupane.setMaxWidth(Store.MENU_EXPAND_WIDTH);
                ProjectUtils.animatePaneSide(menupane, 'w', Store.MENU_EXPAND_WIDTH);
                menuBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MENU_OPEN, 70, Paint.valueOf("gray")));

            }
            menuIsExpanded.set(!menuIsExpanded.get());

        });

        settingsBtn.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/menu_icons/setting2.png", 50, 50)));
        settingsBtn.setStyle("-fx-border-width: 0;-fx-background-color: transparent");
        menuBtn.setStyle("-fx-border-width: 0;-fx-background-color: transparent");

        settingsBtn.setOnAction(e -> {

            try {
                ProjectUtils.openSettings(0,thisStage.get());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ProjectUtils.shakeX(settingsContainer, -5).play();

        });


        // seetings and menu containers

        for (HBox h : new HBox[]{menuexpanhb, settingsContainer}) {
            h.addEventHandler(MouseEvent.MOUSE_ENTERED, this::handleMouseEnter);
            h.addEventHandler(MouseEvent.MOUSE_EXITED, this::handleMouseLeave);
        }
          menuexpanhb.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
          settingsContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleSettingsPressed);

        //////////////////////
        insertMenuItems();
        selectedFeatureProperty.addListener((observableValue, oldnode, newnode) -> {
            newnode.setStyle("-fx-background-color: "+Store.Colors.selectionBg);

            if (!Objects.equals(null,oldnode))oldnode.setStyle("-fx-background-color: transparent");

            int currentNodeIndex  =menuContainers.indexOf((HBox) newnode);
            System.out.println("current node index  "+ currentNodeIndex);

            mainContentTabpane.getSelectionModel().select(currentNodeIndex);



        });


    }


    public void insertMenuItems() {
        menuitemsVb.setSpacing(5);

        for (String featureName : featureNames) {

            int index = featureNames.indexOf(featureName);

            Label fLabel = new Label(Translator.getIntl(featureName).toUpperCase());
            fLabel.setStyle("-fx-font-weight: bold;-fx-font-family: Consolas");

            Button fButton = new Button();
            fButton.setStyle("-fx-border-width: 0;-fx-background-color: transparent");
            fButton.setMinSize(50, 50);
            fButton.setMaxSize(50, 50);


            String filename = features.get(featureName);

            ImageView buttonImageview = new  ImageView(ResourceUtil.getImageFromResource("images/menu_icons/" + filename, 50, 50));

            fButton.setGraphic(buttonImageview);
            fButton.setTooltip(ProjectUtils.createTooltip(Translator.getIntl(featureName).toUpperCase()));
            fButton.setCursor(Cursor.HAND);

            // add grayscale effect
            ColorAdjust grayscale = new ColorAdjust();
            grayscale.setSaturation(0);
            buttonImageview.setEffect(grayscale);


            HBox hb = new HBox(fButton,fLabel);
            hb.setSpacing(20);
            hb.setAlignment(Pos.CENTER_LEFT);
//            hb.setPadding(new Insets(10));
            hb.setId(featureName);
            hb.getStyleClass().add("items-background");
            hb.setMinHeight(60);

//            hb.setEffect(dropshadowEffect);

            hb.addEventHandler(MouseEvent.MOUSE_ENTERED,this::handleMouseEnter);
            hb.addEventHandler(MouseEvent.MOUSE_EXITED,this::handleMouseLeave);

            hb.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                ProjectUtils.shakeX(hb, -5).play();
                selectedFeatureProperty.set(hb);
//                mainContentTabpane.getSelectionModel().select(menuContainers.indexOf(hb));
            });

            fButton.setOnAction(e-> {
                ProjectUtils.shakeX(hb, -5).play();
                selectedFeatureProperty.set(hb);

            });

            if (index==0){
                selectedFeatureProperty.set(hb);
                hb.setStyle("-fx-background-color: "+Store.Colors.selectionBg);
            }

            menuitemsVb.getChildren().add(hb);
            menuContainers.add(hb);


        }


    }


    public void configureStatusBar() {
        statusBar.setProgress(0);
        borderPane.setBottom(statusBar);
        statusBar.setText("");
        statusBar.setStyle("-fx-background-color: #373737");
        statusBar.getStylesheets().add(ResourceUtil.getAppResourceURL("css/recaf/all.css").toExternalForm());
        statusBar.getStyleClass().add("progress-bar");


        String curUsername = PgConnector.getFielorBlank(Store.AuthUser.get(), "displayName");
        TextFlow tf = new TextFlow();
        Text t1 = new Text(Translator.getIntl("logged_inas")+Store.UnicodeSumnbol.blank);
        Text tuser = new Text(Store.UnicodeSumnbol.atSymbole+ProjectUtils.capitalize(curUsername));
        tuser.setStyle("-fx-font-weight: bold;-fx-text-fill: #eeeeee90");
        t1.setStyle("-fx-text-fill: #eeeeee70");
        tf.getChildren().addAll(t1, tuser);
        tf.setTextAlignment(TextAlignment.CENTER);
        tf.setStyle("-fx-padding: 6px 0 0 0");

        HBox loginInfoBox = new HBox(new ImageView(ResourceUtil.getImageFromResource("images/lock.png", 18, 18, true)), tf);
        loginInfoBox.setSpacing(7);
        loginInfoBox.setAlignment(Pos.CENTER_LEFT);
        loginInfoBox.setPadding(new Insets(0,5,0,30));

        ProgressBar pbar = new ProgressBar();
        pbar.setProgress(100);

        ///////////////////////////////////
        ///////  CONNECTION ANIMAITON ////
        ///////////////////////////////////

        FontIcon dbIcon = ProjectUtils.createFontIcon(MaterialDesignD.DATABASE_CHECK, 18, Paint.valueOf(Store.Colors.green));

        Label connectionlabel = new Label((PgConnector.getFielorBlank(Store.currentProjectProperty.get(), "name").toUpperCase()));
        connectionlabel.setEffect(new SepiaTone());
        connectionlabel.setStyle("-fx-padding: 0 10px 0 15px;-fx-font-weight: bold;-fx-font-size: 15;-fx-text-fill: "+Store.Colors.green);
        connectionlabel.setGraphic(dbIcon);

        FadeTransition t = new FadeTransition(Duration.millis(800),connectionlabel);
        t.setFromValue(0.2);
        t.setToValue(1);
        t.setCycleCount(Animation.INDEFINITE);
        t.setInterpolator(Interpolator.EASE_IN);
        t.playFromStart();


        Label appLable = new Label(String.format("SMPRO %s %s2021",Store.UnicodeSumnbol.blank, Store.UnicodeSumnbol.andCopy));
        appLable.setStyle("-fx-font-weight: bold;-fx-text-fill:#eeeeee90;-fx-font-size:  12px;-fx-font-family: 'Bodoni MT Black';-fx-padding: 0 5px 0 15px");

        Button notficationButton = new Button("", ProjectUtils.createFontIcon(MaterialDesignM.MESSAGE_ALERT, 12, Paint.valueOf(Store.Colors.lightestGray)));
        Button meButton = new Button("", ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT, 12, Paint.valueOf(Store.Colors.lightestGray)));


        for (Button b : new Button[]{meButton, notficationButton}) {
            b.getStyleClass().removeAll("ikon-font-icon");
            b.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
            b.setCursor(Cursor.HAND);
        }


        ImageView me = new ImageView(ResourceUtil.getImageFromResource("images/me.png", 300, 300, true));
        Label meinfo = new Label(Translator.getIntl("meinfo"));
        meinfo.setMaxWidth(300);
        meinfo.setWrapText(true);
        meinfo.setStyle("-fx-text-fill:rgb(35, 35, 35);-fx-font-weight: bold;-fx-padding: 5px");

        VBox meBox = new VBox(me, meinfo);
        meBox.setSpacing(5);

        meButton.setOnAction(e -> {
            PopOver p = ProjectUtils.showPopover("", meBox, PopOver.ArrowLocation.BOTTOM_LEFT, false, true);
            p.show(meButton);
        });

        HBox myrightItems = new HBox(pbar,connectionlabel,appLable);
        myrightItems.setSpacing(5);
        myrightItems.setAlignment(Pos.CENTER_LEFT);

        HBox myleftitem = new HBox(meButton, notficationButton,loginInfoBox);
        myleftitem.setSpacing(5);
        myleftitem.setAlignment(Pos.CENTER_LEFT);

        statusBar.getRightItems().addAll(myrightItems);
        statusBar.getLeftItems().addAll(myleftitem);



    }



    public void handleSidemenuSelection() {
        mainContentTabpane.getTabs().forEach(t -> t.setText(""));

        dashboardTab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignD.DESKTOP_MAC_DASHBOARD, 25, Paint.valueOf("gray")));
        teacherstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TEACH, 25, Paint.valueOf("gray")));
        studentTab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SCHOOL, 25, Paint.valueOf("gray")));
        hrtab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CREDIT_CARD, 25, Paint.valueOf("gray")));
        reportstab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PAGE_LAYOUT_BODY, 25, Paint.valueOf("gray")));
        marksheettab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignN.NUMERIC, 25, Paint.valueOf("gray")));
        libtab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignB.BOOK_EDUCATION, 25, Paint.valueOf("gray")));
        messagestab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MESSAGE_REPLY, 25, Paint.valueOf("gray")));
        suveillancetab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CAMERA_REAR, 25, Paint.valueOf("gray")));
        diciplinetab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignW.WHISTLE, 25, Paint.valueOf("gray")));
        timetabletab.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TIMER_SAND, 25, Paint.valueOf("gray")));




        mainContentTabpane.getSelectionModel().selectedIndexProperty().addListener((observableValue, tab, newindex) -> {
            //heighlight hbox
            HBox sideMenuHb = menuContainers.get(newindex.intValue());
            if (!Objects.equals(sideMenuHb,selectedFeatureProperty.get())) selectedFeatureProperty.set(sideMenuHb);

            //build view
            if ( Objects.equals(null,builtViewsMap.get(newindex.intValue()))) {
                System.out.println("BUILDING TAB ="+contentTabs.get(newindex.intValue()).getId());
                tabToViewCallbackMap.get(newindex.intValue()).call(null);
                builtViewsMap.put(newindex.intValue(), true);

            }
            maintoolbar.getItems().clear();
            maintoolbar.getItems().addAll(featureToolbarMap.get(mainContentTabpane.getSelectionModel().getSelectedIndex()));
        });



    }



    public void handleMouseEnter(MouseEvent event) {
        HBox source = (HBox) event.getSource();
        source.setStyle("-fx-background-color: "+Store.Colors.hoverbg);

    }

    public void handleMouseLeave(MouseEvent event) {
        HBox source = (HBox) event.getSource();

        if (!Objects.equals(source.getId(), selectedFeatureProperty.get().getId())) source.setStyle("--fx-background-color: transparent");
        if (Objects.equals(source.getId(), selectedFeatureProperty.get().getId())) source.setStyle("-fx-background-color: "+Store.Colors.selectionBg);

    }

    public void handleMousePressed(MouseEvent event) {
        HBox source = (HBox) event.getSource();

        ProjectUtils.shakeX(source, -5).play();



        menupane.setMinWidth(Store.MENU_COLLAPSE_WIDTH);
        ProjectUtils.animatePaneSide(menupane, 'w', Store.MENU_COLLAPSE_WIDTH);
        menuBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignM.MENU, 70, Paint.valueOf("gray")));
        menuIsExpanded.set(false);

    }


    public void handleSettingsPressed(MouseEvent event) {
//        Animations.pulse(source).play();

        try {
            ProjectUtils.openSettings(0,thisStage.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ProjectUtils.shakeX(settingsContainer, -5).play();


    }


    public void refreshApp() {

    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////                            ///////////////////////////////////////
    ///////////////////////////////////////     FEATURE HANDLERS      /////////////////////////////////////////
    ///////////////////////////////////////                          //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////



    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////   STUDENT | CLASS HANDLER    //////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public void initStudentService() throws SQLException {
        studentClassService = new StudentClassService(
                studentSectionsContainer,
                studentsTableContainer,
                thisStage.get()
                );
        studentTreePane.setMaxWidth(300);

        //build and add toolbar to map
        List<Node> studentToolbarItems = studentClassService.buildToolbarOptions();
        studentClassService.bindFields();

        // student
        featureToolbarMap.put(mainContentTabpane.getSelectionModel().getSelectedIndex(), studentToolbarItems);







    }



    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////   DASHBOARD HANDLER    //////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public void initDashboarService() throws SQLException {
        DashboardService dashboardService = new DashboardService(thisStage.get(),this);

        //build and add toolbar to map
        List<Node> dashboardToolbarActions = dashboardService.buildToolbarOptions();
        dashboardService.bindFields();

        featureToolbarMap.put(0, dashboardToolbarActions);
        builtViewsMap.put(0, true);

        thisStage.addListener((observableValue, stage, s) -> {
            if (!Objects.equals(s,null)) dashboardService.mainStage.set(s);
        });

        // student







    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////   EMPLOYEE HANDLER     ////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public void initEmployeeService() throws SQLException {
        EmployeeService employeeService = new EmployeeService(thisStage.get(),this);

        //build and add toolbar to map
        List<Node> empToolbarActions = employeeService.buildToolbarOptions();
        employeeService.bindFields();

        featureToolbarMap.put(mainContentTabpane.getSelectionModel().getSelectedIndex(), empToolbarActions);

//        Platform.runLater(()->{
//            List<Node> empToolbarActions = employeeService.buildToolbarOptions();
//            employeeService.bindFields();
//
//            featureToolbarMap.put(mainContentTabpane.getSelectionModel().getSelectedIndex(), empToolbarActions);
//
//            maintoolbar.getItems().clear();
//            maintoolbar.getItems().addAll(featureToolbarMap.get(mainContentTabpane.getSelectionModel().getSelectedIndex()));
//
//        });





    }


    /////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////   MARKSHEET HANDLER     ////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public void initMarksheetService() throws SQLException {
        MarksheetService marksheetService = new MarksheetService(thisStage.get(),this);

            List<Node> marksheettbActions = marksheetService.buildToolbarOptions();
            marksheetService.bindFields();
            featureToolbarMap.put(mainContentTabpane.getSelectionModel().getSelectedIndex(), marksheettbActions);

            maintoolbar.getItems().clear();
            maintoolbar.getItems().addAll(featureToolbarMap.get(mainContentTabpane.getSelectionModel().getSelectedIndex()));
            marksheetService.loadTable();

            List<HashMap<String,Object>> accessibleData =marksheetService.getAcceissibleData();

        try {
            marksheetService.studentRecordSearch.setItems(FXCollections.observableList(accessibleData));
        } catch (Exception err) {
            err.printStackTrace();
        }






    }




}