package com.example.taskmanager.fragments.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.taskmanager.R
import kotlinx.android.synthetic.main.activity_main.*

class SettingsFragment : PreferenceFragmentCompat() {
    private val LOG_TAG = "1234"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        // hide settings nav button
        activity?.toolbar?.menu?.findItem(R.id.settingsFragment)?.isVisible = false

        super.onResume()
    }

    override fun onPause() {
        // make settings nav button visible
        activity?.toolbar?.menu?.findItem(R.id.settingsFragment)?.isVisible = true

        super.onPause()
    }
}