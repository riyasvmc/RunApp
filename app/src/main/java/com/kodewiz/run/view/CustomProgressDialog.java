package com.kodewiz.run.view;

import android.app.ProgressDialog;
import android.content.Context;

import com.kodewiz.run.R;

/**
 * Created by Riyas V on 3/6/2016.
 */
public class CustomProgressDialog extends ProgressDialog {
    public CustomProgressDialog(Context context) {
        super(context, R.style.AppTheme);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, R.style.AppTheme);
    }
}
