package com.shoppinglist.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;

public class Utils {
    private static final String LOG_TAG = "Utils";

    public static void showDialog(Context context, String dialogType, Object obj) {
        if (context instanceof Activity ) {
            DialogFragment itemDialog = PopUpInputDialog.newInstance(dialogType, obj);
            itemDialog.show(((Activity) context).getFragmentManager(), Constants.DIALOG_TAG);
        }
    }
}
