package com.example.projectofmurad.tracking;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.training.Training;

import java.time.LocalDate;

public class SaveTrainingDialog extends AppCompatDialog implements RadioGroup.OnCheckedChangeListener {

    private final Training training;
    private final Context context;
    private final OnAddTrainingListener onAddTrainingListener;

    public SaveTrainingDialog(@NonNull Context context, Training training, OnAddTrainingListener onAddTrainingListener) {
        super(context);

        this.context = context;
        this.training = training;

//        this.onAddTrainingListener = (OnAddTrainingListener) context;
        this.onAddTrainingListener = onAddTrainingListener;

        Utils.createCustomDialog(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Choose training type");
        setContentView(R.layout.choose_event_dialog);

        RadioGroup rg_choose_training_type = findViewById(R.id.rg_choose_training_type);
        rg_choose_training_type.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_private_training:
                onAddTrainingListener.onAddTraining(training);
                break;
            case R.id.rb_group_training:
                ChooseEventClickDialog chooseEventDialog
                        = new ChooseEventClickDialog(context, LocalDate.now(), training, onAddTrainingListener);

                chooseEventDialog.show();
                break;
        }

        new Handler().postDelayed(this::dismiss, 500);
    }

    public interface OnAddTrainingListener{
        void onAddTraining(Training training);
    }
}
