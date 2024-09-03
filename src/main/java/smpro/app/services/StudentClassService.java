package smpro.app.services;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.ResourceUtil;
import smpro.app.custom_nodes.ClassSectionsTreeview;
import smpro.app.custom_nodes.CustomTableView;
import smpro.app.custom_nodes.CustomToolbarActionGroup;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentClassService {
    BooleanProperty isLoaded = new SimpleBooleanProperty(false);

    // instance vars
    private final VBox treeContainer;
    private final VBox tableContainer;


    public TreeView<HashMap<String,Object>> studentSections = new ClassSectionsTreeview();
    public CustomTableView studentTableView;

    List<Node> toolbarItems = new ArrayList<>();


    public StudentClassService(VBox treeContainer, VBox tableContainer) {
        this.tableContainer =tableContainer;
        this.treeContainer = treeContainer;

        initUi();

    }

    public void initUi() {
        loadTreeView();
        loadTable();

        isLoaded.set(true);
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
        String fetchAllStudents = """
                select students.firstname,students.lastname,students.gender,students.date_of_birth,students.matricule,students.parent_one,students.address,students.place_of_birth,classes.class_abbreviation,
                students.admission_date,students.contact_one,students.repeater from students inner join classes on classes.id=students.classid order by firstname,lastname
                """;
        List<HashMap<String, Object>> allStudents = PgConnector.fetch(fetchAllStudents, PgConnector.getConnection());

        studentTableView = new CustomTableView(studentcols, 0);
        studentTableView.loadInitialData(allStudents);
        VBox.setVgrow(studentTableView, Priority.ALWAYS);

        tableContainer.getChildren().add(studentTableView);



    }

    public void loadTreeView() {
        treeContainer.getChildren().add(studentSections);
    }

    public void buildToolbarOptions(){
        //register group
        CustomToolbarActionGroup registerGrou = new CustomToolbarActionGroup("");
        Button registerBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/plus.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        registerGrou.addActions(ProjectUtils.capitalize(Translator.getIntl("register_student")), null, registerBtn);

        //selection_edit group
        CustomToolbarActionGroup selectionEditGroup = new CustomToolbarActionGroup(Translator.getIntl("select_edit"));

        CheckBox toggleAllcb = new CheckBox();
        selectionEditGroup.addActions(Translator.getIntl("select_all"),
                new ImageView(ResourceUtil.getImageFromResource("images/success.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)),toggleAllcb);

        FontIcon deleteIcon = ProjectUtils.createFontIcon(MaterialDesignT.TRASH_CAN, Store.TOOBAR_ICONSIZE, Paint.valueOf(Store.Colors.red));
//        Button deleteSelections = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/success.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true));
        Button deleteSelections = new Button("", deleteIcon);
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("delete_selections")),
                null,deleteSelections);

        CustomTextField searchFilter = new CustomTextField();
        searchFilter.setRight(ProjectUtils.createFontIcon(MaterialDesignA.ALPHABETICAL, 12, Paint.valueOf("gray")));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("search")),
                new ImageView(ResourceUtil.getImageFromResource("images/search.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)),searchFilter);

        Button editSelection = new Button("",new ImageView(ResourceUtil.getImageFromResource("images/edit_user.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("view_edit")),
                null,editSelection);


        Button tranferAction = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/arrow_move.png", Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("transfer_promote")),
               null,tranferAction);


        //print/export group
        CustomToolbarActionGroup printGroup = new CustomToolbarActionGroup(Translator.getIntl("print_import"));

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


        toolbarItems.add(registerGrou.build(1));
        for (CustomToolbarActionGroup group : new CustomToolbarActionGroup[]{ selectionEditGroup, printGroup}) {
            toolbarItems.add(group.build(2));
        }








    }

    public  void filterTable() {

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


}
