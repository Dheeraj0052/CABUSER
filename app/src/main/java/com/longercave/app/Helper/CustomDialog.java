package com.longercave.app.Helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

import com.pk.app.R;


public class CustomDialog extends ProgressDialog {

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setIndeterminate(true);
        setMessage(context.getResources().getString(R.string.please_wait));
      //  setContentView(R.layout.custom_dialog);
    }
}