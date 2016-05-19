package com.apps.anker.facepunchdroid;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import static android.text.Html.escapeHtml;

/**
 * Created by Mikkel on 24-02-2016.
 */
public class customCSS {

    public static String cssToString(InputStream asset) {
        // String builder for making nice single line css
        StringBuilder tmp = new StringBuilder();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(asset));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                tmp.append(mLine);
                tmp.append("\n");
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        return  tmp.toString();
    }

    public static String jsToString(InputStream asset) {
        // String builder for making nice single line css
        StringBuilder tmp = new StringBuilder();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(asset));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                tmp.append(mLine);
                tmp.append("\n");
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        return tmp.toString();
    }

    public static String readFromSDcard(Activity mActivitym, Uri path) throws FileNotFoundException {
        //Get the text file

        InputStream inputStream = mActivitym.getContentResolver().openInputStream(path);


        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            Log.e("FileError", "Something went wrong..", e);
        }

        Log.d("Read from file", text.toString());
        return text.toString();
    }

}
