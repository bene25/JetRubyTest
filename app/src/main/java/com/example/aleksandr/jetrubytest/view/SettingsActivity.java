package com.example.aleksandr.jetrubytest.view;

/**
 * Created by aleksandr on 22.01.16.
 * Settings activity class for storing shared preferences
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.aleksandr.jetrubytest.R;
import com.example.aleksandr.jetrubytest.view.ui.CustomEditTextPreference;

/**
 * Settings activity for user preference
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private CustomEditTextPreference mFolderPreference;
    private int[] mResources = {
            R.string.pref_duration_key,
            R.string.pref_effects_key,
            R.string.pref_link_key_1,
            R.string.pref_link_key_2,
            R.string.pref_link_key_3,
            R.string.pref_link_key_4,
            R.string.pref_link_key_5
    };

    public static final Integer SELECT_IMAGE_INTENT = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref);

        for (int res_id : mResources) {
            Preference linkPreference = findPreference(getString(res_id));
            bindPreferenceSummaryToValue(linkPreference);
        }
        mFolderPreference = (CustomEditTextPreference) findPreference(getString(R.string.pref_directory_chooser_key));
        bindPreferenceSummaryToValue(mFolderPreference);

        Preference switchPreference = findPreference(getString(R.string.pref_switch_key));
        bindBooleanPreferenceSummaryToValue(switchPreference);

        mFolderPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_IMAGE_INTENT);
                return true;
            }

        });

    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);

        setPreferenceSummary(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private void bindBooleanPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);

        setPreferenceSummary(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setPreferenceSummary(preference, newValue);
        return true;
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String prefValue = value.toString();
        String key = preference.getKey();

        if (key.equals(getString(R.string.pref_duration_key))) {
            if (prefValue.isEmpty() || Integer.parseInt(prefValue) < 1 || Integer.parseInt(prefValue) > 60) {
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_duration), Toast.LENGTH_SHORT).show();
            } else {
                preference.setSummary(prefValue);
            }

        } else if (key.equals(getString(R.string.pref_directory_chooser_key))) {
            prefValue = (prefValue.equals("")) ? getString(R.string.empty_directory) : prefValue;
            preference.setSummary(prefValue);

        } else if (key.equals(getString(R.string.pref_effects_key))) {
            String[] entries = getResources().getStringArray(R.array.listentries);
            preference.setSummary(entries[Integer.parseInt(prefValue)]);

        } else if (key.equals(getString(R.string.pref_switch_key))) {
            if (prefValue.equals("true")) {
                mFolderPreference.setEnabled(false);
            } else {
                mFolderPreference.setEnabled(true);
            }
        } else {
            preference.setSummary(prefValue);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SELECT_IMAGE_INTENT && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(projection[0]);
                String picturePath = cursor.getString(columnIndex);
                mFolderPreference.setText(picturePath);
                mFolderPreference.setSummary(picturePath);
                cursor.close();
            }

        }
    }

}

