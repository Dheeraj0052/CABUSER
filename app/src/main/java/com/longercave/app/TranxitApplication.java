package com.longercave.app;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.longercave.app.FCM.ForceUpdateChecker;
import com.longercave.app.Helper.LocaleUtils;
import com.pk.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by jayakumar on 29/01/17.
 */

public class TranxitApplication extends Application {

    public static final String TAG = TranxitApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static TranxitApplication mInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base, "en"));
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initCalligraphyConfig();


        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.0");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=com.tranxitpro.app");

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(10) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });
    }

    private void initCalligraphyConfig() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.bariol))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static synchronized TranxitApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void cancelRequestInQueue(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the no_user tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public static String trimMessage(String json){
        String trimmedString = "";

        try{
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray value = jsonObject.getJSONArray(key);
                    for (int i = 0, size = value.length(); i < size; i++) {
                        Log.e("Errors in Form",""+value.getString(i));
                        trimmedString += value.getString(i);
                        if(i < size-1) {
                            trimmedString += '\n';
                        }
                    }
                } catch (JSONException e) {

                     trimmedString += jsonObject.optString(key);
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        Log.e("Trimmed",""+trimmedString);

        return trimmedString;
    }

}
