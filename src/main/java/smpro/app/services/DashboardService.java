package smpro.app.services;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import smpro.app.ResourceUtil;
import smpro.app.SettingsController;
import smpro.app.controllers.AddSubjectController;
import smpro.app.custom_nodes.CustomToolbarActionGroup;
import smpro.app.utils.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardService {

    Button registerBtn;
    Button registerTeacher;
    Button registerClass;
    Button addSubject;
    Button makePayment;
    CustomTextField searchStudent;
    Button refreshBtn;


   public List<Node> toolbarItems = new ArrayList<>();

    public ObjectProperty<Stage> mainStage = new SimpleObjectProperty<>();


    public DashboardService(Stage mainStage) {
        this.mainStage.set(mainStage);

        initUi();

    }

    public void initUi() {
        //plot all graphs/summary displays .tiles fx

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

        }



        toolbarItems.add(registerGrou.build(1));
        toolbarItems.add(paymentgrouup.build(1));
        toolbarItems.add(searchGroup.build(1));
        toolbarItems.add(groupTwo.build(2));
        toolbarItems.add(refreshgroup.build(1));

        return toolbarItems;


    }

    public void bindFields() {

        registerBtn.setOnAction(e->{
            ActionStageLinker.openAddStudent(mainStage.get());
        });

        addSubject.setOnAction(e-> addSubjectHandler());

        registerClass.setOnAction(e -> addClassHandler());



    }


    public void addSubjectHandler() {

            URL url = ResourceUtil.getAppResourceURL("views/others/add-subject.fxml");

            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang"));
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
            stage.showAndWait();



//            Alert a = ProjectUtils.showAlert(mainStage.get(), Alert.AlertType.NONE, "INSERTION SUCCESS", "INFO",
//            Translator.getIntl("data_updated"), ButtonType.OK);
//            a.showAndWait();




    }

    public void addClassHandler() {
        SettingsController settingsController = new SettingsController();
        settingsController.openClassWindow(false);

    }

    public void addTeacherHandler() {

    }

    public void refreshStatsHandler() {

    }







}
