package com.apps.anker.facepunchdroid.Ads;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.apps.anker.facepunchdroid.MainActivity;
import com.apps.anker.facepunchdroid.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Mikkel on 08-04-2016.
 */
public class Ads {
    static Context mContext;
    static Activity mActivity;

    public static void initAds(Context context, final Activity activity, final AdView adview) {
        mContext = context;
        mActivity = activity;

        /* ADS */
        String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();


        final AdView mAdView = adview;

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("ADS TEST", "Ad loaded!");
                SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.refresh);
                ViewGroup.MarginLayoutParams refreshLayoutParams = (ViewGroup.MarginLayoutParams) refreshLayout.getLayoutParams();
                refreshLayoutParams.setMargins(0,0,0,adview.getHeight());
                refreshLayout.requestLayout();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
            }

        });

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(deviceId)
                .build();
        mAdView.loadAd(adRequest);


    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            //Logger.logStackTrace("TAG",e);
        }
        return "";
    }
}
