package com.longercave.app.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.longercave.app.Helper.CustomDialog;
import com.longercave.app.Helper.SharedHelper;
import com.longercave.app.Helper.URLHelper;
import com.pk.app.R;
import com.longercave.app.TranxitApplication;
import com.longercave.app.Utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.longercave.app.TranxitApplication.trimMessage;

public class ActivityHelp extends AppCompatActivity implements View.OnClickListener {

    ImageView imgEmail;
    ImageView imgPhone;
    ImageView imgWeb;
    ImageView backArrow;
    TextView titleTxt;

    String phone = "";
    String email = "";
    Context context = ActivityHelp.this;
    Utilities utils = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);
        findviewById();
        setOnClickListener();
        getHelp();
    }

    private void findviewById() {
        imgEmail = (ImageView) findViewById(R.id.img_mail);
        imgPhone = (ImageView) findViewById(R.id.img_phone);
        imgWeb = (ImageView) findViewById(R.id.img_web);
        titleTxt = (TextView) findViewById(R.id.title_txt);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        titleTxt.setText(context.getResources().getString(R.string.app_name) + " " + getString(R.string.help));
    }

    private void setOnClickListener() {
        imgEmail.setOnClickListener(this);
        imgPhone.setOnClickListener(this);
        imgWeb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgEmail) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name) + "-" + getString(R.string.help));
            intent.putExtra(Intent.EXTRA_TEXT, "Hello team");
            startActivity(Intent.createChooser(intent, "Send Email"));
        }
        if (v == imgPhone) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            boolean hasNetwork = telephonyManager.getNetworkType() != android.telephony.TelephonyManager.NETWORK_TYPE_UNKNOWN;
            if (hasNetwork) {
                if (phone != null && !phone.equalsIgnoreCase("null") && !phone.equalsIgnoreCase("") && phone.length() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse("tel:" + phone));
                        startActivity(intentCall);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(context.getResources().getString(R.string.app_name))
                            .setIcon(R.mipmap.longer)
                            .setMessage(context.getResources().getString(R.string.sorry_for_inconvinent))
                            .setCancelable(false)
                            .setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
            } else {
                displayMessage("Mobile network not available!");
            }
        }
        if (v == imgWeb) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.REDIRECT_URL));
            startActivity(browserIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkCallPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getHelp() {
        final CustomDialog customDialog = new CustomDialog(this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.HELP, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                phone=response.optString("contact_number");
                email=response.optString("contact_email");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(context.getResources().getString(R.string.something_went_wrong));
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(context.getResources().getString(R.string.server_down));
                        } else {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(context.getResources().getString(R.string.something_went_wrong));
                        e.printStackTrace();
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getHelp();
                    }
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(ActivityHelp.this, "access_token"));
                Log.e("", "Access_Token" + SharedHelper.getKey(ActivityHelp.this, "access_token"));
                return headers;
            }
        };
        TranxitApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void refreshAccessToken() {


        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(getApplicationContext(), "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(ActivityHelp.this, "access_token", response.optString("access_token"));
                SharedHelper.putKey(ActivityHelp.this, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(ActivityHelp.this, "token_type", response.optString("token_type"));
                getHelp();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(ActivityHelp.this, "loggedIn", context.getResources().getString(R.string.False));
                    GoToBeginActivity();
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        TranxitApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    public void displayMessage(String toastString){
        utils.print("displayMessage",""+toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(context,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(this, "loggedIn", context.getResources().getString(R.string.False));
        Intent mainIntent = new Intent(this, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        this.finish();
    }
}
