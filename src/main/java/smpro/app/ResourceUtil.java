package smpro.app;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.lang.*;
import java.net.URL;


public class ResourceUtil {


    public static URL getAppResourceURL(String rname){
        return Entry.class.getResource(rname);
    }


    public static InputStream getResourceAsStream(String pathFromRoot) {
        System.out.println("Fetching resource "+pathFromRoot);
        return Entry.class.getResourceAsStream(pathFromRoot);
    }

    public static InputStream getStystemFileStream(String filepath) {
        return null;
    }

    public static Image getImageFromResource(String pathname, int w, int h) {

        return new Image(ResourceUtil.getResourceAsStream(pathname), w, h, true, true);
    }



}