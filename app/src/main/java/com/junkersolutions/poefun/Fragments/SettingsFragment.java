package com.junkersolutions.poefun.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.junkersolutions.poefun.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource

        addPreferencesFromResource(R.xml.preferences);
    }


}
