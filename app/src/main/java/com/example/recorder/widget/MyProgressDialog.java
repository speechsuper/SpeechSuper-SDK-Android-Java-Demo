package com.example.recorder.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import com.example.recorder.R;

public class MyProgressDialog extends Dialog {

    private static final String TAG = "MyProgressDialog";

    public static void dismissSafe(MyProgressDialog dialog) {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public MyProgressDialog(Context context) {
        super(context);
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static MyProgressDialog createDialog(Context context) {
        MyProgressDialog myProgressDialog = new MyProgressDialog(context, R.style.Dialog);
        myProgressDialog.setContentView(R.layout.progress_layout);
        myProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        myProgressDialog.setCanceledOnTouchOutside(false);
        return myProgressDialog;
    }

}
