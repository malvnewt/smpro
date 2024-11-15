package smpro.app.controllers;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.kordamp.ikonli.materialdesign2.MaterialDesignB;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import smpro.app.ResourceUtil;
import smpro.app.services.HrService;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AddClassController implements Initializable {
    public Button saveClassBtn;
    public Button cancelBtn;
    public FlowPane gdPreview;
    public FlowPane gcPreview;
    public FlowPane gbPreview;
    public FlowPane gaPreview;
    public ImageView previewImageview;
    public ListView<String> compulsorylv;
    public Button compulsoryBtn;
    public ListView<String> gdlv;
    public Button groupDbtn;
    public TextField gdLabel;
    public ListView<String> gclv;
    public Button groupCbtn;
    public TextField gclable;
    public ListView<String> gblv;
    public Button groupBbtn;
    public TextField gbLabel;
    public ListView<String> galv;
    public Button groupAbtn;
    public TextField gAlabel;
    public ComboBox<String> sectioncombo;
    public ComboBox<Integer> cyclecombo;
//    public ComboBox<String> classmastercombo;
    public TextField shortnamefield;
    public TextField classnamefiled;


    public ObjectProperty<Stage> thisStage = new SimpleObjectProperty<>();
    public ObjectProperty<Stage> mainStage = new SimpleObjectProperty<>();
    public ComboBox<Integer> levelcombo;
    public BooleanProperty isUpdate = new SimpleBooleanProperty(false);
    public ImageView iconView;
    public TitledPane groupAtpane;
    public VBox logoContainer;
    public GridPane infogrid;
    public Label caption;
    public HBox dragArea;
    public ImageView appIcon;
    public Button closedlg;
    public HBox formatContainer;
    public TextField ptaf;
    public TextField feef;
    public HBox totall;
    public Label totalLabel;


    List<Button> editBtns  = new ArrayList<>();
    List<TextField> editFields = new ArrayList<>();
    List<ListView<String>> editLview = new ArrayList<>();
    List<FlowPane>  previewTiles = new ArrayList<>();


    /// Properties
    StringProperty classnameP = new SimpleStringProperty();
    StringProperty shortNameP = new SimpleStringProperty();
    StringProperty classMasterP = new SimpleStringProperty();
    StringProperty sectionP = new SimpleStringProperty();

    StringProperty totalP = new SimpleStringProperty();
    StringProperty ptaP = new SimpleStringProperty();
    StringProperty feeP = new SimpleStringProperty();

    IntegerProperty cycleP = new SimpleIntegerProperty(1);
    IntegerProperty levelP = new SimpleIntegerProperty(1);



    ListProperty<String> catAsubs = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    ListProperty<String> catBsubs = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    ListProperty<String> catCsubs = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    ListProperty<String> catDsubs = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    ListProperty<String> compulsorySubsP = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    StringProperty catAlabel = new SimpleStringProperty();
    StringProperty catBlabel = new SimpleStringProperty();
    StringProperty catClabel = new SimpleStringProperty();
    StringProperty catDlabel = new SimpleStringProperty();

    List<StringProperty> labelProperties = List.of(catAlabel, catBlabel, catClabel, catDlabel);
    List<ListProperty<String>> categorySubjectsProperties = List.of(catAsubs, catBsubs, catCsubs, catDsubs);



    List<String> dbSubjects = PgConnector.listHashAttrs(PgConnector.
            fetch("select distinct subject_name,id from subjects order by subject_name",
                    PgConnector.getConnection()), "subject_name");


    //CUSTOM UI ELEMENTS
    SearchableComboBox<String> classmastercombo = new SearchableComboBox<>(); // grid position (3,3)
    SearchableComboBox<HashMap<String,Object>> formatsCombo = new SearchableComboBox<>(); // container = formatContainer




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        appIcon.setImage(ResourceUtil.getImageFromResource("images/plus.png", (int) appIcon.getFitWidth(), (int) appIcon.getFitHeight(), true));
        closedlg.setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CLOSE, 30, Paint.valueOf("transparent")));
        closedlg.setOnAction(e->thisStage.get().close());
        closedlg.addEventHandler(MouseEvent.MOUSE_EXITED, e->{
            closedlg.setStyle("-fx-background-color: transparent");
        });
        closedlg.addEventHandler(MouseEvent.MOUSE_ENTERED,e->{
            closedlg.setStyle("-fx-background-color: "+Store.Colors.deepRed);
        });



        editBtns.addAll(List.of(groupAbtn, groupBbtn, groupCbtn, groupDbtn));
        editFields.addAll(List.of(gAlabel, gbLabel, gclable, gdLabel));
        editLview.addAll(List.of(galv, gblv, gclv, gdlv));
        previewTiles.addAll(List.of(gaPreview, gbPreview, gcPreview, gdPreview));


        bindFields();
        bindActions();
        try {
            initUi();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        
    }


    public void bindActions() {
        cancelBtn.setOnAction(e->thisStage.get().close());

    }

    public void bindFields() {
        //bind group labels
        for (StringProperty labelP:labelProperties)labelP.bind(editFields.get(labelProperties.indexOf(labelP)).textProperty());
        //validation for group name fields
        editFields.forEach(f-> f.textProperty().addListener((o, old, newval)->{
            if (!newval.isEmpty())f.getStyleClass().remove("error-textfield");
        }));

        //bind subjectCategories and preview

        previewTiles.forEach(tilePane -> {
            tilePane.setHgap(2);
            tilePane.setVgap(2);
            tilePane.setAlignment(Pos.TOP_LEFT);
        });

        for (ListProperty<String> categorySubsP : categorySubjectsProperties) {
            int index = categorySubjectsProperties.indexOf(categorySubsP);
            ListView<String> lv = editLview.get(index);

            categorySubsP.bind(lv.itemsProperty());

        }

        compulsorySubsP.bind(compulsorylv.itemsProperty());

        catAsubs.bind(galv.itemsProperty());
        catBsubs.bind(gblv.itemsProperty());
        catCsubs.bind(gclv.itemsProperty());
        catDsubs.bind(gdlv.itemsProperty());



        //bind main class fields
        feeP.bind(feef.textProperty());
        ptaP.bind(ptaf.textProperty());
        totalLabel.textProperty().bind(totalP);

        feeP.addListener((observableValue, s, t1) -> {
            try {
                totalP.set(HrService.formatNumberToCurency(Integer.parseInt(Objects.equals(feef.getText(), "") ?"0":feeP.get()) +
                        Integer.parseInt(Objects.equals(ptaf.getText(), "") ?"0":ptaP.get())));
                System.out.println("new totalP >> "+totalP.get());
            } catch (NumberFormatException ner) {
                ner.printStackTrace();
            }
        });

        ptaP.addListener((observableValue, s, t1) -> {
            try {
                totalP.set(HrService.formatNumberToCurency(Integer.parseInt(Objects.equals(feef.getText(), "") ?"0":feeP.get()) +
                        Integer.parseInt(Objects.equals(ptaf.getText(), "") ?"0":ptaP.get())));
                System.out.println("new totalP >> "+totalP.get());
            } catch (NumberFormatException ner) {
                ner.printStackTrace();
            }
        });


        shortNameP.bind(shortnamefield.textProperty());
        classnameP.bind(classnamefiled.textProperty());
        classMasterP.bind(classmastercombo.valueProperty());
        cycleP.bind(cyclecombo.valueProperty());
        levelP.bind(levelcombo.valueProperty());
        sectionP.bind(sectioncombo.valueProperty());


        //bind presets combo
        formatsCombo.valueProperty().addListener((o,old,newClassObj)->{

            if (!Objects.equals(null, newClassObj)) {
            try {
                System.out.println("selected preset >>> "+newClassObj);
                PreparedStatement classStatement = PgConnector.getConnection().prepareStatement("select * from classes where id=?");
                classStatement.setInt(1, PgConnector.getNumberOrNull(newClassObj, "id").intValue());
                ResultSet res = classStatement.executeQuery();

                if (res.next()) {
                    List<String> ga = PgConnector.parsePgArray(res, "subjects_ga");
                    List<String> gb = PgConnector.parsePgArray(res, "subjects_gb");
                    List<String> gc = PgConnector.parsePgArray(res, "subjects_gc");
                    List<String> gd = PgConnector.parsePgArray(res, "subjects_gd");
                    List<String> compulsory = PgConnector.parsePgArray(res, "compulsory_subjects");

                    List<List<String>> stringItems = List.of(ga, gb, gc, gd);

                    for (FlowPane pane : previewTiles) {
                        int index = previewTiles.indexOf(pane);
                        List<String> groupItems = ProjectUtils.getUniqueValues(stringItems.get(index)).stream().filter(s->!s.isEmpty()).toList();

                        TextField gname = editFields.get(index);
                        ListView<String> lv = editLview.get(index);

                        List<String> remainingItems = new ArrayList<>();

                        if (groupItems.size() > 1) {
                            gname.setText(groupItems.get(0));
                            remainingItems.addAll(groupItems.subList(1, groupItems.size()));
                        }

                        lv.getItems().clear();
                        lv.getItems().addAll(remainingItems);

                        pane.getChildren().clear();
                        pane.getChildren().addAll(remainingItems.stream().map(s -> {
                            Label l = new Label(ProjectUtils.capitalize(s));
                            l.setStyle("-fx-font-weight: " +
                                    "bold;-fx-text-fill: #242424;-fx-font-size: 12px;" +
                                    "-fx-background-color: transparent;-fx-border-color: #24242490;-fx-border-width: 1px;-fx-padding: 1px;");
                            l.setTooltip(ProjectUtils.createTooltip(s.toUpperCase()));
                            return l;
                        }).toList());

                    }


                    compulsorylv.getItems().clear();
                    compulsorylv.getItems().addAll(compulsory);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            }


        });

        for (TextField t : new TextField[]{feef, ptaf}) {
            t.textProperty().addListener((o, f, l) -> t.setText(l.replaceAll("\\D", "")));

        }




    }


    public void initUi() throws SQLException {
        ptaf.setText("0");
        feef.setText("0");

        appIcon.setImage(ResourceUtil.getImageFromResource("images/plus.png", (int) appIcon.getFitWidth(), (int) appIcon.getFitHeight(), true));
        caption.setText(ProjectUtils.capitalize(Translator.getIntl("add_new_class")));
        caption.getStyleClass().add("caption-text");
        dragArea.getStyleClass().add("caption-container");

        //add searchable combo for classmaster

        //place logo on preview
        PreparedStatement ps = PgConnector.getConnection().prepareStatement("select logo_bytes from base where id=1");

        InputStream logoStream = PgConnector.readBinarydata(ps);

        if (Objects.equals(null, logoStream)) {
            logoContainer.getChildren().clear();
            Label l = new Label("LOGO");
            l.getStyleClass().add("smpro-large-sub");
            l.setStyle("-fx-text-fill: #242424;-fx-font-weight: bold");
            logoContainer.getChildren().add(l);
            logoContainer.setAlignment(Pos.CENTER);
        } else {
            Image img = new Image(logoStream, previewImageview.getFitWidth(), previewImageview.getFitWidth(), true, true);
            previewImageview.setImage(img);

        }

        editLview.forEach(view->{
            view.getStyleClass().addAll("bordered", "dense");
            view.setCellFactory(new Callback<>() {
                @Override
                public ListCell<String> call(ListView<String> stringListView) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(String s, boolean b) {
                            super.updateItem(s, b);
                            if (!b) {
                                setText(s.toUpperCase());
                                setGraphic(ProjectUtils.createFontIcon(MaterialDesignB.BOOK, 10, Paint.valueOf(Store.Colors.lightestGray)));

                            } else {
                                setText(null);
                                setGraphic(null);
                            }
                        }
                    };
                }
            });
        });

        compulsorylv.getStyleClass().addAll("bordered", "dense");
        compulsorylv.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (!b) {
                            setText(s.toUpperCase());
                            setGraphic(ProjectUtils.createFontIcon(MaterialDesignB.BOOK, 10, Paint.valueOf(Store.Colors.lightestGray)));

                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
            }
        });




        //graphics
        for (Button b:editBtns){
            int index = editBtns.indexOf(b);

            b.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS, 15, Paint.valueOf(Store.Colors.lightestGray)));
            b.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("set_subjects")));

            b.setOnAction(e-> {
                try {
                    changeCategorySubjects(editLview.get(index),editFields.get(index),previewTiles.get(index));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }



        compulsoryBtn.setGraphic(ProjectUtils.createFontIcon(MaterialDesignP.PLUS, 15, Paint.valueOf(Store.Colors.lightestGray)));
        compulsoryBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("compulsory_btn_tp")));
        compulsoryBtn.setOnAction(e -> {
            try {
                changeCategorySubjects(compulsorylv, new TextField(),new FlowPane(),true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        iconView.setImage(ResourceUtil.getImageFromResource("images/plus.png", 50, 50, true));


        // add cycles
        cyclecombo.getItems().addAll(Store.supportedCycles);
        levelcombo.getItems().addAll(Store.supportedLevels);

        List<String> sectionStrings = PgConnector.listHashAttrs(PgConnector.
                fetch("select * from sections order by section_name", PgConnector.getConnection()), "section_name");
        sectioncombo.getItems().addAll(sectionStrings);

        sectioncombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<>() {

                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (!b) {
                            setText(ProjectUtils.capitalize(s));
                            setGraphic(null);
                        } else {
                            setText(null);
                            setGraphic(null);

                        }
                    }
                };
            }
        });

        sectioncombo.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(String s, boolean b) {
                super.updateItem(s, b);
                if (!b) {
                    setText(ProjectUtils.capitalize(s));
                    setGraphic(ProjectUtils.createFontIcon(MaterialDesignC.CIRCLE_DOUBLE, 15, Paint.valueOf(Store.Colors.lightestGray)));
                } else {
                    setText(null);
                    setGraphic(null);

                }
            }
        });



        classmastercombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<>() {

                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (!b) {
                            setText(ProjectUtils.capitalize(s));
                            setGraphic(null);
                        } else {
                            setText(null);
                            setGraphic(null);

                        }
                    }
                };
            }
        } );




        List<String> masterStrings = PgConnector.listHashAttrs(PgConnector.
                fetch("select * from employees where employee_category='teaching_staff' order by first_lastname", PgConnector.getConnection()), "first_lastname");
        classmastercombo.getItems().addAll(masterStrings);
        classmastercombo.getStyleClass().add("alt-icon");
        infogrid.add(classmastercombo, 3, 3);


        formatContainer.getChildren().add(formatsCombo);
        formatsCombo.setMinWidth(200);
        formatsCombo.setPromptText(Translator.getIntl("select"));

        formatsCombo.setItems(FXCollections.observableList(PgConnector.fetch("select * from classes order by level , classname",PgConnector.getConnection())));

        formatsCombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<HashMap<String, Object>> call(ListView<HashMap<String, Object>> hashMapListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(HashMap<String, Object> item, boolean b) {
                        super.updateItem(item, b);
                        if (!b) {
                            setText(String.format("%s (%s)", PgConnector.getFielorBlank(item, "classname").toUpperCase(),
                                    PgConnector.getFielorBlank(item, "class_abbreviation").toUpperCase()));
                            setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CIRCLE_MULTIPLE_OUTLINE, 18, Paint.valueOf(Store.Colors.LightGray)));

                        } else {
                            setText(null);
                            setGraphic(null);

                        }

                    }
                };
            }
        });
        formatsCombo.setButtonCell( new ListCell<>() {
            @Override
            protected void updateItem(HashMap<String, Object> item, boolean b) {
                super.updateItem(item, b);
                if (!b) {
                    setText(String.format("%s (%s)", PgConnector.getFielorBlank(item, "classname").toUpperCase(),
                            PgConnector.getFielorBlank(item, "class_abbreviation").toUpperCase()));
                    setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CIRCLE_MULTIPLE_OUTLINE, 18, Paint.valueOf(Store.Colors.LightGray)));

                } else {
                    setText(null);
                    setGraphic(null);

                }

            }
        });

        //tooltips
        sectioncombo.valueProperty().addListener((o, f, l) -> sectioncombo.setTooltip(ProjectUtils.createTooltip(l.toUpperCase())));
        classMasterP.addListener((o, f, l) -> classmastercombo.setTooltip(ProjectUtils.createTooltip(l.toUpperCase())));
        classnameP.addListener((o, f, l) -> classnamefiled.setTooltip(ProjectUtils.createTooltip(l.toUpperCase())));

        ptaf.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("optional_ptaamount")));




    }


    public void prepareUpdate(int cid) throws SQLException {
        PreparedStatement ps = PgConnector.getConnection().prepareStatement("select * from classes where id=?");
        ps.setInt(1, cid);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String name = rs.getString("classname");;
            String abbreviation =rs.getString("class_abbreviation");
            String section = rs.getString("section");
            int cyc = rs.getInt("cycle");
            int levle = rs.getInt("level");

            String cmaster = rs.getString("class_master");

//            int registrationfee = rs.getInt("registration");
            int ptafee = rs.getInt("pta");
            int schoolfee = rs.getInt("fee");

            //sub groups
           List<String> groupa = PgConnector.parsePgArray(rs,"subjects_ga");
            List<String> groupb = PgConnector.parsePgArray(rs, "subjects_gb");
            List<String> groupc = PgConnector.parsePgArray(rs, "subjects_gc");
            List<String> groupd = PgConnector.parsePgArray(rs, "subjects_gd");
            List<String> gcompulsory = PgConnector.parsePgArray(rs, "compulsory_subjects");

            compulsorylv.getItems().addAll(gcompulsory);

            List<List<String>> stringItems = List.of(groupa, groupb, groupc, groupd);
            System.out.println("prepare updateitems"+ stringItems);


            for (FlowPane pane : previewTiles) {
                int index = previewTiles.indexOf(pane);
                List<String> groupItems = ProjectUtils.getUniqueValues(stringItems.get(index)).stream().filter(s->!s.isEmpty()).toList();

                TextField gname = editFields.get(index);
                ListView<String> lv = editLview.get(index);

                List<String> remainingItems = new ArrayList<>();

                if (groupItems.size() > 1) {
                    gname.setText(groupItems.get(0));
                    remainingItems.addAll(groupItems.subList(1, groupItems.size()));
//                    groupItems = groupItems.subList(1,groupItems.size());
                }

                lv.getItems().addAll(remainingItems);

                pane.getChildren().addAll(remainingItems.stream().map(s -> {
                    Label l = new Label(ProjectUtils.capitalize(s));
                    l.setStyle("-fx-font-weight: " +
                            "bold;-fx-text-fill: #242424;-fx-font-size: 12px;" +
                            "-fx-background-color: transparent;-fx-border-color: #24242490;-fx-border-width: 1px;-fx-padding: 1px;");
                    l.setTooltip(ProjectUtils.createTooltip(s.toUpperCase()));
                    return l;
                }).toList());

            }


            feef.setText(String.valueOf(schoolfee));
            ptaf.setText(String.valueOf(ptafee));
            totalP.set(HrService.formatNumberToCurency(schoolfee + ptafee));


            classnamefiled.setText(name);
            shortnamefield.setText(abbreviation);
            sectioncombo.setValue(section);
            cyclecombo.setValue(cyc);
            levelcombo.setValue(levle);
            classmastercombo.setValue(cmaster);



        }



    }

    public void changeCategorySubjects(ListView<String> lv,TextField textField,FlowPane tilePane,boolean... iscompulsory) throws IOException {


        if (textField.getText().isEmpty()) textField.getStyleClass().add("error-textfield");


        String groupName = textField.getText();
        String popupTitle = groupName.isEmpty() ? Translator.getIntl("select_group_subjects") :
                Translator.getIntl("select_group_subjects") + String.format(" [ %s ]", groupName);
        if (iscompulsory.length > 0) {
            popupTitle = Translator.getIntl("select_compulsory_subjects");
        }

        URL url = ResourceUtil.getAppResourceURL("views/others/list-popup.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
        Parent root =fxmlLoader.load();
        Scene scene = new Scene(root);
//        root.getStylesheets().add(ResourceUtil.getAppResourceURL("css/recaf/all.css").toExternalForm());


        ListDisplayController listDisplayController = fxmlLoader.getController();
        listDisplayController.title.setText(popupTitle);

        listDisplayController.loadDataItems(dbSubjects, lv.getItems());

        listDisplayController.confirmBtn.setOnAction(e->{
            List<String> selectedItems = listDisplayController.dataItems.stream().filter(CheckBox::isSelected).map(CheckBox::getId).sorted().toList();
            lv.getItems().clear();
            lv.setItems(FXCollections.observableList(selectedItems));
        });


        Stage stage = new Stage();
        stage.setTitle("PROMPT");
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(thisStage.get());
        stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
        stage.setResizable(false);

        PopOver subjectsPopup = ProjectUtils.showPopover("", root, PopOver.ArrowLocation.LEFT_CENTER, true, true);

        subjectsPopup.setOnHiding(e->{
            textField.getStyleClass().remove("error-textfield");
            ObservableList<Node> selectItems = listDisplayController.itemcvb.getChildren();
            for (Node n : selectItems) {
                if (((CheckBox) n).isSelected() && textField.getText().isEmpty()) {
                    textField.getStyleClass().add("error-textfield");
                    break;
                }
            }
        });

        listDisplayController.closetP.setOnAction(e->subjectsPopup.hide());


        listDisplayController.confirmBtn.setOnAction(e->{
            List<String> selectedSubs = new ArrayList<>();

            for (Node item : listDisplayController.itemcvb.getChildren()) {
                CheckBox subjectCb = (CheckBox) item;
                if (subjectCb.isSelected()) selectedSubs.add(subjectCb.getId());
            }

            lv.getItems().clear();
            lv.getItems().addAll(selectedSubs);

            // fill preview
            tilePane.setHgap(4);
            tilePane.setVgap(2);
            tilePane.setPadding(new Insets(2));

            tilePane.getChildren().clear();

            for (String s : selectedSubs) {
                Label l = new Label(ProjectUtils.capitalize(s));
                l.setStyle("-fx-font-weight: " +
                        "bold;-fx-text-fill: #242424;-fx-font-size: 12px;" +
                        "-fx-background-color: transparent;-fx-border-color: #24242490;-fx-border-width: 1px;-fx-padding: 1px;");
                l.setTooltip(ProjectUtils.createTooltip(s.toUpperCase()));
                tilePane.getChildren().add(l);

            }

//            stage.close();
            subjectsPopup.hide();

        });



//        stage.show();
        subjectsPopup.show(iscompulsory.length == 0 ? textField : lv);

//        ProjectUtils.positionFloatingStage(thisStage.get(), stage, lv, 20, 0);


    }

    public boolean save() throws SQLException {
        System.out.println("==============  saving class    ===================");

        //check if class exitst
        String checkExist = String.format("""
                select * from classes where classname='%s' or class_abbreviation='%s' """, classnameP.get(), shortNameP.get());
        List<?> res = PgConnector.fetch(checkExist, PgConnector.getConnection());


        if (!res.isEmpty()) {
            String errMessage = String.format("%s \n%s %s", Translator.getIntl("class_exists"), Store.UnicodeSumnbol.rightArrow, classnameP.get().toUpperCase());
            Alert a = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.ERROR, Translator.getIntl("validation_err").toUpperCase(),
                    "ERR", errMessage, ButtonType.CLOSE);
            a.showAndWait();
            return false;

        }


        //validate
        boolean isvalidForm = true;
        StringBuilder errMessageBuilder = new StringBuilder();


        List<ComboBox<?>> numberFields = List.of(cyclecombo,levelcombo,sectioncombo);
        List<TextField> textFields = List.of(classnamefiled,shortnamefield,feef);

        List<String> stringErrors = List.of(
                Translator.getIntl("classname"),
                Translator.getIntl("abbreviation"),
                Translator.getIntl("required_for_human_resource_stats")
        );

        List<String> numberErrors = List.of(
                Translator.getIntl("cycle"), Translator.getIntl("class_level"),
                Translator.getIntl("section"), Translator.getIntl("class_master")
        );


        for (TextField field : textFields) {
            String errt =  stringErrors.get(textFields.indexOf(field));
            field.textProperty().addListener((ob,o,n)->field.getStyleClass().remove("error-textfield"));

            if (field.getText().isEmpty()) {
                field.getStyleClass().add("error-textfield");
                errMessageBuilder.append(Store.UnicodeSumnbol.bullet).append(" ").append(ProjectUtils.capitalize(errt)).append("\n");
                isvalidForm = false;
                ProjectUtils.showPopover("", ProjectUtils.createErrLabel(errt), textFields.indexOf(field)%2==0 ?PopOver.ArrowLocation.LEFT_CENTER :PopOver.ArrowLocation.RIGHT_CENTER, false, true).show(field);

            }else {
                field.getStyleClass().remove("error-textfield");
            }

        }

        for (ComboBox<?> field : numberFields) {
            String errt =  numberErrors.get(numberFields.indexOf(field));
            field.valueProperty().addListener((ob,o,n)->field.getStyleClass().remove("error-textfield"));


            if (field.getValue() ==null) {
                field.getStyleClass().add("error-textfield");
                errMessageBuilder.append(Store.UnicodeSumnbol.bullet).append(" ").append(ProjectUtils.capitalize(errt)).append("\n");
                isvalidForm = false;
                ProjectUtils.showPopover("", ProjectUtils.createErrLabel(errt), numberFields.indexOf(field)%2==0 ?PopOver.ArrowLocation.LEFT_CENTER :PopOver.ArrowLocation.RIGHT_CENTER, false, true).show(field);

            }else {
                field.getStyleClass().remove("error-textfield");
            }

        }


        StringBuilder grounnameErrBuilder = new StringBuilder();

        int missingCount = 0;
        List<String> names = List.of(
                Translator.getIntl("group_a"),
                Translator.getIntl("group_b"),
                Translator.getIntl("group_c"),
                Translator.getIntl("group_d")
        );

        for (ListView<String> view : editLview ) {
            int index =editLview.indexOf(view);
            TextField tf = editFields.get(index);
            if ((!view.getItems().isEmpty()) && tf.getText().isEmpty()) {
                isvalidForm = false;
                grounnameErrBuilder.append(String.format("%s %s\n", Store.UnicodeSumnbol.bullet, names.get(index)));
                missingCount += 1;
                ProjectUtils.showPopover("", ProjectUtils.createErrLabel(names.get(index)),
                        index%2==0 ?PopOver.ArrowLocation.LEFT_CENTER :PopOver.ArrowLocation.RIGHT_CENTER, false, true).show(tf);
            } else {
                // add groupname to the list
                String gname = tf.getText();
                if (!gname.isEmpty()) {

                ListProperty<String> groupProperty = categorySubjectsProperties.get(index);
                groupProperty.add(0, gname);
                }

            }

        }



        if (!isvalidForm)  return false;


        int pta = Objects.equals(null, ptaP.get()) || ptaP.get().isEmpty() ? 0 : Integer.parseInt(ptaP.get());
        int feeamount = Integer.parseInt(feeP.get());

        String query = """
                insert into classes 
                ( classname,class_abbreviation,section,class_master,cycle,level,
                subjects_ga,subjects_gb,subjects_gc,subjects_gd,compulsory_subjects,
                pta,fee
                )  values
                (?,?,?,?,?,?,?,?,?,?,?,?,?)
                
                
                """;

        Connection con = PgConnector.getConnection();
        PreparedStatement insertStatement = con.prepareStatement(query);

        insertStatement.setString(1, classnameP.get());
        insertStatement.setString(2, shortNameP.get());
        insertStatement.setString(3, sectionP.get());
        insertStatement.setString(4, classMasterP.get());

        insertStatement.setInt(5, cycleP.get());
        insertStatement.setInt(6, levelP.get());



        insertStatement.setArray(7, con.createArrayOf("text", ProjectUtils.getUniqueValues(catAsubs.get()).toArray()));
        insertStatement.setArray(8,  con.createArrayOf("text", ProjectUtils.getUniqueValues(catBsubs.get()).toArray()));
        insertStatement.setArray(9, con.createArrayOf("text", ProjectUtils.getUniqueValues(catCsubs.get()).toArray()));
        insertStatement.setArray(10, con.createArrayOf("text", ProjectUtils.getUniqueValues(catDsubs.get()).toArray()));
        insertStatement.setArray(11, con.createArrayOf("text", ProjectUtils.getUniqueValues(compulsorySubsP.get()).toArray()));

        insertStatement.setInt(12, pta);
        insertStatement.setInt(13, feeamount);


        insertStatement.executeUpdate();

        thisStage.get().close();

        Alert successAlert = ProjectUtils.showAlert(mainStage.get(), Alert.AlertType.INFORMATION,
               "INFO", Translator.getIntl("info").toUpperCase(),
                Translator.getIntl("insertion_complete") + String.format(" %s%s %s",Store.UnicodeSumnbol.blank,Store.UnicodeSumnbol.rightArrow,classnameP.get().toUpperCase()), ButtonType.OK);
        successAlert.showAndWait();

        // clear fields
        classnamefiled.setText("");
        shortnamefield.setText("");

        classnamefiled.requestFocus();

        return true;




    }

    public boolean update(int cid) throws SQLException {
        System.out.println("==============  saving class    ===================");

        //validate
        boolean isvalidForm = true;
        StringBuilder errMessageBuilder = new StringBuilder();


        List<ComboBox<?>> numberFields = List.of(cyclecombo,levelcombo,sectioncombo);
        List<TextField> textFields = List.of(classnamefiled,shortnamefield,feef);

        List<String> stringErrors = List.of(
                Translator.getIntl("classname"), Translator.getIntl("abbreviation"),
                Translator.getIntl("required_for_human_resource_stats")

        );

        List<String> numberErrors = List.of(
                Translator.getIntl("cycle"), Translator.getIntl("class_level"),
                Translator.getIntl("section"), Translator.getIntl("class_master")
        );


        for (TextField field : textFields) {
            String errt =  stringErrors.get(textFields.indexOf(field));
            field.textProperty().addListener((ob,o,n)->field.getStyleClass().remove("error-textfield"));

            if (field.getText().isEmpty()) {
                field.getStyleClass().add("error-textfield");
                errMessageBuilder.append(Store.UnicodeSumnbol.bullet).append(" ").append(ProjectUtils.capitalize(errt)).append("\n");
                isvalidForm = false;
            }else {
                field.getStyleClass().remove("error-textfield");
            }

        }

        for (ComboBox<?> field : numberFields) {
            String errt =  numberErrors.get(numberFields.indexOf(field));
            field.valueProperty().addListener((ob,o,n)->field.getStyleClass().remove("error-textfield"));


            if (field.getValue() ==null) {
                field.getStyleClass().add("error-textfield");
                errMessageBuilder.append(Store.UnicodeSumnbol.bullet).append(" ").append(ProjectUtils.capitalize(errt)).append("\n");
                isvalidForm = false;
            }else {
                field.getStyleClass().remove("error-textfield");
            }

        }


        StringBuilder grounnameErrBuilder = new StringBuilder();

        int missingCount = 0;
        List<String> names = List.of(
                Translator.getIntl("group_a"),
                Translator.getIntl("group_b"),
                Translator.getIntl("group_c"),
                Translator.getIntl("group_d")
        );

        for (ListView<String> view : editLview ) {
            int index =editLview.indexOf(view);
            TextField tf = editFields.get(index);
            if ((!view.getItems().isEmpty()) && tf.getText().isEmpty()) {
                isvalidForm = false;
                grounnameErrBuilder.append(String.format("%s %s\n", Store.UnicodeSumnbol.bullet, names.get(index)));
                missingCount += 1;
            } else {
                // add groupname to the list
                String gname = tf.getText();
                ListProperty<String> groupProperty = categorySubjectsProperties.get(index);
                groupProperty.add(0, gname);

            }


        }

        if (missingCount>0){
            errMessageBuilder.append("\n");
            for (int i = 0; i < 18; i++) errMessageBuilder.append(Store.UnicodeSumnbol.dash).append(" ");
            errMessageBuilder.append("\n").append(String.format("%d %s %s\n", missingCount,Store.UnicodeSumnbol.blank, Translator.getIntl("labels_missing")));
            errMessageBuilder.append(grounnameErrBuilder.toString());

        }






        if (!isvalidForm) {
            Alert errAlert = ProjectUtils.showAlert(thisStage.get(), Alert.AlertType.ERROR,
                    Translator.getIntl("invalid_form"), "ERR INFO", errMessageBuilder.toString(), ButtonType.CLOSE);
            errAlert.showAndWait();
            return false;
        }


        int pta = Objects.equals(null, ptaP.get()) || ptaP.get().isEmpty() ? 0 : Integer.parseInt(ptaP.get());
        int feeamount = Integer.parseInt(feeP.get());

        String updateQuery = """
                update classes set classname=?,class_abbreviation=?,section=?,class_master=?,cycle=?,
                level=?,subjects_ga=?,subjects_gb=?,subjects_gc=?,subjects_gd=?,compulsory_subjects=?,
                pta=?,fee=?
                 where id=?
                """;


        Connection con = PgConnector.getConnection();
        PreparedStatement insertStatement = con.prepareStatement(updateQuery);

        insertStatement.setString(1, classnameP.get());
        insertStatement.setString(2, shortNameP.get());
        insertStatement.setString(3, sectionP.get());
        insertStatement.setString(4, classMasterP.get());

        insertStatement.setInt(5, cycleP.get());
        insertStatement.setInt(6, levelP.get());


        insertStatement.setArray(7, con.createArrayOf("text", ProjectUtils.getUniqueValues(catAsubs.get()).toArray()));
        insertStatement.setArray(8,  con.createArrayOf("text", ProjectUtils.getUniqueValues(catBsubs.get()).toArray()));
        insertStatement.setArray(9, con.createArrayOf("text", ProjectUtils.getUniqueValues(catCsubs.get()).toArray()));
        insertStatement.setArray(10, con.createArrayOf("text", ProjectUtils.getUniqueValues(catDsubs.get()).toArray()));
        insertStatement.setArray(11, con.createArrayOf("text", ProjectUtils.getUniqueValues(compulsorySubsP.get()).toArray()));

        insertStatement.setInt(12, pta);
        insertStatement.setInt(13, feeamount);

        insertStatement.setInt(14, cid);

        System.out.println(insertStatement);

        insertStatement.executeUpdate();
        thisStage.get().close();


        Alert successAlert = ProjectUtils.showAlert(mainStage.get(), Alert.AlertType.INFORMATION,
                "INFO", Translator.getIntl("info").toUpperCase(),
                Translator.getIntl("update_complete") + String.format(" %s%s %s",
                        Store.UnicodeSumnbol.blank,Store.UnicodeSumnbol.rightArrow,classnameP.get().toUpperCase()), ButtonType.OK);
        successAlert.showAndWait();

        return true;


    }


}
