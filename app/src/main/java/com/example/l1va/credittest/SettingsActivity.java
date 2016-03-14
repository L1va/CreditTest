package com.example.l1va.credittest;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_fragment);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SettingsActivity.getTheme(getBaseContext()));
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new SettingsFragment()).commit();
        }
    }

    public static final String THEME_PREFERENCE = "listThemes";
    public static final String CELLS_COUNT_PREFERENCE = "listCellsCount";

    @Override
    public void onBackPressed() {
        MainActivity.instance.recreate();
        super.onBackPressed();
    }

    public static int getTheme(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = sharedPrefs.getString(SettingsActivity.THEME_PREFERENCE, "Dark");
        switch (theme) {
            case "Dark":
                return R.style.AppThemeDark;
        }
        return R.style.AppThemeLight;
    }

    public static int getCellsCount(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String count = sharedPrefs.getString(SettingsActivity.CELLS_COUNT_PREFERENCE, "4");

        return Integer.parseInt(count);
    }
}
