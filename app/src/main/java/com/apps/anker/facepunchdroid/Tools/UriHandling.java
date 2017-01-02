package com.apps.anker.facepunchdroid.Tools;

import android.net.Uri;
import android.net.UrlQuerySanitizer;

import java.util.Set;

/**
 * Created by Mikkel on 15-04-2016.
 */


public class UriHandling {

    static Boolean keyExist;

    public static Uri replaceUriParameter(Uri uri, String key, String newValue) {
        final Set<String> params = uri.getQueryParameterNames();
        final Uri.Builder newUri = uri.buildUpon().clearQuery();
        final String goToPostKey = "p"; // Query to ignore

        keyExist = false;

        for (String param : params) {
            String value;
            if (param.equals(key)) {
                value = newValue;
                keyExist = true;
            } else if(param.equals(goToPostKey)) { // Remove goToPostKey as it hinders going to other pages
                continue;
            } else {
                value = uri.getQueryParameter(param);
            }

            newUri.appendQueryParameter(param, value);
        }

        // Add page query if it doesn't exist!
        if (!keyExist) {
            newUri.appendQueryParameter(key, newValue);
        }

        return newUri.build();
    }

    public static Integer getThreadIdFromURL(String url) {
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
        return Integer.parseInt(sanitizer.getValue("t")); // get your value
    }
}
