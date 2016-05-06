package com.apps.anker.facepunchdroid.Tools;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Mikkel on 04-05-2016.
 */
public class Assets {

    public static String assetToString(Context mContext, String assetName) throws IOException {
        StringBuilder buf = new StringBuilder();
        InputStream asset = mContext.getAssets().open(assetName);
        BufferedReader in = new BufferedReader(new InputStreamReader(asset, "UTF-8"));
        String str;

        while ((str = in.readLine()) != null) {
            buf.append(str);
        }

        in.close();

        return str;
    }
}
