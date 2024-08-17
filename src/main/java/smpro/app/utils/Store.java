package smpro.app.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.Light;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class Store {

    public static ObjectProperty<HashMap<String, Object>> currentProjectProperty = new SimpleObjectProperty<>(null);

    public static String RESOURCE_BASE_URL = "smpro/app/";
//    public static String desktopPath = Path.of(System.getProperty("os.home"),"Desktop").toAbsolutePath().toString();

    public static double MENU_COLLAPSE_WIDTH = 75d;
    public static double MENU_EXPAND_WIDTH = 280d;


    public static class Colors{
        public static String Gray = "gray";
        public static String black = "#242424";
        public static String White = "white";
        public static String LightGray = "#ddd";
        public static String lightestGray = "#eeee";
//
        public static String hoverbg = "#20aaaa25";
        public static String selectionBg = "#20aaaa65";
        public static String red = "#ff6060";



    }

    public static class UnicodeSumnbol{
        public static String leftSquarebracket = "\u005b";
        public static String rightSquarebracket = "\u005d";
        public static String tm = "\u2122";
        public static String atSymbole = "\u0040";
        public static String blank = "\u2800";
        public static String dash = "\u2796";
    }


    public static class Sectors{
        public static String Public = "public_sector";
        public static String Private = "private_sector";

        public static List<String> supportedSectors = List.of(Public, Private);




    }

    public static class Insitutions {
        public static String Primary = "primary_institution";
        public  static String Secondary = "secondary_institution";
        public static String University = "university_institution";

        public static List<String> supportedInstitutions = List.of(Primary, Secondary, University);



    }



}
