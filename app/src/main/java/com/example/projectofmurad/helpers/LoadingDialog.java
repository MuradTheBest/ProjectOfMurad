package com.example.projectofmurad.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.example.projectofmurad.R;

public class LoadingDialog extends Dialog {

    private String message;
    private TextView textView;

    public LoadingDialog(@NonNull Context context) {
        super(context);

        getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        getWindow().setBackgroundDrawableResource(R.drawable.round_picker_dialog_background);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        setCanceledOnTouchOutside(false);

        textView = findViewById(R.id.tv);
        textView.setText(message);
    }

   public void setMessage(String message){
        if (isShowing()) {
            textView.setText(message);
        }
        else {
            this.message = message;
        }
    }

   public void setMessage(@StringRes int message){
       setMessage(getContext().getString(message));
   }
}
