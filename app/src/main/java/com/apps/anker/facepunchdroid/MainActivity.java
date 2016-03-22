package com.apps.anker.facepunchdroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String baseURL = "https://facepunch.com/";
    WebView webview;
    CircularProgressBar pb;
    RelativeLayout pbc;
    private boolean isInjected;
    String CSSfromfile;
    String JSfromfile;
    String Jquery;
    Boolean loginStatus;

    private ActionBarDrawerToggle toggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private NavigationView navigationView;

    private int mShortAnimationDuration;

    private SharedPreferences sharedPref;

    boolean useCustomStyles;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                          String key) {
                        switch (key) {
                            case "enable_custom_styles":
                                webview.reload();
                                break;
                            case "custom_style_file":
                                webview.reload();
                                break;
                        }

                    }
                };

        sharedPref.registerOnSharedPreferenceChangeListener(spChanged);


        // Setup Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mActivityTitle = getTitle().toString();

        // Generate strings for CSS and JS
        try {
            CSSfromfile = customCSS.cssToString(getAssets().open("fp-mobile.css")).replace('"', '\"');
            JSfromfile = customCSS.cssToString(getAssets().open("fp-mobile.js"));
            Jquery = customCSS.cssToString(getAssets().open("jquery.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        // Progressbar and WebView
        pb = (CircularProgressBar) findViewById(R.id.progressBar);
        pbc = (RelativeLayout) findViewById(R.id.progressBarContainer);
        webview = (WebView) findViewById(R.id.webView);


        // Setup Webview
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {
                Log.d("useCustomStyles", String.valueOf(sharedPref.getBoolean("enable_custom_styles", false)));
                if(sharedPref.contains("enable_custom_styles")) {
                    useCustomStyles = sharedPref.getBoolean("enable_custom_styles", false);
                    Log.d("useCustomStyles", String.valueOf(useCustomStyles));
                }

                // Hide webview and show progressbar
                pb.setProgress(0);
                crossfadeToLoader();

                Log.d("Webview", "onPageStarted " + url);

                if (!isInjected) {

                }

                isInjected = false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadUrl(url);
                String urlHost = Uri.parse(url).getHost();
                Log.d("Webview", "ShouldOverrideURLloading " + url );
                switch (urlHost) {
                    case "facepunch.com":
                        return false;
                    case "www.facepunch.com":
                        return false;
                    default:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);

                pb.setProgressWithAnimation(webview.getProgress());
                Log.d("Progess", String.valueOf(isInjected));
                if (webview.getProgress() == 100 && !isInjected) {
                    Log.d("Inside if", String.valueOf(isInjected));
                    String jquery = "javascript:" + Jquery;
                    view.loadUrl(jquery);

                    String javascript = "javascript:" + JSfromfile;
                    view.loadUrl(javascript);

                    // Inject CSS
                    String injectCSS = "javascript:var css=\"" + CSSfromfile + "\",head=document.head;style=document.createElement(\"style\"),style.type=\"text/css\",style.appendChild(document.createTextNode(css)),head.appendChild(style);";
                    view.loadUrl(injectCSS);


                    if(useCustomStyles) {
                        if(sharedPref.contains("custom_style_file")) {
                            String filePath = sharedPref.getString("custom_style_file", "");
                            Log.d("CUSTOM CSS", String.valueOf(Uri.parse(filePath)));
                            String userCustomCSS = customCSS.readFromSDcard(sharedPref.getString("custom_style_file", ""));
                            String injectUserCSS = "javascript:var css=\"" + userCustomCSS + "\",head=document.head;style=document.createElement(\"style\"),style.type=\"text/css\",style.appendChild(document.createTextNode(css)),head.appendChild(style);";
                            Log.d("Injected css", injectUserCSS);
                            view.loadUrl(injectUserCSS);
                        }

                    }
                    isInjected = true;


                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Change Actionbar Title
                mActivityTitle = webview.getTitle();
                getSupportActionBar().setTitle(mActivityTitle);
                Log.d("Webview", "onPageFinished");

            }
        });

        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebContentsDebuggingEnabled(true);
        webview.addJavascriptInterface(new WebAppInterface(this), "Android");
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(false);

        // Set new UA
        //String ua = webview.getSettings().getUserAgentString();
        webview.getSettings().setUserAgentString("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        if (savedInstanceState != null)
            webview.restoreState(savedInstanceState);
        else
            webview.loadUrl(baseURL);


        // Handle Share to intent
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                webview.loadUrl(intent.getStringExtra(Intent.EXTRA_TEXT));
            }
        }

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri IntentData = intent.getData();
            webview.loadUrl(IntentData.toString());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }

    private void setupDrawer() {
        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        switch (item.getItemId()) {
            case R.id.action_refresh:
                webview.reload();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actionbar, menu);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if(drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else if (webview.canGoBack()) {
                        webview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            Log.d("DEBUG", "SHOW TOAST");
        }

        @JavascriptInterface
        public void showImage(String url) {
            Log.d("ImageView", "Starting imageviewer intent!");
            Intent i = new Intent(mContext, ImageViewer.class);
            i.putExtra("url", url);
            mContext.startActivity(i);
        }

        @JavascriptInterface
        public void finishedInjection() {
            Log.d("DEBUG", "Finished Injection");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    crossfadeToWebview();
                }
            });

        }

        @JavascriptInterface
        public void setLoginStatus(boolean status, final String username, final int userid) {
            if(status) {
                Log.d("LOLOLOOL", "Got Status update");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView txtView_fp_username = (TextView) findViewById(R.id.txtView_fp_username);
                        txtView_fp_username.setText(username);

                        navigationView.getMenu().setGroupVisible(R.id.group_notloggedin, false);
                        navigationView.getMenu().setGroupVisible(R.id.group_loggedin, true);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView txtView_fp_username = (TextView) findViewById(R.id.txtView_fp_username);
                        txtView_fp_username.setText(getString(R.string.not_logged_in));
                        navigationView.getMenu().setGroupVisible(R.id.group_notloggedin, true);
                        navigationView.getMenu().setGroupVisible(R.id.group_loggedin, false);
                    }
                });

            }

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            webview.loadUrl(baseURL);
        } else if (id == R.id.nav_events) {
            webview.loadUrl(baseURL + "fp_events.php"); // Events
        } else if (id == R.id.nav_popular) {
            webview.loadUrl(baseURL + "fp_popular.php"); // Popular
        } else if (id == R.id.nav_read) {
            webview.loadUrl(baseURL + "fp_read.php"); // Read
        } else if (id == R.id.nav_search) {
            webview.loadUrl(baseURL + "search.php"); // Search
        } else if (id == R.id.nav_usercp) {
            webview.loadUrl(baseURL + "usercp.php"); // User control panel
        } else if (id == R.id.nav_messages) {
            webview.loadUrl(baseURL + "private.php"); // Private messages
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                CookieManager.getInstance().removeAllCookies(null);
                                CookieManager.getInstance().flush();
                            }

                            webview.loadUrl(baseURL);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            builder.show();
        } else if (id == R.id.nav_settings || id == R.id.nav_loggedin_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void crossfadeToWebview() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        //webview.setAlpha(0f);
        //webview.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        //webview.animate()
        //        .alpha(1f)
        //        .setDuration(mShortAnimationDuration)
        //        .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        pbc.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pbc.setVisibility(View.GONE);
                    }
                });
    }

    private void crossfadeToLoader() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        //pb.setAlpha(0f);
        pbc.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        pbc.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        /*pbc.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //webview.setVisibility(View.GONE);
                    }
                });*/
    }
}
