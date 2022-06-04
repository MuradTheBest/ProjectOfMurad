package com.example.projectofmurad.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.utils.Utils;

public class LoadingDialog extends Dialog {

    private String message;
    private TextView tv_message;

    public LoadingDialog(@NonNull Context context) {
        super(context);

        Utils.createCustomDialog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        setCanceledOnTouchOutside(false);

        tv_message = findViewById(R.id.tv_message);
        tv_message.setText(message);
    }

   public void setMessage(String message){
        if (isShowing()) {
            tv_message.setText(message);
        }
        else {
            this.message = message;
        }
    }

   public void setMessage(@StringRes int message){
       setMessage(getContext().getString(message));
   }
}
