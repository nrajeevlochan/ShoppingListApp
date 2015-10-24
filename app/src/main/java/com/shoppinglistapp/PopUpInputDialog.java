package com.shoppinglistapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by r.nalluru on 10/20/15.
 */
public class PopUpInputDialog extends DialogFragment {

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    public static PopUpInputDialog newInstance(String diaplayDialogType, Object obj) {
        PopUpInputDialog popUpInputDialog = new PopUpInputDialog();
        Item item = (Item) obj;
        if (item != null) {
            Bundle args = new Bundle();
            args.putString(Constants.DIALOG_TYPE_KEY, diaplayDialogType);
            args.putParcelable(Constants.DIALOG_INPUT_KEY, item);
            popUpInputDialog.setArguments(args);
        }
        return popUpInputDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(getActivity());
        }
        // Get the layout inflater
        LayoutInflater inflater = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            inflater = getActivity().getLayoutInflater();
        }

        Bundle bundle = getArguments();
        Item item = null;
        if (bundle != null) {
            item = bundle.getParcelable(Constants.DIALOG_INPUT_KEY);
        }

        if (builder != null && inflater != null) {
            View dialogView = inflater.inflate(R.layout.dialog_input, null);
            if (item != null) {
                ((EditText) dialogView.findViewById(R.id.dialog_item_name)).setText(item.getName());
                ((EditText) dialogView.findViewById(R.id.dialog_item_desc)).setText(item.getDescription());
            }
            builder.setView(dialogView)
                    .setTitle(R.string.dialog_title)
                    .setPositiveButton(R.string.add_item, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Send the positive button event back to the host activity
                            mListener.onDialogPositiveClick(PopUpInputDialog.this);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Send the negative button event back to the host activity
                            mListener.onDialogNegativeClick(PopUpInputDialog.this);
                        }
                    });
        } else {
            return null;
        }
        return builder.create();
    }
}
