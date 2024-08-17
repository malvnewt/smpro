package smpro.app;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import smpro.app.utils.Store;
import smpro.app.utils.Translator;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.*;
import java.net.URL;
import java.util.List;


public class ResourceUtil {


    public static URL getAppResourceURL(String rname){
        return Entry.class.getResource(rname);
    }


    public static InputStream getResourceAsStream(String pathFromRoot) {
        System.out.println("Fetching resource "+pathFromRoot);
        return Entry.class.getResourceAsStream(pathFromRoot);
    }

    public static InputStream getStystemFileStream(Stage parent,  List<FileChooser.ExtensionFilter> filter) {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle(Translator.getIntl("choose_file"));
//        fileDialog.setInitialDirectory(new File(Store.desktopPath));
        fileDialog.getExtensionFilters().addAll(filter);

        File file = fileDialog.showOpenDialog(parent);

        try {
            return new FileInputStream(file);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
    public static String getStystemFilePath(Stage parent,  List<FileChooser.ExtensionFilter> filter) {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle(Translator.getIntl("choose_file"));
//        fileDialog.setInitialDirectory(new File(Store.desktopPath));
        fileDialog.getExtensionFilters().addAll(filter);

        File file = fileDialog.showOpenDialog(parent);

        return file.getAbsolutePath();


    }

    public static Image getImageFromResource(String pathname, int w, int h,boolean... preseveAspectRatio) {

        return new Image(ResourceUtil.getResourceAsStream(pathname), w, h, preseveAspectRatio.length >0 ? preseveAspectRatio[0] : true, true);
    }



}