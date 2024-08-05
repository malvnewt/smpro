package smpro.app.utils;

import smpro.app.ResourceUtil;

import java.net.URL;

public class ProjectUtils {



    public static URL getAppResource(String rname){
        return ResourceUtil.class.getResource(rname);
    }

}
