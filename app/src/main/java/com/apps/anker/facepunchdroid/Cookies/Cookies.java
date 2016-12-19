package com.apps.anker.facepunchdroid.Cookies;

import android.webkit.CookieManager;

/**
 * Created by Mikkel on 19-12-2016.
 */

public class Cookies {

    public static String getCookie(String siteName,String CookieName){
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp=cookies.split(";");
        for (String ar1 : temp ){
            if(ar1.contains(CookieName)){
                String[] temp1=ar1.split("=");
                CookieValue = temp1[1];
            }
        }
        return CookieValue;
    }
}
