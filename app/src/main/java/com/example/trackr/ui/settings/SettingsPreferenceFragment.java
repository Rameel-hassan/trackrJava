package com.example.trackr.ui.settings;

import androidx.fragment.app.Fragment;

/**
 * @author Rameel Hassan
 * Created 14/06/2023 at 3:57 PM
 */
import android.content.res.Configuration;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;



import com.example.trackr.R;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Preference darkModePref = getPreferenceManager().findPreference(getString(R.string.dark_mode_key));
        if (darkModePref != null) {
            darkModePref.setOnPreferenceChangeListener(this);
        }

        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_NO:
                    view.setBackgroundColor(getResources().getColor(R.color.trackr_white_50, getContext().getTheme()));
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    view.setBackgroundColor(getResources().getColor(R.color.trackr_blue_700, getContext().getTheme()));
                    break;
            }
        }

        return view;
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    private void refreshDarkModePreference(String newValue) {
        String enabled = getString(R.string.enabled_value);
        String disabled = getString(R.string.disabled_value);
        String defaultMode = getString(R.string.system_default_value);

        if (newValue.equalsIgnoreCase(enabled)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else if (newValue.equalsIgnoreCase(disabled)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_NO:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
            }
        }
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        refreshDarkModePreference((String) newValue);
        return true;
    }

}