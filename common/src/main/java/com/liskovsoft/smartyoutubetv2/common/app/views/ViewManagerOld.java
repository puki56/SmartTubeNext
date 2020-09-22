package com.liskovsoft.smartyoutubetv2.common.app.views;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.liskovsoft.sharedutils.mylogger.Log;

import java.util.HashMap;
import java.util.Map;

public class ViewManagerOld {
    private static final String PARENT_ACTIVITY = "PARENT_ACTIVITY";
    private static final String TAG = ViewManagerOld.class.getSimpleName();
    private static ViewManagerOld sInstance;
    private final Context mContext;
    private final Map<Class<?>, Class<? extends Activity>> mViewMapping;
    private final Map<Class<? extends Activity>, Class<? extends Activity>> mParentMapping;
    private Class<?> mDefaultActivity;
    private Class<?> mRootActivity;

    private ViewManagerOld(Context context) {
        mContext = context;
        mViewMapping = new HashMap<>();
        mParentMapping = new HashMap<>();
    }

    public static ViewManagerOld instance(Context context) {
        if (sInstance == null) {
            sInstance = new ViewManagerOld(context.getApplicationContext());
        }

        return sInstance;
    }

    public void register(Class<?> viewClass, Class<? extends Activity> activityClass) {
        register(viewClass, activityClass, null);
    }

    public void register(Class<?> viewClass, Class<? extends Activity> activityClass, Class<? extends Activity> parentActivityClass) {
        mViewMapping.put(viewClass, activityClass);

        if (parentActivityClass != null) {
            mParentMapping.put(activityClass, parentActivityClass);
        }
    }

    public void unregister(Class<?> viewClass) {
        mViewMapping.remove(viewClass);
    }
    
    public void startView(Class<?> viewClass) {
        Class<?> activityClass = mViewMapping.get(viewClass);

        if (activityClass != null) {
            Intent intent = new Intent(mContext, activityClass);

            // Fix: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(intent);
        } else {
            Log.e(TAG, "Activity not registered for view " + viewClass.getSimpleName());
        }
    }

    public void startView(Object parentView, Class<?> viewClass) {
        if (parentView instanceof Fragment) {
            Fragment fragment = (Fragment) parentView;

            Class<?> activityClass = mViewMapping.get(viewClass);

            if (activityClass != null) {
                Intent intent = new Intent(fragment.getActivity(), activityClass);
                intent.putExtra(PARENT_ACTIVITY, fragment.getActivity().getClass().getName());

                fragment.startActivity(intent);
            } else {
                Log.e(TAG, "Activity not registered for view " + viewClass.getSimpleName());
            }
        }
    }

    public void startParentView(Activity activity) {
        if (activity.getIntent() != null) {
            Class<?> parentActivity = getParent(activity);

            if (parentActivity == null) {
                Log.d(TAG, "Parent activity name doesn't stored in registry. Exiting to Home...");
                activity.moveTaskToBack(true);
                return;
            }

            try {
                Log.d(TAG, "Launching parent activity...");
                setDefault(null); // current activity is finished, so do reset
                Intent intent = new Intent(activity, parentActivity);

                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "Parent activity not found.");
            }
        }
    }

    private Class<?> getParent(Activity activity) {
        Class<?> parentActivity = null;
        String parentActivityName = null;

        if (activity.getIntent() != null) {
            parentActivityName = activity.getIntent().getStringExtra(PARENT_ACTIVITY);
        }

        if (parentActivityName != null) {
            try {
                parentActivity = Class.forName(parentActivityName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "Activity class not found.");
            }
        } else {
            parentActivity = getDefaultParent(activity);
        }

        return parentActivity;
    }

    private Class<?> getDefaultParent(Activity activity) {
        Class<?> parentActivity = null;

        for (Class<?> activityClass : mParentMapping.keySet()) {
            if (activityClass.isInstance(activity)) {
                parentActivity = mParentMapping.get(activityClass);
            }
        }

        return parentActivity;
    }

    public void startDefaultView(Activity activity) {
        Class<?> lastActivity = mDefaultActivity;

        if (lastActivity == null) {
            lastActivity = mRootActivity;
        }

        Log.d(TAG, "Starting activity: " + lastActivity.getSimpleName());

        Intent intent = new Intent(activity, lastActivity);

        activity.startActivity(intent);
    }

    public void setDefault(@Nullable Class<?> defaultActivity) {
        mDefaultActivity = defaultActivity;
    }

    public void setRoot(@NonNull Class<?> rootActivity) {
        mRootActivity = rootActivity;
    }
}