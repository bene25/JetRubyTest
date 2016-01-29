package com.example.aleksandr.jetrubytest.view.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by aleksandr on 23.01.16.
 * Custom edit text preference without dialog box
 */
public class CustomEditTextPreference extends EditTextPreference {

    public CustomEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        Dialog dlg = getDialog();
        dlg.dismiss();
    }

}
