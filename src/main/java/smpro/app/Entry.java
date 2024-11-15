package smpro.app;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import smpro.app.controllers.AddStudentController;
import smpro.app.custom_titlebar.CaptionConfiguration;
import smpro.app.custom_titlebar.CustomCaption;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class Entry extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("========= LAUNCHING SMPROV2 ==========");
        System.out.println("Testing Internationalisation");
//        System.out.println(Translator.getIntl("test_intl"));

        // resisize main window
        double screenHeight =  Screen.getPrimary().getBounds().getHeight();
        double screenWidth =  Screen.getPrimary().getBounds().getWidth();



        //check database connectivity and show error window if any
        try {
            SessionHandler.connectToProjectDb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
            //handle failed connection
        }
        Store.EntryStage.set(stage);

        Store.DbsubjectCategories.addAll(PgConnector.listHashAttrs(PgConnector.fetch("select * from subject_categories order by category_name",
                PgConnector.getConnection()), "category_name"));

        //////////////////////////////////////////////////  perform migrations from old version
        //////////////////////////////////////////////////
//        performDbMigrations();
        //////////////////////////////////////////////////
        //////////////////////////////////////////////////


        URL mainurl = ResourceUtil.getAppResourceURL("views/entry-view.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(mainurl);
        fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
        Parent root =fxmlLoader.load();
        Scene scene = new Scene(root, 320, 240);
        scene.getStylesheets().addAll(
                ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
        );

        String title = String.format("%s %s\u2800 \u2800 \u2800 %s \u2800 \u2800%s %s %s", "SM\u2796PRO",Store.UnicodeSumnbol.tm,
                ProjectUtils.getFormatedDate(new Date().getTime(), DateFormat.getDateInstance(DateFormat.FULL,
                        Translator.getLocale())),Store.UnicodeSumnbol.leftSquarebracket,"SERVER SESSION",Store.UnicodeSumnbol.rightSquarebracket);
        stage.setTitle(title.toUpperCase());

        stage.getIcons().add(ResourceUtil.getImageFromResource("images/logo-server.png", 50, 50));
        stage.setScene(scene);

        // all done. Now action data is hydrated
        EntryController entryController = fxmlLoader.getController();
        entryController.configureMenuActions();

        stage.show();
        stage.setMaxHeight(stage.getHeight());
        stage.setMaxWidth(stage.getWidth());


        MenuBar menuBar = (MenuBar) fxmlLoader.getNamespace().get("menubar");
        menuBar.setStyle("-fx-border-width: 0 0 1px 0;-fx-border-color: "+Store.Colors.lightestGray);

        CustomCaption.useForStage(stage, new CaptionConfiguration().setCaptionHeight(40)
                .setIconColor(Color.web(Store.Colors.LightGray))
                .setIconHoverColor(Color.web(Store.Colors.White))
                .setControlBackgroundColor(Color.web(Store.Colors.transparent))
                        .setButtonHoverColor(Color.web("#aaaaaa30"))
                .setCaptionDragRegion(menuBar).
                useControls(true).
                setCloseButtonHoverColor(Color.web(Store.Colors.deepRed))
        );


        EntryController controller = fxmlLoader.getController();
        controller.thisStage.set(stage);


        stage.setMinHeight(0.8 * screenHeight);
        stage.setMinWidth(0.7 * screenWidth);
        stage.centerOnScreen();
        menuBar.requestFocus();
        stage.setResizable(false);





    }

    public static void main(String[] args) {
        launch();
    }


    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    public void performDbMigrations() {
        try {
            System.out.println("PERFORMING MIGRATIONS FROM THE OLD PRODUCT");
            Connection oldconnection = PgConnector.initConnect("pae_2023_2024", "localhost");
            Connection newconnection = PgConnector.initConnect("pae_2024_2025", "localhost");

            //insert students

            List<HashMap<String, Object>> oldStudents = PgConnector.fetch("select * from students", oldconnection);


            for (HashMap<String, Object> sOld : oldStudents) {
                String fullname = PgConnector.getFielorBlank(sOld, "full_name");
                String dob = PgConnector.getFielorBlank(sOld, "date of birth");

                String fname = fullname.substring(0, fullname.indexOf(" ")).strip();
                String lname = fullname.substring(fullname.indexOf(" ")).strip();

                String delimiter = dob.contains("-") ? "-" : "/";
                LocalDate dobLocaldate;
                if (delimiter.equals("-")) {
                    int year = Integer.parseInt(dob.strip().split(delimiter)[2]);
                    int day = Integer.parseInt(dob.strip().split(delimiter)[0]);

//                    int temp;
//                    if (String.valueOf(year).length()<4){
//                        temp = day;
//                        day=year;
//                        year=temp;
//                    }

                    try {
                        dobLocaldate = LocalDate.of(
                                year,
                                Integer.parseInt(dob.strip().split(delimiter)[1]),
                                day  );

                    } catch (DateTimeException derr) {
                        dobLocaldate = LocalDate.of(
                                day,
                                Integer.parseInt(dob.strip().split(delimiter)[1]),
                                year  );

                    }




                } else {


                    try {
                        dobLocaldate = LocalDate.of(
                                Integer.parseInt(dob.strip().split(delimiter)[0]),
                                Integer.parseInt(dob.strip().split(delimiter)[1]),
                                Integer.parseInt(dob.strip().split(delimiter)[2])
                        );
                    } catch (DateTimeException derr2) {
                        dobLocaldate = LocalDate.of(
                                Integer.parseInt(dob.strip().split(delimiter)[2]),
                                Integer.parseInt(dob.strip().split(delimiter)[1]),
                                Integer.parseInt(dob.strip().split(delimiter)[1])
                        );
                    }

                }

                String birthplace = PgConnector.getFielorBlank(sOld, "place of birth");
                String address = PgConnector.getFielorBlank(sOld, "address");
                String sex = PgConnector.getFielorBlank(sOld, "sex");
                String trade = PgConnector.getFielorBlank(sOld, "trade");
                String parent = PgConnector.getFielorBlank(sOld, "parent");
                boolean repeater = Boolean.parseBoolean(PgConnector.getFielorBlank(sOld, "repeater"));
                String contact = PgConnector.getFielorBlank(sOld, "contact");
                int classid = PgConnector.getNumberOrNull(sOld, "class").intValue();

                int sectionid = PgConnector.getNumberOrNull(sOld, "section").intValue();
                List<HashMap<String, Object>> foundSection = PgConnector.fetch
                        (String.format("select * from sections where id=%d", sectionid), oldconnection);


                //transfer
                String q = """
                        insert into students\s
                        (
                        classid,section,trade,
                        firstname,lastname,
                        place_of_birth,address,
                                        
                        admission_date,date_of_birth,
                                        
                        parent_one,contact_one,
                    
                        gender,repeater,
                        matricule
                                        
                        )
                                        
                        values  (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                                        
                        """;


                try (PreparedStatement insert = PgConnector.getConnection().prepareStatement(q)) {

                    insert.setInt(1, classid);
                    insert.setString(2, PgConnector.getFielorBlank(foundSection.get(0), "section"));
                    insert.setString(3, trade);

                    insert.setString(4, fname);
                    insert.setString(5, lname);
                    insert.setString(6, birthplace);
                    insert.setString(7, address);

                    insert.setLong(8, java.time.Instant.now().minusMillis(1*Store.EPOCK_DAY_MILLISECS).toEpochMilli());
                    insert.setLong(9, dobLocaldate.toEpochDay()*Store.EPOCK_DAY_MILLISECS);

                    insert.setString(10, parent);
                    insert.setString(11, contact);
//                    insert.setString(12, lParentP.get());
//                    insert.setString(13, lcontactP.get());

                    insert.setString(12, sex);
                    insert.setBoolean(13, repeater);
                    insert.setString(14, AddStudentController.genMatricule(fname, lname));


                    // insert
//                    System.out.println(insert);

//                    insert.executeUpdate();


                } catch (Exception err) {
                    err.printStackTrace();
                }




        }


    } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


}




class SessionHandler {
    public static void  connectToProjectDb() throws SQLException {
        Connection baseconn;
        System.out.println("initialising sessionhandler");


        try {
             baseconn =  PgConnector.initConnect(PgConnector.baseDbname,PgConnector.dbHost);
            PgConnector.baseConnection.set(baseconn);

            URL url = ResourceUtil.getAppResourceURL("views/others/connect.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setResources(ResourceBundle.getBundle(Store.RESOURCE_BASE_URL+"lang",Translator.getLocale()));
            Parent root =fxmlLoader.load();


            Stage stage = new Stage(StageStyle.UNDECORATED);

            Scene scene = new Scene(root);
            scene.getStylesheets().addAll(
                    ResourceUtil.getAppResourceURL("css/recaf/recaf.css").toExternalForm()
            );


            stage.setScene(scene);

//            new JMetro(Style.DARK).setScene(stage.getScene());
//



            String title = String.format("%s %s\u2800 \u2800 \u2800 %s \u2800 \u2800%s %s %s", "SM\u2796PRO",Store.UnicodeSumnbol.tm,
                    ProjectUtils.getFormatedDate(new Date().getTime(), DateFormat.getDateInstance(DateFormat.FULL,
                            Translator.getLocale())),Store.UnicodeSumnbol.leftSquarebracket,"DB CONNECTIVITY",Store.UnicodeSumnbol.rightSquarebracket);
            stage.setTitle(title.toUpperCase()); // TODO :: SET UNDECORATED TITLE
            stage.getIcons().add(ResourceUtil.getImageFromResource("images/admin.png", 50, 50));

            ConnectController controller = fxmlLoader.getController();
            controller.thisStage.set(stage);


            //get databases
            List<HashMap<String, Object>> dbObjects = PgConnector.fetch("select * from  databases order by id",PgConnector.baseConnection.get()
            );
            controller.dbs.set(FXCollections.observableList(dbObjects));

            Store.dbNamesProperty.set(FXCollections.observableList(dbObjects.stream().map(db -> PgConnector.getFielorBlank(db, "name")).toList()));

            stage.showAndWait();


            // CONNECTION SUCCESSFULL
            PgConnector.update("CREATE EXTENSION IF NOT EXISTS hstore"); // for Map k,v pairs





        } catch (SQLException e) {
            System.err.println(e.getLocalizedMessage());

            //TODO PROJECT NOT CONFIGURED DIALOG AND SHUTDOWN

        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }





    }// main method

}




