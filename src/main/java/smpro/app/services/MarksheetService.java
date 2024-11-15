package smpro.app.services;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.Entry;
import smpro.app.EntryController;
import smpro.app.ResourceUtil;
import smpro.app.controllers.GenericDialogController;
import smpro.app.controllers.SingleScoreViewController;
import smpro.app.custom_nodes.CustomTableView;
import smpro.app.custom_nodes.CustomToolbarActionGroup;
import smpro.app.custom_nodes.SubjectsClassesTreeview;
import smpro.app.tableadapter.EditableTableFx;
import smpro.app.utils.*;

import javax.swing.text.PlainDocument;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

public class MarksheetService{

    // instance vars
    private final Stage mainStage;
    EntryController entryController;
    List<Node> toolbarItems = new ArrayList<>();

    SubjectsClassesTreeview classesTreeview = new SubjectsClassesTreeview();
    CustomTableView scoreEntryView;
    BooleanProperty isloaded = new SimpleBooleanProperty(false);
    SearchableComboBox<HashMap<String, Object>> examsSubjectcombo = new SearchableComboBox<>();

    ObjectProperty<HashMap<String, String>> mp = new SimpleObjectProperty<>(new HashMap<>());
    StringProperty statP = new SimpleStringProperty();


    // node items
    Button printMarksheet;
    Button importExcelScores;
    Button genExcelSheetBtn;
    Button printAllLRecordsBtn;
    Button refreshBtn;
    Button syncBtn;
    Button deleteSelectedScoresBtn;
    public SearchableComboBox<HashMap<String,Object>> studentRecordSearch;
    TableColumn<HashMap<String, Object>, String> scorecol;


    PopOver p;
    boolean isAdminUser = Boolean.parseBoolean(PgConnector.getFielorBlank(Store.AuthUser.get(), "isAdmin"));

    public MarksheetService(Stage s,EntryController ec) {
        this.entryController =ec;
        this.mainStage=s;


        initUi();
//        bindFields();

    }



    private void initUi() {

        entryController.examsTableContainer.getChildren().add(ProjectUtils.getTablePlaceholder());

        HashMap<String, Object> basedata = PgConnector.fetch("select * from base",PgConnector.getConnection()).get(0);
        List<HashMap<String, Object>> dbSubjects = PgConnector.fetch("select * from subjects order by subject_name", PgConnector.getConnection());

        entryController.examsclasstreeContainer.getChildren().clear();
        entryController.examsclasstreeContainer.getChildren().add(classesTreeview);


        entryController.examCopyscoresbtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CONTENT_COPY, 30, Paint.valueOf(Store.Colors.LightGray)));
        entryController.examdeleteBtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignD.DELETE, 30, Paint.valueOf(Store.Colors.LightGray)));
        entryController.examimportBtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignI.IMPORT, 30, Paint.valueOf(Store.Colors.LightGray)));
        entryController.examAddBtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PLUS_MINUS, 30, Paint.valueOf(Store.Colors.LightGray)));
        entryController.examAssignBtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignS.SET_ALL, 30, Paint.valueOf(Store.Colors.LightGray)));
        entryController.examsSaveMarksBtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CHECK_BOLD, 30, Paint.valueOf(Store.Colors.LightGray)));

        //tooltips
        entryController.examCopyscoresbtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("copy_scorestp")));
        entryController.examdeleteBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("delete_scorestp")));
        entryController.examimportBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("import_scorestp")));
        entryController.examAssignBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("assign_scorestp")));
        entryController.examAddBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("add_scorestp")));

        // set the subjects rreeview
        entryController.examsclasstreeContainer.getChildren().clear();
        entryController.examsclasstreeContainer.getChildren().add(classesTreeview);

        // fill static data
        examsSubjectcombo.getItems().clear();
        examsSubjectcombo.getItems().addAll(dbSubjects);
        examsSubjectcombo.getStyleClass().addAll("dense", "bordered","text-bold");

        setSubjectComboCellFactory();
        classesTreeview.selectedSubjectProperty.addListener((o, f, l) -> setSubjectComboCellFactory());

        examsSubjectcombo.setButtonCell( new ListCell<>(){
            @Override
            protected void updateItem(HashMap<String, Object> stringObjectHashMap, boolean b) {
                super.updateItem(stringObjectHashMap, b);
                if (!b) {
                    setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_MULTIPLE,30,Paint.valueOf(Store.Colors.White)));

                    setText(String.format("%s ", PgConnector.getFielorBlank(stringObjectHashMap, "subject_abbreviation")));



                } else {
                    setGraphic(null);
                    setText(null);

                }
            }
        });

        entryController.examsubContainer.getChildren().add(examsSubjectcombo);

        entryController.examsTermconbo.getItems().addAll(List.of(1, 2, 3));
        entryController.examsevalcombo.getItems().addAll(List.of(1, 2, 3, 4, 5, 6));


        entryController.examsTermconbo.getSelectionModel().selectedItemProperty().addListener((observableValue, integer, newval) -> {
            entryController.examsevalcombo.getItems().clear();
            entryController.examsevalcombo.getItems().addAll(Store.termToSeqsMap.get(newval.intValue()));
            entryController.examsevalcombo.setValue(Store.termToSeqsMap.get(newval.intValue()).get(0));


        });

        entryController.examsTermconbo.setValue(PgConnector.getNumberOrNull(basedata,"current_term").intValue());

        // marks table
        buildMarkForm();




    }

    public void setSubjectComboCellFactory() {
        examsSubjectcombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<HashMap<String, Object>> call(ListView<HashMap<String, Object>> hashMapListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(HashMap<String, Object> stringObjectHashMap, boolean b) {
                        super.updateItem(stringObjectHashMap, b);
                        if (!b) {
                            setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE, 30, Paint.valueOf(Store.Colors.White)));



                            String subjectName = PgConnector.getFielorBlank(stringObjectHashMap, "subject_name");
                            String subjectAbbr = PgConnector.getFielorBlank(stringObjectHashMap, "subject_abbreviation");
                            HashMap<String, Object> cobj = classesTreeview.selectedSubjectProperty.get();

                            if (!isAdminUser) {
                                setText(String.format("%s ( %s ) ", ProjectUtils.capitalize(subjectName)
                                        ,subjectAbbr));
                            } else {
                                try {
                                    setText(String.format("%s (%s) %s%s %s %s %s ",
                                            ProjectUtils.capitalize(subjectName),
                                            subjectAbbr,
                                            Store.UnicodeSumnbol.blank,
                                            Store.UnicodeSumnbol.blank,

                                            Objects.equals(null, cobj) ? "" : Translator.getIntl("instructor"),
                                            Objects.equals(null, cobj) ? "" : Store.UnicodeSumnbol.rightArrow,
                                            Objects.equals(null, cobj) ? "" :
                                                    ProjectUtils.getTeacherForSubject(subjectName, PgConnector.getNumberOrNull(cobj, "id").intValue()).toUpperCase()
                                    ));
                                } catch (SQLException e) {
                                    setText(String.format("%s ( %s ) ", ProjectUtils.capitalize(subjectName)
                                            ,subjectAbbr));
                                    throw new RuntimeException(e);
                                }

                            }


                        } else {
                            setGraphic(null);
                            setText(null);

                        }
                    }
                };
            }
        });


    }

    public List<Node> buildToolbarOptions(){
        //register group
        CustomToolbarActionGroup registerGrou = new CustomToolbarActionGroup();
        syncBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/internet.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE, true)));
        registerGrou.addActions(ProjectUtils.capitalize(Translator.getIntl("sync_data")), null, syncBtn);
        syncBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("sync_datatp")));

        //selection_edit group
        CustomToolbarActionGroup selectionEditGroup = new CustomToolbarActionGroup();

        //////////////////////////////////
        printAllLRecordsBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/printer_laser.png",
                Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("exam_records")),
                null,printAllLRecordsBtn);

        deleteSelectedScoresBtn = new Button("", ProjectUtils.createFontIconColored(MaterialDesignT.TRASH_CAN, Store.TOOBAR_ICONSIZE, Paint.valueOf(Store.Colors.red)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("delete_selections")),
                null,deleteSelectedScoresBtn);
        deleteSelectedScoresBtn.setStyle("-fx-background-color: transparent;-fx-border-width: 0");

        //////////////////////////////////////////////


        importExcelScores = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/excel_import.png",
                Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("import_scores")),
                null,importExcelScores);
        importExcelScores.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("import_scorestp")));



        printMarksheet = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/printer.png",
                Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("print_marksheets")),
                null,printMarksheet);



        //////////////////////////////

        genExcelSheetBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/excel.png",
                Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("excel_form")),
                null,genExcelSheetBtn);

        ////////////////////////////////


        studentRecordSearch = new SearchableComboBox<>();

        studentRecordSearch.setMinWidth(250);
        studentRecordSearch.setMaxWidth(250);
        studentRecordSearch.setPromptText(Translator.getIntl("search_student"));
        studentRecordSearch.setCursor(Cursor.TEXT);

        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("search")),
                null,studentRecordSearch);

        studentRecordSearch.setCellFactory(hashMapListView -> new ListCell<>(){
            @Override
            protected void updateItem(HashMap<String, Object> stringObjectHashMap, boolean b) {
                super.updateItem(stringObjectHashMap, b);
                if (!b) {
                    setText(ProjectUtils.capitalize(String.format("%s %s %s (%s)",
                            PgConnector.getFielorBlank(stringObjectHashMap, "firstname"),
                            PgConnector.getFielorBlank(stringObjectHashMap, "lastname"),
                            Store.UnicodeSumnbol.rightArrow,
                            PgConnector.getFielorBlank(PgConnector.getObjectFromKey("id",
                                            PgConnector.getFielorBlank(stringObjectHashMap, "classid"), "classes"),
                                    "class_abbreviation")
                    )));
                    setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CIRCLE_SMALL, 15, Paint.valueOf(Store.Colors.LightGray)));

                } else {
                    setText(null);
                    setGraphic(null);
                } ;
            }
        });

        studentRecordSearch.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(HashMap<String, Object> stringObjectHashMap, boolean b) {
                super.updateItem(stringObjectHashMap, b);
                if (!b) {
                    setText(ProjectUtils.capitalize(String.format("%s %s %s (%s)",
                            PgConnector.getFielorBlank(stringObjectHashMap,"firstname"),
                            PgConnector.getFielorBlank(stringObjectHashMap,"lastname"),
                            Store.UnicodeSumnbol.rightArrow,
                            PgConnector.getFielorBlank(stringObjectHashMap,"matricule")
                    )));
                    setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CIRCLE_SMALL, 15, Paint.valueOf(Store.Colors.LightGray)));

                } else {
                    setText(null);
                    setGraphic(null);
                } ;
            }
        });


        //refresh gorup
        CustomToolbarActionGroup refreshgroup = new CustomToolbarActionGroup();


        refreshBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/refresh.png", Store.TOOBAR_ICONSIZE+15, Store.TOOBAR_ICONSIZE+15, true)));
        refreshgroup.addActions(ProjectUtils.capitalize(Translator.getIntl("refresh_display")),
                null,refreshBtn);
        refreshBtn.setStyle("-fx-background-color: transparent");


        for (Button b : new Button[]{genExcelSheetBtn,printAllLRecordsBtn,printMarksheet,refreshBtn,syncBtn,importExcelScores}) {
            b.setCursor(Cursor.HAND);
            b.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
            b.getStyleClass().add("toolbar-btn");

        }



        toolbarItems.add(registerGrou.build(1));
        toolbarItems.add(selectionEditGroup.build(2));
        toolbarItems.add(refreshgroup.build(1));


        // overide tps
        studentRecordSearch.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("search_edit_scores")));
        genExcelSheetBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("print_marksheets_excel")));
        printAllLRecordsBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("score_recorded_marks")));
        printMarksheet.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("print_marksheets_for_entry")));



        return toolbarItems;

    }

    public void bindFields() {
        entryController.examsStatsLabel.textProperty().bind(statP);

        entryController.examsTermconbo.valueProperty().addListener((o, old, newval) -> loadTable());
        entryController.examsevalcombo.valueProperty().addListener((o, old, newval) -> loadTable());
        examsSubjectcombo.valueProperty().addListener((o, old, newval) -> loadTable());
        classesTreeview.selectedSubjectProperty.addListener((o, old, newval) -> loadTable());


        entryController.examsSaveMarksBtn.setOnAction(e -> saveEntries());

        //score helper actions
        entryController.examCopyscoresbtn.setOnAction(e -> copyScores());
        entryController.examdeleteBtn.setOnAction(e -> deleteAllEvalRecord());
        entryController.examAddBtn.setOnAction(e -> incrementScores());
        entryController.examAssignBtn.setOnAction(e -> assignSameScore());

        refreshBtn.setOnAction(e->refresh());
        printMarksheet.setOnAction(e-> {
            try {
                printMarkSheet();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        studentRecordSearch.valueProperty().addListener((observableValue, stringObjectHashMap, studentObj) -> {
            if (!Objects.equals(studentObj, null)) {
                try {
                    openSingleSheet(studentObj);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        deleteSelectedScoresBtn.setOnAction(e->deleteSelections());



    }


    public void buildMarkForm() {
        //table cols
        TableColumn<HashMap<String, Object>, String> idcol = ProjectUtils.createTableColumn("ID", "id");
        TableColumn<HashMap<String, Object>, String> fnamecol = ProjectUtils.createTableColumn(Translator.getIntl("firstname").toUpperCase(), "firstname",true);
        TableColumn<HashMap<String, Object>, String> lnamecol = ProjectUtils.createTableColumn(Translator.getIntl("lastname").toUpperCase(), "lastname",true);
        TableColumn<HashMap<String, Object>, String> matriculecol = ProjectUtils.createTableColumn(Translator.getIntl("matricule").toUpperCase(), "matricule");
        scorecol = ProjectUtils.createTableColumn(Translator.getIntl("score20").toUpperCase(), "score");
        TableColumn<HashMap<String, Object>, String> coeffcol = ProjectUtils.createTableColumn("COEF", "coefficient");
        TableColumn<HashMap<String, Object>, String> totalcol = ProjectUtils.createTableColumn(Translator.getIntl("total").toUpperCase(), "total");

        scorecol.setCellFactory(hashMapStringTableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(String string, boolean b) {
                super.updateItem(string, b);

                if (!b) {
                    TextField textField = new TextField(string);
                    textField.setStyle("-fx-font-weight: bold;-fx-padding: 0 10");
                    textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
                    setStyle("-fx-background-color: #000000;-fx-background-insets: 2");
                    setPadding(new Insets(4));

                    try {
                        if (Double.parseDouble(string) <= 10) {
                            textField.setStyle("-fx-padding:0 10;-fx-font-weight:bold;-fx-text-fill: " + Store.Colors.red);
                        } else {

                            textField.setStyle("-fx-padding:0 10;-fx-font-weight:bold;-fx-text-fill: " + Store.Colors.White);
                        }
                    } catch (Exception err) {
                        textField.setStyle("-fx-padding:0 10;-fx-font-weight:bold;-fx-text-fill: " + Store.Colors.White);
                    }

                    setText(null);
                    setGraphic(textField);

                    String matricule = PgConnector.getFielorBlank(getTableRow().getItem(), "matricule");
                    textField.setId(matricule);

                    textField.textProperty().addListener((observableValue, oldtext, newentry) -> {
//                        if (!newentry.matches("^(\\d{0,2}(\\.?\\d{0,2})?)?$")) {
//                            textField.setText(oldtext);
//                        }

                        textField.setText(newentry.replaceAll("[^\\d\\.]",""));

                        try {
                            if (Double.parseDouble(textField.getText()) > 20 || Double.parseDouble(textField.getText()) < 0)
                                textField.setText(oldtext);
                        } catch (Exception e) {

                        }

                        updateEntry(getTableRow().getItem(), textField.getText());


                    });

                    textField.focusedProperty().addListener((observableValue, aBoolean, isfocused) -> {
                        if (!Objects.equals(p, null)) {
                            if (p.isShowing())p.hide();

                        }

                        p = ProjectUtils.showPopover("",
                                ProjectUtils.createInfoLabel(String.format("%s Tab %s %s", Translator.getIntl("press"),
                                        Store.UnicodeSumnbol.Tabkey, Translator.getIntl("marktf_info"))),PopOver.ArrowLocation.LEFT_CENTER, false, true);

                        if (isfocused) {
                            scoreEntryView.getSelectionModel().select(getTableRow().getIndex());

                            ////////////////////////////////
                            textField.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                                if (Objects.equals(keyEvent.getCode(), KeyCode.ENTER) || Objects.equals(keyEvent.getCode(), KeyCode.DOWN)) {
                                    try {
                                        p.show(this);
                                    } catch (Exception err) {
                                    }
                                }

                            });
                            ////////////////////////////////

                        }

                    });

                } else {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;-fx-background-insets: 2");

                }
            }
        });


        scorecol.setEditable(true);

        coeffcol.setMinWidth(60);
        totalcol.setMinWidth(110);
        scorecol.setMinWidth(110);
        fnamecol.setMinWidth(120);
        lnamecol.setMinWidth(120);
        matriculecol.setMinWidth(120);

        List<TableColumn<HashMap<String, Object>, String>> cols = List.of(idcol,fnamecol, lnamecol, matriculecol, scorecol, coeffcol, totalcol);

        scoreEntryView = new CustomTableView(cols,0);
        scoreEntryView.setFocusTraversable(true);

        scoreEntryView.setEditable(true);
        VBox.setVgrow(scoreEntryView, Priority.ALWAYS);

    }


    public void loadTable() {

        clearEntries();
        statP.set("");
        // check all params
        HashMap<String, Object> currentClassObj = classesTreeview.selectedSubjectProperty.get();
        int term  = entryController.examsTermconbo.getValue();
        int seq  = entryController.examsevalcombo.getValue();
        HashMap<String, Object> subject = examsSubjectcombo.getValue();

        if (Objects.equals(null, currentClassObj) ||Objects.equals("rootid",String.valueOf(currentClassObj.get("id"))) || Objects.equals(null, subject)) return;
        int classid = Integer.parseInt(String.valueOf(currentClassObj.get("id")));


        //get students
        List<HashMap<String, Object>> foundStudents = PgConnector.fetch(String.format("select * from students where classid=%d order by firstname,lastname",classid),
                PgConnector.getConnection());

        //get records
        List<HashMap<String, Object>> foundMarks = PgConnector.fetch(String.format("""
                select * from marks where classid=%d and sequence=%d and subject='%s' 
                """, classid, seq, PgConnector.getFielorBlank(subject, "subject_name")), PgConnector.getConnection());

        //construct data
        List<HashMap<String, Object>> data = new ArrayList<>();

        int passcount= 0;
        long date  = 0;

        for (HashMap<String, Object> studentItem : foundStudents) {
            String fname = PgConnector.getFielorBlank(studentItem, "firstname");
            String lname = PgConnector.getFielorBlank(studentItem, "lastname");
            String matricule = PgConnector.getFielorBlank(studentItem, "matricule");
            String sid = PgConnector.getFielorBlank(studentItem, "id");

            HashMap<String, Object> formRowObj = new HashMap<>();

            formRowObj.put("id", matricule);
            formRowObj.put("firstname", fname);
            formRowObj.put("lastname", lname);
            formRowObj.put("matricule", PgConnector.getFielorBlank(studentItem, "matricule"));

            int coeff = ProjectUtils.getSubjectCoefficient(currentClassObj, PgConnector.getFielorBlank(subject, "subject_name"));
            formRowObj.put("coefficient", coeff);

            if (!foundMarks.isEmpty()) {
                HashMap<String, Object> studentScore = foundMarks.stream().filter(fm->Objects.equals(PgConnector.getFielorBlank(fm,"student_matricule"),matricule)).findFirst().orElse(null);

                if (!Objects.equals(null, studentScore)) {
                    double scoreVal = Objects.requireNonNull(PgConnector.getNumberOrNull(studentScore, "score")).doubleValue();
                    double total = scoreVal*coeff;

                    formRowObj.put("score", scoreVal);
                    formRowObj.put("total", total);

                    if (scoreVal >= 10)passcount += 1;
                    date = Long.parseLong(PgConnector.getFielorBlank(studentScore, "submitted_on"));

                } else {
                    formRowObj.put("score", "");
//                    formRowObj.put("score", "0");
                    formRowObj.put("total", "");
                    //TODO: Paint cell orange to indicate that the student is missing scores
                }


            } else {
                formRowObj.put("score", "");
//                formRowObj.put("score", "0");
                formRowObj.put("total", "");

            }

            data.add(formRowObj);

        }

        // set stats
        double srate = passcount == 0 ? 0 : ((double) passcount / foundMarks.size()) * 100;

        if (foundMarks.isEmpty()) {
            statP.set(Translator.getIntl("unsubmitted"));
        } else {

            statP.set(String.format("%%Sucess: %5.2f%%\t%s %s", srate,Translator.getIntl("submitted"),ProjectUtils.getFormatedDate(date, DateFormat.getDateInstance(1,Translator.getLocale()))));
        }


        scoreEntryView.setOpacity(0);

        FadeTransition f = new FadeTransition(Duration.millis(200), scoreEntryView);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);


        if (!isloaded.get()) {

            scoreEntryView.loadInitialData(data);
//            scoreEntryView.itemsProperty().set(FXCollections.observableList(data));
            entryController.examsTableContainer.getChildren().clear();
            entryController.examsTableContainer.getChildren().add(scoreEntryView);
            isloaded.set(true);

        } else {
            scoreEntryView.filteredItemsProperty.set(FXCollections.observableList(data));
        }


        f.playFromStart();

        scoreEntryView.currentItemSelectorP.forEach(cb -> cb.setSelected(false));
        scoreEntryView.scrollTo(0);


    }

    public static boolean isvalidEntry(String scoredata) {
        try {
            double val =  Double.parseDouble(scoredata);
            return !(val > 20) && !(val < 0);
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }


    public void updateEntry(HashMap<String,Object> rowdata,String score) {
        HashMap<String, String> currentEntries = mp.get();

        String studentRef = PgConnector.getFielorBlank(rowdata, "matricule");

        if (!currentEntries.containsKey(studentRef)) {
            currentEntries.put(studentRef, score);
        } else {
            currentEntries.replace(studentRef, score);
        }

        mp.setValue(currentEntries);

    }

    public static void updateScore(String matricule, int classid, String subject, int seq, double score) {

        String update = String.format("""
                update marks set score=%05.2f where student_matricule='%s' and classid=%d and sequence=%d and subject='%s'
                """, score, matricule, classid, seq, subject.toLowerCase());
        PgConnector.update(update);

    }
    public static void insertScore(String matricule, int classid, String subject, int term, int seq, String teacher, double score) {
        score = score > 20 ? 20 : (score < 0 ? 0 : score);

        String insert = String.format("""
                insert into marks (student_matricule,classid,teacher,subject,term,sequence,score,submitted_on)
                values ('%s',%d,'%s','%s',%d,%d,%05.2f,%d)
                """, matricule, classid, teacher.toLowerCase(), subject.toLowerCase(), term, seq, score, new Date().getTime());

        PgConnector.insert(insert);
    }

    public void deleteAllEvalRecord() {
        HashMap<String, Object> currentClassObj = classesTreeview.selectedSubjectProperty.get();
        HashMap<String, Object> subjectobj = examsSubjectcombo.getValue();
        if (Objects.equals(null, currentClassObj) || Objects.equals(null, subjectobj)) return;

        int term  = entryController.examsTermconbo.getValue();
        int seq  = entryController.examsevalcombo.getValue();
        String subject = PgConnector.getFielorBlank(examsSubjectcombo.getValue(),"subject_name");

        int classid = Integer.parseInt(String.valueOf(currentClassObj.get("id")));

        String info = String.format("%s : %s\n%s : %s\n%s : %s", Translator.getIntl("class"), PgConnector.getFielorBlank(currentClassObj, "name"),
                Translator.getIntl("evaluation"), seq, Translator.getIntl("subject"), subject);
        Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.WARNING, Translator.getIntl("yes_no"),
                "PROMPT", Translator.getIntl("do_you_delete")+"\n"+info, ButtonType.YES, ButtonType.NO);
        a.showAndWait().ifPresent(bt->{
            if (bt == ButtonType.YES) {
                PgConnector.update(String.format("delete from marks where classid=%d and subject='%s' and sequence=%d ", classid, subject, seq));
                loadTable();

            }
        });

    }
    public void incrementScores() {
        HashMap<String, Object> currentClassObj = classesTreeview.selectedSubjectProperty.get();
        HashMap<String, Object> subjectobj = examsSubjectcombo.getValue();
        if (Objects.equals(null, currentClassObj) || Objects.equals(null, subjectobj)) return;

        int term  = entryController.examsTermconbo.getValue();
        int seq  = entryController.examsevalcombo.getValue();
        String subject = PgConnector.getFielorBlank(examsSubjectcombo.getValue(),"subject_name");

        int classid = Integer.parseInt(String.valueOf(currentClassObj.get("id")));

        TextInputDialog dlg = ProjectUtils.getTextDialog(mainStage, "PROMPT", Translator.getIntl("add_remove_points"), Translator.getIntl("scores_to_add"),
                new ImageView(ResourceUtil.getImageFromResource("images/edit_large.png", 50, 50, true)));


        dlg.showAndWait().ifPresent(val->{

            try {
                double increment = Double.parseDouble(val);

                String info = String.format("%s : %s\n%s : %s\n%s : %s\n%s : %.2f", Translator.getIntl("class"), PgConnector.getFielorBlank(currentClassObj, "name"),
                        Translator.getIntl("evaluation"), seq, Translator.getIntl("subject"), subject,Translator.getIntl("points"),increment);
                Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.WARNING, Translator.getIntl("yes_no"),
                        "PROMPT", Translator.getIntl("do_you_update")+"\n"+info, ButtonType.YES, ButtonType.NO);

                a.showAndWait().ifPresent(bt->{
                    if (bt == ButtonType.YES) {
                        //map and add
                        List<HashMap<String, Object>> foundMarks = PgConnector.fetch(String.
                                format("select * from marks where   sequence=%d and subject='%s' and classid=%d"
                                        , seq, subject, classid), PgConnector.getConnection());

                        for (HashMap<String, Object> rec : foundMarks) {
                            String studentRef = PgConnector.getFielorBlank(rec, "student_matricule");
                            double dbscore = PgConnector.getNumberOrNull(rec,"score").doubleValue();
                            double updatedScore = dbscore + increment;
                            updatedScore = updatedScore > 20 ? 20 : (updatedScore < 0 ? 0 : updatedScore);

                            this.updateScore(studentRef, classid, subject, seq, updatedScore);

                        }
                        loadTable();

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        });




    }

    public void saveEntries(HashMap<String,Object>... params) {
        //params ={showAlert:boolean}

        if (mp.get().keySet().isEmpty())return;

        HashMap<String, Object> currentClassObj = classesTreeview.selectedSubjectProperty.get();
        int term  = entryController.examsTermconbo.getValue();
        int seq  = entryController.examsevalcombo.getValue();
        String subject = PgConnector.getFielorBlank(examsSubjectcombo.getValue(),"subject_name");

        if (Objects.equals(null, currentClassObj) || Objects.equals("", subject)) return;
        int classid = Integer.parseInt(String.valueOf(currentClassObj.get("id")));

        // traverse score map
        for (String student_matricule : mp.get().keySet()) {
            String cleanScore = mp.get().get(student_matricule).strip();


            if (!isvalidEntry(cleanScore))continue;


//            if (cleanScore.endsWith(".")) cleanScore = cleanScore.substring(0, cleanScore.length()-2);


            // check such a score exist in the database
            List<HashMap<String, Object>> foundMarks = PgConnector.fetch(String.
                    format("select * from marks where student_matricule='%s' and sequence=%d and subject='%s' and classid=%d",
                            student_matricule, seq, subject, classid), PgConnector.getConnection());

            if (foundMarks.isEmpty()) {
                System.out.println("inserting for "+student_matricule);
                insertScore(student_matricule, classid, subject, term, seq,
                        PgConnector.getFielorBlank(Store.AuthUser.get(), "displayName"),Double.parseDouble(cleanScore));

            } else {
                System.out.println("updating for "+student_matricule);
                updateScore(student_matricule, classid, subject, seq, Double.parseDouble(cleanScore));

            }


        }

        Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);

        if (params.length == 0)  a.showAndWait();
        loadTable();



    }

    public void copyScores() {
        HashMap<String, Object> currentClassObj = classesTreeview.selectedSubjectProperty.get();
        HashMap<String, Object> subjectobj = examsSubjectcombo.getValue();
        if (Objects.equals(null, currentClassObj) || Objects.equals(null, subjectobj)) return;

        int term  = entryController.examsTermconbo.getValue();
        int seq  = entryController.examsevalcombo.getValue();
        String subject = PgConnector.getFielorBlank(examsSubjectcombo.getValue(),"subject_name");

        int classid = Integer.parseInt(String.valueOf(currentClassObj.get("id")));


        ChoiceDialog<Integer> dlg = new ChoiceDialog<>();
        dlg.getItems().addAll(List.of(1, 2, 3, 4, 5, 6).stream().filter(s -> s != seq).toList());
        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.initOwner(mainStage);

        dlg.setTitle("PROMP");
        dlg.setHeaderText(Translator.getIntl("copy_scores"));
        dlg.setContentText(Translator.getIntl("select_evaluation"));
        dlg.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/download1.png", 50, 50, true)));

        dlg.getDialogPane().getScene().getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        Optional<Integer> res = dlg.showAndWait();

        res.ifPresent(destSeq->{
            //get values for term
            List<HashMap<String, Object>> records = PgConnector.fetch(String.
                    format("select * from marks where sequence=%d and classid=%d and subject='%s'",
                            destSeq, classid,subject
                    ),PgConnector.getConnection());

            if (records.isEmpty()) {

                Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.NONE, "COMPLETE", "INFO", Translator.getIntl("no_scores_tocopy"), ButtonType.OK);
                a.showAndWait();

            } else {// scores found. so copy
                for (HashMap<String, Object> recordmap : records) {
                    String studentRef = PgConnector.getFielorBlank(recordmap, "student_matricule");
                    String teacherRef = PgConnector.getFielorBlank(Store.AuthUser.get(), "displayName");
                    double scoreval = PgConnector.getNumberOrNull(recordmap, "score").doubleValue();

                    List<HashMap<String, Object>> foundMarks = PgConnector.fetch(String.
                            format("select * from marks where student_matricule='%s' and sequence=%d and subject='%s' and classid=%d",
                                    studentRef, seq, subject, classid), PgConnector.getConnection());

                    if (foundMarks.isEmpty()) {
                        insertScore(studentRef, classid, subject, term, seq,
                                teacherRef,scoreval);

                    } else {
                        updateScore(studentRef, classid, subject, seq, scoreval);

                    }


                }
                dlg.close();
                Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
                a.showAndWait();
                loadTable();

            }


        });



    }
    public void assignSameScore() {
        HashMap<String, Object> currentClassObj = classesTreeview.selectedSubjectProperty.get();
        HashMap<String, Object> subjectobj = examsSubjectcombo.getValue();
        if (Objects.equals(null, currentClassObj) || Objects.equals(null, subjectobj)) return;

        int term  = entryController.examsTermconbo.getValue();
        int seq  = entryController.examsevalcombo.getValue();
        String subject = PgConnector.getFielorBlank(examsSubjectcombo.getValue(),"subject_name");

        int classid = Integer.parseInt(String.valueOf(currentClassObj.get("id")));

        TextInputDialog dlg = ProjectUtils.getTextDialog(mainStage, "PROMPT", Translator.getIntl("apply_same_score"), Translator.getIntl("scores_to_apply"),
                new ImageView(ResourceUtil.getImageFromResource("images/edit_large.png", 50, 50, true)));


        dlg.showAndWait().ifPresent(valToAssign->{
            try {
                Double.parseDouble(valToAssign);
            } catch (Exception e) {
                return;
            }

            List<HashMap<String, Object>> students = PgConnector.fetch(String.format("select * from  students where classid=%d order by firstname", classid), PgConnector.getConnection());

            for (HashMap<String, Object> stud : students) {
                List<HashMap<String, Object>> exists = PgConnector.fetch(String.
                        format("select * from marks where sequence=%d and classid=%d and subject='%s' and student_matricule='%s'",
                                seq, classid,subject,PgConnector.getFielorBlank(stud,"matricule")
                        ),PgConnector.getConnection());

                if (exists.isEmpty()) {
                    this.insertScore(PgConnector.getFielorBlank(stud, "matricule"),
                            classid,subject,term,seq,PgConnector.getFielorBlank(Store.AuthUser.get(),"dislplayName"),Double.parseDouble(valToAssign)
                    );

                } else {
                    this.updateScore(PgConnector.getFielorBlank(stud, "matricule"),classid,subject,seq,Double.parseDouble(valToAssign));

                }


            }

            dlg.close();
            Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
            a.showAndWait();
            loadTable();




        });



    }

    public void clearEntries() {
        mp.get().clear();
    }


    public void printMarkSheet() throws SQLException {
        // check current logged in user
        HashMap<String, Object> user = Store.AuthUser.get();

        boolean isAdmin = Boolean.parseBoolean(PgConnector.getFielorBlank(user, "isAdmin"));

        if (isAdmin) {
            exportAllMarksheets();
        } else {
            String username = PgConnector.getFielorBlank(user, "username");
            HashMap<String, Object> teacherObj = PgConnector.getObjectFromKey("username", username, "employees");
            EmployeeService.printEmployeeMarksheets(null, teacherObj);
        }



    }


    public void exportAllMarksheets() {

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

    public void openSingleSheet(HashMap<String,Object> studentObj) throws SQLException {

        try {
            SingleScoreViewController singleScoreViewController  = ActionStageLinker.openSingleScoreDialog(mainStage,studentObj);
            studentRecordSearch.setItems(FXCollections.observableList(new ArrayList<>()));
            studentRecordSearch.setItems(FXCollections.observableList(getAcceissibleData()));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<HashMap<String,Object>> getAcceissibleData() {
        HashMap<String,Object> user = Store.AuthUser.get();
        boolean isAdmin = Boolean.parseBoolean(PgConnector.getFielorBlank(user,"isAdmin"));
        List<HashMap<String, Object>> allStudents = PgConnector.fetch("select * from students order by firstname,lastname", PgConnector.getConnection());

        List<HashMap<String, Object>> ownStudents = new ArrayList<>();

        if (isAdmin) {
            ownStudents.addAll(allStudents);

        } else {
            String empName = PgConnector.getFielorBlank(user, "displayName");

            List<HashMap<String, Object>> staffOwnStudents = PgConnector.fetch(String.format("""
                    select * from students where classid in (select classid from permissions where teacher='%s')
                    """,empName), PgConnector.getConnection());

            ownStudents.addAll(staffOwnStudents);

        }

        return ownStudents;

    }


    public void deleteSelections() {
        HashMap<String, Object> currentClassObj = classesTreeview.selectedSubjectProperty.get();
        HashMap<String, Object> subjectobj = examsSubjectcombo.getValue();
        List<String> selectScores = scoreEntryView.currentSelectedIds.get();


        if (Objects.equals(null, currentClassObj) || Objects.equals(null, subjectobj) || selectScores.isEmpty()) return;

        int term  = entryController.examsTermconbo.getValue();
        int seq  = entryController.examsevalcombo.getValue();
        String subject = PgConnector.getFielorBlank(examsSubjectcombo.getValue(),"subject_name");
        int classid = Integer.parseInt(String.valueOf(currentClassObj.get("id")));

        StringBuilder info = new StringBuilder(String.format("%s : %s\n%s : %d\n%s : %s\n----------------\n\t", Translator.getIntl("class"),
                PgConnector.getFielorBlank(currentClassObj, "name"),
                Translator.getIntl("evaluation"), seq, Translator.getIntl("subject"),subject));
        for (String sid : selectScores) {
            int index = selectScores.indexOf(sid);
            String fname = PgConnector.getFielorBlank(PgConnector.getObjectFromKey("matricule", sid, "students"), "firstname");
            String lname = PgConnector.getFielorBlank(PgConnector.getObjectFromKey("matricule", sid, "students"), "lastname");
            info.append(String.format("\n\t-> %s %s", fname, lname).toUpperCase());
            if (index == 10) {
                info.append(String.format("\n+%d ...", selectScores.size() - 10));
                break;
            }
        }

        Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.WARNING, Translator.getIntl("yes_no"),
                "PROMPT", Translator.getIntl("do_you_delete")+"\n"+info, ButtonType.YES, ButtonType.NO);

        a.showAndWait().ifPresent(bt->{
            if (bt == ButtonType.YES) {
                for (String sid : selectScores) {
                    PgConnector.update(String.format("delete from marks where classid=%d and subject='%s' and sequence=%d and student_matricule='%s' ", classid, subject, seq,sid));
                }
                loadTable();

            }
        });


    }


    public void refresh() {
        HashMap<String, Object> classITemSelected = classesTreeview.selectedSubjectProperty.get();
        scoreEntryView.currentItemSelectorP.forEach(cb -> cb.setSelected(false));

        HashMap<String, Object> currentClassObj = classesTreeview.selectedSubjectProperty.get();
        TreeItem<HashMap<String, Object>> currentSelectedTreeItem = classesTreeview.selectedItemP.get();

        if (!mp.get().keySet().isEmpty()) {
            Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.NONE, Translator.getIntl("changes_detected"), "PROMPT", Translator.getIntl("do_you_save_changes"),
                    ButtonType.YES, ButtonType.NO);
            a.showAndWait().ifPresent(btype -> {
                if (Objects.equals(btype, ButtonType.YES)) {
                    saveEntries(new HashMap<>());
                }
            });
        }



        if (!Objects.equals(null, currentClassObj)) {
            classesTreeview.reloadTree();
        }

        scoreEntryView.getSelectionModel().clearSelection();
        loadTable();
        classesTreeview.getSelectionModel().select(currentSelectedTreeItem);

        if (!Objects.equals(null,classITemSelected))classesTreeview.selectItem(PgConnector.getFielorBlank(classITemSelected, "id"));






    }






}
