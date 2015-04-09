package org.jensen.galrev.app;

/**
 * Created by jensen on 09.04.15.
 */
public class AppInfo {
    private static final String APPLICATION_NAME = "GalRev";
    private static final String MAJOR_VERSION = "1";
    private static final String MINOR_VERSION = "0";
    private static final String REVISION = "@no_revision@";

    public static String getFullApplicationName() {
        return APPLICATION_NAME + " " + getVersionString();
    }

    private static String getVersionString() {
        String versionString = MAJOR_VERSION + "." + MINOR_VERSION;
        if (REVISION.contains("@")){
            versionString += "*";
        }else{
            versionString += "("+REVISION+")";
        }
        return versionString;
    }
}
