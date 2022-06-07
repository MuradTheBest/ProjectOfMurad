package com.example.projectofmurad.helpers;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.projectofmurad.utils.Utils;

public class MyAlertDialogBuilder extends AlertDialog.Builder {
    /**
     * Click listeners
     */
    private DialogInterface.OnClickListener mPositiveButtonListener = null;
    private DialogInterface.OnClickListener mNegativeButtonListener = null;
    private DialogInterface.OnClickListener mNeutralButtonListener = null;

    /**
     * Buttons text
     */
    private CharSequence mPositiveButtonText = null;
    private CharSequence mNegativeButtonText = null;
    private CharSequence mNeutralButtonText = null;

    /**
     * Whether to dismiss or not after click on button
     */
    private boolean mPositiveButtonDismiss = true;
    private boolean mNegativeButtonDismiss = true;
    private boolean mNeutralButtonDismiss = true;

    public MyAlertDialogBuilder(Context context) {
        super(context);
        setCancelable(false);
    }

    public MyAlertDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener, boolean dismiss) {
        if (dismiss){
            setNegativeButton(text, listener);
        }
        else {
            mNegativeButtonListener = listener;
            mNegativeButtonText = text;
            mNegativeButtonDismiss = false;
        }
        return this;
    }

    public MyAlertDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener, boolean dismiss) {
        if (dismiss) {
            setNeutralButton(text, listener);
        }
        else {
            mNeutralButtonListener = listener;
            mNeutralButtonText = text;
            mNeutralButtonDismiss = false;
        }
        return this;
    }

    public MyAlertDialogBuilder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener, boolean dismiss) {
        if (dismiss) {
            setNeutralButton(text, listener);
        }
        else {
            mPositiveButtonListener = listener;
            mPositiveButtonText = text;
            mPositiveButtonDismiss = false;
        }
        return this;
    }

    public MyAlertDialogBuilder setNegativeButton(int textId, DialogInterface.OnClickListener listener, boolean toDismiss) {
        setNegativeButton(getContext().getString(textId), listener, toDismiss);
        return this;
    }

    public MyAlertDialogBuilder setNeutralButton(int textId, DialogInterface.OnClickListener listener, boolean toDismiss) {
        setNeutralButton(getContext().getString(textId), listener, toDismiss);
        return this;
    }

    public MyAlertDialogBuilder setPositiveButton(int textId, DialogInterface.OnClickListener listener, boolean toDismiss) {
        setPositiveButton(getContext().getString(textId), listener, toDismiss);
        return this;
    }

    @NonNull
    @Override
    public AlertDialog create() {
        return show();
    }

    private final DialogInterface.OnClickListener emptyOnClickListener = (dialog, which) -> dialog.dismiss();

    @Override
    public AlertDialog show() {
        final AlertDialog alertDialog = Utils.createCustomDialog(super.create());

        // Enable buttons (needed for Android 1.6) - otherwise later getButton() returns null
        if (!mPositiveButtonDismiss && mPositiveButtonText != null) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mPositiveButtonText, emptyOnClickListener);
        }

        if (!mNegativeButtonDismiss && mNegativeButtonText != null) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mNegativeButtonText, emptyOnClickListener);
        }

        if (!mNeutralButtonDismiss && mNeutralButtonText != null) {
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mNeutralButtonText, emptyOnClickListener);
        }

        alertDialog.show();

        // Set the OnClickListener directly on the Button object, avoiding the auto-dismiss feature
        // IMPORTANT: this must be after alert.show(), otherwise the button doesn't exist..
        // If the listeners are null don't do anything so that they will still dismiss the dialog when clicked
        if (!mPositiveButtonDismiss && mPositiveButtonListener != null) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                    v -> mPositiveButtonListener.onClick(alertDialog, AlertDialog.BUTTON_POSITIVE));
        }

        if (!mNegativeButtonDismiss && mNegativeButtonListener != null) {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                    v -> mNegativeButtonListener.onClick(alertDialog, AlertDialog.BUTTON_NEGATIVE));
        }

        if (!mNeutralButtonDismiss && mNeutralButtonListener != null) {
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(
                    v -> mNeutralButtonListener.onClick(alertDialog, AlertDialog.BUTTON_NEUTRAL));
        }

        return alertDialog;
    }
}
