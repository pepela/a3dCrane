package com.pepela.a3dcrane;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity
		implements Preference.OnPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Add 'general' preferences, defined in the XML file
		addPreferencesFromResource(R.xml.pref_general);


		bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_ip_key)));
		bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sending_port_key)));
		bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_receiving_port_key)));
	}

	private void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(this);

		// Trigger the listener immediately with the preference's
		// current value.
		setPreferenceSummary(preference,
				PreferenceManager
						.getDefaultSharedPreferences(preference.getContext())
						.getString(preference.getKey(), ""));
	}


	private void setPreferenceSummary(Preference preference, Object value) {
		String stringValue = value.toString();
		String key = preference.getKey();

		preference.setSummary(stringValue);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object value) {
		setPreferenceSummary(preference, value);

		return true;
	}

	@Override
	public Intent getParentActivityIntent() {
		return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}

}