package com.apps.anker.facepunchdroid;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.Cookies.Cookies;
import com.apps.anker.facepunchdroid.Migrations.MainMigration;
import com.apps.anker.facepunchdroid.RealmObjects.UserScript;
import com.apps.anker.facepunchdroid.Services.PrivateMessageService;
import com.apps.anker.facepunchdroid.Tools.Language;
import com.koushikdutta.ion.Ion;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    static Context mContext;
    static Activity mActivity;

    public static View settingsView;

    private static NotificationManager mNM;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            Log.d("Pref change", preference.getKey());

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if(preference.getKey().equals("language")) {
                Log.d("LANG update", stringValue);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Boolean enableDarkTheme = sharedPref.getBoolean("enable_dark_theme", false);
        Log.d("DarkTheme", String.valueOf(enableDarkTheme));

        // Set dark theme if enabled dark mode
        if(enableDarkTheme) {
            super.setTheme(R.style.AppThemeSettingsDark);
        }

        // Update language
        String selectedLang = sharedPref.getString("language", "system");
        Language.setLanguage(selectedLang, getResources());

        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        mActivity = MainActivity.mActivity;

        settingsView = getListView();

        setupActionBar();
    }

    @Override
    protected void onResume() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLang = sharedPref.getString("language", "system");
        Language.setLanguage(selectedLang, getResources());
        super.onResume();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_settings);
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || StylePreferenceFragment.class.getName().equals(fragmentName)
                || UserscriptPreferenceFragment.class.getName().equals(fragmentName)
                || AboutPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationsPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

            // Update language
            String selectedLang = sharedPref.getString("language", "system");
            Language.setLanguage(selectedLang, getResources());

            super.onCreate(savedInstanceState);

            ((SettingsActivity) getActivity()).setActionBarTitle(getString(R.string.pref_header_general));

            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            final Preference clear_image_cache = findPreference("clear_image_cache");
            clear_image_cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Ion.getDefault(mContext).getCache().clear();
                    Snackbar.make(settingsView, R.string.settings_snackbar_image_cache_was_cleared, Snackbar.LENGTH_LONG).show();
                    return false;
                }
            });


            /*final Preference pinned_pages_sync = findPreference("pinned_pages_sync");
            pinned_pages_sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), PinnedPagesSyncActivity.class);
                    startActivity(intent);
                    return false;
                }
            });*/

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            final Preference startpage = findPreference("current_startpage");
            bindPreferenceSummaryToValue(startpage);

            Preference reset = findPreference("reset_startpage");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    SharedPreferences sharedPref = PreferenceManager
                            .getDefaultSharedPreferences(getActivity().getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.remove("current_startpage");
                    editor.apply();

                    startpage.setSummary("Default");
                    return true;
                }
            });

            Preference language = findPreference("language");
            language.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d("LANG UPDATE", newValue.toString());
                    Log.d("LANG UPDATE", Locale.getDefault().getLanguage());

                    // Update language
                    Language.setLanguage(newValue.toString(), getResources());

                    Intent refresh = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(refresh);
                    getActivity().finish();

                    return true;
                }
            });



        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }



    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class StylePreferenceFragment extends PreferenceFragment {
        private SharedPreferences sharedPref;
        private Preference filePicker;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

            // Update language
            String selectedLang = sharedPref.getString("language", "system");
            Language.setLanguage(selectedLang, getResources());

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_style);
            setHasOptionsMenu(true);
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

            ((SettingsActivity) getActivity()).setActionBarTitle(getString(R.string.pref_header_styles));

            filePicker = findPreference("custom_style_file");
            filePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    }
                    else
                    {
                        if (Build.VERSION.SDK_INT < 19){
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            startActivityForResult(intent, 1);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("*/*");
                            startActivityForResult(intent, 1);
                        }
                    }
                    return true;
                }
            });

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            SharedPreferences sharedPref = PreferenceManager
                    .getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();


            if (requestCode == 1) {
                if(resultCode == Activity.RESULT_OK){
                    // Get the Uri of the selected file
                    Uri URL = Uri.parse(data.getData().getPath());
                    Log.d("File", data.getData().toString());

                    String CSS = null;
                    try {
                        CSS = customCSS.readFromSDcard(getActivity(), data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    filePicker.setSummary(URL.getLastPathSegment());
                    Toast.makeText(getActivity(), "You might need to do a refresh to see the changes", Toast.LENGTH_LONG).show();
                    editor.putString("custom_style_file", CSS);
                    editor.apply();


                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 2: {
                    if (Build.VERSION.SDK_INT < 19){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(intent, 1);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, 1);
                    }
                    return;
                }
            }
        }



        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UserscriptPreferenceFragment extends PreferenceFragment {
        private SharedPreferences sharedPref;
        private PreferenceCategory userscriptList;
        private Preference filePicker;

        Realm realm;
        RealmConfiguration realmConfig;
        RealmResults<UserScript> userScripts;

        PreferenceScreen screen;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

            // Open the Realm for the UI thread.
            realm = Realm.getInstance(MainActivity.realmConfig);

            // Update language
            String selectedLang = sharedPref.getString("language", "system");
            Language.setLanguage(selectedLang, getResources());

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_userscripts);
            setHasOptionsMenu(true);
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

            ((SettingsActivity) getActivity()).setActionBarTitle(getString(R.string.pref_header_userscripts));

            screen = this.getPreferenceScreen();

            userscriptList = (PreferenceCategory) findPreference("userscript_list");

            userScripts = realm.where(UserScript.class).findAll();

            if(userScripts.size() > 0) {
                for (final UserScript uScript : userScripts)
                {
                    Preference dummy = new Preference(screen.getContext());
                    dummy.setTitle(uScript.getTitle());
                    dummy.setKey(uScript.getUrl());

                    dummy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(final Preference preference) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(R.string.userscript_dialog_title)
                                    .setItems(R.array.userscript_options, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setTitle(R.string.dialog_enternewtitle_title);


                                                    // Set up the input
                                                    final EditText input = new EditText(getActivity());

                                                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                                    input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

                                                    builder.setView(input);

                                                    // Set up the buttons
                                                    builder.setPositiveButton(R.string.answer_ok, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            realm.beginTransaction();
                                                            uScript.setTitle(input.getText().toString()); // Delete and remove object directly
                                                            preference.setTitle(input.getText().toString());
                                                            realm.commitTransaction();

                                                            Snackbar.make(settingsView , R.string.userscript_was_added, Snackbar.LENGTH_LONG).show();
                                                        }
                                                    });
                                                    builder.setNegativeButton(R.string.answer_cancel, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                                    builder.show();



                                                    break;
                                                case 1:
                                                    realm.beginTransaction();
                                                    uScript.removeFromRealm(); // Delete and remove object directly
                                                    realm.commitTransaction();

                                                    userscriptList.removePreference(preference);
                                                    Snackbar.make(settingsView , R.string.userscript_was_deleted, Snackbar.LENGTH_LONG).show();
                                                    break;
                                            }
                                        }
                                    });
                            builder.create().show();
                            return false;
                        }
                    });

                    userscriptList.addPreference(dummy);
                }

            }






            filePicker = findPreference("add_userscript");
            filePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    }
                    else
                    {
                        if (Build.VERSION.SDK_INT < 19){
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            startActivityForResult(intent, 1);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("*/*");
                            startActivityForResult(intent, 1);
                        }
                    }
                    return true;
                }
            });


            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == 1) {
                if(resultCode == Activity.RESULT_OK){
                    // Get the Uri of the selected file
                    Uri URL = Uri.parse(data.getData().getPath());
                    Log.d("File", data.getData().toString());

                    String CSS = null;
                    try {
                        CSS = customCSS.readFromSDcard(getActivity(), data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    realm.beginTransaction();

                    // Get filename
                    Cursor returnCursor =
                            getActivity().getContentResolver().query(data.getData(), null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    returnCursor.moveToFirst();

                    // Add a new userscript
                    final UserScript newUserScript = realm.createObject(UserScript.class);

                    newUserScript.setTitle(returnCursor.getString(nameIndex));
                    newUserScript.setUrl(URL.toString());
                    newUserScript.setJavascript(CSS);

                    realm.commitTransaction();

                    Preference dummy = new Preference(screen.getContext());
                    dummy.setTitle(returnCursor.getString(nameIndex));
                    dummy.setKey(URL.toString());

                    dummy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(final Preference preference) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(R.string.userscript_dialog_title)
                                    .setItems(R.array.userscript_options, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setTitle(R.string.dialog_enternewtitle_title);


                                                    // Set up the input
                                                    final EditText input = new EditText(getActivity());

                                                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                                    input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

                                                    builder.setView(input);

                                                    // Set up the buttons
                                                    builder.setPositiveButton(R.string.answer_ok, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            realm.beginTransaction();
                                                            newUserScript.setTitle(input.getText().toString()); // Delete and remove object directly
                                                            preference.setTitle(input.getText().toString());
                                                            realm.commitTransaction();

                                                            Snackbar.make(settingsView ,"Item was renamed", Snackbar.LENGTH_LONG).show();
                                                        }
                                                    });
                                                    builder.setNegativeButton(R.string.answer_cancel, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                                    builder.show();



                                                    break;
                                                case 1:
                                                    realm.beginTransaction();
                                                    newUserScript.removeFromRealm(); // Delete and remove object directly
                                                    realm.commitTransaction();

                                                    userscriptList.removePreference(preference);
                                                    Snackbar.make(settingsView , R.string.userscript_was_deleted, Snackbar.LENGTH_LONG).show();
                                                    break;
                                            }
                                        }
                                    });
                            builder.create().show();
                            return false;
                        }
                    });

                    userscriptList.addPreference(dummy);

                    Snackbar.make(settingsView, R.string.userscript_was_added, Snackbar.LENGTH_LONG).show();

                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 2: {
                    if (Build.VERSION.SDK_INT < 19){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(intent, 1);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, 1);
                    }
                    return;
                }
            }
        }



        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AboutPreferenceFragment extends PreferenceFragment {
        private SharedPreferences sharedPref;
        private Preference licenseItem;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

            // Update language
            String selectedLang = sharedPref.getString("language", "system");
            Language.setLanguage(selectedLang, getResources());

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_about);
            setHasOptionsMenu(true);
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

            ((SettingsActivity) getActivity()).setActionBarTitle(getString(R.string.pref_header_about));

            licenseItem = findPreference("license");
            licenseItem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    WebView view = (WebView) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_licenses, null);
                    view.getSettings().getUseWideViewPort();
                    view.loadUrl("file:///android_asset/license.html");
                    AlertDialog mAlertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                            .setTitle("Licenses")
                            .setView(view)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationsPreferenceFragment extends PreferenceFragment {
        private SharedPreferences sharedPref;
        private Preference licenseItem;
        boolean loginState;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);



            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notifications);
            setHasOptionsMenu(true);
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

            if(Cookies.getCookie("https://facepunch.com/", "bb_userid") != null) {
                Log.d("Cookie", Cookies.getCookie("https://facepunch.com/", "bb_userid"));
                loginState = true;
            } else {
                Snackbar.make(settingsView, R.string.settings_loggedin_requirement, Snackbar.LENGTH_LONG).show();
                loginState = false;
            }

            Log.d("Loginstate", String.valueOf(loginState));

            ((SettingsActivity) getActivity()).setActionBarTitle(getString(R.string.pref_header_notifications));

            final SwitchPreference useNotifications = (SwitchPreference) findPreference("useNotifications");
            useNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d("Pref", newValue.toString() );
                    if(!Boolean.valueOf(newValue.toString())) {
                        MainActivity.serviceManager.stopPrivateMessageService();
                    }

                    if(Boolean.valueOf(newValue.toString()) ) {
                        MainActivity.serviceManager.startPrivateMessageService();
                    }
                    return true;
                }
            });

            final ListPreference checkinterval = (ListPreference) findPreference("pm_check_interval");
            checkinterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.serviceManager.restartPrivateMessageService();
                    return true;
                }
            });

            final SwitchPreference pm_check_vibrate = (SwitchPreference) findPreference("pm_check_vibrate");
            pm_check_vibrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.serviceManager.restartPrivateMessageService();
                    return true;
                }
            });

            final SwitchPreference pm_check_sound = (SwitchPreference) findPreference("pm_check_sound");
            pm_check_sound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.serviceManager.restartPrivateMessageService();
                    return true;
                }
            });

            final SwitchPreference pm_check_light = (SwitchPreference) findPreference("pm_check_light");
            pm_check_light.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.serviceManager.restartPrivateMessageService();
                    return true;
                }
            });

            final Preference testnotification = findPreference("testnotification");
            testnotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {


                    // Get notification settings
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                    Boolean shouldVibrate = sharedPref.getBoolean("pm_check_vibrate", true);
                    Boolean shouldPlaySound = sharedPref.getBoolean("pm_check_sound", true);
                    Boolean shouldUseLight = sharedPref.getBoolean("pm_check_light", true);

                    // Set the info for the views that show in the notification panel.
                    Notification.Builder notificationbuilder = new Notification.Builder(mContext)
                            .setSmallIcon(R.drawable.ic_stat_placeholder_trans)  // the status icon
                            .setTicker("From: Test")  // the status text
                            .setWhen(System.currentTimeMillis())  // the time stamp
                            .setContentTitle("This is a test")  // the label of the entry
                            .setContentText("From: Test")  // the contents of the entry
                            .setPriority(Notification.PRIORITY_MAX)
                            .setAutoCancel(true);


                    // set notification ligt
                    if(shouldUseLight) {
                        notificationbuilder.setLights(0xff00ff00, 300, 100);
                    }

                    final Notification notification = notificationbuilder.build();

                    // Set notification sound
                    if(shouldPlaySound) {
                        notification.defaults |= Notification.DEFAULT_SOUND;
                    }

                    // Set notification vibration
                    if(shouldVibrate) {
                        notification.defaults |= Notification.DEFAULT_VIBRATE;
                    }

                    Toast.makeText(mContext, "Sending test notification in 5 seconds", Toast.LENGTH_SHORT).show();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Send the notification.
                            mNM.notify(0, notification);
                        }
                    }, 5000);

                    return true;
                }
            });

            /**
             * Subscribed Threads
             */
            final SwitchPreference useSubThreadsNotifications = (SwitchPreference) findPreference("useSubThreadsNotifications");
            useSubThreadsNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d("Pref", newValue.toString() );
                    if(!Boolean.valueOf(newValue.toString())) {
                        MainActivity.serviceManager.stopSubscribedThreadsService();
                    }

                    if(Boolean.valueOf(newValue.toString()) ) {
                        MainActivity.serviceManager.startSubscribedThreadsService(MainActivity.mActivity);
                    }
                    return true;
                }
            });

            final ListPreference subthreads_checkinterval = (ListPreference) findPreference("subthreads_check_interval");
            subthreads_checkinterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.serviceManager.restartSubscribedThreadsService();
                    return true;
                }
            });

            final SwitchPreference subthreads_check_vibrate = (SwitchPreference) findPreference("subthreads_check_vibrate");
            subthreads_check_vibrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.serviceManager.restartSubscribedThreadsService();
                    return true;
                }
            });

            final SwitchPreference subthreads_check_sound = (SwitchPreference) findPreference("subthreads_check_sound");
            subthreads_check_sound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.serviceManager.restartSubscribedThreadsService();
                    return true;
                }
            });

            final SwitchPreference subthreads_check_light = (SwitchPreference) findPreference("subthreads_check_light");
            subthreads_check_light.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.serviceManager.restartSubscribedThreadsService();
                    return true;
                }
            });

            //Preference loginPref = findPreference("needtobeloggedin");


            if(!loginState) {
                useNotifications.setEnabled(false);
                useSubThreadsNotifications.setEnabled(false);
            } else {
                useNotifications.setEnabled(true);
                useSubThreadsNotifications.setEnabled(true);
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }




}
