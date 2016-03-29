package com.apps.anker.facepunchdroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
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



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    String baseURL = "https://facepunch.com/";
    WebView webview;
    CircularProgressBar pb;
    RelativeLayout pbc;
    private boolean isInjected;
    String CSSfromfile;
    String JSfromfile;
    String Jquery;
    Boolean loginStatus;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the Realm configuration
        realmConfig = new RealmConfiguration.Builder(this).build();
        // Open the Realm for the UI thread.
        realm = Realm.getInstance(realmConfig);

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
                Log.d("Resource", url);
                pb.setProgressWithAnimation(webview.getProgress());
                Log.d("Progress", String.valueOf(webview.getProgress()));


                // Need a better way to detect if DOM is ready to inject CSS
                if (webview.getProgress() > 30 && !isInjected) {
                    Log.d("Progress", "INJECT!!");
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
                Log.d("Webview", "onPageFinished");

                // Change Actionbar Title
                mActivityTitle = webview.getTitle();
                toolbar.setTitle(mActivityTitle);
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

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webview.reload();
                mSwipeRefreshLayout.setRefreshing(false);

            }

        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        refreshDrawerItems();
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
                        }


                        return false;
                    }
                })
                .build();

                addDrawerItems();

    }

    protected void addDrawerItems() {

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem nav_home      = new PrimaryDrawerItem().withIdentifier(1).withName("Home").withIcon(R.drawable.ic_home_black_24dp).withSelectable(false);
        PrimaryDrawerItem nav_events    = new PrimaryDrawerItem().withIdentifier(2).withName("Events").withIcon(R.drawable.ic_event_black_24dp).withSelectable(false);
        PrimaryDrawerItem nav_popular   = new PrimaryDrawerItem().withIdentifier(3).withName("Popular").withIcon(R.drawable.ic_favorite_black_24dp).withSelectable(false);
        PrimaryDrawerItem nav_read      = new PrimaryDrawerItem().withIdentifier(4).withName("Read").withIcon(R.drawable.ic_markunread_mailbox_black_24dp).withSelectable(false);
        PrimaryDrawerItem nav_search    = new PrimaryDrawerItem().withIdentifier(5).withName("Search").withIcon(R.drawable.ic_search_black_24dp).withSelectable(false);
        PrimaryDrawerItem nav_messages  = new PrimaryDrawerItem().withIdentifier(6).withName("Messages").withIcon(R.drawable.ic_mail_outline_black_24dp).withSelectable(false);
        PrimaryDrawerItem nav_cpanel    = new PrimaryDrawerItem().withIdentifier(7).withName("Control panel").withIcon(R.drawable.ic_build_black_24dp).withSelectable(false);

        PrimaryDrawerItem nav_settings  = new PrimaryDrawerItem().withIdentifier(8).withName("Settings").withIcon(R.drawable.ic_settings_black_24dp).withSelectable(false);

        if(sharedPref.getBoolean("isLoggedIn", false)) {
            nav_logout    = new PrimaryDrawerItem().withIdentifier(9).withName("Logout").withIcon(R.drawable.ic_lock_open_black_24dp).withSelectable(false).withEnabled(true);
        } else {
            nav_logout    = new PrimaryDrawerItem().withIdentifier(9).withName("Logout").withIcon(R.drawable.ic_lock_open_black_24dp).withSelectable(false).withEnabled(false);
        }


        PrimaryDrawerItem nav_donate    = new PrimaryDrawerItem().withIdentifier(10).withName("Donate").withIcon(R.drawable.ic_card_giftcard_black_24dp).withSelectable(false);
        drawer.addItems(
                nav_home,
                nav_events,
                nav_popular,
                nav_read,
                nav_search,
                nav_messages,
                nav_cpanel
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
            drawer.addItem(new SecondaryDrawerItem().withName("Edit pinned items").withIcon(R.drawable.ic_edit_black_24dp).withIdentifier(11).withSelectable(false));
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
                    default:
                        return false;
                }
            }
        });

        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.activity_main_actionbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actionbar, menu);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
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

    private void crossfadeToWebview() {
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
        pbc.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        pbc.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

    }
}
