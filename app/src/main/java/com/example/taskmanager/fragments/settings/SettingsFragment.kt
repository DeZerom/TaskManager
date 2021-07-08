package com.example.taskmanager.fragments.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.taskmanager.R

class SettingsFragment : PreferenceFragmentCompat() {
    private val LOG_TAG = "1234"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}