package smpro.app.services;

import javafx.animation.FadeTransition;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.EntryController;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;
import smpro.app.controllers.AddPayeditemController;
import smpro.app.controllers.AddStudentController;
import smpro.app.controllers.GenericDialogController;
import smpro.app.controllers.SingleScoreViewController;
import smpro.app.custom_nodes.ClassSectionsTreeview;
import smpro.app.custom_nodes.CustomTableView;
import smpro.app.custom_nodes.CustomToolbarActionGroup;
import smpro.app.utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

import static smpro.app.utils.ProjectUtils.applyDialogCaption;

public class HrService {
    BooleanProperty isLoaded = new SimpleBooleanProperty(false);

    private final Stage mainStage;


    public ClassSectionsTreeview studentSections = new ClassSectionsTreeview(true);
    public CustomTableView feeHistoryView;
    public CustomTableView transactionslogView;
    public CustomTableView expenseView;

    List<Node> toolbarItems = new ArrayList<>();

    RadioButton toggleAllcb;
    Button refreshBtn;
    Button deleteSelections;
    Button editRecord;
    Button genBalanceSheetBtn;
    Button addRevenueSourceBtn;
    Button addExpenseSourceBtn;
    Button trackFeeBtn;

    EntryController entryController;



    ////////////// CREATED NOTES
    SearchableComboBox<HashMap<String,Object>> feeStudentSearch;
    ObjectProperty<Image> displayImagP = new SimpleObjectProperty<>();

    //FEE UPDATES
    StringProperty registrationP = new SimpleStringProperty("");
    StringProperty ptaP = new SimpleStringProperty("");
    StringProperty feeP = new SimpleStringProperty("");

    IntegerProperty amountLeftProperty = new SimpleIntegerProperty(0);

    ObjectProperty<HashMap<String, Integer>> payedItemsMapP = new SimpleObjectProperty<>(new HashMap<>());







    //preperties
    public HrService(Stage s, EntryController entryController) {
        this.entryController = entryController;
        this.mainStage = s;

        initUi();

    }

    public List<Node> buildToolbarOptions(){

        //register group
        CustomToolbarActionGroup registerGrou = new CustomToolbarActionGroup();

        addRevenueSourceBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/plus.png", Store.TOOBAR_ICONSIZE+15,
                Store.TOOBAR_ICONSIZE+20, true)));
        registerGrou.addActions(ProjectUtils.capitalize(Translator.getIntl("add_revenue_source")), null, addRevenueSourceBtn);

        addExpenseSourceBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/plus.png", Store.TOOBAR_ICONSIZE+15,
                Store.TOOBAR_ICONSIZE+20, true)));
        registerGrou.addActions(ProjectUtils.capitalize(Translator.getIntl("add_expense_source")), null, addExpenseSourceBtn);


        //selection_edit group
        CustomToolbarActionGroup selectionEditGroup = new CustomToolbarActionGroup();

        genBalanceSheetBtn = new Button("",new ImageView(ResourceUtil.getImageFromResource("images/balance_sheet.png",
                Store.TOOBAR_ICONSIZE+10, Store.TOOBAR_ICONSIZE+10, true)));
        selectionEditGroup.addActions(Translator.getIntl("balance_sheet"),null, genBalanceSheetBtn);


        //////////////////////////////////
        deleteSelections = new Button("", ProjectUtils.createFontIconColored(MaterialDesignT.TRASH_CAN, Store.TOOBAR_ICONSIZE, Paint.valueOf(Store.Colors.red)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("delete_transactions")),
                null,deleteSelections);
        deleteSelections.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
        ////////////////////////////////

        editRecord = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/edit_large.png",
                Store.TOOBAR_ICONSIZE, Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("edit_record")),
                null, editRecord);




        //////////////////////////////
        trackFeeBtn= new Button("",new ImageView(ResourceUtil.getImageFromResource("images/edit.png", Store.TOOBAR_ICONSIZE,
                Store.TOOBAR_ICONSIZE, true)));
        selectionEditGroup.addActions(ProjectUtils.capitalize(Translator.getIntl("track_fee")),
                null,trackFeeBtn);

        ////////////////////////////////





        //refresh gorup
        CustomToolbarActionGroup refreshgroup = new CustomToolbarActionGroup();


        Label currencyLabel = new Label("XAF", new ImageView(ResourceUtil.getImageFromResource("images/cmr.png", 15, 15, true)));
        currencyLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 16;-fx-padding: 0 10 0 0");
        refreshgroup.addActions(ProjectUtils.capitalize(Translator.getIntl("currency")),
                null,currencyLabel);
//        currencyBtn.getStyleClass().add("transparent-bg");

        refreshBtn = new Button("", new ImageView(ResourceUtil.getImageFromResource("images/refresh.png", Store.TOOBAR_ICONSIZE+15,
                Store.TOOBAR_ICONSIZE+15, true)));
        refreshgroup.addActions(ProjectUtils.capitalize(Translator.getIntl("refresh_display")),
                null,refreshBtn);
        refreshBtn.setStyle("-fx-background-color: transparent");


        for (Button b : new Button[]{addRevenueSourceBtn,addExpenseSourceBtn, genBalanceSheetBtn, trackFeeBtn,deleteSelections,
                editRecord,refreshBtn}) {
            b.setCursor(Cursor.HAND);
            b.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
            b.getStyleClass().add("toolbar-btn");
        }



        toolbarItems.add(registerGrou.build(1));
        toolbarItems.add(selectionEditGroup.build(2));
        toolbarItems.add(refreshgroup.build(1));

        addExpenseSourceBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("addexpensetp")));
        addRevenueSourceBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("addrevenuetp")));
        genBalanceSheetBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("gen_balancesheet_tp")));
        editRecord.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("edit_transactiontp")));
        deleteSelections.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("delete_selectedtractionstp")));
        trackFeeBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("trackfee_tp")));

        return toolbarItems;



    }


    public void initUi() {
        configureTables();
        entryController.hrsearchHelp.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/search.png", 20, 20, true)));

        entryController.hrImagpreview.setImage(ResourceUtil.getImageFromResource("images/44image.png", (int) entryController.hrImagpreview.getFitWidth(),
                (int) entryController.hrImagpreview.getFitHeight(),true));

        entryController.transactionRecordsTab.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignD.DATABASE, 10, Paint.valueOf(Store.Colors.LightGray)));
        entryController.payfeeTab.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CASH_CHECK, 10, Paint.valueOf(Store.Colors.LightGray)));
        entryController.paySalarytab.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CASH_REFUND, 10, Paint.valueOf(Store.Colors.LightGray)));

        entryController.hrMatriculeLabel.setText("\u2796\u2796\u2796\u2796\u2796\u2796\u2796");
        entryController.hrclassLabel.setText("\u2796\u2796\u2796\u2796\u2796\u2796\u2796\u2796\u2796");
        entryController.hrdob.setText("\u2796\u2796\u2796\u2796\u2796\u2796\u2796");

        entryController.hradmission.setText("\u2796\u2796\u2796\u2796\u2796\u2796\u2796");

        for (TextField tf : new TextField[]{entryController.feeEntryf, entryController.ptaEntryf, entryController.registrationf}) {
            tf.textProperty().addListener((o, f, l) -> tf.setText(l.replaceAll("\\D", "")));


        }

        entryController.hrAdmininfol.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignL.LIGHTBULB, 10, Paint.valueOf(Store.Colors.White)));

        feeStudentSearch = new SearchableComboBox<>();

        feeStudentSearch.getItems().clear();
        entryController.hrStudentsearchcontainer.getChildren().add(1,feeStudentSearch);

        feeStudentSearch.setPromptText(Translator.getIntl("type_"));
        feeStudentSearch.getItems().addAll(PgConnector.fetch("select * from students order by firstname,lastname", PgConnector.getConnection()));

        feeStudentSearch.setCellFactory(hashMapListView -> new ListCell<>(){
            @Override
            protected void updateItem(HashMap<String, Object> stringObjectHashMap, boolean b) {
                super.updateItem(stringObjectHashMap, b);
                if (!b) {
                    setText(ProjectUtils.capitalize(String.format("%s %s",
                            PgConnector.getFielorBlank(stringObjectHashMap, "firstname"),
                            PgConnector.getFielorBlank(stringObjectHashMap, "lastname")
                    )));
                    setStyle("-fx-font-weight: bold");
                    String gender = PgConnector.getFielorBlank(stringObjectHashMap, "gender");
                    Node g;
                    if (gender.equalsIgnoreCase("m")) {
                        g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_MALE, 18, Paint.valueOf("lightgray"));
                    } else if (gender.equalsIgnoreCase("f")){
                        g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_FEMALE, 18, Paint.valueOf("lightgray"));

                    }else g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_FEMALE, 18, Paint.valueOf("lightgray"));


                } else {
                    setText(null);
                    setGraphic(null);
                } ;
            }
        });

        feeStudentSearch.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(HashMap<String, Object> stringObjectHashMap, boolean b) {
                super.updateItem(stringObjectHashMap, b);
                if (!b) {
                    setText(ProjectUtils.capitalize(String.format("%s %s",
                            PgConnector.getFielorBlank(stringObjectHashMap,"firstname"),
                            PgConnector.getFielorBlank(stringObjectHashMap,"lastname")

                    ).toUpperCase()));
                    setStyle("-fx-font-weight: bold");

                    String gender = PgConnector.getFielorBlank(stringObjectHashMap, "gender");
                    Node g;
                    if (gender.equalsIgnoreCase("m")) {
                        g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_MALE, 18, Paint.valueOf("lightgray"));
                    } else if (gender.equalsIgnoreCase("f")){
                        g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_FEMALE, 18, Paint.valueOf("lightgray"));

                    }else g = ProjectUtils.createFontIcon(MaterialDesignG.GENDER_FEMALE, 18, Paint.valueOf("lightgray"));



                } else {
                    setText(null);
                    setGraphic(null);
                } ;
            }
        });
        feeStudentSearch.setStyle("-fx-border-width:1;-fx-border-color:  #57aaff");

        entryController.hrprintreceipt.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/printer_thermal.png", 30, 30, true)));
        entryController.addsaleBtn.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PLUS_THICK, 20, Paint.valueOf(Store.Colors.LightGray)));
        entryController.hrvalidate.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CHECK_BOLD, 15, Paint.valueOf(Store.Colors.LightGray)));



        //populate payment methods
        entryController.hrmethodCombo.getItems().clear();
        entryController.hrmethodCombo.getItems().addAll(Store.supportedPaymentMethods.keySet());

        entryController.hrmethodCombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> hashMapListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean b) {
                        super.updateItem(item, b);
                        if (!b) {
                            setText(Translator.getIntl(item));
                            setGraphic(ProjectUtils.createFontIcon(Store.supportedPaymentMethods.get(item), 18, Paint.valueOf(Store.Colors.LightGray)));
                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
            }
        });

        entryController.hrmethodCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean b) {
                super.updateItem(item, b);
                if (!b) {
                    setText(Translator.getIntl(item));
                    setGraphic(ProjectUtils.createFontIcon(Store.supportedPaymentMethods.get(item), 18, Paint.valueOf(Store.Colors.LightGray)));
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
//        entryController.hrmethodCombo.setValue(Translator.getIntl("cash"));
        entryController.hrmethodCombo.setPromptText(Translator.getIntl("select"));



        entryController.hrprintreceipt.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("print_receipttp")));
        entryController.addsaleBtn.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("addsalestp")));
        entryController.hrvalidate.setTooltip(ProjectUtils.createTooltip(Translator.getIntl("hrvalidatetp")));




        isLoaded.set(true);
    }

    public  void configureTables() {

            //set colums
        TableColumn<HashMap<String, Object>, String> motivecol = ProjectUtils.createTableColumn(Translator.getIntl("payment_for"), "motive", true);
        TableColumn<HashMap<String, Object>, String> amountcol = ProjectUtils.createTableColumn(Translator.getIntl("amount"), "amount", true);
        TableColumn<HashMap<String, Object>, String> datecol = ProjectUtils.createTableColumn(Translator.getIntl("date"), "date");
        TableColumn<HashMap<String, Object>, String> mehodcol = ProjectUtils.createTableColumn(Translator.getIntl("payment_method"), "payment_method");
        feeHistoryView = new CustomTableView(List.of(motivecol,amountcol,datecol,mehodcol));

//        TableColumn<HashMap<String, Object>, String> motivecol2 = ProjectUtils.createTableColumn(Translator.getIntl("payment_for"), "motive", true);
//        TableColumn<HashMap<String, Object>, String> amountcol2 = ProjectUtils.createTableColumn(Translator.getIntl("amount"), "amount", true);
//        TableColumn<HashMap<String, Object>, String> datecol2 = ProjectUtils.createTableColumn(Translator.getIntl("date"), "date");
//        TableColumn<HashMap<String, Object>, String> mehodcol2 = ProjectUtils.createTableColumn(Translator.getIntl("payment_method"), "payment_method");
//        TableColumn<HashMap<String, Object>, String> itemcol = ProjectUtils.createTableColumn(Translator.getIntl("item"), "item");
//        sales_RequirementsView = new CustomTableView(List.of(itemcol,amountcol2,datecol2,mehodcol2));

        for (TableColumn<HashMap<String, Object>, String> col : new TableColumn[]{ mehodcol}) {
            col.setCellFactory(hashMapStringTableColumn -> new TableCell<>() {
                @Override
                protected void updateItem(String s, boolean b) {
                    super.updateItem(s, b);

                    if (!b) {
                        setText(ProjectUtils.capitalize(s));
                        Ikon code = Store.supportedPaymentMethods.get(s.toLowerCase().replaceAll(" ","_"))
                                == null ? MaterialDesignC.CIRCLE_MULTIPLE : Store.supportedPaymentMethods.get(s.toLowerCase().strip().replaceAll(" ","_"));
                        setGraphic(ProjectUtils.createFontIcon(code, 12, Paint.valueOf("lightgray")));
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            });
        }

        motivecol.setMinWidth(130);
        datecol.setMinWidth(200);
        amountcol.setMinWidth(120);

        mehodcol.setMinWidth(150);

        feeHistoryView.filteredItemsProperty.set(FXCollections.observableList(new ArrayList<>()));

        entryController.feehistoryContainer.getChildren().add(feeHistoryView);

        feeHistoryView.getStyleClass().remove("striped");

    }


    public void toggleEnableEntry(boolean toggle) {

        if (!toggle) {
            for (TextField t : new TextField[]{entryController.ptaEntryf, entryController.feeEntryf, entryController.registrationf}) {
                t.setDisable(true);
            }
            entryController.addsaleBtn.setDisable(true);
            entryController.hrvalidate.setDisable(true);

            feeHistoryView.filteredItemsProperty.set(FXCollections.observableList(new ArrayList<>()));

        } else {
            for (TextField t : new TextField[]{entryController.ptaEntryf, entryController.feeEntryf, entryController.registrationf}) {
                t.setDisable(false);
            }
            entryController.addsaleBtn.setDisable(false);
            entryController.hrvalidate.setDisable(false);

        }
    }


    public void bindFields() {
        entryController.hrsearchHelp.setOnAction(e -> {
//            PopOver helpP = ProjectUtils.showPopover("", ProjectUtils.createInfoLabel(Translator.getIntl("hrsearchhelp")), PopOver.ArrowLocation.BOTTOM_CENTER, false, true);
//            helpP.show(feeStudentSearch);
            feeStudentSearch.getItems().clear();
            feeStudentSearch.setItems(FXCollections.observableList(PgConnector.fetch("select * from students order by firstname,lastname",PgConnector.getConnection())));
            feeStudentSearch.requestFocus();
            toggleEnableEntry(false);

        });
        entryController.hrvalidate.setOnAction(e -> saveNewPayments());

        feeStudentSearch.valueProperty().addListener((o,f,n)->loadStudentInfo());

        entryController.hrImagpreview.imageProperty().bind(displayImagP);
        displayImagP.set(ResourceUtil.getImageFromResource("images/44image.png", (int) entryController.hrImagpreview.getFitWidth(),
                (int) entryController.hrImagpreview.getFitHeight(),true));

        feeP.bind(entryController.feeEntryf.textProperty());
        registrationP.bind(entryController.registrationf.textProperty());
        ptaP.bind(entryController.ptaEntryf.textProperty());

        feeP.addListener((observableValue, s, t1) -> {
            try {
                updateTotal();
            } catch (Exception ner) {
                ner.printStackTrace();
            }
        });

        ptaP.addListener((observableValue, s, t1) -> {
            try {
                updateTotal();
            } catch (Exception ner) {
                ner.printStackTrace();
            }
        });

        registrationP.addListener((observableValue, s, t1) -> {
            try {
                updateTotal();
            } catch (Exception ner) {
                ner.printStackTrace();
            }
        });

        entryController.addsaleBtn.setOnAction(e -> addPayedItem());




    }

    public void updateTotal() {

        double total = 0;
        try {


            for (StringProperty p : new StringProperty[]{ptaP, feeP, registrationP}) {
                if (!p.get().isEmpty()) total += Integer.parseInt(p.get());
            }
        } catch (NumberFormatException nerr) {
            System.err.println(nerr.getLocalizedMessage());
        }

        // sales and requiremetns
        HashMap<String, Integer> salesItems = payedItemsMapP.get();

        GridPane itemNode = new GridPane();
        itemNode.setHgap(15);
        itemNode.setVgap(2);
        int row=0;
        for (String key : new ArrayList<>(salesItems.keySet()).stream().sorted().toList()) {
            Integer amount = salesItems.get(key);
            total += amount;

            //append item
            Button removeBtn = new Button("", ProjectUtils.createFontIconColored(MaterialDesignD.DELETE, 8, Paint.valueOf(Store.Colors.red)));
            removeBtn.setStyle("-fx-background-color: transparent;-fx-border-width: 0");
            removeBtn.setOnAction(e->{
                salesItems.remove(key);
                updateTotal();

            });
            Label nameL = new Label(ProjectUtils.capitalize(key));
            Label amountL = new Label(formatNumberToCurency(amount));

            nameL.setStyle("-fx-font-weight: bold;-fx-min-width: 120");
            amountL.setStyle("-fx-font-weight: bold");

            itemNode.add(removeBtn, 0, row);
            itemNode.add(nameL,1,row);
            itemNode.add(amountL,2,row);
            row += 1;

        }
        entryController.salesitemscontainer.getChildren().clear();
        entryController.salesitemscontainer.getChildren().add(itemNode);

        entryController.totalenteredl.setText(formatNumberToCurency(total));
    }


//////////////////////////////////////////////////////////////////
    public void loadStudentInfo() {
        toggleEnableEntry(true);
        entryController.salesitemscontainer.getChildren().clear();
        payedItemsMapP.set(new HashMap<>());

        entryController.feeEntryf.clear();
        entryController.ptaEntryf.setText("0");
        entryController.registrationf.setText("0");

        entryController.totalenteredl.setText(formatNumberToCurency(0));

        entryController.hrdob.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CALENDAR_TODAY, 20, Paint.valueOf("#bbbbbb80")));
        entryController.hrMatriculeLabel.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CARD_ACCOUNT_DETAILS, 20, Paint.valueOf("#bbbbbb80")));

        HashMap<String, Object> studentObj = feeStudentSearch.getValue();
        if (Objects.equals(null,studentObj)) return;
        HashMap<String, Object> classObj = ProjectUtils.getObject(PgConnector.getFielorBlank(studentObj, "classid"), "classes");

        String classname = PgConnector.getFielorBlank(classObj, "classname");
        String classAbbreviation = PgConnector.getFielorBlank(classObj, "class_abbreviation");

        HashMap<String, Object> tradeObj = PgConnector.getObjectFromKey("trade_name", PgConnector.getFielorBlank(studentObj, "trade"), "trades");
        String tradeAbbr = PgConnector.getFielorBlank(tradeObj, "trade_abbreviation");
        String tradeName = PgConnector.getFielorBlank(tradeObj, "trade_name");

        String matricule = PgConnector.getFielorBlank(studentObj, "matricule");
        String formatedDob = ProjectUtils.getFormatedDate(PgConnector.getNumberOrNull(studentObj, "date_of_birth").longValue(),
                DateFormat.getDateInstance(DateFormat.LONG, Translator.getLocale()));
        String formatedAdm = ProjectUtils.getFormatedDate(PgConnector.getNumberOrNull(studentObj, "admission_date").longValue(),
                DateFormat.getDateInstance(DateFormat.LONG, Translator.getLocale()));

        entryController.hrclassLabel.setText(String.format("%s (%s)", classname.toUpperCase(), classAbbreviation));
        entryController.hrMatriculeLabel.setText(matricule.toUpperCase());
        entryController.hrdob.setText(formatedDob.toUpperCase());
        entryController.hradmission.setText(formatedAdm.toUpperCase());




        //reset and set image
        displayImagP.set(ResourceUtil.getImageFromResource("images/44image.png", (int) entryController.hrImagpreview.getFitWidth(),
                (int) entryController.hrImagpreview.getFitHeight(),true));

        try {
            PreparedStatement ps = PgConnector.getConnection().prepareStatement("select image from students where id=?");
            ps.setInt(1, PgConnector.getNumberOrNull(studentObj, "id").intValue());

            InputStream is = PgConnector.readBinarydata(ps);
            if (!Objects.equals(null, is)) {
                displayImagP.set(new Image(is, entryController.hrImagpreview.getFitWidth(), entryController.hrImagpreview.getFitHeight(), true, true));

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        /////////   fill class tuition
        Integer classfee = PgConnector.getNumberOrNull(classObj, "fee").intValue();
//        Integer classregistration = PgConnector.getNumberOrNull(classObj, "registration").intValue();
        Integer ptafee = PgConnector.getNumberOrNull(classObj, "pta").intValue();

        entryController.registrationl.setText(tradeAbbr);
        entryController.registrationl.setTooltip(ProjectUtils.createTooltip(ProjectUtils.capitalize(tradeName)));

        entryController.ptal.setText(formatNumberToCurency(ptafee));
        entryController.feeAmount.setText(formatNumberToCurency(classfee));

        Number tuitionTotal = classfee + ptafee;
        entryController.fee_ptatoall.setText(formatNumberToCurency(tuitionTotal));

        //get Hr records (fees,sales,registrations,pta)
        List<HashMap<String, Object>> studentFees = PgConnector.fetch(String.format("select * from hr_fees where student_matricule='%s' order by date", matricule), PgConnector.getConnection());
        List<HashMap<String, Object>> studentSales = PgConnector.fetch(String.format("select * from hr_sales_requirements where student_matricule='%s' order by date", matricule),
                PgConnector.getConnection());
        List<HashMap<String, Object>> studentRegistration = PgConnector.fetch(String.format("select * from hr_registrations where student_matricule='%s' order by date", matricule),
                PgConnector.getConnection());
        List<HashMap<String, Object>> studentPta = PgConnector.fetch(String.format("select * from hr_pta where student_matricule='%s' order by date", matricule), PgConnector.getConnection());

        Number feesPayed = PgConnector.aggregateNumericFieldsAndSum(studentFees, "amount");
        Number ptaPayed = PgConnector.aggregateNumericFieldsAndSum(studentPta, "amount");
        Number registrationPayed = PgConnector.aggregateNumericFieldsAndSum(studentRegistration, "amount");

        int tuitionPayed = feesPayed.intValue() + ptaPayed.intValue();

        entryController.payedl.setText(formatNumberToCurency(tuitionPayed));
        entryController.duel.setText(formatNumberToCurency(tuitionTotal.intValue() - tuitionPayed));

        amountLeftProperty.set(classfee - feesPayed.intValue());


        if (registrationPayed.intValue() > 0) {
            registrationP.unbind();
            entryController.hrRegistrationtick.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CHECK_BOLD, 12, Paint.valueOf(Store.Colors.green)));
            entryController.registrationf.setDisable(true);

        } else {
            registrationP.bind(entryController.registrationf.textProperty());
            entryController.hrRegistrationtick.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PENCIL, 12, Paint.valueOf(Store.Colors.LightGray)));
            entryController.registrationf.setDisable(false);

        }

        if (ptaPayed.intValue() > 0) {
            ptaP.unbind();
            entryController.ptaEntryf.setDisable(true);
            entryController.hrPtatick.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CHECK_BOLD, 12, Paint.valueOf(Store.Colors.green)));
        } else {
            ptaP.bind(entryController.ptaEntryf.textProperty());
            entryController.hrPtatick.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PENCIL, 12, Paint.valueOf(Store.Colors.LightGray)));
            entryController.ptaEntryf.setDisable(false);
        }

        if (feesPayed.intValue() < classfee.intValue()) {
            feeP.bind(entryController.feeEntryf.textProperty());
            entryController.feeEntryf.setDisable(false);
            entryController.hrFeetick.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignP.PENCIL, 12, Paint.valueOf(Store.Colors.LightGray)));
        } else {
            feeP.unbind();
            entryController.hrFeetick.setGraphic(ProjectUtils.createFontIconColored(MaterialDesignC.CHECK_BOLD, 12, Paint.valueOf(Store.Colors.green)));
            entryController.feeEntryf.setDisable(true);

        }

        entryController.ptaEntryf.setText(String.valueOf(ptaPayed.intValue()));
        entryController.registrationf.setText(String.valueOf(registrationPayed.intValue()));



        entryController.feeEntryf.clear();
        entryController.feeEntryf.requestFocus();

        //load side tables
        FadeTransition ffees = new FadeTransition(Duration.millis(500), feeHistoryView);

        ffees.setToValue(1);
        ffees.setFromValue(0);


        if (!Objects.equals(feeHistoryView,null)) feeHistoryView.setOpacity(0);

        Platform.runLater(() -> {
        loadFeeHistoryTable(studentRegistration, studentPta, studentFees,studentSales);
        ffees.playFromStart();
        });




    }

    public void loadFeeHistoryTable(List<HashMap<String, Object>>
                                            registrationsData, List<HashMap<String, Object>> ptaData,
                                    List<HashMap<String, Object>> feesData,List<HashMap<String, Object>> salesData) {
        System.out.println("LOADING FEES  HISTORY TABLE");

        //parsedata
        List<HashMap<String, Object>> data = new ArrayList<>();

        data.addAll(parseResTodata(registrationsData, Translator.getIntl("registration")));
        data.addAll(parseResTodata(ptaData, Translator.getIntl("pta")));
        data.addAll(parseResTodata(feesData, Translator.getIntl("fees")));
        data.addAll(parseSalesResTodata(salesData));

        System.out.println("all data collected ");
        data.forEach(System.out::println);
        feeHistoryView.filteredItemsProperty.set(FXCollections.observableList(new ArrayList<>(data)));


    }



    public List<HashMap<String, Object>> parseResTodata(List<HashMap<String, Object>>
                                                                input,String motive,String...extraKeys) {
        List<HashMap<String, Object>> out = new ArrayList<>();
        for (HashMap<String, Object> item : input) {
            HashMap<String, Object> outItem = new HashMap<>();

            outItem.put("motive", motive);
            outItem.put("date", ProjectUtils.getFormatedDate(Long.parseLong(PgConnector.
                    getFielorBlank(item, "date")), DateFormat.getDateInstance(DateFormat.FULL, Translator.getLocale())));
            outItem.put("amount", formatNumberToCurency(Integer.parseInt(PgConnector.getFielorBlank(item, "amount"))));
            outItem.put("payment_method", Translator.getIntl(PgConnector.getFielorBlank(item, "payment_method").toLowerCase()));
            if (extraKeys.length > 0) {
                outItem.put("item", PgConnector.getFielorBlank(item, "item"));
            }

            out.add(outItem);
        }
        System.out.println("out item returned " + out);
        return out;
    }

    public List<HashMap<String, Object>> parseSalesResTodata(List<HashMap<String, Object>>
                                                                input) {
        List<HashMap<String, Object>> out = new ArrayList<>();
        for (HashMap<String, Object> item : input) {
            HashMap<String, Object> outItem = new HashMap<>();

            outItem.put("motive", PgConnector.getFielorBlank(item,"item"));
            outItem.put("date", ProjectUtils.getFormatedDate(Long.parseLong(PgConnector.
                    getFielorBlank(item, "date")), DateFormat.getDateInstance(DateFormat.FULL, Translator.getLocale())));
            outItem.put("amount", formatNumberToCurency(Integer.parseInt(PgConnector.getFielorBlank(item, "amount"))));
            outItem.put("payment_method", Translator.getIntl(PgConnector.getFielorBlank(item, "payment_method").toLowerCase()));
            out.add(outItem);
        }
        System.out.println("out item returned " + out);
        return out;
    }


    public void addPayedItem() {
        if (Objects.equals(null,feeStudentSearch.getValue())) return;

        URL url = ResourceUtil.getAppResourceURL("views/others/add-sales.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainStage);
        stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
        stage.setResizable(false);
        Store.SingleMarksheetStage.set(stage);

        AddPayeditemController controller = fxmlLoader.getController();
        controller.thisStage.set(stage);
        applyDialogCaption(stage,controller.dragArea);

        controller.confirmBtn.setOnAction(e -> {
            String itemName = controller.itemProperty.get();
            String itemAmount = controller.amountProperty.get();
            System.out.println("name=" + itemName + " amount=" + itemAmount);

            HashMap<String, Integer> currentItems = payedItemsMapP.get();

            if (currentItems.containsKey(itemName)) {
                currentItems.replace(itemName, Integer.parseInt(itemAmount));
            } else currentItems.put(itemName, Integer.parseInt(itemAmount));

            stage.close();
            updateTotal();


        });

        scene.getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        stage.showAndWait();



    }


//////////////////////////////////////////////////////////////////


    public void saveNewPayments() {
        // bypass empty payments
        if (formatCurrentToNumber(entryController.totalenteredl.getText()).intValue()==0) return;

        BooleanProperty isvalidadmin = new SimpleBooleanProperty(false);

        TextInputDialog confirmdlg = ProjectUtils.getTextDialog(mainStage, "AUTHENTICATION", Translator.getIntl("validate_payment") + " "+entryController.totalenteredl.getText(),
                Translator.getIntl("enter_password_confirm"), new ImageView(ResourceUtil.getImageFromResource("images/lock.png", 50, 50, true)));
        confirmdlg.showAndWait().ifPresent(pwd->{
            String validpass = PgConnector.getFielorBlank(Store.AuthUser.get(), "password");
            if (Objects.equals(validpass, pwd)) {
                isvalidadmin.set(true);
            }
        });

        if (!isvalidadmin.get()) {
            Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.ERROR, Translator.getIntl("auth_err"), "ERR", Translator.getIntl("invalid_password_opfailed"), ButtonType.OK);
            a.showAndWait();
            return;

        }

        //validate
        String feeEntered = entryController.feeEntryf.getText().isEmpty() ? "0" : entryController.feeEntryf.getText();
        if (Integer.parseInt(feeEntered) > amountLeftProperty.get()) {
            String errFee = String.format("%s %s %s %d",
                    Translator.getIntl("amount"),
                    entryController.feeEntryf.getText(),
                    Translator.getIntl("cannot_begreater_thanleft"),
                    amountLeftProperty.get()
            );
            PopOver errp = ProjectUtils.showPopover("", ProjectUtils.createErrLabel(errFee), PopOver.ArrowLocation.LEFT_CENTER, false, true);
            errp.show(entryController.feeEntryf);

            return;
        }

        List<String> queries = new ArrayList<>();

        HashMap<String, Object> studentObj = feeStudentSearch.getValue();
        if (Objects.equals(null,studentObj)) return;

        String studentMatricule = PgConnector.getFielorBlank(studentObj, "matricule");
        Integer classid = PgConnector.getNumberOrNull(studentObj, "classid").intValue();

        String method = entryController.hrmethodCombo.getValue() == null ? "cash" : entryController.hrmethodCombo.getValue();


        if (!ptaP.get().isEmpty() && !entryController.ptaEntryf.isDisabled()) {
            int ptaAmount = Integer.parseInt(ptaP.get());
            String insert = String.format("insert into hr_pta (student_matricule,classid,amount,date,payment_method) values ('%s',%d,%d,%d ,'%s')",
                    studentMatricule, classid, ptaAmount, new Date().getTime(),method);
            String update = String.format("update  hr_pta set amount=%d where student_matricule='%s'", ptaAmount,studentMatricule);

            List<HashMap<String, Object>> found = PgConnector.fetch(String.format("select * from hr_pta where student_matricule='%s'", studentMatricule), PgConnector.getConnection());

            if (!Objects.equals(0, ptaAmount)) {
                if (found.isEmpty()) {
                    queries.add(insert);
                } else {
                    queries.add(update);
                }
            }

        }


        if (!registrationP.get().isEmpty() && !entryController.registrationf.isDisabled()) {
            int regAmount = Integer.parseInt(registrationP.get());
            String insert = String.format("insert into hr_registrations (student_matricule,classid,amount,date,payment_method) values ('%s',%d,%d,%d,'%s')",
                    studentMatricule, classid, regAmount, new Date().getTime(),method);
            String update = String.format("update  hr_registrations set amount=%d where student_matricule='%s'", regAmount,studentMatricule);

            List<HashMap<String, Object>> found = PgConnector.fetch(String.format("select * from hr_registrations where student_matricule='%s'", studentMatricule), PgConnector.getConnection());

            if (!Objects.equals(regAmount, 0)) {

                if (found.isEmpty()) {
                    queries.add(insert);
                } else {
                    queries.add(update);
                }


            }


        }

        if (!feeP.get().isEmpty() && !entryController.feeEntryf.isDisabled()) {
            int feeAmount = Integer.parseInt(feeP.get());
            String insert = String.format("insert into hr_fees (student_matricule,classid,amount,date,payment_method) values ('%s',%d,%d,%d,'%s')",
                    studentMatricule, classid, feeAmount, new Date().getTime(),method);

            if (!(feeAmount >amountLeftProperty.get()) && !Objects.equals(feeAmount,0)) queries.add(insert);
        }

        //save sales
        HashMap<String, Integer> salesAdded = payedItemsMapP.get();
        if (!salesAdded.keySet().isEmpty()) {
            for (String itemName : salesAdded.keySet()) {
                Integer itemAmount = salesAdded.get(itemName);
                String insert = String.format("insert into hr_sales_requirements (student_matricule,classid,amount," +
                                "date,payment_method,item) values ('%s',%d,%d,%d,'%s','%s')",
                        studentMatricule, classid, itemAmount, new Date().getTime(),method,itemName);
                queries.add(insert);


            }
        }

        queries.forEach(System.out::println);

        for (String q:queries) PgConnector.update(q);

        Alert a = ProjectUtils.showAlert(mainStage, Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO", Translator.getIntl("data_updated"), ButtonType.OK);
        a.showAndWait();

        loadStudentInfo();


    }




    public static String formatNumberToCurency(Number number,boolean... showDp) {
        if (showDp.length > 0) return String.format("%,2d XAF", number.intValue());
         return String.format("%,d XAF", number.intValue());
    }

    public static Number formatCurrentToNumber(String curency) {
        if (curency.isEmpty()) return 0;
        String cleaned = curency.replaceAll("\\D", "");
        return Integer.parseInt(cleaned);
    }







}
