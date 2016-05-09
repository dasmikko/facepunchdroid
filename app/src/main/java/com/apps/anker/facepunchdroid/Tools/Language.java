package com.apps.anker.facepunchdroid.Tools;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by Mikkel on 08-05-2016.
 */
public class Language {
    public static void setLanguage(String lang, Resources res) {
        Locale myLocale = null;

        Log.d("New Language",lang);

        if(lang.equals("system")) {
            myLocale = new Locale(Locale.getDefault().getLanguage());
            Log.d("System Language", Locale.getDefault().getLanguage());
        } else {
            myLocale = new Locale(lang.toString());
        }

        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
