package org.jensen.galrev.ui.translate;

import org.apache.logging.log4j.LogManager;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by jensen on 10.06.15.
 */
public class Texts {

    public static final String BUNDLE_NAME = "galrev";
    public static final String BUNDLE_PACKAGE = Texts.class.getPackage().getName();
    private static Locale locale = Locale.getDefault();
    private static ResourceBundle bundle;
    private static HashMap<String,HashSet<String>> missingKeys = new HashMap<>();

    public static String getText(String key, Object... params){
        if (bundle == null){
            bundle = ResourceBundle.getBundle(BUNDLE_PACKAGE+"."+BUNDLE_NAME, locale);
        }
        String text;

        try {
            text = bundle.getString(key);
            if (params != null) {
                MessageFormat format = new MessageFormat(text, locale);
                text = format.format(params);
            }
        }catch(MissingResourceException mre){
            if  (!missingKeys.containsKey(locale.toString())){
                missingKeys.put(locale.toString(), new HashSet<>());
            }
            HashSet<String> set = missingKeys.get(locale.toString());
            set.add(key);
            text = key;
        }
        return text;
    }
    
    public static void printMissingTexts(){
        for (String aLocale: missingKeys.keySet()){
            StringBuffer bf = new StringBuffer();
            missingKeys.get(aLocale).stream().forEach(txt -> bf.append(txt+" = \n"));

            if (!bf.toString().isEmpty()){
                LogManager.getLogger().debug("Missing texts in locale " + aLocale+":\n"+bf.toString());
            }
        }
    }
}
