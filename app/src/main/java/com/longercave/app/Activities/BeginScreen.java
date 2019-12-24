package com.longercave.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.splunk.mint.Mint;
import com.pk.app.R;

public class BeginScreen extends AppCompatActivity {

    TextView enter_ur_mailID;
    LinearLayout social_layout, lnrBegin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(this.getApplication(), "3c1d6462");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_begin);
        enter_ur_mailID = (TextView)findViewById(R.id.enter_ur_mailID);
        social_layout = (LinearLayout) findViewById(R.id.social_layout);
        lnrBegin = (LinearLayout) findViewById(R.id.lnrBegin);
        enter_ur_mailID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(BeginScreen.this, ActivityEmail.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        social_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(BeginScreen.this, ActivitySocialLogin.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

    }


}
