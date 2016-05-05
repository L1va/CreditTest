package com.example.l1va.credittest;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class ActivitySettings extends AppCompatActivity {

    public static final String LIST_THEMES_KEY = "listThemes";

    public static final String LIST_CELLS_COUNT_KEY = "listCellsCount";
    public static final String LIGHT_THEME_KEY = "Light";
    public static final String DARK_THEME_KEY = "Dark";

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_fragment);

            Preference themePreference = findPreference(LIST_THEMES_KEY);
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

                    if (!preference.getSharedPreferences().getString(LIST_THEMES_KEY, DARK_THEME_KEY).equals(stringValue)) {
                        getActivity().recreate();
                    }
                    return true;
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(getTheme(getBaseContext()));
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static int getTheme(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = sharedPrefs.getString(LIST_THEMES_KEY, DARK_THEME_KEY);
        switch (theme) {
            case DARK_THEME_KEY:
                return R.style.AppThemeDark;
            default:
                return R.style.AppThemeLight;
        }
    }

    public static int getThemeNoActionBar(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = sharedPrefs.getString(LIST_THEMES_KEY, DARK_THEME_KEY);

        switch (theme) {
            case DARK_THEME_KEY:
                return R.style.AppThemeDark_NoActionBar;
            default:
                return R.style.AppThemeLight_NoActionBar;
        }
    }

    public static int getCellsCount(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(LIST_CELLS_COUNT_KEY, "4"));
    }

}
