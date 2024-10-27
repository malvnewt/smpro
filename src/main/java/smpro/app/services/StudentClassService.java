package smpro.app.services;

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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;
import smpro.app.controllers.AddStudentController;
import smpro.app.controllers.GenericDialogController;
import smpro.app.custom_nodes.ClassSectionsTreeview;
import smpro.app.custom_nodes.CustomTableView;
import smpro.app.custom_nodes.CustomToolbarActionGroup;
import smpro.app.utils.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

public class StudentClassService {
    BooleanProperty isLoaded = new SimpleBooleanProperty(false);

    // instance vars
    private final VBox treeContainer;
    private final VBox tableContainer;
    private final Stage mainStage;


    public ClassSectionsTreeview studentSections = new ClassSectionsTreeview(true);
    public CustomTableView studentTableView;

    List<Node> toolbarItems = new ArrayList<>();

    //TOOLBAR CONTROLS
    Button registerBtn;
    RadioButton toggleAllcb;
    Button refreshBtn;
    CustomTextField searchFilter;
    Button deleteSelections;
    Button editClassBtn;
    Button editSelection;
    Button printClasslist;
    Button exportexcel;
    Button generateMarksheet;
    Button importExcel;
    Button transferBtn;


    //preperties

    public List<Node> buildToolbarOptions(){
        //register group
        CustomToolbarActionGroup registerGrou = new CustomToolbarActionGroup();
        registerBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/plus_green.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE, true)));
        registerGrou.addActions(ProjectUtils.capitalize(Translator.getIntl("register_student")), null, registerBtn);


        //selection_edit group
        CustomToolbarActionGroup selectionEditGroup = new CustomToolbarActionGroup();

        toggleAllcb = new RadioButton();
        selectionEditGroup.addActions(Translator.getIntl("select_all"),
                new ImageView(ResourceUtil.getImageFromResource("images/success.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)),toggleAllcb);


        transferBtn = new Button("");
        selectionEditGroup.addActions(Translator.getIntl("transfer_promote_short"),
                new ImageView(ResourceUtil.getImageFromResource("images/success.png",
                        Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)),transferBtn);

        //////////////////////////////////
        deleteSelections = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/remove_user.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("delete_selections")),
                null,deleteSelections);
        ////////////////////////////////

        editClassBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/edit_large.png",
                Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("class_settings")),
                null,editClassBtn);




        //////////////////////////////
        editSelection= new Button("",new ImageView(ResourceUtil.getImageFromResource("images/edit.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("view_edit")),
                null,editSelection);
        editSelection.setId("images/edit.png");

        ////////////////////////////////

        searchFilter = new CustomTextField();
        searchFilter.setMaxWidth(150);
        searchFilter.setPromptText(Translator.getIntl("type_"));
        searchFilter.setRight(new ImageView(ResourceUtil.getImageFromResource("images/search.png", 18, 18, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("search")),
                null,searchFilter);


        //print/export group
        CustomToolbarActionGroup printGroup = new CustomToolbarActionGroup();

        printClasslist = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/printer.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        printGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("print_list")),
                null,printClasslist);

        importExcel = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/excel_import.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        printGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("import_students_excel")),
                null,importExcel);

        exportexcel = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/excel2.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        printGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("export_excel")),
                null,exportexcel);

        generateMarksheet = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/dblist.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        printGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("generate_marksheet")),
                null,generateMarksheet);

        //refresh gorup
        CustomToolbarActionGroup refreshgroup = new CustomToolbarActionGroup();


        refreshBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/refresh.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE+15, true)));
        refreshgroup.addActions(ProjectUtils.capitalize(Translator.getIntl("refresh_display")),
                null,refreshBtn);
        refreshBtn.setStyle("-fx-background-color: transparent");


        for (Button b : new Button[]{editClassBtn,registerBtn, generateMarksheet, exportexcel,deleteSelections, importExcel, printClasslist, editSelection,refreshBtn,transferBtn}) {
            b.setCursor(Cursor.HAND);
            b.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
            b.getStyleClass().add("toolbar-btn");

        }



        toolbarItems.add(registerGrou.build(1));
        for (CustomToolbarActionGroup group : new CustomToolbarActionGroup[]{ selectionEditGroup, printGroup,refreshgroup}) {
            toolbarItems.add(group.build(2));
        }


        transferBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("transfer_promote")));


        return toolbarItems;



    }

    public StudentClassService(VBox treeContainer, VBox tableContainer, Stage s) {
        this.tableContainer =tableContainer;
        this.treeContainer = treeContainer;
        this.mainStage=s;

        initUi();

    }

    public void initUi() {
        loadTreeView();
        loadTable();

        isLoaded.set(true);
    }

    public void bindFields() {
        registerBtn.setOnAction(e->{
            ActionStageLinker.openAddStudent(mainStage);
        });


        toggleAllcb.setOnAction(e -> toggleSelectAll());

        refreshBtn.setOnAction(e -> refreshTable());

        searchFilter.textProperty().addListener((observableValue, s, t1) -> Platform.runLater(this::handleSearchFilter));

        deleteSelections.setOnAction(e->deleteSelections());

        editSelection.setOnAction(e -> {
            try {
                openUpdateView();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        printClasslist.setOnAction(e -> exportStudentListToPdf());

        editClassBtn.setOnAction(e->openUpdateClassview());

        generateMarksheet.setOnAction(e->exportMarksheetPdf());



    }


    public  void  loadTable(){
        TableColumn<HashMap<String, Object>, String> fnamecol =
                ProjectUtils.createTableColumn(Translator.getIntl("firstname").toUpperCase(), "firstname");

        TableColumn<HashMap<String, Object>, String> lnamecol = ProjectUtils.createTableColumn(Translator.getIntl("lastname").toUpperCase(), "lastname" );

        TableColumn<HashMap<String, Object>, String> gendercol = ProjectUtils.createTableColumnWithGraphic(Translator.getIntl("sex").toUpperCase(), "gender", o -> {
            FontIcon g;
            String gender =(String) o;
            if (gender.equalsIgnoreCase("M")) {
                g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_MALE, 12, Paint.valueOf("gray"));
            } else {
                g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_FEMALE, 12, Paint.valueOf("gray"));
            }
            return g;

        });

        TableColumn<HashMap<String, Object>, String> dobcol = ProjectUtils.createTableColumnWithGraphic(Translator.getIntl("dateofbirth").toUpperCase(), "date_of_birth",
                o -> ProjectUtils.createFontIcon(MaterialDesignC.CALENDAR, 12, Paint.valueOf("gray")));

        TableColumn<HashMap<String, Object>, String> matriculecol = ProjectUtils.createTableColumnWithGraphic(Translator.getIntl("matricule").toUpperCase(), "matricule",
                o -> ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_SMALL, 12, Paint.valueOf("gray")),true);

        TableColumn<HashMap<String, Object>, String> parentcol = ProjectUtils.createTableColumn(Translator.getIntl("parent_guardian").toUpperCase(), "parent_one" );
        TableColumn<HashMap<String, Object>, String> addresscol = ProjectUtils.createTableColumn(Translator.getIntl("address").toUpperCase(), "address" );
        TableColumn<HashMap<String, Object>, String> birthplacecol = ProjectUtils.createTableColumn(Translator.getIntl("placeofbirth").toUpperCase(), "place_of_birth" );
        TableColumn<HashMap<String, Object>, String> classnamecol = ProjectUtils.createTableColumn(Translator.getIntl("class").toUpperCase(), "class_abbreviation" ,true);
        TableColumn<HashMap<String, Object>, String> admissiondatecol = ProjectUtils.createTableColumn(Translator.getIntl("admission_date").toUpperCase(), "admission_date" );
        TableColumn<HashMap<String, Object>, String> contactcol = ProjectUtils.createTableColumn(Translator.getIntl("contact").toUpperCase(), "contact_one" );
        TableColumn<HashMap<String, Object>, String> repeatercol = ProjectUtils.createTableColumn(Translator.getIntl("repeater").toUpperCase(), "repeater" ,true);
        TableColumn<HashMap<String, Object>, String> idcol = ProjectUtils.createTableColumn("", "id" );

        List<TableColumn<HashMap<String, Object>, String>> studentcols =List.of(
                idcol, fnamecol, lnamecol, gendercol, matriculecol, birthplacecol, repeatercol, dobcol, admissiondatecol, addresscol, classnamecol, parentcol, contactcol
        );
        fnamecol.setMinWidth(120);
        lnamecol.setMinWidth(120);
        parentcol.setMinWidth(150);
        classnamecol.setMinWidth(60);
        gendercol.setMinWidth(60);


//        idcol.setMinWidth(50);
        idcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignS.SELECT, 15, Paint.valueOf("gray")));
        contactcol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PHONE, 15, Paint.valueOf("gray")));
        fnamecol.setGraphic(ProjectUtils.createFontIcon(MaterialDesignA.ACCOUNT, 15, Paint.valueOf("gray")));

        //set cell factories
        for (TableColumn<HashMap<String, Object>, String> col : new TableColumn[]{fnamecol, lnamecol, parentcol, addresscol,birthplacecol}) {
            col.setCellFactory(new Callback<>() {
                @Override
                public TableCell<HashMap<String, Object>, String> call(TableColumn<HashMap<String, Object>, String> hashMapStringTableColumn) {
                    return new TableCell<>() {
                        @Override
                        protected void updateItem(String s, boolean b) {
                            super.updateItem(s, b);
                            if (!b) {
                                setText(ProjectUtils.capitalize(s));
                            } else {
                                setText(null);
                                setGraphic(null);
                            }
                        }
                    };
                }
            });

        }

        for (TableColumn<HashMap<String, Object>, String> col : new TableColumn[]{ admissiondatecol, dobcol}) {
            col.setCellFactory(new Callback<>() {
                @Override
                public TableCell<HashMap<String, Object>, String> call(TableColumn<HashMap<String, Object>, String> hashMapStringTableColumn) {
                    return new TableCell<>() {
                        @Override
                        protected void updateItem(String s, boolean b) {
                            super.updateItem(s, b);
                            if (!b) {
                                String formateddate = ProjectUtils.getFormatedDate(Long.parseLong(s), DateFormat.getDateInstance(0));
                                setText(ProjectUtils.capitalize(formateddate));
                            } else {
                                setText(null);
                                setGraphic(null);
                            }
                        }
                    };
                }
            });

        }



        String fetchAllStudents = """
                select * from students order by firstname,lastname
                """;
        List<HashMap<String, Object>> allStudents = PgConnector.fetch(fetchAllStudents, PgConnector.getConnection());

        studentTableView = new CustomTableView(studentcols, 0);
        studentTableView.loadInitialData(allStudents);
        VBox.setVgrow(studentTableView, Priority.ALWAYS);

        tableContainer.getChildren().add(studentTableView);

        studentTableView.getColumns().remove(classnamecol);



    }

    public void loadTreeView() {
        treeContainer.getChildren().add(studentSections);
        // selection hanlder
        studentSections.selectedClassProperty.addListener((observableValue, hashMapTreeItem, data) -> {
                toggleAllcb.setSelected(false);

                //filter table
            String filterquery;

            String classid = PgConnector.getFielorBlank(data, "id");
            if (classid.equals("allitem")) {
                filterquery = "select * from students order by firstname";

            }else{
                filterquery = String.format("select * from students where classid=%d order by firstname", Integer.parseInt(classid));
            }



            studentTableView.filter(filterquery);

        });


    }


    public void handleSearchFilter() {
        String string = searchFilter.getText().toLowerCase();

        String q;
        String part = "%" + string + "%";
        HashMap<String,Object> item = studentSections.selectedClassProperty.get();
        if (item==null || item.get("id")=="allitem") {

            q = String.format("select * from students where firstname like '%s' or lastname like '%s' order by firstname,lastname ", part,part);
        } else {

            q = String.format("select * from students where firstname like '%s' or lastname like '%s' and classid=%d order by firstname,lastname ",
                    part, part, Integer.parseInt(PgConnector.getFielorBlank(studentSections.selectedClassProperty.get(), "id")));
        }

   studentTableView.filter(q);


    }

    public void toggleSelectAll() {
        if (toggleAllcb.isSelected()) {
            studentTableView.currentItemSelectorP.forEach(cb -> cb.setSelected(true));

        } else {
            studentTableView.currentItemSelectorP.forEach(cb -> cb.setSelected(false));


        }


    }

    public void importFromExcel() {

    }

    public void exportStudentListToExcel() {

    }

    public void exportStudentListToPdf() {

        List<String> selectedClasses = new ArrayList<>();
        List<String> selectedFields = new ArrayList<>();
        List<CheckBox> cbs = new ArrayList<>();

        HBox dlgContent = new HBox();
        dlgContent.setAlignment(Pos.CENTER_LEFT);
        dlgContent.setSpacing(10);
        VBox.setVgrow(dlgContent,Priority.ALWAYS);

        List<HashMap<String, Object>> sortedclasses = PgConnector.fetch("select * from classes order by level,classname", PgConnector.getConnection());

        List<CheckBox> classCbs = new ArrayList<>();

        GridPane pane = new GridPane();
        pane.setVgap(10);
        pane.setHgap(12);
        pane.setAlignment(Pos.CENTER_LEFT);
        ScrollPane scrollPane = new ScrollPane(pane);

        int classcount = sortedclasses.size();
        int colcount= 4;

        


        for (HashMap<String, Object> cls : sortedclasses) {

            String shortname = PgConnector.getFielorBlank(cls, "class_abbreviation");
            String fullname = PgConnector.getFielorBlank(cls, "classname");
            Number classid = PgConnector.getNumberOrNull(cls, "id");
            int studentCount = PgConnector.fetch(String.format("select * from students where classid=%d", classid.intValue()),PgConnector.getConnection()).size();

            HBox container = new HBox();
            container.setSpacing(5);
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPadding(new Insets(4));

            CheckBox cb = new CheckBox(shortname.toUpperCase());
            cb.setId(String.valueOf(classid));
            cb.setTooltip(ProjectUtils.createTooltip(ProjectUtils.capitalize(fullname)));
            cb.setStyle("-fx-font-weight: bold");
            cb.setMinWidth(80);
            cb.setMaxWidth(80);
            cb.selectedProperty().addListener((o,old,selected)->{
                if (selected) {
                    selectedClasses.add(cb.getId());
                } else {
                    selectedClasses.remove(cb.getId());
                }
            });

            cbs.add(cb);

            int index = sortedclasses.indexOf(cls);
            
            int row = index/colcount;
            int col = index % colcount;

//            Button plusBtn = new Button("",ProjectUtils.createFontIconColored(MaterialDesignP.PLUS,15,Paint.valueOf(Store.Colors.green)));
//            Button minusBtn = new Button("",ProjectUtils.createFontIconColored(MaterialDesignM.MINUS,15,Paint.valueOf(Store.Colors.White)));
//            plusBtn.setMaxSize(15,15);
//            minusBtn.setMaxSize(15,15);

            Label countLabel = new Label(String.valueOf(studentCount));
            countLabel.getStyleClass().add("text-bold");
            countLabel.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ACCOUNT, 15, Paint.valueOf("lightgray")));

//            plusBtn.setOnAction(e -> countLabel.setText(String.valueOf(Integer.parseInt(countLabel.getText()) + 1)));
//            minusBtn.setOnAction(e->{
//                int currentcount = Integer.parseInt(countLabel.getText());
//                if (currentcount > 0) {
//                    int newocount = currentcount -1;
//                    countLabel.setText(String.valueOf(newocount));
//                }
//
//            });


            container.setStyle("-fx-border-width: 1;-fx-border-color: " + Store.Colors.Gray);
//            container.getChildren().addAll(cb,ProjectUtils.createHspacer(10),minusBtn,countLabel,plusBtn);
            container.getChildren().addAll(cb,ProjectUtils.createHspacer(10),countLabel);

            pane.add(container, col, row);

        }

        VBox fieldsVb = new VBox();
        fieldsVb.setSpacing(5);
        fieldsVb.setPadding(new Insets(10));
        fieldsVb.setMinWidth(150);


        fieldsVb.setStyle("-fx-background-color: "+Store.Colors.drakula);

        List<String> fieldNames = List.of("firstname","lastname","gender","parent_one","matricule","date_of_birth","admission_date","trade","contact_one");

        for (String f : fieldNames) {
            int index = fieldNames.indexOf(f);
            CheckBox cb = new CheckBox(Translator.getIntl(f));
            cb.getStyleClass().add("text-bold");
            cb.setId(f);

            cb.selectedProperty().addListener((o,old,selected)->{
                if (selected) {
                    selectedFields.add(cb.getId());
                } else {
                    selectedFields.remove(cb.getId());
                }
            });

            if (index <= 5) {
                cb.setSelected(true);
                cb.setDisable(true);
            }

            fieldsVb.getChildren().add(cb);
        }


        dlgContent.getChildren().addAll(scrollPane, fieldsVb);
        HBox.setHgrow(scrollPane,Priority.ALWAYS);

        //
        
        //load the view
        FXMLLoader dlgLoader = new FXMLLoader(ResourceUtil.getAppResourceURL("views/dialog-view.fxml"),
                ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));

        try {
            Parent root = dlgLoader.load();

            Stage stage = new Stage();
            stage.initOwner(mainStage.getOwner());
            stage.initModality(Modality.APPLICATION_MODAL);

//            double contentwidth = pane.getWidth() + fieldsVb.getWidth();
//            double contentHeight = pane.getHeight();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            GenericDialogController controller = dlgLoader.getController();
            controller.dlgTitle.setText(Translator.getIntl("select_classes_fields"));
            controller.content.getChildren().add(dlgContent);
            controller.thisStage.set(stage);
            controller.confirmBtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PRINTER, 20, Paint.valueOf("white")));
            controller.confirmBtn.setText(Translator.getIntl("print"));

            controller.confirmBtn.setOnAction(e->controller.printClassLists(selectedClasses,selectedFields));

            //add other controls
            CheckBox toggleselect = new CheckBox(Translator.getIntl("toggle_select_all"));
            toggleselect.selectedProperty().addListener((o, old, selected) -> {
                if (selected) {
                    cbs.forEach(item -> item.setSelected(true));
                } else cbs.forEach(item -> item.setSelected(false));
            });
            controller.optionalTools.getChildren().add(toggleselect);


            ProjectUtils.applyDialogCaption(stage, controller.dragbox);

            stage.showAndWait();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void exportMarksheetPdf() {

        List<String> selectedClasses = new ArrayList<>();
        HashMap<Integer, Integer> cidTocountMap = new HashMap<>();


        List<CheckBox> cbs = new ArrayList<>();

        HBox dlgContent = new HBox();
        dlgContent.setAlignment(Pos.CENTER_LEFT);
        dlgContent.setSpacing(10);
        VBox.setVgrow(dlgContent,Priority.ALWAYS);

        List<HashMap<String, Object>> sortedclasses = PgConnector.fetch("select * from classes order by level,classname", PgConnector.getConnection());

        List<CheckBox> classCbs = new ArrayList<>();

        GridPane pane = new GridPane();
        pane.setVgap(10);
        pane.setHgap(12);
        pane.setAlignment(Pos.CENTER_LEFT);
        ScrollPane scrollPane = new ScrollPane(pane);

        int classcount = sortedclasses.size();
        int colcount= 3;




        for (HashMap<String, Object> cls : sortedclasses) {

            String shortname = PgConnector.getFielorBlank(cls, "class_abbreviation");
            String fullname = PgConnector.getFielorBlank(cls, "classname");
            Number classid = PgConnector.getNumberOrNull(cls, "id");
            int studentCount = PgConnector.fetch(String.format("select * from students where classid=%d", classid.intValue()),PgConnector.getConnection()).size();

            HBox container = new HBox();
            container.setSpacing(5);
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPadding(new Insets(4));

            CheckBox cb = new CheckBox(shortname.toUpperCase());
            Label countLabel = new Label("1");// One copy



            cb.setId(String.valueOf(classid));
            cb.setTooltip(ProjectUtils.createTooltip(ProjectUtils.capitalize(fullname)));
            cb.setStyle("-fx-font-weight: bold");
            cb.setMinWidth(80);
            cb.setMaxWidth(80);
            cb.selectedProperty().addListener((o,old,selected)->{
                if (selected) {

                    selectedClasses.add(cb.getId());
                    if (cidTocountMap.containsKey(classid.intValue())) {
                        cidTocountMap.replace(classid.intValue(), Integer.parseInt(countLabel.getText()));
                    } else {
                        cidTocountMap.put(classid.intValue(), Integer.parseInt(countLabel.getText()));
                    }

                } else {
                    selectedClasses.remove(cb.getId());
                    cidTocountMap.remove(classid.intValue());
                }
            });

            cbs.add(cb);

            int index = sortedclasses.indexOf(cls);

            int row = index/colcount;
            int col = index % colcount;

            Button plusBtn = new Button("",ProjectUtils.createFontIconColored(MaterialDesignP.PLUS,15,Paint.valueOf(Store.Colors.green)));
            Button minusBtn = new Button("",ProjectUtils.createFontIconColored(MaterialDesignM.MINUS,15,Paint.valueOf(Store.Colors.White)));
            plusBtn.setMaxSize(15,15);
            minusBtn.setMaxSize(15,15);

            countLabel.getStyleClass().add("text-bold");
            countLabel.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignA.ACCOUNT, 15, Paint.valueOf("lightgray")));

            plusBtn.setOnAction(e -> {
                countLabel.setText(String.valueOf(Integer.parseInt(countLabel.getText()) + 1));
                if (cidTocountMap.containsKey(classid.intValue())) {
                    cidTocountMap.replace(classid.intValue(), Integer.parseInt(countLabel.getText()));
                } else {
                    cidTocountMap.put(classid.intValue(), Integer.parseInt(countLabel.getText()));
                }

            });
            minusBtn.setOnAction(e->{
                int currentcount = Integer.parseInt(countLabel.getText());
                if (currentcount > 0) {
                    int newocount = currentcount -1;
                    countLabel.setText(String.valueOf(newocount));
                }

                if (cidTocountMap.containsKey(classid.intValue())) {
                    cidTocountMap.replace(classid.intValue(), Integer.parseInt(countLabel.getText()));
                } else {
                    cidTocountMap.put(classid.intValue(), Integer.parseInt(countLabel.getText()));
                }

            });


            container.setStyle("-fx-border-width: 1;-fx-border-color: " + Store.Colors.Gray);
            container.getChildren().addAll(cb,ProjectUtils.createHspacer(10),minusBtn,countLabel,plusBtn);
//            container.getChildren().addAll(cb,ProjectUtils.createHspacer(10),countLabel);

            pane.add(container, col, row);

        }



        dlgContent.getChildren().addAll(scrollPane);
        HBox.setHgrow(scrollPane,Priority.ALWAYS);

        //

        //load the view
        FXMLLoader dlgLoader = new FXMLLoader(ResourceUtil.getAppResourceURL("views/dialog-view.fxml"),
                ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));

        try {
            Parent root = dlgLoader.load();

            Stage stage = new Stage();
            stage.initOwner(mainStage.getOwner());
            stage.initModality(Modality.APPLICATION_MODAL);

//            double contentwidth = pane.getWidth() + fieldsVb.getWidth();
//            double contentHeight = pane.getHeight();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            GenericDialogController controller = dlgLoader.getController();
            controller.dlgTitle.setText(Translator.getIntl("set_classes_toprint"));
            controller.content.getChildren().add(dlgContent);
            controller.thisStage.set(stage);
            controller.confirmBtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PRINTER, 20, Paint.valueOf("white")));
            controller.confirmBtn.setText(Translator.getIntl("print"));


            controller.confirmBtn.setOnAction(e -> controller.printMarkSheets(
                    selectedClasses.stream().map(cid -> ProjectUtils.getObject(cid, "classes")).toList(),
                    selectedClasses.stream().map(c -> "").toList(),
                    selectedClasses.stream().map(c -> cidTocountMap.get(Integer.parseInt(c))).toList()
            ));

            //add other controls
            CheckBox toggleselect = new CheckBox(Translator.getIntl("toggle_select_all"));
            toggleselect.selectedProperty().addListener((o, old, selected) -> {
                if (selected) {
                    cbs.forEach(item -> item.setSelected(true));
                } else cbs.forEach(item -> item.setSelected(false));
            });
            controller.optionalTools.getChildren().add(toggleselect);


            ProjectUtils.applyDialogCaption(stage, controller.dragbox);

            stage.showAndWait();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    public void exportMarksheetExcel() {

    }

    public void exportClassListToExcel() {

    }
    public void openUpdateView() throws SQLException {
        HashMap<String, Object> selectStudent = studentTableView.getSelectionModel().getSelectedItem();

        if (selectStudent ==null) return;

      AddStudentController controller =  ActionStageLinker.openAddStudent(mainStage);

        controller.prepareUpdate(selectStudent);
        controller.confirmBtn.setOnAction(e-> {
            try {
                controller.updateStudent(PgConnector.getNumberOrNull(selectStudent, "id").intValue(), unused -> {
                    refreshTable();
                    return null;
                });
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });




    }

    public void openUpdateClassview() {

        HashMap<String, Object> selectedClass = studentSections.selectedClassProperty.get();
        if (!Objects.equals(null, selectedClass)) {
            SettingsController.openUpdateClassview(true, mainStage, selectedClass);
        }


    }

    public void deleteSelections() {
        List<String> selctedIds = studentTableView.currentSelectedIds.get();
        selctedIds = selctedIds.stream().filter(id -> !Objects.equals("null", id)).toList();
        Set<String> idSet = new HashSet<>(selctedIds);
        System.out.println(idSet);
        final List<String> cleanedSelections = idSet.stream().filter(id -> !id.isEmpty()).toList();
        if (cleanedSelections.isEmpty())return;

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%d %s\n", cleanedSelections.size(), Translator.getIntl("students")));

        for (String s : cleanedSelections) {
            HashMap<String, Object> sdata = PgConnector.getObjectFromId(Integer.parseInt(s),"students");
            assert sdata != null;
            builder.append(String.format("\t%s %s %s [ %s ]\n", Store.UnicodeSumnbol.bullet,ProjectUtils.capitalize( PgConnector
                            .getFielorBlank(sdata, "firstname")), ProjectUtils.capitalize(PgConnector.getFielorBlank(sdata, "lastname")),
                    PgConnector.getFielorBlank(sdata, "matricule")));
        }


        Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.WARNING, Translator.getIntl("do_you_delete"),
                "WARNING", cleanedSelections.size() <= 15 ? builder.toString() :
                        String.format("%s \n%d %s", Translator.getIntl("note_ireversible"), cleanedSelections.size(), Translator.getIntl("students")), ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
        Optional<ButtonType> res = a.showAndWait();
        res.ifPresent(b->{
            if (Objects.equals(b, ButtonType.YES)) {
                for (String itemid : cleanedSelections) {
                    String del = String.format("delete from students where id=%d", Integer.parseInt(itemid));
                    PgConnector.update(del);


                }
                refreshTable();

            }
        });



    }



    public void refreshTable() {
        toggleAllcb.setSelected(false);
        String q ;
        HashMap<String,Object> item = studentSections.selectedClassProperty.get();

        if (Objects.equals(null,item)) {
            q = "select * from students order by firstname";

        }else {
            if (Objects.equals("allitem", item.get("id"))) {
                q = "select * from students order by firstname";

            } else {

                try {

                    int cid = Integer.parseInt(PgConnector.getFielorBlank(studentSections.selectedClassProperty.get(), "id"));
                    q = String.format("select * from students where classid=%d order by firstname", cid);
                } catch (NumberFormatException nerr) {
                    q = "select * from students order by firstname";

                }
            }
        }

        //filter table
        studentTableView.filter(q);
        studentSections.reloadTree();
        studentSections.getSelectionModel().select(studentSections.selectedItemP.get());




    }







}
