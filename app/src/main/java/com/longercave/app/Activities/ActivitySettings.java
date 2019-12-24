package com.longercave.app.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.longercave.app.Helper.CustomDialog;
import com.longercave.app.Helper.LocaleUtils;
import com.longercave.app.Helper.SharedHelper;
import com.pk.app.R;
import com.longercave.app.Retrofit.ApiInterface;
import com.longercave.app.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Esack N on 9/27/2017.
 */

public class ActivitySettings extends AppCompatActivity {

    private RadioButton radioEnglish, radioArabic;

    private LinearLayout lnrEnglish, lnrArabic, lnrHome, lnrWork;

    private int UPDATE_HOME_WORK = 1;

    private ApiInterface mApiInterface;

    private TextView txtHomeLocation, txtWorkLocation, txtDeleteWork, txtDeleteHome;

    private CustomDialog customDialog;

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {

        radioEnglish = (RadioButton) findViewById(R.id.radioEnglish);
        radioArabic = (RadioButton) findViewById(R.id.radioArabic);

        lnrEnglish = (LinearLayout) findViewById(R.id.lnrEnglish);
        lnrArabic = (LinearLayout) findViewById(R.id.lnrArabic);
        lnrHome = (LinearLayout) findViewById(R.id.lnrHome);
        lnrWork = (LinearLayout) findViewById(R.id.lnrWork);

        txtHomeLocation = (TextView) findViewById(R.id.txtHomeLocation);
        txtWorkLocation = (TextView) findViewById(R.id.txtWorkLocation);
        txtDeleteWork = (TextView) findViewById(R.id.txtDeleteWork);
        txtDeleteHome = (TextView) findViewById(R.id.txtDeleteHome);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        customDialog = new CustomDialog(ActivitySettings.this);
        customDialog.setCancelable(false);
        customDialog.show();

        getFavoriteLocations();

        lnrHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedHelper.getKey(ActivitySettings.this, "home").equalsIgnoreCase("")){
                    gotoHomeWork("home");
                }
            }
        });

        lnrWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedHelper.getKey(ActivitySettings.this, "work").equalsIgnoreCase("")){
                    gotoHomeWork("work");
                }
            }
        });

        txtDeleteHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFavoriteLocations("home");
            }
        });

        txtDeleteWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFavoriteLocations("work");
            }
        });



        if (SharedHelper.getKey(ActivitySettings.this, "language").equalsIgnoreCase("en")){
            radioEnglish.setChecked(true);
        }else{
            radioArabic.setChecked(true);
        }

        lnrEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioArabic.setChecked(false);
                radioEnglish.setChecked(true);
            }
        });

        lnrArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEnglish.setChecked(false);
                radioArabic.setChecked(true);
            }
        });

        radioArabic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    radioEnglish.setChecked(false);
                    SharedHelper.putKey(ActivitySettings.this, "language", "ar");
                    setLanguage();
                    recreate();
                }
            }
        });

        radioEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    radioArabic.setChecked(false);
                    SharedHelper.putKey(ActivitySettings.this, "language", "en");
                    setLanguage();
                    recreate();
                }
            }
        });
    }


    private void gotoHomeWork(String strTag) {
        Intent intentHomeWork = new Intent(ActivitySettings.this, AddHomeWorkActivity.class);
        intentHomeWork.putExtra("tag", strTag);
        startActivityForResult(intentHomeWork, UPDATE_HOME_WORK);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(ActivitySettings.this, "language");
        LocaleUtils.setLocale(this, languageCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_HOME_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                getFavoriteLocations();
            }
        }
    }

    private void getFavoriteLocations() {
        mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<ResponseBody> call = mApiInterface.getFavoriteLocations("XMLHttpRequest",
                SharedHelper.getKey(ActivitySettings.this, "token_type") + " " + SharedHelper.getKey(ActivitySettings.this, "access_token"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS","SUCESS"+response.body());
                customDialog.dismiss();
                if (response.body() != null){
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS","bodyString"+bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray homeArray = jsonObj.optJSONArray("home");
                            JSONArray workArray = jsonObj.optJSONArray("work");
                            JSONArray othersArray = jsonObj.optJSONArray("others");
                            JSONArray recentArray = jsonObj.optJSONArray("recent");
                            if (homeArray.length() > 0){
                                Log.v("Home Address", ""+homeArray);
                                txtHomeLocation.setText(homeArray.optJSONObject(0).optString("address"));
                                txtDeleteHome.setVisibility(View.VISIBLE);
                                SharedHelper.putKey(ActivitySettings.this, "home", homeArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(ActivitySettings.this, "home_lat", homeArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(ActivitySettings.this, "home_lng", homeArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(ActivitySettings.this, "home_id", homeArray.optJSONObject(0).optString("id"));
                            }else{
                                txtDeleteHome.setVisibility(View.GONE);
                                txtDeleteHome.setText(getResources().getString(R.string.delete));
                                SharedHelper.putKey(ActivitySettings.this, "home", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_lat", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_lng","");
                                SharedHelper.putKey(ActivitySettings.this, "home_id", "");
                            }
                            if (workArray.length() > 0){
                                Log.v("Work Address", ""+workArray);
                                txtWorkLocation.setText(workArray.optJSONObject(0).optString("address"));
                                txtDeleteWork.setVisibility(View.VISIBLE);
                                SharedHelper.putKey(ActivitySettings.this, "work", workArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(ActivitySettings.this, "work_lat", workArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(ActivitySettings.this, "work_lng", workArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(ActivitySettings.this, "work_id", workArray.optJSONObject(0).optString("id"));
                            }else{
                                txtDeleteWork.setVisibility(View.GONE);
                                txtDeleteWork.setText(getResources().getString(R.string.delete));
                                SharedHelper.putKey(ActivitySettings.this, "work", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_lat", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_lng","");
                                SharedHelper.putKey(ActivitySettings.this, "work_id", "");
                            }
                            if (othersArray.length() > 0){
                                Log.v("Others Address", ""+othersArray);
                            }
                            if (recentArray.length() > 0){

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure"+call.request().url());
                customDialog.dismiss();
            }
        });
    }
    String strLatitude = "", strLongitude = "", strAddress = "", id = "";

    private void deleteFavoriteLocations(final String strTag) {
        if (strTag.equalsIgnoreCase("home")){
            strLatitude = SharedHelper.getKey(ActivitySettings.this, "home_lat");
            strLongitude = SharedHelper.getKey(ActivitySettings.this, "home_lng");
            strAddress = SharedHelper.getKey(ActivitySettings.this, "home");
            id = SharedHelper.getKey(ActivitySettings.this, "home_id");
        }else{
            strLatitude = SharedHelper.getKey(ActivitySettings.this, "work_lat");
            strLongitude = SharedHelper.getKey(ActivitySettings.this, "work_lng");
            strAddress = SharedHelper.getKey(ActivitySettings.this, "work");
            id = SharedHelper.getKey(ActivitySettings.this, "work_id");
        }
        mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        customDialog.show();

        Call<ResponseBody> call = mApiInterface.deleteFavoriteLocations(id, "XMLHttpRequest",
                SharedHelper.getKey(ActivitySettings.this, "token_type") + " " + SharedHelper.getKey(ActivitySettings.this, "access_token")
                ,strTag, strLatitude, strLongitude, strAddress);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS","SUCESS"+response.body());
                customDialog.dismiss();
                if (response.body() != null){
                    if (strTag.equalsIgnoreCase("home")){
                        SharedHelper.putKey(ActivitySettings.this, "home", "");
                        SharedHelper.putKey(ActivitySettings.this, "home_lat", "");
                        SharedHelper.putKey(ActivitySettings.this, "home_lng","");
                        SharedHelper.putKey(ActivitySettings.this, "home_id", "");
                        txtHomeLocation.setText(getResources().getString(R.string.add_home_location));
                        txtDeleteHome.setVisibility(View.GONE);
                    }else{
                        SharedHelper.putKey(ActivitySettings.this, "work", "");
                        SharedHelper.putKey(ActivitySettings.this, "work_lat", "");
                        SharedHelper.putKey(ActivitySettings.this, "work_lng","");
                        SharedHelper.putKey(ActivitySettings.this, "work_id", "");
                        txtWorkLocation.setText(getResources().getString(R.string.add_work_location));
                        txtDeleteWork.setVisibility(View.GONE);
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure"+call.request().url());
                customDialog.dismiss();
            }
        });
    }


}
