package smpro.app.controllers;

import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import smpro.app.ResourceUtil;
import smpro.app.custom_nodes.CustomTableView;
import smpro.app.services.MarksheetService;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SingleScoreViewController implements Initializable {
    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public ImageView imagview;
    public Label fnamel;
    public Label lnamel;
    public Label classl;
    public Label matriculel;
//    public ComboBox<HashMap<String,Object>> subjectcombo;
    public ComboBox<Integer> evalcombo;
    public VBox tablecontainer;
    public Button cancelBtn;
    public Button confirmBtn;
    public ImageView appIcon;
    public HBox dragArea;
    public Label empTitle;
    public Button closedlg;
    public Label tradel;

    public RadioButton selectAllBtn;
    public Button deleteMarksBtn;
    public VBox imageContainer;
    CustomTableView singleEntryView;

    PopOver p;

    ObjectProperty<HashMap<String, String>> mp = new SimpleObjectProperty<>(new HashMap<>());

    HashMap<String, Object> user = Store.AuthUser.get();
    String fullname = PgConnector.getFielorBlank(user, "displayName");
    boolean isAdmin = Boolean.parseBoolean(PgConnector.getFielorBlank(user, "isAdmin"));

    IntegerProperty classidObj = new SimpleIntegerProperty();
    public ObjectProperty<HashMap<String, Object>> studentObjP = new SimpleObjectProperty<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appIcon.setImage(ResourceUtil.getImageFromResource("images/plus.png", (int) appIcon.getFitWidth(), (int) appIcon.getFitHeight(), true));
        closedlg.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOSE, 30, Paint.valueOf("transparent")));
        closedlg.setOnAction(e->thisStage.get().close());
        closedlg.addEventHandler(MouseEvent.MOUSE_EXITED, e->{
            closedlg.setStyle("-fx-background-color: transparent");
        });
        closedlg.addEventHandler(MouseEvent.MOUSE_ENTERED,e->{
            closedlg.setStyle("-fx-background-color: "+ Store.Colors.deepRed);
        });

        bindFields();

        studentObjP.addListener((o, old, newvalue) -> {
            try {
                loadSingleViewTable();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            if (studentObjP.get()!=null)loadSingleViewTable();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }


        deleteMarksBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TRASH_CAN, 50, Paint.valueOf(Store.Colors.red)));

    }

    public void bindFields() {
        cancelBtn.setOnAction(e -> thisStage.get().close());
        confirmBtn.setOnAction(e-> {
            try {
                saveChanges();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // build score table
        TableColumn<HashMap<String, Object>, String> idcol = ProjectUtils.createTableColumn("ID", "id");
        TableColumn<HashMap<String, Object>, String> subjectcol = ProjectUtils.createTableColumn(Translator.getIntl("subject").toUpperCase(), "subject");
        TableColumn<HashMap<String, Object>, String>  singleScorecol = ProjectUtils.createTableColumn(Translator.getIntl("score20").toUpperCase(), "score");
        TableColumn<HashMap<String, Object>, String> coeffcol = ProjectUtils.createTableColumn("COEF", "coefficient");
        TableColumn<HashMap<String, Object>, String> totalcol = ProjectUtils.createTableColumn(Translator.getIntl("total").toUpperCase(), "total");
        TableColumn<HashMap<String, Object>, String> tutorCol = ProjectUtils.createTableColumn(Translator.getIntl("instructor").toUpperCase(), "teacher");

        singleScorecol.setCellFactory(hashMapStringTableColumn -> new TableCell<>() {
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

                    HashMap<String,Object> rowdata =   getTableRow().getItem();

                    String subName = PgConnector.getFielorBlank(rowdata, "subject");
                    textField.setId(subName);

                    textField.textProperty().addListener((observableValue, oldtext, newentry) -> {
                        System.out.println("newvalue "+newentry);
//                        if (!newentry.matches("^(\\d{0,2}(\\.?\\d{0,2})?)?$")) {
//                            textField.setText(oldtext);
//                        }

                        textField.setText(newentry.replaceAll("[^\\d\\.]",""));

                        try {
                            if (Double.parseDouble(textField.getText()) > 20 || Double.parseDouble(textField.getText()) < 0)
                                textField.setText(oldtext);
                        } catch (Exception e) {

                        }

                        updateSingleEntry(subName, textField.getText());



                    });

                    textField.focusedProperty().addListener((observableValue, aBoolean, isfocused) -> {
                        if (!Objects.equals(p, null)) {
                            if (p.isShowing())p.hide();

                        }

                        p = ProjectUtils.showPopover("",
                                ProjectUtils.createInfoLabel(String.format("%s Tab %s %s", Translator.getIntl("press"),
                                        Store.UnicodeSumnbol.Tabkey, Translator.getIntl("marktf_info"))),
                                PopOver.ArrowLocation.LEFT_CENTER, false, true);

                        if (isfocused) {
                            singleEntryView.getSelectionModel().select(getTableRow().getIndex());

                            ////////////////////////////////
                            textField.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                                if (Objects.equals(keyEvent.getCode(), KeyCode.ENTER) || Objects.equals(keyEvent.getCode(), KeyCode.DOWN)) {
                                    try {
                                        p.show(this);
                                    } catch (Exception err) {
                                        System.out.println(err.getLocalizedMessage());
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


        singleScorecol.setEditable(true);

        coeffcol.setMinWidth(60);
        totalcol.setMinWidth(60);
        singleScorecol.setMinWidth(110);;
//        subjectcol.setMinWidth(100);

        List<TableColumn<HashMap<String, Object>, String>> cols = List.of(idcol, subjectcol, singleScorecol, coeffcol, totalcol,tutorCol);

        singleEntryView = new CustomTableView(cols,0);
        singleEntryView.setFocusTraversable(true);
        tablecontainer.getChildren().clear();
        tablecontainer.getChildren().add(singleEntryView);

        singleEntryView.setEditable(true);
        VBox.setVgrow(singleEntryView, Priority.ALWAYS);

        //actions
        evalcombo.valueProperty().addListener((o, old, newval) -> {
            try {
                loadSingleViewTable();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        selectAllBtn.setOnAction(e->toggleSelectAll());
        deleteMarksBtn.setOnAction(e->deleteRecords());



    }

    public void loadSingleViewTable() throws SQLException {
        if (Objects.equals(null,studentObjP.get())) return;

        singleEntryView.setOpacity(0);
        FadeTransition f = new FadeTransition(Duration.millis(300), singleEntryView);
        f.setFromValue(0);
        f.setToValue(1);

        PreparedStatement userPermStatement = PgConnector.getConnection().prepareStatement("select * from permissions where teacher=?");
        userPermStatement.setString(1, fullname);
        ResultSet permResultSet = userPermStatement.executeQuery();

        List<HashMap<String,Object>> accessibleSubjects = new ArrayList<>(PgConnector.
                aggregatePgArray(permResultSet, "subjects")).stream().sorted().
                map(s->PgConnector.getObjectFromKey("subject_name",s,"subjects")).toList();

        List<HashMap<String, Object>> marksRes = PgConnector.fetch(String.format("select * from marks where " +
                        "student_matricule='%s' and sequence=%d and classid=%d",
                PgConnector.getFielorBlank(studentObjP.get(),"matricule"), evalcombo.getValue(), classidObj.get()), PgConnector.getConnection());



        if (isAdmin) {
            List<String> subjectsInMarks = marksRes.stream().map(m -> PgConnector.getFielorBlank(m, "subject")).toList();
            accessibleSubjects = PgConnector.fetch("select * from subjects order by subject_name", PgConnector.getConnection()).stream().sorted((o1, o2) -> {
                if (subjectsInMarks.contains(PgConnector.getFielorBlank(o2, "subject_name"))) {
                    return 1;
                } else return -1;
            }).toList();

            List<HashMap<String, Object>> accessibleSubjectsCopy = accessibleSubjects;
            accessibleSubjects = accessibleSubjects.stream().sorted((o1, lval) -> {
                int sindex = accessibleSubjectsCopy.indexOf(lval);
                if (sindex < marksRes.size()) {
                    return 0;
                } else if (subjectsInMarks.contains(PgConnector.getFielorBlank(lval, "subject_name"))) {
                    return 1;
                } else return -1;


            }).toList();


        }




        List<HashMap<String, Object>> data = new ArrayList<>();

        for (HashMap<String, Object> sobj : accessibleSubjects) {
            HashMap<String, Object> item = new HashMap<>();


            String subName =PgConnector.getFielorBlank(sobj, "subject_name");
            //get score record
            List<HashMap<String, Object>> marks = PgConnector.fetch(String.format("select * from marks where " +
                            "student_matricule='%s' and subject='%s' and sequence=%d and classid=%d",
                    PgConnector.getFielorBlank(studentObjP.get(),"matricule"),subName, evalcombo.getValue(), classidObj.get()), PgConnector.getConnection());



            int coeff = ProjectUtils.getSubjectCoefficient(PgConnector.getObjectFromId(classidObj.get(),"classes"), subName);
            item.put("coefficient", coeff);

            String score = "";
            String total = "";

            if (!marks.isEmpty()) {
                score = String.valueOf(marks.get(0).get("score"));
                total = String.valueOf(Double.parseDouble(score) * coeff);
            }

            item.put("subject", ProjectUtils.capitalize(subName));
            item.put("coefficient", PgConnector.getFielorBlank(sobj, "subject_coefficient"));
            item.put("teacher", ProjectUtils.capitalize(ProjectUtils.getTeacherForSubject(subName, classidObj.get())));
            item.put("score", score);
            item.put("total", total);
            item.put("id", PgConnector.getFielorBlank(sobj,"subject_name"));

            data.add(item);

        }

        singleEntryView.filteredItemsProperty.setValue(FXCollections.observableList(data));

        singleEntryView.currentItemSelectorP.forEach(c -> c.setSelected(false));
        selectAllBtn.setSelected(false);

        f.playFromStart();

    }

    public void updateSingleEntry(String subjectname,String score) {
        HashMap<String, String> currentEntries = mp.get();

        if (!currentEntries.containsKey(subjectname)) {
            currentEntries.put(subjectname, score);
        } else {
            currentEntries.replace(subjectname, score);
        }

        System.out.println("entries updated  \n" + currentEntries);
        mp.setValue(currentEntries);

    }

    public void preparedUpdate(HashMap<String, Object> studentObj) throws SQLException {
        int sid = PgConnector.getNumberOrNull(studentObj, "id").intValue();
        int cid = PgConnector.getNumberOrNull(studentObj, "classid").intValue();
        classidObj.set(cid);
//        studentObjP.set(studentObj);

        String fname = PgConnector.getFielorBlank(studentObj, "firstname");
        String lname = PgConnector.getFielorBlank(studentObj, "lastname");
        String matricule = PgConnector.getFielorBlank(studentObj, "matricule");
        String trade = PgConnector.getFielorBlank(studentObj, "trade");
        String classString = PgConnector.getFielorBlank(PgConnector.getObjectFromId(cid, "classes"), "classname").toUpperCase();

        lnamel.setText(ProjectUtils.getShortPersonName(lname, 2).toUpperCase());
        fnamel.setText(ProjectUtils.getShortPersonName(fname, 2).toUpperCase());
        matriculel.setText(matricule);
        classl.setText(classString);
        tradel.setText(PgConnector.getFielorBlank(PgConnector.getObjectFromKey("trade_name", trade, "trades"), "trade_abbreviation").toUpperCase());

        evalcombo.getItems().clear();
        evalcombo.getItems().addAll(1, 2, 3, 4, 5, 6);
        evalcombo.setValue(Store.termToSeqsMap.get(Integer.parseInt(String.valueOf(PgConnector.getObjectFromId(1, "base").get("current_term")))).get(0));

        //fill subjects combo

        //set image
        PreparedStatement s = null;
        try {
            s = PgConnector.getConnection().prepareStatement("select * from students where id=?");
            s.setInt(1, sid);
            ResultSet res = s.executeQuery();
            if (res.next()) {
                InputStream imgStream = res.getBinaryStream("image");
                if (!Objects.equals(null, imgStream)) {
                    imageContainer.getChildren().remove(0);
                    imagview.setImage(new Image(imgStream, imagview.getFitWidth(), imagview.getFitHeight(), true, true));
                } else {
                    imageContainer.getChildren().remove(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }

    public void saveChanges(HashMap<String,Object>... params) throws SQLException {
        //params ={showAlert:boolean}
        HashMap<String, Object> baseObj = PgConnector.getObjectFromId(1, "base");

        if (mp.get().keySet().isEmpty())return;

        int seq  = evalcombo.getValue();
        int term = PgConnector.getNumberOrNull(baseObj, "current_term").intValue();

        int classid = classidObj.get();

        // traverse score map
        for (String subjectName : mp.get().keySet()) {
            String cleanScore = mp.get().get(subjectName).strip();
            subjectName = subjectName.toLowerCase();


            System.out.println("cleaned score " + cleanScore);
            if (!MarksheetService.isvalidEntry(cleanScore))continue;


//            if (cleanScore.endsWith(".")) cleanScore = cleanScore.substring(0, cleanScore.length()-2);


            // check such a score exist in the database
            List<HashMap<String, Object>> foundMarks = PgConnector.fetch(String.
                    format("select * from marks where student_matricule='%s' and sequence=%d and subject='%s' and classid=%d",
                            PgConnector.getFielorBlank(studentObjP.get(),"matricule"), seq, subjectName.toLowerCase(), classid), PgConnector.getConnection());

            if (foundMarks.isEmpty()) {
                MarksheetService.insertScore(PgConnector.getFielorBlank(studentObjP.get(),"matricule"), classid, subjectName.toLowerCase(), term, seq,
                        isAdmin ? ProjectUtils.getTeacherForSubject(subjectName,classid) : PgConnector.getFielorBlank(Store.AuthUser.get(),"displayName"),
                        Double.parseDouble(cleanScore));

            } else {
                MarksheetService.updateScore(PgConnector.getFielorBlank(studentObjP.get(),"matricule"), classid, subjectName, seq, Double.parseDouble(cleanScore));

            }


        }

        loadSingleViewTable();
        singleEntryView.scrollTo(0);



    }


    public void toggleSelectAll() {
        if (selectAllBtn.isSelected()) {
            singleEntryView.currentItemSelectorP.forEach(cb -> cb.setSelected(true));

        } else {
            singleEntryView.currentItemSelectorP.forEach(cb -> cb.setSelected(false));


        }

    }



    public void deleteRecords() {
        List<String> selctedIds = singleEntryView.currentSelectedIds.get();
        String checkRecords = String.format("select *  from marks where  student_matricule='%s' and sequence=%d and classid=%d",
                PgConnector.getFielorBlank(studentObjP.get(),"matricule"),evalcombo.getValue(),classidObj.get() );
        if (PgConnector.fetch(checkRecords,PgConnector.getConnection()).isEmpty())return;

        final List<String> cleanedSelections =  new HashSet<>(selctedIds.stream().filter(id -> !Objects.equals("null", id)).toList()).
                stream().filter(id -> !id.isEmpty()).toList();

        if (cleanedSelections.isEmpty())return;

        StringBuilder builder = new StringBuilder();
        for (String s : cleanedSelections) {
            int index = cleanedSelections.indexOf(s);
            if (index == 10 && cleanedSelections.size() == 10) {
                builder.append(String.format("---- +%d ", cleanedSelections.size() - 10));
                break;
            }
            builder.append(String.format("\t%s %s\n", Store.UnicodeSumnbol.bullet,
                    ProjectUtils.capitalize(s)
              ));
        }


        Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.WARNING, Translator.getIntl("do_you_delete"),
                "WARNING", cleanedSelections.size() <= 15 ? builder.toString() :
                        String.format("%s \n%d %s", Translator.getIntl("note_ireversible"), cleanedSelections.size(),
                                Translator.getIntl("score_records")), ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
        Optional<ButtonType> res = a.showAndWait();
        res.ifPresent(b->{
            if (Objects.equals(b, ButtonType.YES)) {
                for (String itemid : cleanedSelections) {
                    String del = String.format("delete from marks where subject='%s' and student_matricule='%s' and sequence=%d and classid=%d",
                            itemid.toLowerCase(),PgConnector.getFielorBlank(studentObjP.get(),"matricule"),evalcombo.getValue(),classidObj.get()
                    );
                    PgConnector.update(del);


                }
                try {
                    loadSingleViewTable();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });



    }












}





























