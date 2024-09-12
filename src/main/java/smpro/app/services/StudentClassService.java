package smpro.app.services;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.ResourceUtil;
import smpro.app.controllers.AddStudentController;
import smpro.app.custom_nodes.ClassSectionsTreeview;
import smpro.app.custom_nodes.CustomTableView;
import smpro.app.custom_nodes.CustomToolbarActionGroup;
import smpro.app.utils.*;

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
    CheckBox toggleAllcb;
    Button refreshBtn;
    CustomTextField searchFilter;
    Button deleteSelections;
    Button editSelection;





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


        toggleAllcb.setOnAction(e -> toggleSelectAll(toggleAllcb));

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


        //fetch data
//        String fetchAllStudents = """
//                select students.id,students.classid,
//                 students.firstname,students.lastname,students.gender,students.date_of_birth,students.matricule,
//                 students.parent_one,students.address,students.place_of_birth,classes.class_abbreviation,
//                students.admission_date,students.contact_one,students.repeater from students inner join classes on classes.id=students.classid order by firstname,lastname
//                """;

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

    public List<Node> buildToolbarOptions(){
        //register group
        CustomToolbarActionGroup registerGrou = new CustomToolbarActionGroup();
         registerBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/plus_green.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE, true)));
        registerGrou.addActions(ProjectUtils.capitalize(Translator.getIntl("register_student")), null, registerBtn);




        //selection_edit group
        CustomToolbarActionGroup selectionEditGroup = new CustomToolbarActionGroup();

         toggleAllcb = new CheckBox();
        selectionEditGroup.addActions(Translator.getIntl("select_all"),
                new ImageView(ResourceUtil.getImageFromResource("images/success.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)),toggleAllcb);

        //////////////////////////////////
         deleteSelections = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/remove_user.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("delete_selections")),
                null,deleteSelections);
        ////////////////////////////////

         searchFilter = new CustomTextField();
        searchFilter.setMaxWidth(150);
        searchFilter.setPromptText(Translator.getIntl("type_"));
        searchFilter.setRight(new ImageView(ResourceUtil.getImageFromResource("images/search.png", 18, 18, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("search")),
                null,searchFilter);



    //////////////////////////////
         editSelection= new Button("",new ImageView(ResourceUtil.getImageFromResource("images/edit.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("view_edit")),
                null,editSelection);
        editSelection.setId("images/edit.png");


        //print/export group
        CustomToolbarActionGroup printGroup = new CustomToolbarActionGroup();

        Button printcurrent = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/printer.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        printGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("print_list")),
                null,printcurrent);

        Button importExcel = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/excel_import.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        printGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("import_students_excel")),
                null,importExcel);

        Button exportexcel = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/excel2.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        printGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("export_excel")),
                null,exportexcel);

        Button generateMarksheet = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/dblist.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        printGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("generate_marksheet")),
                null,generateMarksheet);

        //refresh gorup
        CustomToolbarActionGroup refreshgroup = new CustomToolbarActionGroup();


         refreshBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/refresh.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE+15, true)));
        refreshgroup.addActions(ProjectUtils.capitalize(Translator.getIntl("refresh_display")),
                null,refreshBtn);
        refreshBtn.setStyle("-fx-background-color: transparent");


        for (Button b : new Button[]{registerBtn, generateMarksheet, exportexcel,deleteSelections, importExcel, printcurrent, editSelection,refreshBtn}) {
            b.setCursor(Cursor.HAND);
            b.setStyle("-fx-background-color: transparent;-fx-border-width: 0");

        }



        toolbarItems.add(registerGrou.build(1));
        for (CustomToolbarActionGroup group : new CustomToolbarActionGroup[]{ selectionEditGroup, printGroup,refreshgroup}) {
            toolbarItems.add(group.build(2));
        }




        return toolbarItems;



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

    public void toggleSelectAll(CheckBox toggle) {
        if (toggle.isSelected()) {
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

    }

    public void exportClasslistToPdf() {

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
                "WARNING", cleanedSelections.size()<=15 ? builder.toString(): Translator.getIntl("note_ireversible"), ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
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
        if (item==null|| item.get("id").equals("allitem")) {
            q = "select * from students order by firstname";
        } else {
            int cid = Integer.parseInt(PgConnector.getFielorBlank(studentSections.selectedClassProperty.get(), "id"));
            q = String.format("select * from students where classid=%d order by firstname", cid);
        }

        //filter table
        studentTableView.filter(q);




    }







}
