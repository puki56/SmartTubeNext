package com.liskovsoft.smartyoutubetv2.tv.ui.settings;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.liskovsoft.sharedutils.mylogger.Log;
import com.liskovsoft.smartyoutubetv2.tv.R;

public class AppSettingsActivity extends FragmentActivity {
    private static final String TAG = AppSettingsActivity.class.getSimpleName();
    private AppSettingsFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_app_settings);
        mFragment = (AppSettingsFragment) getFragmentManager().findFragmentById(R.id.app_settings_fragment);
    }

    private void setupActivity() {
        // Fix crash in AppSettingsActivity: "Only fullscreen opaque activities can request orientation"
        // Error happen only on Android O (api 26) when you set "portrait" orientation in manifest
        // So, to fix the problem, set orientation here instead of manifest
        // More info: https://stackoverflow.com/questions/48072438/java-lang-illegalstateexception-only-fullscreen-opaque-activities-can-request-o
        if (VERSION.SDK_INT != 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void finish() {
        // NOTE: Fragment's onDestroy/onDestroyView are not reliable way to catch dialog finish
        Log.d(TAG, "Dialog finish");
        mFragment.onFinish();
        
        super.finish();
    }
}
