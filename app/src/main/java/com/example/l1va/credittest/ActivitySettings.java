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

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_fragment);

            Preference themePreference = findPreference(getString(R.string.list_themes_key));
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

                    //TODO: getString keys
                    if (!preference.getSharedPreferences().getString(getActivity().getString(R.string.list_themes_key), "Dark").equals(stringValue)) {
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
        String theme = sharedPrefs.getString(context.getString(R.string.list_themes_key), "Dark");
        switch (theme) {
            case "Dark":
                return R.style.AppThemeDark;
        }
        return R.style.AppThemeLight;
    }

    public static int getThemeNoActionBar(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String dark = context.getString(R.string.dark_theme_key);
        String theme = sharedPrefs.getString(context.getString(R.string.list_themes_key), dark);

        switch (theme) {
            case "Dark":
                return R.style.AppThemeDark_NoActionBar;
        }
        return R.style.AppThemeLight_NoActionBar;
    }

    public static int getCellsCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.list_cells_count_key), 4);
    }

}
