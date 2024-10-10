package smpro.app.services;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import org.postgresql.jdbc.PgArray;
import smpro.app.Entry;
import smpro.app.EntryController;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;
import smpro.app.controllers.AddStudentController;
import smpro.app.controllers.GenericDialogController;
import smpro.app.controllers.ListDisplayController;
import smpro.app.custom_nodes.ClassSectionsTreeview;
import smpro.app.custom_nodes.CustomTableView;
import smpro.app.custom_nodes.CustomToolbarActionGroup;
import smpro.app.utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

public class EmployeeService {
    BooleanProperty isLoaded = new SimpleBooleanProperty(false);

    // instance vars
    private final Stage mainStage;
    EntryController entryController;


    public CustomTableView empTableview;

    List<Node> toolbarItems = new ArrayList<>();

    //TOOLBAR CONTROLS
    Button registerBtn;
    CheckBox toggleAllcb;
    Button refreshBtn;
    CustomTextField searchFilter;
    Button deleteSelections;

    Button printStafflist;
    Button editSelection;


    ObjectProperty<HashMap<String, Object>> selectEmpP = new SimpleObjectProperty<>();

    //preperties

    public EmployeeService(Stage s,EntryController ec) {
        this.entryController =ec;
        this.mainStage=s;

        initUi();
//        bindFields();

    }

    public List<Node> buildToolbarOptions(){
        //register group
        CustomToolbarActionGroup registerGrou = new CustomToolbarActionGroup();
        registerBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/add_user.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE, true)));
        registerGrou.addActions(ProjectUtils.capitalize(Translator.getIntl("new_employee")), null, registerBtn);




        //selection_edit group
        CustomToolbarActionGroup selectionEditGroup = new CustomToolbarActionGroup();

        toggleAllcb = new CheckBox();
        selectionEditGroup.addActions(Translator.getIntl("select_all"),
                new ImageView(ResourceUtil.getImageFromResource("images/success.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE,
                        true)),toggleAllcb);

        //////////////////////////////////
        deleteSelections = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/remove_user.png",
                Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("delete_selections")),
                null,deleteSelections);


        editSelection = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/edit_user.png",
                Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("edit_teacher")),
                null,editSelection);
        ////////////////////////////////


        searchFilter = new CustomTextField();
        searchFilter.setMaxWidth(150);
        searchFilter.setPromptText(Translator.getIntl("type_"));
        searchFilter.setRight(new ImageView(ResourceUtil.getImageFromResource("images/kfind.png", 18, 18, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("search")),
                null,searchFilter);



        //////////////////////////////

        printStafflist = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/printer.png",
                Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("print_stafflist")),
                null,printStafflist);


        //refresh gorup
        CustomToolbarActionGroup refreshgroup = new CustomToolbarActionGroup();


        refreshBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/refresh.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE+15, true)));
        refreshgroup.addActions(ProjectUtils.capitalize(Translator.getIntl("refresh_display")),
                null,refreshBtn);
        refreshBtn.setStyle("-fx-background-color: transparent");


        for (Button b : new Button[]{registerBtn,editSelection,deleteSelections,refreshBtn,printStafflist}) {
            b.setCursor(Cursor.HAND);
            b.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
            b.getStyleClass().add("toolbar-btn");

        }



        toolbarItems.add(registerGrou.build(1));
        toolbarItems.add(selectionEditGroup.build(2));
        toolbarItems.add(refreshgroup.build(1));



        return toolbarItems;

    }

    public void initUi() {
        loadTable();
        isLoaded.set(true);

        //
        entryController.print_marksheets.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PRINTER, 18, Paint.valueOf(Store.Colors.LightGray)));
        entryController.collapseStaffDetails.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ARROW_RIGHT_BOLD, 18, Paint.valueOf(Store.Colors.LightGray)));
    }

    public void bindFields() {
        registerBtn.setOnAction(e->{
            ActionStageLinker.openAddEmployee(mainStage);
            refreshTable();
        });

        editSelection.setOnAction(e->{
            HashMap<String, Object> selectedEmp = empTableview.getSelectionModel().getSelectedItem();
            if (Objects.equals(selectedEmp,null))return;

            ActionStageLinker.openAddEmployee(mainStage,selectedEmp);
            refreshTable();
        });


        toggleAllcb.setOnAction(e -> toggleSelectAll(toggleAllcb));

        refreshBtn.setOnAction(e -> refreshTable());

        searchFilter.textProperty().addListener((observableValue, s, t1) -> Platform.runLater(this::handleEmpFilter));

        deleteSelections.setOnAction(e->deleteSelections());

        printStafflist.setOnAction(e -> exportStaffListToPdf());

        entryController.collapseStaffDetails.setOnAction(e->{
            //animate details width to0
            entryController.empdetailspane.setMinWidth(0);
            ProjectUtils.animatePaneSide(entryController.empdetailspane, 'w', 0);
        });

        empTableview.getSelectionModel().selectedItemProperty().addListener((observableValue, stringObjectHashMap, newval) -> {
            selectEmpP.set(newval);
            entryController.empdetailspane.setMaxWidth(450);
            ProjectUtils.animatePaneSide(entryController.empdetailspane, 'w', 450);
            try {
                fillStaffDetails();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });


        entryController.empdetailspane.setMinWidth(0);
        ProjectUtils.animatePaneSide(entryController.empdetailspane, 'w', 0);




    }


    public  void  loadTable(){


        TableColumn<HashMap<String, Object>, String> idcol = ProjectUtils.createTableColumn("", "id" );

        TableColumn<HashMap<String, Object>, String> namescol =
                ProjectUtils.createTableColumn(Translator.getIntl("employee_name").toUpperCase(), "first_lastname");

        TableColumn<HashMap<String, Object>, String> timefcol =
                ProjectUtils.createTableColumn(Translator.getIntl("time_scope").toUpperCase(), "time_factor");

        TableColumn<HashMap<String, Object>, String> empCat = ProjectUtils.createTableColumn(Translator.getIntl("category").toUpperCase(), "employee_category" );
        TableColumn<HashMap<String, Object>, String> contactcol = ProjectUtils.createTableColumn(Translator.getIntl("contact").toUpperCase(), "contact" );
        TableColumn<HashMap<String, Object>, String> addresscol = ProjectUtils.createTableColumn(Translator.getIntl("address").toUpperCase(), "address" );

        TableColumn<HashMap<String, Object>, String> usernamecol = ProjectUtils.createTableColumn(Translator.getIntl("username").toUpperCase(), "username" );
        TableColumn<HashMap<String, Object>, String> passcol = ProjectUtils.createTableColumn(Translator.getIntl("password").toUpperCase(), "password" );

        TableColumn<HashMap<String, Object>, String> departcol = ProjectUtils.createTableColumn(Translator.getIntl("department").toUpperCase(), "department");
        TableColumn<HashMap<String, Object>, String> gendercol = ProjectUtils.createTableColumnWithGraphic(Translator.getIntl("gender").toUpperCase(), "gender", o -> {
            FontIcon g;
            String gender =(String) o;
            if (gender.equalsIgnoreCase("M")) {
                g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_MALE, 12, Paint.valueOf("gray"));
            } else {
                g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_FEMALE, 12, Paint.valueOf("gray"));
            }
            return g;

        });

        TableColumn<HashMap<String, Object>, String> dateaddedcol = ProjectUtils.createTableColumnWithGraphic(Translator.getIntl("added_on").toUpperCase(), "date_added",
                o -> ProjectUtils.createFontIcon(MaterialDesignC.CALENDAR, 12, Paint.valueOf("gray")));

        List<TableColumn<HashMap<String, Object>, String>> empcols =List.of(
                idcol, namescol, empCat,timefcol,departcol,contactcol, dateaddedcol,addresscol,gendercol,usernamecol,passcol
        );
        namescol.setMinWidth(150);
//        departcol.setMinWidth(100);
//        empCat.setMinWidth(100);
        timefcol.setMinWidth(100);
//        gendercol.setMinWidth(60);

//
        idcol.setMinWidth(50);
        idcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SELECT, 15, Paint.valueOf("gray")));
        namescol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT, 15, Paint.valueOf("gray")));
        contactcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PHONE, 15, Paint.valueOf("gray")));
        passcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignK.KEY, 15, Paint.valueOf("gray")));
        contactcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PHONE, 15, Paint.valueOf("gray")));

        // custom cellvalueFActories
        dateaddedcol.setCellValueFactory(data -> {
            HashMap<String, Object> val = data.getValue();
            long epochtime = Long.parseLong(String.valueOf(val.get("date_added")));
            return new SimpleStringProperty(ProjectUtils.getFormatedDate(epochtime, DateFormat.getDateInstance(DateFormat.MEDIUM, Translator.getLocale())));
        });

        timefcol.setCellValueFactory(data -> {
            HashMap<String, Object> val = data.getValue();
            return new SimpleStringProperty(Translator.getIntl(PgConnector.getFielorBlank(val, "time_factor")));
        });

        namescol.setCellValueFactory(data -> {
            HashMap<String, Object> val = data.getValue();
            return new SimpleStringProperty(ProjectUtils.getShortPersonName(PgConnector.getFielorBlank(val, "first_lastname"), 3));
        });


        //set cell factories
        for (TableColumn<HashMap<String, Object>, String> col : new TableColumn[]{namescol, addresscol, empCat,departcol,timefcol,dateaddedcol}) {
            col.setCellFactory(new Callback<>() {
                @Override
                public TableCell<HashMap<String, Object>, String> call(TableColumn<HashMap<String, Object>, String> hashMapStringTableColumn) {
                    return new TableCell<>() {
                        @Override
                        protected void updateItem(String s, boolean b) {
                            super.updateItem(s, b);
                            if (!b) {
                                setText(ProjectUtils.capitalize(s));
                                setTooltip(ProjectUtils.createTooltip(ProjectUtils.capitalize(s)));
                            } else {
                                setText(null);
                                setGraphic(null);
                            }
                        }
                    };
                }
            });

        }

       //fetch data
        String fetchEmps = """
                select * from employees order by employee_category,time_factor,first_lastname
                """;

        List<HashMap<String, Object>> allStaff = PgConnector.fetch(fetchEmps, PgConnector.getConnection());

        empTableview = new CustomTableView(empcols, 0);
        empTableview.loadInitialData(allStaff);
        VBox.setVgrow(empTableview, Priority.ALWAYS);
        entryController.empTablecontainer.getChildren().clear();
        entryController.empTablecontainer.getChildren().add(empTableview);



    }

    public void toggleSelectAll(CheckBox toggle) {
        if (toggle.isSelected()) {
            empTableview.currentItemSelectorP.forEach(cb -> cb.setSelected(true));

        } else {
            empTableview.currentItemSelectorP.forEach(cb -> cb.setSelected(false));


        }


    }

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////


    public void exportStaffListToPdf() {
        List<HashMap<String, Object>> sortedEmps = PgConnector.fetch("select * from employees order by employee_category, first_lastname", PgConnector.getConnection());

        List<String> fieldNames = List.of("first_lastname","employee_category","department","gender","time_factor","username","date_added","contact");

        new DocumentBase(ResourceUtil.getStystemFilePath(mainStage,
                List.of(new FileChooser.ExtensionFilter("CHOOSE FILE", "*.pdf")))).buildEmployeeList(sortedEmps, fieldNames);






    }

    public void handleEmpFilter() {
        String string = searchFilter.getText().toLowerCase();

        String q;
        String part = "%" + string + "%";
        q = String.format("select * from employees where first_lastname like '%s' or department like '%s' order by first_lastname ",
                part, part);

        empTableview.filter(q);


    }


    public void deleteSelections() {

        List<String> selctedIds = empTableview.currentSelectedIds.get();
        selctedIds = selctedIds.stream().filter(id -> !Objects.equals("null", id)).toList();
        Set<String> idSet = new HashSet<>(selctedIds);
        System.out.println(idSet);

        final List<String> cleanedSelections = idSet.stream().filter(id -> !id.isEmpty()).toList();
        if (cleanedSelections.isEmpty())return;

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%d %s\n", cleanedSelections.size(), Translator.getIntl("employees")));

        for (String s : cleanedSelections) {
            HashMap<String, Object> sdata = PgConnector.getObjectFromId(Integer.parseInt(s),"employees");
            assert sdata != null;
            builder.append(String.format("\t%s %s %s [ %s ]\n", Store.UnicodeSumnbol.bullet,ProjectUtils.capitalize( PgConnector
                            .getFielorBlank(sdata, "first_lastname")), "",
                    PgConnector.getFielorBlank(sdata, "username")));
        }


        Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.WARNING, Translator.getIntl("do_you_delete"),
                "WARNING", cleanedSelections.size()<=15 ? builder.toString(): Translator.getIntl("note_ireversible"), ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
        Optional<ButtonType> res = a.showAndWait();
        res.ifPresent(b->{
            if (Objects.equals(b, ButtonType.YES)) {
                for (String itemid : cleanedSelections) {
                    String del = String.format("delete from employees where id=%d", Integer.parseInt(itemid));
                    PgConnector.update(del);


                }
                refreshTable();

            }
        });



    }

    public void fillStaffDetails(int... expandedTitledPaneIndex) throws SQLException {
        entryController.allocationsVb.setOpacity(0);
        FadeTransition f = new FadeTransition(Duration.millis(200), entryController.allocationsVb);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);

        entryController.allocationsVb.getChildren().clear();
        entryController.empPass.setText("-----");
        entryController.employeeNames.setText("--------------------");
        entryController.empUsername.setText("-----");
        entryController.empview.setImage(null);

        HashMap<String, Object> selectedEmp = empTableview.getSelectionModel().getSelectedItem();
        String empName = PgConnector.getFielorBlank(selectedEmp, "first_lastname");
        if (Objects.equals(null, selectedEmp)) return;

        String names = PgConnector.getFielorBlank(selectedEmp, "first_lastname");
        String department = PgConnector.getFielorBlank(selectedEmp, "department").isEmpty() ? Translator.getIntl("unset") : PgConnector.getFielorBlank(selectedEmp, "department");
        String username = PgConnector.getFielorBlank(selectedEmp, "username");
        String passw = PgConnector.getFielorBlank(selectedEmp, "password");
        int empId = PgConnector.getNumberOrNull(selectedEmp, "id").intValue();

        entryController.empUsername.setText(username);
        entryController.empPass.setText(passw);
        entryController.employeeNames.setText(names.toUpperCase());
        entryController.empdepartment.setText(ProjectUtils.capitalize(department));

        entryController.empPass.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignK.KEY, 12, Paint.valueOf(Store.Colors.LightGray)));
        entryController.empUsername.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ACCOUNT, 12, Paint.valueOf(Store.Colors.LightGray)));

        for (Label l : new Label[]{entryController.empdepartment, entryController.empUsername, entryController.empPass, entryController.employeeNames})
            l.setTooltip(ProjectUtils.createTooltip(l.getText().toUpperCase()));

        //fill image
        Image img = ResourceUtil.getImageFromResource("images/44image.png", (int) entryController.empview.getFitWidth(),
                (int) entryController.empview.getFitHeight(), true);
        try {
            PreparedStatement ps = PgConnector.getConnection().prepareStatement("select display_image from employees where id=?");
            ps.setInt(1, empId);
            InputStream is = PgConnector.readBinarydata(ps);
            if (!Objects.equals(null, is)) {
                img = new Image(is);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        entryController.empview.setImage(img);


        // set permissions
        List<HashMap<String, Object>> empPerms = PgConnector.fetch(String.format("select * from permissions where teacher='%s'", empName.toLowerCase()), PgConnector.getConnection());
        ResultSet empPermResultset = PgConnector.getConnection().
                prepareStatement(String.format("select * from permissions where teacher='%s'", empName.toLowerCase())).executeQuery();

        entryController.allocationsVb.setAlignment(Pos.TOP_LEFT);

        if (empPerms.isEmpty()) {
            entryController.allocationsVb.setAlignment(Pos.CENTER);
            entryController.allocationsVb.getChildren().add(new Label(Translator.getIntl("no_classes_assigned").toUpperCase()));
        } else {

            Accordion accordion = new Accordion();
            entryController.allocationsVb.getChildren().add(accordion);

            List<String> subjects = PgConnector.aggregatePgArray(empPermResultset, "subjects");

            for (String sub : subjects) {
                String subAbbr = PgConnector.getFielorBlank(PgConnector.getObjectFromKey("subject_name", sub, "subjects"), "subject_code");


                // get classes
                empPermResultset = PgConnector.getConnection().
                        prepareStatement(String.format("select * from permissions where teacher='%s'", empName.toLowerCase())).executeQuery();

                List<String> classesForSubject = PgConnector.getAggregatedListIfValueInPgArray(empPermResultset, "subjects", "classid", sub);
                List<HashMap<String, Object>> sortedClassObjects = classesForSubject.stream().map(s->PgConnector.getObjectFromId(Integer.parseInt(s),"classes")).sorted(
                        (o1, o2) -> PgConnector.getNumberOrNull(o1, "level").intValue() > PgConnector.getNumberOrNull(o2, "level").intValue() ? 1 : -1
                ).toList();



                TitledPane tp = new TitledPane();
                tp.setText(sub.toUpperCase() + String.format(" \t ( %s )", subAbbr.toUpperCase()));
                tp.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CIRCLE_MULTIPLE_OUTLINE, 20, Paint.valueOf("lightgray")));
                accordion.getPanes().add(tp);
                tp.getStyleClass().addAll("alt-icon", "elevated-2", "dense");
                tp.setCursor(Cursor.HAND);

                // add contents of the tp
                VBox contentContainer = new VBox();
                contentContainer.setSpacing(10);
                contentContainer.setPadding(new Insets(5));


                for (HashMap<String,Object> clsObj : sortedClassObjects) {
//                    HashMap<String, Object> clsObj = PgConnector.getObjectFromId(Integer.parseInt(classid), "classes");
                    int classid = PgConnector.getNumberOrNull(clsObj,"id").intValue();

                    String classname = PgConnector.getFielorBlank(clsObj, "classname");
                    String classabr = PgConnector.getFielorBlank(clsObj, "class_abbreviation");

                    Label contentClassLabel = new Label(String.format("%s \t( %s ) ", classname.toUpperCase(), classabr.toUpperCase()));
                    contentClassLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12px");
                    contentClassLabel.setId(String.valueOf(classid));
                    contentClassLabel.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ARROW_RIGHT_BOLD, 18, Paint.valueOf(Store.Colors.LightGray)));

                    contentContainer.getChildren().add(contentClassLabel);
                }
                //add edit button
                    contentContainer.getChildren().add(ProjectUtils.createVspacer(20));
                    Button editButton = new Button(Translator.getIntl("add_remove"));
                    editButton.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PENCIL, 25, Paint.valueOf(Store.Colors.LightGray)));

                    HBox buttonContainer = new HBox(editButton);
                    buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                    contentContainer.getChildren().add(buttonContainer);
                    tp.setContent(contentContainer);


                editButton.setOnAction(e -> {
                        // open vertical list and populate with the classees
                        URL url = ResourceUtil.getAppResourceURL("views/others/list-popup.fxml");
                        FXMLLoader loader = new FXMLLoader(url);
                        loader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));

                        try {
                            Parent root = loader.load();

                            Stage stage = new Stage();
                            stage.setTitle("PROMPT");
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.initModality(Modality.WINDOW_MODAL);
                            stage.initOwner(mainStage);
                            stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
                            stage.setResizable(false);

                            PopOver classesPop = ProjectUtils.showPopover("", root, PopOver.ArrowLocation.RIGHT_CENTER, false, true);


                            ListDisplayController listDisplayController = loader.getController();
                            listDisplayController.title.setText(sub.toUpperCase());

                            List<String> classNames = PgConnector.listHashAttrs(PgConnector.
                                    fetch("select * from classes order by level,classname", PgConnector.getConnection()), "classname");

                            listDisplayController.loadDataItems(classNames, sortedClassObjects.stream().map(o -> PgConnector.getFielorBlank(o, "classname")).toList());

                            List<String> initialSelectedClasses = listDisplayController.dataItems.stream().filter(CheckBox::isSelected).map(CheckBox::getId).sorted().toList();
                            listDisplayController.confirmBtn.setOnAction(ev->{
                                List<String> selectedClasses = listDisplayController.dataItems.stream().filter(CheckBox::isSelected).map(CheckBox::getId).sorted().toList();

                                List<String> removedClasses = initialSelectedClasses.stream().filter(c -> !selectedClasses.contains(c)).toList();
                                List<String> maintainedClasses = initialSelectedClasses.stream().filter(selectedClasses::contains).toList();
                                List<String> addedClasses = selectedClasses.stream().filter(c -> !initialSelectedClasses.contains(c)).toList();
                                System.out.println("removed classes "+removedClasses);
                                System.out.println("added classes "+addedClasses);
                                System.out.println("maintained classes "+maintainedClasses);

                                for (String cname : removedClasses) {
                                    try {
                                        int classid = PgConnector.getNumberOrNull(PgConnector.getObjectFromKey("classname",cname,"classes"),"id").intValue();
                                        PreparedStatement getClasspermStatement = PgConnector.getConnection().prepareStatement("select * from permissions where classid=?");
                                        getClasspermStatement.setInt(1, classid);

                                        ResultSet classRs = getClasspermStatement.executeQuery();
                                        if (classRs.next()) {
                                            //class exist so update "subjects arr"
                                            List<String> currentSubs = PgConnector.parsePgArray(classRs, "subjects");
                                            List<String> updatedList = currentSubs.stream().filter(s -> !s.equals(sub)).toList();
                                            updatePerms(updatedList,classid,empName);
                                        }

                                    } catch (SQLException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }

                                for (String cname : addedClasses) {

                                        int classid = PgConnector.getNumberOrNull(PgConnector.getObjectFromKey("classname",cname,"classes"),"id").intValue();
                                            //add class perm
                                    try {
                                        addPerm(List.of(sub.toLowerCase()), classid, empName);
                                    } catch (SQLException ex) {
                                        throw new RuntimeException(ex);
                                    }

                                }
//                                for (String cname : maintainedClasses) {} // Do nothing

                                classesPop.hide();

                            });

                            listDisplayController.closetP.setOnAction(ev -> classesPop.hide());
                            classesPop.show(tp);

                            classesPop.setOnHiding(hev -> {
                                // update the detail view
                                try {
                                    fillStaffDetails(accordion.getPanes().indexOf(tp));

                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            });

                        } catch (Exception err) {
                            throw new RuntimeException(err);
                        }





                });

            }

            if (expandedTitledPaneIndex.length > 0) {
                accordion.setExpandedPane(accordion.getPanes().get(expandedTitledPaneIndex[0]));
            } else {
                accordion.setExpandedPane(accordion.getPanes().get(0));
            }

        }

            f.playFromStart();
    }


    public void updatePerms(List<String> newsublist, int classid,String teacher) throws SQLException {
//        int classid = PgConnector.getNumberOrNull(PgConnector.getObjectFromKey("classname",classname,"classes"),"id").intValue();

        PreparedStatement updatePermStatement = PgConnector.getConnection().prepareStatement("update permissions set subjects=? where classid=? and teacher=?");

        updatePermStatement.setArray(1, PgConnector.getConnection().createArrayOf("TEXT", newsublist.toArray()));
        updatePermStatement.setInt(2, classid);
        updatePermStatement.setString(3, teacher);

        System.out.println(updatePermStatement);
        updatePermStatement.executeUpdate();

    }
    public void addPerm(List<String> sublist, int classid,String empName) throws SQLException {
//        int classid = PgConnector.getNumberOrNull(PgConnector.getObjectFromKey("classname",classname,"classes"),"id").intValue();

        boolean exists = !PgConnector.fetch(String.format("select * from permissions where classid=%d and teacher='%s'", classid, empName), PgConnector.getConnection()).isEmpty();
        if (exists) {
            updatePerms(sublist, classid, empName);
            return;
        }




        PreparedStatement insertPerm = PgConnector.getConnection().prepareStatement("insert into permissions (teacher,classid,subjects) values (?,?,?)");

        insertPerm.setString(1, empName.toLowerCase());
        insertPerm.setInt(2, classid);
        insertPerm.setArray(3, PgConnector.getConnection().createArrayOf("TEXT", sublist.toArray()));

        System.out.println(insertPerm);
        insertPerm.executeUpdate();

    }

    public void refreshTable() {
        toggleAllcb.setSelected(false);
        searchFilter.setText("");

        empTableview.getSelectionModel().clearSelection();
        entryController.empdetailspane.setMinWidth(0);
        ProjectUtils.animatePaneSide(entryController.empdetailspane, 'w', 0);

        String q  = "select * from employees order by employee_category,time_factor,first_lastname";

        //filter table
        empTableview.filter(q);

    }







}
