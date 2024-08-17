package smpro.app.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import smpro.app.ResourceUtil;

import java.text.MessageFormat;
import java.util.*;

public class Translator {
//    String RESOURCE_BASE = ResourceUtil.getAppResource("")
    static Locale sysLocale = Locale.getDefault();
    public static ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.ENGLISH);
    public static ObjectProperty<Locale> localeAltProperty = new SimpleObjectProperty<>(Locale.FRENCH);
    public static List<Locale> supportedLocales = List.of(
            Locale.ENGLISH,
            Locale.FRENCH,
            Locale.GERMAN,
            Locale.CHINESE,
//            Locale.ITALIAN,
            Locale.of("ru"),
            Locale.of("es")
    );

    static {
        localeProperty.addListener(((observableValue, oldloc, newloc) -> {
            System.out.printf("locale changed from %s to %s",oldloc.getDisplayName(),newloc.getDisplayName());
            retranslateUi();
        }));

        if (supportedLocales.contains(sysLocale)) {
            System.out.println("System locale supported "+sysLocale );
            setLocale(sysLocale);
        }
    }


    public static void setLocale(Locale newloc) {
        localeProperty.set(newloc);

        if (Objects.equals(newloc, Locale.ENGLISH)) {
            localeAltProperty.set(Locale.FRENCH);
        }else localeAltProperty.set(Locale.ENGLISH);
    }
    public static Locale getLocale() {
        return localeProperty.get();
    }

    public static Locale getLocaleAlt() {
        return localeAltProperty.get();
    }

    public static void retranslateUi() {

    }


    public static String getIntl(String key) {
        String res;
        try {
            res =  ResourceBundle.getBundle(Store.RESOURCE_BASE_URL + "lang", localeProperty.get()).getString(key);
        } catch (Exception err) {
            System.err.println(err.getLocalizedMessage());
             res =  ResourceBundle.getBundle(Store.RESOURCE_BASE_URL + "lang", Locale.ENGLISH).getString(key);

        }
        return res;

    }

    public static String getIntlAlt(String key) {
        String res;
        try {
            res =  ResourceBundle.getBundle(Store.RESOURCE_BASE_URL + "lang", localeAltProperty.get()).getString(key);
        } catch (Exception err) {
            res =  ResourceBundle.getBundle(Store.RESOURCE_BASE_URL + "lang", Locale.ENGLISH).getString(key);

        }
        return res;
    }

}
