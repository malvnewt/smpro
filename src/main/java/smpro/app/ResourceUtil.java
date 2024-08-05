package smpro.app;

import java.io.InputStream;
import java.lang.*;


public class ResourceUtil {


    public static String getAppResource(String pathFromRoot) {
        return Entry.class.getResource(pathFromRoot).toExternalForm();
    }

    public static InputStream getResourceAsStream(String pathFromRoot) {
        return null;

    }

    public static InputStream getStystemFileStream(String filepath) {
        return null;

    }


}