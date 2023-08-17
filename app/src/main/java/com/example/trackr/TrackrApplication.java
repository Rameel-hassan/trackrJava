package com.example.trackr;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;


import com.example.trackr.R;

import dagger.hilt.android.HiltAndroidApp;

/**
 * @author Rameel Hassan
 * Created 14/03/2023 at 11:26 AM
 */

@HiltAndroidApp
public class TrackrApplication extends Application {
    @Override
    public void  onCreate() {
        super.onCreate();
        setupDarkModePreference();
    }

    private void setupDarkModePreference() {
        String defaultValue = getResources().getString(R.string.system_default_value);
        String disabledValue = getResources().getString(R.string.disabled_value);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String darkModeSetting = sharedPreferences.getString(getResources().getString(R.string.dark_mode_key), defaultValue);

        if (!darkModeSetting.equals(defaultValue)) {
            if (darkModeSetting.equals(disabledValue)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
    }
}
