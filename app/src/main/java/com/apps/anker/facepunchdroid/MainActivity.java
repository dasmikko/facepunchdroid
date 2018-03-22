package com.apps.anker.facepunchdroid;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.Facepunch.Objects.SubscriptionFolder;
import com.apps.anker.facepunchdroid.Facepunch.Subscription;
import com.apps.anker.facepunchdroid.Migrations.MainMigration;
import com.apps.anker.facepunchdroid.Pagepinning.PagePinningManager;
import com.apps.anker.facepunchdroid.RealmObjects.UserScript;
import com.apps.anker.facepunchdroid.Services.PrivateMessageService;
import com.apps.anker.facepunchdroid.Services.ServiceManager;
import com.apps.anker.facepunchdroid.Tools.Assets;
import com.apps.anker.facepunchdroid.Tools.CustomTabsHelper;
import com.apps.anker.facepunchdroid.Tools.Downloading;
import com.apps.anker.facepunchdroid.Tools.Language;
import com.apps.anker.facepunchdroid.Tools.ShortcutsManager;
import com.apps.anker.facepunchdroid.Tools.UriHandling;
import com.apps.anker.facepunchdroid.Webview.VideoEnabledWebChromeClient;
import com.apps.anker.facepunchdroid.Webview.VideoEnabledWebView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.cookie.CookieMiddleware;
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


import java.io.ByteArrayInputStream;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    String baseURL = "https://forum.facepunch.com/f/";
    VideoEnabledWebView webview;
    private VideoEnabledWebChromeClient webChromeClient;
    ProgressBar pb;
    RelativeLayout pbc;
    private boolean isInjected;
    String CSSfromfile;
    String JSfromfile;
    String Jquery;
    Boolean loginStatus = false;

    boolean isFullscreen = false;

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
    boolean disableUserTitleImages;
    boolean enableDarkTheme;
    boolean enableUserscripts;
    String selectedLang;

    SwipeRefreshLayout mSwipeRefreshLayout;
    Toolbar toolbar;
    private String mActivityTitle;

    // Pinned items
    static RealmConfiguration realmConfig;

    public static Realm realm;

    // Userscripts
    RealmResults<UserScript> userScripts;
    String fullJSUserScriptsString = "";


    // Drawer
    Drawer drawer;
    AccountHeader headerResult;
    ProfileDrawerItem defaultProfile;

    // Drawer items
    PrimaryDrawerItem nav_logout;

    static Activity mActivity;
    Context mContext;

    Boolean dissableAllImages = false;

    // Video handling variables
    Boolean shouldDownloadVideo = false;
    String videoContextUrl = null;

    Boolean wauterboiMode;


    String tempImageDownloadUrl;
    String tempDownloadUrl;

    // Search toolbar
    EditText search_input;
    ImageButton search_next_button;
    ImageButton search_previous_button;
    ImageButton search_close;
    Toolbar search_toolbar;


    Boolean isThread = false;


    String defaultUserAgent = "";

    public static ServiceManager serviceManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        disableUserTitleImages = sharedPref.getBoolean("disable_usertitle_images", false);
        Log.d("disableUserTitleImages", String.valueOf(disableUserTitleImages));

        enableDarkTheme = sharedPref.getBoolean("enable_dark_theme", false);
        Log.d("DarkTheme", String.valueOf(enableDarkTheme));

        enableUserscripts = sharedPref.getBoolean("enable_userscripts", false);
        Log.d("Userscript", String.valueOf(enableUserscripts));

        wauterboiMode = false;

        // Update language
        selectedLang = sharedPref.getString("language", "system");
        Language.setLanguage(selectedLang, getResources());

        // Set dark theme if enabled dark mode
        if (enableDarkTheme) {
            super.setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        Realm.init(this);
        setContentView(R.layout.activity_main);



        mActivity = this;
        mContext = getApplicationContext();

        // Create the Realm configuration
        realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(Constants.schemaVersion) // Must be bumped when the schema changes
                .migration(MainMigration.getMigration()) // Migration to run instead of throwing an exception
                .build();

        Realm.setDefaultConfiguration(realmConfig);


        // Open the Realm for the UI thread.
        realm = Realm.getDefaultInstance();

        // Inject UserScripts
        if (enableUserscripts) {
            // Get all userscripts
            userScripts = realm.where(UserScript.class).findAll();

            Log.d("Userscript", "USE USERSCRIPTS");

            if (userScripts.size() > 0) {
                fullJSUserScriptsString += "jQuery(function() { \n";
                for (UserScript uScript : userScripts) {
                    Log.d("USCRIPT", uScript.getJavascript());
                    fullJSUserScriptsString += uScript.getJavascript();
                }
                fullJSUserScriptsString += "}); \n";

            }
        }


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
            JSfromfile = customCSS.cssToString(getAssets().open("fp-mobile2.js"));
            Jquery = customCSS.cssToString(getAssets().open("jquery.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        // Progressbar and WebView
        pb = (ProgressBar) findViewById(R.id.toolbarProgressbar);
        webview = (VideoEnabledWebView) findViewById(R.id.webView);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = findViewById(R.id.refresh); // Your own view, read class comments
        final ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments


        mActivity.registerForContextMenu(webview);


        // On Scroll handling
        webview.setOnScrollChangedCallback(new VideoEnabledWebView.OnScrollChangedCallback() {
            public void onScroll(int l, int t, int oldl, int oldt) {
                //Do stuff

                if (oldScrollPos != t && paginationEnabled) {
                    Log.d("Scroll pos", "Changed");
                    if (oldScrollPos < t) {
                        if (paginationEnabled && !paginationHidden) {
                            hideViews();
                        }

                    } else if (oldScrollPos > t) {
                        if (paginationEnabled && paginationHidden) {
                            showViews();
                        }
                    }
                }
                oldScrollPos = t;
            }
        });


        // Setup Webview
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("useCustomStyles", String.valueOf(sharedPref.getBoolean("enable_custom_styles", false)));
                if (sharedPref.contains("enable_custom_styles")) {
                    useCustomStyles = sharedPref.getBoolean("enable_custom_styles", false);
                    Log.d("useCustomStyles", String.valueOf(useCustomStyles));
                }

                if(url.startsWith("https://facepunch.com/showthread.php?t=")) {
                    isThread = true;
                    mActivity.invalidateOptionsMenu();
                } else {
                    isThread = false;
                    mActivity.invalidateOptionsMenu();
                }

                hideSearchViews();

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
            public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
                Log.d("WebResource", url);
                if (dissableAllImages && !url.contains("facepunch.com")) {
                    Log.d("WebResource", "DISABLE IMAGE");
                    if (url.contains(".jpg") || url.contains(".png") || url.contains(".jpeg") || url.contains(".gif")) {
                        Log.d("WebResource", "IMAGE DISABLED");
                        return new WebResourceResponse("text/html", "UTF-8", null);
                    }
                }

                if (url.contains("/styles/user")) {
                    return getCssWebResourceResponseFromAsset();
                } else if (url.contains(".com/manifest?")) {
                    Log.d("Intercept", "manifest file");
                    return getJsWebResourceResponseFromAsset();
                } else {
                    return super.shouldInterceptRequest(view, url);
                }
            }

            private WebResourceResponse getCssWebResourceResponseFromAsset() {
                try {
                    WebResourceResponse fullCSS = null;
                    String fullCSSString = "";

                    //fullCSSString += customCSS.cssToString(getAssets().open("fpstyle.css"));

                     // Inject Mobile style
                    fullCSSString += "@import url('https://dasmikko.github.io/facepunchdroid/styles.css');\n";

                    // Inject darktheme
                    if (enableDarkTheme) {
                        Log.d("Darktheme", "USE DARKTHEME");

                        //fullCSSString += customCSS.cssToString(getAssets().open("dark_theme.css"));
                        fullCSSString += "@import url('https://pgsil.github.io/awfullydark2/styles.css');";
                        Log.d("Darktheme", "Injecting this: " + "@import url('https://pgsil.github.io/awfullydark2/styles.css');");
                    }

                    // Inject user title disablign
                    if (disableUserTitleImages) {
                        fullCSSString += customCSS.cssToString(getAssets().open("disableusertitleimages.css"));
                    }



                    // Check if custom user style is enabled
                    if (useCustomStyles && sharedPref.contains("custom_style_file")) {
                        fullCSSString += sharedPref.getString("custom_style_file", "");
                    }

                    Log.d("FullCSS", fullCSSString);
                    fullCSS = stringToWebResource(fullCSSString);

                    return fullCSS;
                } catch (IOException e) {
                    return null;
                }
            }


            private WebResourceResponse stringToWebResource(String CSS) {
                return new WebResourceResponse("text/css", "UTF-8", new ByteArrayInputStream(CSS.getBytes(StandardCharsets.UTF_8)));
            }

            private WebResourceResponse stringToWebResourceJS(String JS) {
                return new WebResourceResponse("text/javascript", "UTF-8", new ByteArrayInputStream(JS.getBytes(StandardCharsets.UTF_8)));
            }


            private WebResourceResponse getJsWebResourceResponseFromAsset() {
                WebResourceResponse fullJS = null;
                String fullJSString = "";

                try {
                    // Inject Mobile Javascript
                    fullJSString += customCSS.cssToString(getAssets().open("fp-mobile2.js"));

                    // Inject UserScripts
                    if (enableUserscripts) {
                        Log.d("Userscript", "USE USERSCRIPTS");

                        fullJSString += fullJSUserScriptsString;
                    }


                    Log.d("FullJS", fullJSString);
                    fullJS = stringToWebResourceJS(fullJSString);

                    return fullJS;
                } catch (IOException e) {
                    return null;
                }


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadUrl(url);







                // Handle direct image links
                if (url.endsWith(".jpg") ||
                        url.endsWith(".jpeg") ||
                        url.endsWith(".png") ||
                        url.endsWith(".gif")) {

                    Intent i = new Intent(mContext, ImageViewer.class);
                    i.putExtra("url", url);
                    mActivity.startActivity(i);
                    return true;
                }

                if (url.startsWith("https://facepunch.com/misc.php")) {
                    //Toast.makeText(getApplicationContext(), "Show smiley dialog", Toast.LENGTH_LONG).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();

                    View dialog_view = inflater.inflate(R.layout.smiley_dialog, null);

                    // set the custom dialog components - text, image and button
                    WebView sWebview = (WebView) dialog_view.findViewById(R.id.smiley_webview);
                    sWebview.getSettings().setJavaScriptEnabled(true);
                    sWebview.setWebContentsDebuggingEnabled(true);
                    sWebview.addJavascriptInterface(new WebAppInterface(mActivity), "Android");
                    sWebview.loadUrl("file:///android_asset/smileys.html");


                    builder.setTitle(getString(R.string.select_smiley));
                    builder.setView(dialog_view);
                    builder.setPositiveButton(getString(R.string.action_close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    builder.create().show();

                    return true;
                }

                if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mt.getTo()});
                    intent.setType("message/rfc822");
                    startActivity(intent);
                    return true;
                }

                if (url.contains("auth.google.") || url.contains("accounts.google.")) {
                    return false;
                }

                if (url.contains("api.twitter.")) {
                    return false;
                }

                if (url.contains("facebook.com/login") || url.contains("facebook.com/dialog/oauth")) {
                    return false;
                }

                if (url.contains("github.com/login")) {
                    return false;
                }

                if (url.contains("steamcommunity.com/openid/")) {
                    return false;
                }

                String urlHost = Uri.parse(url).getHost();
                Log.d("Webview", "ShouldOverrideURLloading " + url);
                switch (urlHost) {
                    case "forum.facepunch.com":
                        //webview.getSettings().setUserAgentString(defaultUserAgent + " FacepunchDroid");
                        return false;
                    case "auth.facepunch.com":
                        //webview.getSettings().setUserAgentString(defaultUserAgent + " FacepunchDroid");
                        return false;
                    default:
                        String thepackage = CustomTabsHelper.getPackageNameToUse(mActivity);

                        if (thepackage == null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        } else {
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                                    .setShowTitle(true)
                                    .setSecondaryToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
                            builder.setStartAnimations(mActivity, R.anim.slide_in_right, R.anim.slide_out_left);
                            builder.setExitAnimations(mActivity, R.anim.slide_in_left, R.anim.slide_out_right);

                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.launchUrl(mActivity, Uri.parse(url));

                        }
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

        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webview);
        webview.setWebChromeClient(webChromeClient);

        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                isFullscreen = fullscreen;

                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    videoLayout.setBackgroundColor(Color.parseColor("#000000"));
                    if (Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                    toolbar.setVisibility(View.GONE);
                    videoLayout.setVisibility(View.VISIBLE);
                } else {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                    toolbar.setVisibility(View.VISIBLE);
                    videoLayout.setVisibility(View.GONE);
                }

            }
        });

        // Config webview
        webview.setWebContentsDebuggingEnabled(true);
        webview.addJavascriptInterface(new WebAppInterface(this), "Android");
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(false);
        webview.getSettings().setDomStorageEnabled(true);


        // Allow Zoom on tablet!
        if (isTablet(mContext)) {
            webview.getSettings().setBuiltInZoomControls(true);
            webview.getSettings().setDisplayZoomControls(false);
        }

        if (enableDarkTheme) {
            webview.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.nightDrawerBackground));
        }

        // Set new UA
        String ua = webview.getSettings().getUserAgentString();

        // Save default useragent for later switching
        if(defaultUserAgent.equals("")) {
            defaultUserAgent = ua;
        }

        //webview.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android) FacepunchDroid");
        webview.getSettings().setUserAgentString("Mozilla/5.0 Google");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        // First load
        if (savedInstanceState != null) {
            webview.restoreState(savedInstanceState);
        } else {
            if (sharedPref.getBoolean("enable_custom_startpage", false) && sharedPref.contains("current_startpage")) {
                webview.loadUrl(sharedPref.getString("current_startpage", baseURL));
            } else {
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
        setupSearchToolbar();

        Intent messageintent = getIntent();



        //Log.d("Extras", intent.getExtras().toString());
        if (intent.getStringExtra("viewMessage") != null) {
            Log.d("Extra", "NEW MESSAGE OPEN IT");
            webview.loadUrl("https://facepunch.com/" + messageintent.getStringExtra("viewMessage"));
        } else {
            Log.d("Extra", "no new message");
        }


        //Log.d("Extras", intent.getExtras().toString());
        if (intent.getStringExtra("viewThreadUrl") != null) {
            Log.d("Extra", "NEW THREAD SUBSCRIPTION OPEN IT");
            webview.loadUrl("https://facepunch.com/" + messageintent.getStringExtra("viewThreadUrl") + "&goto=newpost");
        } else {
            Log.d("Extra", "no new thread subscription notification click");
        }


        // Init services
        /*serviceManager = new ServiceManager();
        serviceManager.initServices(getApplicationContext());
        */

        /*if(sharedPref.getBoolean("isLoggedIn", false)) {
            Subscription sub = new Subscription();
            ArrayList<SubscriptionFolder> folders = sub.getSubscriptionFolders();

            for(SubscriptionFolder folder : folders) {
                Log.d("Folder", folder.getName());
            }

        }*/


        final RelativeLayout rlLayout = (RelativeLayout) findViewById(R.id.activityMainLayout);
        rlLayout.post(new Runnable()
        {
            @Override
            public void run(){
                //create your anim here
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animateRevealShow(rlLayout);
                }
            }
        });


        // Update the shortcuts!
        updateShortcuts();


        /**
         *
         * ON CREATE END
         *
         * TODO Make this a shit tonne more clean
         *
         * sorry everyone :<
         */

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent.getStringExtra("shortcut") != null) {
            Log.d("Extra", "SHORTCUT");
            webview.loadUrl(intent.getStringExtra("shortcut"));
        }

        //Log.d("Extras", intent.getExtras().toString());
        if (intent.getStringExtra("viewMessage") != null) {
            Log.d("Extra", "NEW MESSAGE OPEN IT");
            webview.loadUrl("https://facepunch.com/" + intent.getStringExtra("viewMessage"));
        } else {
            Log.d("Extra", "no new message");
        }


        //Log.d("Extras", intent.getExtras().toString());
        if (intent.getStringExtra("viewThreadUrl") != null) {
            Log.d("Extra", "NEW THREAD SUBSCRIPTION OPEN IT");
            webview.loadUrl("https://facepunch.com/" + intent.getStringExtra("viewThreadUrl") + "&goto=newpost");
        } else {
            Log.d("Extra", "no new thread subscription notification click");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        WebView cWebview = (WebView) v;
        final WebView.HitTestResult result = cWebview.getHitTestResult();
        //Log.d("HitTestResult", result.getExtra());

        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                // do the menu action

                switch (item.getItemId()) {
                    case 1:
                        Downloading.downloadImage(getIntent(), result.getExtra().toString(), mActivity);
                        tempImageDownloadUrl = result.getExtra().toString();
                        break;
                    case 2:
                        Log.d("ImageView", "Starting imageviewer intent!");
                        Intent i = new Intent(mActivity, ImageViewer.class);
                        i.putExtra("url", result.getExtra());
                        mActivity.startActivity(i);
                        break;
                    case 3:
                        // Open
                        webview.loadUrl(result.getExtra());
                        break;
                    case 4:
                        // Open in browser
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getExtra()));
                        startActivity(intent);
                        break;
                    case 5:
                        // Share link
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, result.getExtra());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                    case 6:
                        // Share video
                        Intent sendVideoIntent = new Intent();
                        sendVideoIntent.setAction(Intent.ACTION_SEND);
                        sendVideoIntent.putExtra(Intent.EXTRA_TEXT, result.getExtra());
                        sendVideoIntent.setType("video/*");
                        startActivity(sendVideoIntent);
                        break;
                    case 7:
                        // Open video in other app
                        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoContextUrl));
                        videoIntent.setDataAndType(Uri.parse(videoContextUrl), "video/*");
                        startActivity(videoIntent);
                        break;
                    case 8:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.DOWNLOAD_VIDEO_PERMISSION);
                        } else {
                            // Download video
                            Downloading.downloadUrl(getIntent(), videoContextUrl, mActivity);
                        }
                        break;
                    case 9:
                        ClipboardManager clipboard = (ClipboardManager)
                                getSystemService(Context.CLIPBOARD_SERVICE);

                        ClipData clip = ClipData.newPlainText("simple text", result.getExtra());

                        clipboard.setPrimaryClip(clip);

                        Snackbar.make((SwipeRefreshLayout) findViewById(R.id.refresh), R.string.snackbar_copied, Snackbar.LENGTH_LONG).show();
                        break;
                    case 10:
                        ClipboardManager clipboard2 = (ClipboardManager)
                                getSystemService(Context.CLIPBOARD_SERVICE);

                        ClipData clip2 = ClipData.newPlainText("simple text", videoContextUrl);

                        clipboard2.setPrimaryClip(clip2);

                        Snackbar.make((SwipeRefreshLayout) findViewById(R.id.refresh), R.string.snackbar_copied, Snackbar.LENGTH_LONG).show();
                        break;
                }

                shouldDownloadVideo = false;
                return true;
            }
        };

        if (!shouldDownloadVideo) {
            Log.d("ContextType", String.valueOf(result.getType()));
            if (result.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                    result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                // Menu options for an image.
                //set the header title to the image url
                menu.setHeaderTitle(result.getExtra());
                menu.add(0, 9, 0, R.string.context_menu_copy_url).setOnMenuItemClickListener(handler);
                menu.add(0, 1, 0, R.string.context_menu_save_image).setOnMenuItemClickListener(handler);
                menu.add(0, 2, 0, R.string.context_menu_view_image).setOnMenuItemClickListener(handler);
            } else if (result.getType() == WebView.HitTestResult.ANCHOR_TYPE ||
                    result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
                // Menu options for a hyperlink.
                //set the header title to the link url
                menu.setHeaderTitle(result.getExtra());
                menu.add(0, 3, 0, R.string.context_menu_open).setOnMenuItemClickListener(handler);
                menu.add(0, 4, 0, R.string.context_menu_open_in_browser).setOnMenuItemClickListener(handler);
                menu.add(0, 9, 0, R.string.context_menu_copy_url).setOnMenuItemClickListener(handler);
                menu.add(0, 1, 0, R.string.context_menu_save_link).setOnMenuItemClickListener(handler);
                menu.add(0, 5, 0, R.string.context_menu_share_link).setOnMenuItemClickListener(handler);
            }
        } else {
            menu.setHeaderTitle(videoContextUrl);
            menu.add(0, 7, 0, R.string.context_menu_open_in_other_app).setOnMenuItemClickListener(handler);
            menu.add(0, 10, 0, R.string.context_menu_copy_url).setOnMenuItemClickListener(handler);
            menu.add(0, 8, 0, R.string.context_menu_save_video).setOnMenuItemClickListener(handler);
            menu.add(0, 6, 0, R.string.context_menu_share_video).setOnMenuItemClickListener(handler);
        }
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
        Language.setLanguage(selectedLang, getResources());
        super.onResume();
        webview.onResume();
        refreshDrawerItems();
        sharedPref.registerOnSharedPreferenceChangeListener(spChanged);
        Log.d("OnResume", "Activity resumed!");

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        disableUserTitleImages = sharedPref.getBoolean("disable_usertitle_images", false);
        Log.d("disableUserTitleImages", String.valueOf(disableUserTitleImages));

        enableDarkTheme = sharedPref.getBoolean("enable_dark_theme", false);
        Log.d("DarkTheme", String.valueOf(enableDarkTheme));

        enableUserscripts = sharedPref.getBoolean("enable_userscripts", false);
        Log.d("Userscript", String.valueOf(enableUserscripts));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.DOWNLOAD_VIDEO_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Download video
                    Downloading.downloadUrl(getIntent(), videoContextUrl, mActivity);
                    return;
                }
            }
            case Constants.DOWNLOAD_IMAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Downloading.downloadImage(getIntent(), tempImageDownloadUrl, mActivity);
                    return;
                }
            }
        }
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }

    /**
     * Setups the drawer, with all styles and user picture
     */
    private void setupDrawer() {
        // Setup Profile
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            String username = sharedPref.getString("username", "Not logged in");
            String userid = sharedPref.getString("userid", "");
            defaultProfile = new ProfileDrawerItem().withName(username).withIcon("https://facepunch.com/image.php?u=" + userid);
        } else {
            defaultProfile = new ProfileDrawerItem().withName(getString(R.string.not_logged_in));
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

                        if (sharedPref.contains("userid")) {
                            String userid = sharedPref.getString("userid", "");
                            webview.loadUrl(baseURL + "member.php?u=" + userid);
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
                        .noCache()
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
                        } else if (id == 2) { // Changes
                            webview.loadUrl("https://forum.facepunch.com/commits");
                        } else if (id == 3) { // Roadmap
                            webview.loadUrl("https://forum.facepunch.com/roadmap");
                        } else if (id == 4) { // Help
                            webview.loadUrl("https://forum.facepunch.com/help");
                        } else if (id == 9) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog));
                            builder.setMessage(getString(R.string.logout_description))
                                    .setPositiveButton(R.string.answer_yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                CookieManager.getInstance().removeAllCookies(null);
                                                CookieManager.getInstance().flush();
                                            }

                                            loginStatus = false;
                                            serviceManager.killServices();
                                            webview.loadUrl(baseURL);
                                        }
                                    })
                                    .setNegativeButton(R.string.answer_no, new DialogInterface.OnClickListener() {
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
        PrimaryDrawerItem nav_home = new PrimaryDrawerItem().withIdentifier(1).withName(getString(R.string.nav_home)).withIcon(GoogleMaterial.Icon.gmd_home).withSelectable(false);
        PrimaryDrawerItem nav_changes = new PrimaryDrawerItem().withIdentifier(2).withName(getString(R.string.nav_changes)).withIcon(GoogleMaterial.Icon.gmd_event).withSelectable(false);
        PrimaryDrawerItem nav_roadmap = new PrimaryDrawerItem().withIdentifier(3).withName(getString(R.string.nav_roadmap)).withIcon(GoogleMaterial.Icon.gmd_favorite).withSelectable(false);
        PrimaryDrawerItem nav_help = new PrimaryDrawerItem().withIdentifier(4).withName(getString(R.string.nav_help)).withIcon(GoogleMaterial.Icon.gmd_markunread_mailbox).withSelectable(false);



        PrimaryDrawerItem nav_settings = new PrimaryDrawerItem().withIdentifier(8).withName(getString(R.string.action_settings)).withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false);

        if (sharedPref.getBoolean("isLoggedIn", false)) {
            nav_logout = new PrimaryDrawerItem().withIdentifier(9).withName(getString(R.string.nav_logout)).withIcon(GoogleMaterial.Icon.gmd_lock_open).withSelectable(false).withEnabled(true);
        } else {
            nav_logout = new PrimaryDrawerItem().withIdentifier(9).withName(getString(R.string.nav_logout)).withIcon(GoogleMaterial.Icon.gmd_lock_open).withSelectable(false).withEnabled(false);
        }


        PrimaryDrawerItem nav_donate = new PrimaryDrawerItem().withIdentifier(10).withName(getString(R.string.nav_donate)).withIcon(GoogleMaterial.Icon.gmd_card_giftcard).withSelectable(false);


        drawer.addItems(
                nav_home,
                nav_changes,
                nav_roadmap,
                nav_help
        );

        // Get Pinned items
        RealmResults<PinnedItem> pinnedItems = realm.where(PinnedItem.class).findAll();

        Log.d("Pitem list", pinnedItems.toString());

        if (pinnedItems.size() > 0) {
            drawer.addItem(new SectionDrawerItem().withName(getString(R.string.nav_pinnedpages)));
            for (PinnedItem pitem : pinnedItems) {

                drawer.addItem(new PrimaryDrawerItem().withName(pitem.getTitle()).withSelectable(false).withTag(pitem.getUrl()).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (sharedPref.getBoolean("settings_pagepinning_goto_newest_post", true)) {
                            if (drawerItem.getTag().toString().contains("?")) {
                                webview.loadUrl(drawerItem.getTag().toString() + "&goto=newpost");
                            } else {
                                webview.loadUrl(drawerItem.getTag().toString() + "?goto=newpost");
                            }
                            return false;
                        }

                        webview.loadUrl(drawerItem.getTag().toString());
                        return false;
                    }
                }));
            }
            drawer.addItem(new SecondaryDrawerItem().withName(getString(R.string.nav_editpinnedpages)).withIcon(GoogleMaterial.Icon.gmd_edit).withIdentifier(11).withSelectable(false));
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

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {


        return super.onCreateOptionsMenu(menu);
    }

    public void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.mActionbar);
        setSupportActionBar(toolbar);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.forward:
                        if (webview.canGoForward())
                            webview.goForward();
                        return true;
                    case R.id.action_refresh:
                        webview.reload();
                        return true;
                    case R.id.pinpage:
                        PagePinningManager.pin_page(mActivity, webview.getTitle(), webview.getUrl());
                        final SwipeRefreshLayout mlayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
                        Snackbar.make(mlayout, getString(R.string.paged_was_pinned), Snackbar.LENGTH_LONG).show();
                        updateShortcuts();

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
                        Snackbar.make(mlayout2, getString(R.string.new_startpage_set), Snackbar.LENGTH_LONG).show();
                        return true;
                    case R.id.dissableAllImages:
                        if (dissableAllImages) {
                            dissableAllImages = false;
                        } else {
                            dissableAllImages = true;
                        }
                        mActivity.invalidateOptionsMenu();
                        webview.reload();
                        return true;
                    case R.id.find:
                        /*android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity);
                        builder.setTitle(R.string.dialog_find);


                        // Set up the input
                        final EditText input = new EditText(mActivity);

                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton(R.string.answer_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                webview.findAllAsync(input.getText().toString());
                                showSearchViews();
                            }
                        });
                        builder.setNegativeButton(R.string.answer_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

                        input.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                        */

                        showSearchViews();
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

        hideViews();

        toolbar_bottom.inflateMenu(R.menu.activity_main_actionbar_bottom);//changed
        //toolbar2 menu items CallBack listener
        toolbar_bottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.action_nextpage):
                        if (currentpage < totalpages) {
                            Integer nextpage = currentpage + 1;
                            webview.loadUrl(UriHandling.replaceUriParameter(Uri.parse(webview.getUrl()), "page", nextpage.toString()).toString());
                        }
                        break;
                    case (R.id.action_prevpage):
                        if (currentpage > 1) {
                            Integer prevpage = currentpage - 1;
                            webview.loadUrl(UriHandling.replaceUriParameter(Uri.parse(webview.getUrl()), "page", prevpage.toString()).toString());
                        }
                        break;
                    case (R.id.gotopage):
                        View dView = getLayoutInflater().inflate(R.layout.dialog_gotopage, null);
                        final AlertDialog Adialog = new AlertDialog.Builder(mActivity)
                                .setTitle(getString(R.string.go_to_page))
                                .setView(dView)
                                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SwipeRefreshLayout mlayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
                                        NumberPicker nP = (NumberPicker) ((AlertDialog) dialog).findViewById(R.id.pagenumberPicker);
                                        Integer val = nP.getValue();

                                        Uri newUrl = UriHandling.replaceUriParameter(Uri.parse(webview.getUrl()), "page", String.valueOf(val));

                                        webview.loadUrl(newUrl.toString());

                                    }
                                })
                                .show();

                        Button lastpageBtn = (Button) dView.findViewById(R.id.btn_gotolasttpage);
                        lastpageBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri newUrl = UriHandling.replaceUriParameter(Uri.parse(webview.getUrl()), "page", String.valueOf(totalpages));
                                webview.loadUrl(newUrl.toString());
                                Adialog.dismiss();
                            }
                        });

                        Button firstBtn = (Button) dView.findViewById(R.id.btn_gotofirstpage);
                        firstBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri newUrl = UriHandling.replaceUriParameter(Uri.parse(webview.getUrl()), "page", String.valueOf(1));
                                webview.loadUrl(newUrl.toString());
                                Adialog.dismiss();
                            }
                        });





                        NumberPicker nP = (NumberPicker) Adialog.findViewById(R.id.pagenumberPicker);
                        nP.setMaxValue(totalpages);
                        nP.setMinValue(1);
                        nP.setValue(currentpage);
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
        if (!sharedPref.getBoolean("enable_custom_startpage", false)) {
            Log.d("Toolbar", "Disable menu item");

            menu.findItem(R.id.setasstartpage).setVisible(false);

        } else {
            Log.d("Toolbar", "Enable menu item");
        }

        if (dissableAllImages) {
            menu.findItem(R.id.dissableAllImages).setChecked(true);
        } else {
            menu.findItem(R.id.dissableAllImages).setChecked(false);
        }


        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (!webChromeClient.onBackPressed()) { // Handle fullscreen
                        if (drawer.isDrawerOpen()) {
                            drawer.closeDrawer();
                        } else if (webview.canGoBack()) {
                            webview.goBack();
                        } else {
                            finish();
                        }
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("https://forum.facepunch.com/f/"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
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
        public void openVideoContextMenu(String url) {
            final String videourl = url;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    shouldDownloadVideo = true;
                    videoContextUrl = videourl;
                    openContextMenu(webview);


                }
            });

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
        public void insertToInput(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    webview.loadUrl("javascript:insertAtCaret('" + text + "')");

                }
            });

            Toast.makeText(mActivity, "Smiley was added", Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void copyToClipholder(String text) {
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clip = ClipData.newPlainText("simple text", text);

            clipboard.setPrimaryClip(clip);

            Toast.makeText(mActivity, "Smiley was copied to clipboard", Toast.LENGTH_SHORT).show();
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
                    toolbar_bottom.setVisibility(View.VISIBLE);
                    // Update toolbar title and subsitle
                    toolbar_bottom.setTitle(getString(R.string.currentpage));
                    toolbar_bottom.setSubtitle(currentpage + " " + getString(R.string.of) + " " + totalpages);

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
        public void setLoginStatus(boolean status, final String username) {
            final SharedPreferences.Editor editor = sharedPref.edit();

            if (status) {
                Log.d("Got Status update", "Username: " + username);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nav_logout.withEnabled(true);
                        drawer.updateItem(nav_logout);

                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", username);
                        editor.apply();

                        defaultProfile.withName(username);
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

                        defaultProfile.withName(getString(R.string.not_logged_in)).withIcon(R.drawable.placeholder);
                        headerResult.updateProfile(defaultProfile);
                    }
                });

            }

        }
    }

    private void setupSearchToolbar() {
        search_next_button = (ImageButton) findViewById(R.id.search_next);
        search_previous_button = (ImageButton) findViewById(R.id.search_previous);
        search_close = (ImageButton) findViewById(R.id.search_close);
        search_input = (EditText) findViewById(R.id.search_input);
        search_toolbar = (Toolbar) findViewById(R.id.mActionbarSearch);


        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                webview.findAllAsync(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("search", "searching next...");
                webview.findNext(true);
            }
        });

        search_previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("search", "searching previous...");
                webview.findNext(false);
            }
        });

        search_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("search", "Closing...");
                search_input.setText("");
                hideSearchViews();
            }
        });

        //hideSearchViews();
        //search_toolbar.setVisibility(View.VISIBLE);

    }

    private void hideViews() {
        if (!paginationHidden) {
            Log.d("Bottom Toolbar", "Hide toolbar");
            paginationHidden = true;
            toolbar_bottom.animate().translationY(toolbar_bottom.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        }
    }


    private void showViews() {
        if (paginationHidden && paginationEnabled) {
            Log.d("Bottom Toolbar", "show toolbar");
            paginationHidden = false;
            toolbar_bottom.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    private void hideSearchViews() {
        Log.d("Search Toolbar", "Hide toolbar");
        //search_toolbar.animate().translationY(search_toolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2)).start();
        search_toolbar.animate().alpha(0).setInterpolator(new DecelerateInterpolator(2)).start();
        toolbar.animate().alpha(1).setInterpolator(new DecelerateInterpolator(2)).start();

        toolbar.setVisibility(View.VISIBLE);
        search_toolbar.setVisibility(View.GONE);

        webview.findAllAsync("");

        search_input.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_input.getWindowToken(), 0);
    }

    private void showSearchViews() {
        Log.d("Search  Toolbar", "show toolbar");
        //search_toolbar.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2)).start();
        search_toolbar.animate().alpha(1).setInterpolator(new DecelerateInterpolator(2)).start();
        toolbar.animate().alpha(0).setInterpolator(new DecelerateInterpolator(2)).start();

        toolbar.setVisibility(View.GONE);
        search_toolbar.setVisibility(View.VISIBLE);

        search_input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    // Test if tablet or phone
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }


    private void updateShortcuts() {
        ShortcutsManager.updateShortcuts(realm, mActivity);
    }
}
