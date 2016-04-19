package com.apps.anker.facepunchdroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.Tools.UriHandling;
import com.apps.anker.facepunchdroid.Webview.ObservableWebView;
import com.koushikdutta.ion.Ion;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;


import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    String baseURL = "https://facepunch.com/";
    ObservableWebView webview;
    ProgressBar pb;
    RelativeLayout pbc;
    private boolean isInjected;
    String CSSfromfile;
    String JSfromfile;
    String Jquery;
    Boolean loginStatus;

    Toolbar toolbar_bottom;

    Integer currentpage;
    Integer totalpages;

    MenuItem addstartpage;
    SharedPreferences.OnSharedPreferenceChangeListener spChanged;

    Integer oldScrollPos = 0;
    Boolean paginationEnabled = false;
    Boolean paginationHidden = true;


    private int mShortAnimationDuration;

    private SharedPreferences sharedPref;

    boolean useCustomStyles;

    SwipeRefreshLayout mSwipeRefreshLayout;
    Toolbar toolbar;
    private String mActivityTitle;

    // Pinned items
    RealmConfiguration realmConfig;
    Realm realm;

    // Drawer
    Drawer drawer;
    AccountHeader headerResult;
    ProfileDrawerItem defaultProfile;

    // Drawer items
    PrimaryDrawerItem nav_logout;

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        // Create the Realm configuration
        realmConfig = new RealmConfiguration.Builder(this).build();
        // Open the Realm for the UI thread.
        realm = Realm.getInstance(realmConfig);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                          String key) {
                        Log.d("Sharedpref changed", key);
                        switch (key) {
                            case "enable_custom_styles":
                                webview.reload();
                                break;
                            case "custom_style_file":
                                webview.reload();
                                break;
                            case "enable_custom_startpage":
                                invalidateOptionsMenu();
                                break;
                        }

                    }
                };

        sharedPref.registerOnSharedPreferenceChangeListener(spChanged);




        // Setup Drawer
        setupToolbar();
        setupDrawer();




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
        pb = (ProgressBar) findViewById(R.id.toolbarProgressbar);
        webview = (com.apps.anker.facepunchdroid.Webview.ObservableWebView) findViewById(R.id.webView);




        // On Scroll handling
        webview.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback(){
            public void onScroll(int l, int t,int oldl, int oldt){
                //Do stuff

                if(oldScrollPos != t && paginationEnabled)
                {
                    Log.d("Scroll pos", "Changed");
                    if (oldScrollPos < t) {
                        Log.d("Scroll pos", "Scrolling down");
                        if(paginationEnabled && !paginationHidden) { hideViews(); }

                    }
                    else if (oldScrollPos > t) {
                        Log.d("Scroll pos", "Scrolling up");
                        if(paginationEnabled && paginationHidden) { showViews(); }
                    }
                }
                oldScrollPos = t;
            }
        });



        // Setup Webview
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {
                Log.d("useCustomStyles", String.valueOf(sharedPref.getBoolean("enable_custom_styles", false)));
                if(sharedPref.contains("enable_custom_styles")) {
                    useCustomStyles = sharedPref.getBoolean("enable_custom_styles", false);
                    Log.d("useCustomStyles", String.valueOf(useCustomStyles));
                }

                // Show progressbar
                pb.setProgress(0);
                pb.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);


                Log.d("Webview", "onPageStarted " + url);

                if (!isInjected) {

                }

                isInjected = false;

            }

            @Override
            public WebResourceResponse shouldInterceptRequest (final WebView view, String url) {

                if (url.contains("small.css")) {
                    return getCssWebResourceResponseFromAsset();
                } else if (url.contains("fp.js")) {
                    Log.d("Intercept", "fp.js file");
                    return getJsWebResourceResponseFromAsset();
                /*} else if (url.contains("jquery.min.js")) {
                    Log.d("Intercept", "jquery file");
                    return getJqueryWebResourceResponseFromAsset();*/
                } else {
                    return super.shouldInterceptRequest(view, url);
                }
            }

            private WebResourceResponse getCssWebResourceResponseFromAsset() {
                try {
                    // Check if custom user style is enabled
                    if(useCustomStyles) {
                        if(sharedPref.contains("custom_style_file")) {
                            String userCSS = sharedPref.getString("custom_style_file", "");

                            return getUtf8EncodedCssAndCustomWebResourceResponse(getAssets().open("fpstyle.css"), userCSS);
                        }

                    }
                    return getUtf8EncodedCssWebResourceResponse(getAssets().open("fpstyle.css"));
                } catch (IOException e) {
                    return null;
                }
            }

            private WebResourceResponse getJsWebResourceResponseFromAsset() {
                try {
                    Log.d("Intercept", getUtf8EncodedJsWebResourceResponse(getAssets().open("fp-mobile.js")).toString());
                    return getUtf8EncodedJsWebResourceResponse(getAssets().open("fp-mobile.js"));
                } catch (IOException e) {
                    return null;
                }
            }

            private WebResourceResponse getJqueryWebResourceResponseFromAsset() {
                try {
                    return getUtf8EncodedJsWebResourceResponse(getAssets().open("jquery.js"));
                } catch (IOException e) {
                    return null;
                }
            }

            private WebResourceResponse getUtf8EncodedCssWebResourceResponse(InputStream data) {
                return new WebResourceResponse("text/css", "UTF-8", data);
            }

            private WebResourceResponse getUtf8EncodedCssAndCustomWebResourceResponse(InputStream data, String CSS) {
                return new WebResourceResponse("text/css", "UTF-8", new SequenceInputStream(data, new ByteArrayInputStream(CSS.getBytes(StandardCharsets.UTF_8))));
            }

            private WebResourceResponse getUtf8EncodedJsWebResourceResponse(InputStream data) {
                return new WebResourceResponse("text/javascript ", "UTF-8", data);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadUrl(url);
                if (url.startsWith("mailto:")) {
                        MailTo mt = MailTo.parse(url);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mt.getTo() });
                        intent.setType("message/rfc822");
                        startActivity(intent);
                        return true;
                }


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

                pb.setProgress(webview.getProgress());




                // Need a better way to detect if DOM is ready to inject CSS
                if (webview.getProgress() > 30 && !isInjected) {

                    isInjected = true;


                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("Webview", "onPageFinished");

                // Change Actionbar Title
                mActivityTitle = webview.getTitle();
                toolbar.setTitle(mActivityTitle);


                pb.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);

            }


        });

        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebContentsDebuggingEnabled(true);
        webview.addJavascriptInterface(new WebAppInterface(this), "Android");
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(false);

        // Set new UA
        String ua = webview.getSettings().getUserAgentString();
        //webview.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android) FacepunchDroid");
        webview.getSettings().setUserAgentString(ua + " FacepunchDroid");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        // First load
        if (savedInstanceState != null) {
            webview.restoreState(savedInstanceState);
        }
        else {
            if( sharedPref.getBoolean("enable_custom_startpage", false) && sharedPref.contains("current_startpage"))
            {
                webview.loadUrl(sharedPref.getString("current_startpage", baseURL));
            }
            else {
                webview.loadUrl(baseURL);
            }

        }

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webview.reload();


            }

        });

        setupBottomToolbar();


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPref.unregisterOnSharedPreferenceChangeListener(spChanged);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
        sharedPref.unregisterOnSharedPreferenceChangeListener(spChanged);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
        refreshDrawerItems();
        sharedPref.registerOnSharedPreferenceChangeListener(spChanged);
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }

    private void setupDrawer() {
        // Setup Profile
        if(sharedPref.getBoolean("isLoggedIn", false)) {
            String username = sharedPref.getString("username", "Not logged in");
            String userid = sharedPref.getString("userid", "");
            defaultProfile = new ProfileDrawerItem().withName(username).withIcon("https://facepunch.com/image.php?u="+userid);;
        } else {
            defaultProfile = new ProfileDrawerItem().withName("Not logged in");
        }



        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.cover)
                .withCompactStyle(true)
                .withSelectionListEnabledForSingleProfile(false)

                .addProfiles(
                        defaultProfile
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {

                        if(sharedPref.contains("userid")) {
                            String userid = sharedPref.getString("userid", "");
                            webview.loadUrl(baseURL + "member.php?u="+userid);
                        }

                        return false;
                    }
                })
                .build();



        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {

            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Ion.with(imageView.getContext())
                        .load(uri.toString())
                        .withBitmap()
                        .placeholder(R.drawable.placeholder)
                        .intoImageView(imageView);

                //Picasso.with(imageView.getContext()).load(uri).placeholder(R.drawable.placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                //Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // Handle navigation view item clicks here.
                        long id = drawerItem.getIdentifier();


                        if (id == 1) { // Home
                            webview.loadUrl(baseURL);
                        } else if (id == 2) { // Events
                            webview.loadUrl(baseURL + "fp_events.php");
                        } else if (id == 3) { // Popular
                            webview.loadUrl(baseURL + "fp_popular.php");
                        } else if (id == 4) { // Read
                            webview.loadUrl(baseURL + "fp_read.php");
                        } else if (id == 5) { // Search
                            webview.loadUrl(baseURL + "search.php");
                        } else if (id == 6) {
                            webview.loadUrl(baseURL + "private.php"); // Private messages
                        } else if (id == 7) { // User control panel
                            webview.loadUrl(baseURL + "usercp.php");
                        } else if (id == 9) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog));
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
                        } else if (id == 8) {
                            Intent i = new Intent(getBaseContext(), SettingsActivity.class);
                            startActivity(i);
                        } else if (id == 10) {
                            Intent i = new Intent(getBaseContext(), DonationsActivity.class);
                            startActivityForResult(i, 1);
                        } else if (id == 11) {
                            Intent i = new Intent(getBaseContext(), EditPinnedItemsActivity.class);
                            startActivityForResult(i, 1);
                        } else if (id == 12) { // Ticker
                            webview.loadUrl(baseURL + "fp_ticker.php");
                        }


                        return false;
                    }
                })
                .build();

                addDrawerItems();

    }

    protected void addDrawerItems() {

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem nav_home      = new PrimaryDrawerItem().withIdentifier(1).withName("Home").withIcon(GoogleMaterial.Icon.gmd_home).withSelectable(false);
        PrimaryDrawerItem nav_events    = new PrimaryDrawerItem().withIdentifier(2).withName("Events").withIcon(GoogleMaterial.Icon.gmd_event).withSelectable(false);
        PrimaryDrawerItem nav_popular   = new PrimaryDrawerItem().withIdentifier(3).withName("Popular").withIcon(GoogleMaterial.Icon.gmd_favorite).withSelectable(false);
        PrimaryDrawerItem nav_read      = new PrimaryDrawerItem().withIdentifier(4).withName("Read").withIcon(GoogleMaterial.Icon.gmd_markunread_mailbox).withSelectable(false);
        PrimaryDrawerItem nav_search    = new PrimaryDrawerItem().withIdentifier(5).withName("Search").withIcon(GoogleMaterial.Icon.gmd_search).withSelectable(false);
        PrimaryDrawerItem nav_messages  = new PrimaryDrawerItem().withIdentifier(6).withName("Messages").withIcon(GoogleMaterial.Icon.gmd_mail_outline).withSelectable(false);
        PrimaryDrawerItem nav_cpanel    = new PrimaryDrawerItem().withIdentifier(7).withName("Control panel").withIcon(GoogleMaterial.Icon.gmd_build).withSelectable(false);
        PrimaryDrawerItem nav_ticker    = new PrimaryDrawerItem().withIdentifier(12).withName("Ticker").withIcon(GoogleMaterial.Icon.gmd_link).withSelectable(false);

        PrimaryDrawerItem nav_settings  = new PrimaryDrawerItem().withIdentifier(8).withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false);

        if(sharedPref.getBoolean("isLoggedIn", false)) {
            nav_logout    = new PrimaryDrawerItem().withIdentifier(9).withName("Logout").withIcon(GoogleMaterial.Icon.gmd_lock_open).withSelectable(false).withEnabled(true);
        } else {
            nav_logout    = new PrimaryDrawerItem().withIdentifier(9).withName("Logout").withIcon(GoogleMaterial.Icon.gmd_lock_open).withSelectable(false).withEnabled(false);
        }


        PrimaryDrawerItem nav_donate    = new PrimaryDrawerItem().withIdentifier(10).withName("Donate").withIcon(GoogleMaterial.Icon.gmd_card_giftcard).withSelectable(false);


        drawer.addItems(
                nav_home,
                nav_events,
                nav_popular,
                nav_read,
                nav_search,
                nav_messages,
                nav_cpanel,
                nav_ticker
        );

        // Get Pinned items
        RealmResults<PinnedItem> pinnedItems = realm.where(PinnedItem.class).findAll();

        Log.d("Pitem list", pinnedItems.toString());

        if(pinnedItems.size() > 0) {
            drawer.addItem(new SectionDrawerItem().withName("Pinned pages"));
            for (PinnedItem pitem : pinnedItems)
            {

                drawer.addItem(new PrimaryDrawerItem().withName(pitem.getTitle()).withSelectable(false).withTag(pitem.getUrl()).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        webview.loadUrl(drawerItem.getTag().toString());
                        return false;
                    }
                }));
            }
            drawer.addItem(new SecondaryDrawerItem().withName("Edit pinned items").withIcon(GoogleMaterial.Icon.gmd_edit).withIdentifier(11).withSelectable(false));
        }

        drawer.addItem(new DividerDrawerItem());
        drawer.addItem(nav_settings);
        drawer.addItem(nav_logout);
        drawer.addItem(new DividerDrawerItem());
        drawer.addItem(nav_donate);
    }

    protected void refreshDrawerItems() {
        drawer.removeAllItems();
        addDrawerItems();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //toggle.onConfigurationChanged(newConfig);
    }

    public void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.mActionbar);
        setSupportActionBar(toolbar);





        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_refresh:
                        webview.reload();
                        return true;
                    case R.id.pinpage:
                        realm.beginTransaction();

                        // Add a person
                        PinnedItem pinitem = realm.createObject(PinnedItem.class);

                        pinitem.setTitle(webview.getTitle());
                        pinitem.setUrl(webview.getUrl());
                        realm.commitTransaction();

                        SwipeRefreshLayout mlayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
                        Snackbar.make(mlayout,"Page was pinned", Snackbar.LENGTH_LONG).show();
                        refreshDrawerItems();
                        return true;
                    case R.id.openinbrowser:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webview.getUrl()));
                        startActivity(intent);
                        return true;
                    case R.id.sharepage:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, webview.getUrl());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        return true;
                    case R.id.setasstartpage:
                        // Save new startpage
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("current_startpage", webview.getUrl());
                        editor.apply();

                        // Show snackbar
                        SwipeRefreshLayout mlayout2 = (SwipeRefreshLayout) findViewById(R.id.refresh);
                        Snackbar.make(mlayout2,"New startpage is now set", Snackbar.LENGTH_LONG).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.activity_main_actionbar);





    }

    public void setupBottomToolbar() {
        toolbar_bottom = (Toolbar) findViewById(R.id.toolbar_bottom);



        toolbar_bottom.inflateMenu(R.menu.activity_main_actionbar_bottom);//changed
        //toolbar2 menu items CallBack listener
        toolbar_bottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case(R.id.action_nextpage):
                        if(currentpage < totalpages) {
                            Integer nextpage = currentpage + 1;
                            webview.loadUrl(UriHandling.replaceUriParameter(Uri.parse(webview.getUrl()), "page", nextpage.toString()).toString());
                        }
                        break;
                    case(R.id.action_prevpage):
                        if(currentpage > 1)
                        {
                            Integer prevpage = currentpage-1;
                            webview.loadUrl(UriHandling.replaceUriParameter(Uri.parse(webview.getUrl()), "page", prevpage.toString()).toString());
                        }
                        break;
                    case(R.id.gotopage):
                        final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getApplicationContext())
                                .minValue(1)
                                .maxValue(totalpages)
                                .defaultValue(currentpage)
                                .backgroundColor(Color.WHITE)
                                .separatorColor(Color.TRANSPARENT)
                                .textColor(Color.BLACK)
                                .textSize(20)
                                .enableFocusability(true)
                                .wrapSelectorWheel(false)
                                .build();

                        new AlertDialog.Builder(mActivity)
                                .setTitle("Go to page")
                                .setView(numberPicker)
                                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SwipeRefreshLayout mlayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
                                        //Snackbar.make(mlayout, "You picked : " + numberPicker.getValue(), Snackbar.LENGTH_LONG).show();

                                        Uri newUrl = UriHandling.replaceUriParameter(Uri.parse(webview.getUrl()), "page", String.valueOf(numberPicker.getValue()) );

                                        webview.loadUrl(newUrl.toString());

                                    }
                                })
                                .show();
                        break;
                }

                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actionbar, menu);


        Log.d("Toolbar", String.valueOf(sharedPref.getBoolean("enable_custom_startpage", false)));

        // Enable disable set start page item
        if(!sharedPref.getBoolean("enable_custom_startpage", false)) {
            Log.d("Toolbar", "Disable menu item");

            menu.findItem(R.id.setasstartpage).setVisible(false);

        }
        else {
            Log.d("Toolbar", "Enable menu item");
        }


        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if(drawer.isDrawerOpen()) {
                        drawer.closeDrawer();
                    }
                    else if (webview.canGoBack()) {
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

                }
            });

        }

        @JavascriptInterface
        public void setupPagination(final String currentPage, final String totalPages) {
            Log.d("DEBUG", "Update spinner");

            currentpage = Integer.parseInt(currentPage);
            totalpages = Integer.parseInt(totalPages);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    paginationEnabled = true;

                    // Update toolbar title and subsitle
                    toolbar_bottom.setTitle("Current page");
                    toolbar_bottom.setSubtitle(currentpage + " of " + totalpages);

                }
            });

        }

        @JavascriptInterface
        public void disablePagination() {
             paginationEnabled = false;

        }

        @JavascriptInterface
        public void showPagination() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showViews();
                }
            });

        }

        @JavascriptInterface
        public void hidePagination() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideViews();
                }
            });
        }

        @JavascriptInterface
        public void setLoginStatus(boolean status, final String username, final String userid) {
            final SharedPreferences.Editor editor = sharedPref.edit();

            if(status) {
                Log.d("Got Status update", "Username: " + username + " Userid: " + userid );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nav_logout.withEnabled(true);
                        drawer.updateItem(nav_logout);

                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", username);
                        editor.putString("userid", userid);
                        editor.apply();

                        defaultProfile.withName(username).withIcon("https://facepunch.com/image.php?u="+userid);
                        headerResult.updateProfile(defaultProfile);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nav_logout.withEnabled(false);
                        drawer.updateItem(nav_logout);

                        editor.putBoolean("isLoggedIn", false);
                        editor.remove("username");
                        editor.remove("userid");
                        editor.apply();

                        defaultProfile.withName("Not logged in").withIcon(R.drawable.placeholder);
                        headerResult.updateProfile(defaultProfile);
                    }
                });

            }

        }
    }

    private void hideViews() {
        if(!paginationHidden) {
            Log.d("Bottom Toolbar", "Hide toolbar");
            paginationHidden = true;
            toolbar_bottom.animate().translationY(toolbar_bottom.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        }
    }

    private void showViews() {
        if(paginationHidden && paginationEnabled) {
            Log.d("Bottom Toolbar", "show toolbar");
            paginationHidden = false;
            toolbar_bottom.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }
}
