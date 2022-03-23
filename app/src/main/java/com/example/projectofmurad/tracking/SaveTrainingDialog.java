package com.example.projectofmurad.tracking;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;

import java.time.LocalDate;

public class SaveTrainingDialog extends Dialog implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {

    RadioButton rb_private_training;
    RadioButton rb_group_training;
    LinearLayout ll_private_training;
    LinearLayout ll_group_training;

    private Training training;

    private Context context;

    private OnAddTrainingListener onAddTrainingListener;

    public SaveTrainingDialog(@NonNull Context context, Training training, OnAddTrainingListener onAddTrainingListener) {
        super(context);

        this.context = context;
        this.training = training;

//        this.onAddTrainingListener = (OnAddTrainingListener) context;
        this.onAddTrainingListener = onAddTrainingListener;

        this.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        this.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
    }

    public SaveTrainingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Choose training type");
        setCancelable(false);
        setContentView(R.layout.choose_training_dialog);

        RadioGroup rg_choose_training = findViewById(R.id.rg_choose_training);

        rb_private_training = findViewById(R.id.rb_private_training);
        rb_group_training = findViewById(R.id.rb_group_training);

        rb_private_training.setOnCheckedChangeListener(this);
        rb_group_training.setOnCheckedChangeListener(this);

        ll_private_training = findViewById(R.id.ll_private_training);
        ll_private_training.setOnClickListener(this);
        ll_group_training = findViewById(R.id.ll_group_training);
        ll_group_training.setOnClickListener(this);


        Utils.createCustomDialog(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            if (buttonView == rb_private_training) {
                rb_group_training.setChecked(false);
                ll_private_training.setBackgroundResource(R.drawable.calendar_cell_selected_background);
                ll_group_training.setBackgroundResource(R.drawable.calendar_cell_unclicked_background);

//                new MyRepository(getOwnerActivity().getApplication()).insert(training);
                onAddTrainingListener.onAddTraining(training);
            }
            if (buttonView == rb_group_training) {
                rb_private_training.setChecked(false);
                ll_private_training.setBackgroundResource(R.drawable.calendar_cell_unclicked_background);
                ll_group_training.setBackgroundResource(R.drawable.calendar_cell_selected_background);

                ChooseEventClickDialog chooseEventDialog
                        = new ChooseEventClickDialog(context, LocalDate.now(), training, onAddTrainingListener);

                chooseEventDialog.show();
            }

            new Handler().postDelayed(this::dismiss, 500);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ll_private_training) {
            rb_private_training.setChecked(true);
        }
        if (v == ll_group_training) {
            rb_group_training.setChecked(true);
        }
    }

    public interface OnAddTrainingListener{
        void onAddTraining(Training training);
    }
}
