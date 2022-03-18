package com.example.projectofmurad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EmptyBlankActivity extends AppCompatActivity {

    private AlertDialog alertDialog;

    public static final String ACTION_TO_SET_ALARM = Utils.APPLICATION_ID + "action_to_set_alarm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_blank);
        setTheme(R.style.Theme_Transparent);

        Intent gotten_intent = getIntent();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpDialog();
    }

    long before = 0;
    String toast = "";

    private void setUpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();



        dialogBuilder.setView(R.layout.activity_empty_blank);
        dialogBuilder.setSingleChoiceItems(new CharSequence[]{
                "At the time of event",
                "5 minutes",
                "15 minutes",
                "30 minutes",
                "1 hour",
                "Custom",
                }, 0,
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case 1:
                        before = 5 * 60 * 1000;
                        toast = "Alarm was set for 5 minutes before beginning of the event";
                        break;
                    case 2:
                        before = 15 * 60 * 1000;
                        toast = "Alarm was set for 15 minutes before beginning of the event";
                        break;
                    case 3:
                        before = 30 * 60 * 1000;
                        toast = "Alarm was set for 30 minutes before beginning of the event";
                        break;
                    case 4:
                        before = 60 * 60 * 1000;
                        toast = "Alarm was set for 1 hour before beginning of the event";
                        break;
                }


            }
        });
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                AlarmManagerForToday.addAlarm(EmptyBlankActivity.this, event, before);
                Toast.makeText(EmptyBlankActivity.this, toast, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(dialog::dismiss,300);
            }
        });
        dialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler().postDelayed(dialog::dismiss,300);
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        if(!isFinishing())
        {
            alertDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(alertDialog != null){
            alertDialog.dismiss();
        }
        ;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}