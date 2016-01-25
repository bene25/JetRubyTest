package com.example.aleksandr.jetrubytest;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterViewFlipper;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AdapterViewFlipper mFlipper;
    private ArrayList<String> mArrayList;
    private SharedPreferences mSharedPreferences;
    private String mPrefFile;
    private Integer mStartPosition = 0;
    private int[] mResources = {
            R.string.pref_link_key_1,
            R.string.pref_link_key_2,
            R.string.pref_link_key_3,
            R.string.pref_link_key_4,
            R.string.pref_link_key_5
    };
    private SliderBaseAdapter mSliderBaseAdapter;
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFlipper = (AdapterViewFlipper) findViewById(R.id.flipper);

        mArrayList = new ArrayList<>();
        mSliderBaseAdapter = new SliderBaseAdapter(this, mArrayList);
        mFlipper.setAdapter(mSliderBaseAdapter);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mPrefFile = mSharedPreferences.getString(getString(R.string.pref_directory_chooser_key), "");
        String interval = mSharedPreferences.getString(getString(R.string.pref_duration_key), "10");
        String effect = mSharedPreferences.getString(getString(R.string.pref_effects_key), "0");

        fillArray();
        mFlipper.setFlipInterval(Integer.parseInt(interval) * 1000);
        setAnimation(Integer.parseInt(effect));

        /**
         * Check permission for 6.0 android
         */

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), getString(R.string.disable_permission_menu), Toast.LENGTH_SHORT).show();
                return false;
            }

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shared preference changes listener
     */

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_duration_key))) {
            String interval = mSharedPreferences.getString(getString(R.string.pref_duration_key), "10");
            mFlipper.setFlipInterval(Integer.parseInt(interval) * 1000);
        } else if (key.equals(getString(R.string.pref_effects_key))) {
            String effect = mSharedPreferences.getString(getString(R.string.pref_effects_key), "");
            setAnimation(Integer.parseInt(effect));
        } else {
            fillArray();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Fill array with appropriate values
     */

    private void fillArray() {
        if (mArrayList.size() > 0) {
            mArrayList.clear();
        }
        Boolean isRemote = mSharedPreferences.getBoolean(getString(R.string.pref_switch_key), false);
        if (!isRemote) {
            mPrefFile = mSharedPreferences.getString(getString(R.string.pref_directory_chooser_key), "");
            if (mPrefFile.equals("") || mPrefFile.equals(getString(R.string.empty_directory))) {
                Toast.makeText(getApplicationContext(), getString(R.string.empty_resource_message), Toast.LENGTH_SHORT).show();
            } else {

                File prefFile = new File(mPrefFile);
                File directory = prefFile.getParentFile();

                File[] file = directory.listFiles();
                for (int i = 0; i < file.length; i++) {

                    String filePath = file[i].getAbsolutePath();

                    if (filePath != null && isSupportedFile(filePath)) {
                        mArrayList.add(filePath);

                        mStartPosition = mArrayList.indexOf(mPrefFile);

                    }
                }
                mSliderBaseAdapter.notifyDataSetChanged();
                mFlipper.setSelection(mStartPosition);
            }


        } else {
            for (int resId : mResources) {
                String link = mSharedPreferences.getString(getString(resId), "");
                if (!link.equals("")) {
                    mArrayList.add(link);
                }

            }
            mSliderBaseAdapter.notifyDataSetChanged();
        }

        if (mArrayList.size() == 0 || mArrayList == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_resource_message), Toast.LENGTH_SHORT).show();
        }


    }

    private boolean isSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1), filePath.length());

        if (Const.FILE_EXTN.contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;

    }

    /**
     * Set animation method
     */
    private void setAnimation(Integer effectId) {
        switch (effectId) {
            case 0:
                mFlipper.setInAnimation(this, R.anim.no_animation_in);
                mFlipper.setOutAnimation(this, R.anim.no_animation_out);
                break;
            case 1:
                mFlipper.setInAnimation(this, R.anim.in_from_left);
                mFlipper.setOutAnimation(this, R.anim.out_to_right);
                break;
            case 2:
                mFlipper.setInAnimation(this, R.anim.in_from_right);
                mFlipper.setOutAnimation(this, R.anim.out_to_left);
                break;
            case 3:
                mFlipper.setInAnimation(this, R.anim.fade_in);
                mFlipper.setOutAnimation(this, R.anim.fade_out);
                break;
            default:
                mFlipper.setInAnimation(this, R.anim.no_animation_in);
                mFlipper.setOutAnimation(this, R.anim.no_animation_out);

                break;
        }

    }

    /**
     * Permission result for android 6.0
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.disable_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }
}