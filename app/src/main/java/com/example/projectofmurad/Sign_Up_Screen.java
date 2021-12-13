
	 
	/*
	 *	This content is generated from the API File Info.
	 *	(Alt+Shift+Ctrl+I).
	 *
	 *	@desc 		
	 *	@file 		projectforsubmit
	 *	@date 		1635162014809
	 *	@title 		Page 1
	 *	@author 	
	 *	@keywords 	
	 *	@generator 	Export Kit v1.3.figma
	 *
	 */
	

package com.example.projectofmurad;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

    public class Sign_Up_Screen extends Activity {



    private TextView tv_sign_in;
    private TextView tv_already_have_an_account;
    private ImageView vector;
    private View rectangle_1;
    private TextView et_username;
    private View rectangle_2;
    private TextView et_email_adress;
    private View rectangle_3;
    private TextView et_password;
    private View rectangle_4;
    private TextView et_confirm_password;
    private View ellipse_5;
    private TextView sign_up;

    private Button btn_sign_up;


    boolean match = false;
    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        sp = getSharedPreferences(BuildConfig.APPLICATION_ID + " savedData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        tv_sign_in = (TextView) findViewById(R.id.tv_sign_in);
        tv_already_have_an_account = (TextView) findViewById(R.id.tv_already_have_an_account);
        vector = (ImageView) findViewById(R.id.vector);
        rectangle_1 = (View) findViewById(R.id.rectangle_1);
        et_username = (TextView) findViewById(R.id.et_username);
        rectangle_2 = (View) findViewById(R.id.rectangle_2);
        et_email_adress = (TextView) findViewById(R.id.et_email_address);
        rectangle_3 = (View) findViewById(R.id.rectangle_3);
        et_password = (TextView) findViewById(R.id.et_password);
        rectangle_4 = (View) findViewById(R.id.rectangle_4);
        et_confirm_password = (TextView) findViewById(R.id.et_confirm_password);
        ellipse_5 = (View) findViewById(R.id.ellipse_5);
        sign_up = (TextView) findViewById(R.id.tv_sign_up);



        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(Utils.SHARED_PREFERENCES_KEY_USERNAME, et_username.getText().toString());
                editor.putString(Utils.SHARED_PREFERENCES_KEY_EMAIL_ADDRESS, et_email_adress.getText().toString());
                editor.putString(Utils.SHARED_PREFERENCES_KEY_PASSWORD, et_password.getText().toString());

                editor.commit();
            }
        });



        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!btn_sign_up.isPressed()){
                    TextView tv_match;
                    tv_match = findViewById(R.id.tv_match);
                    try {
                        if(!et_confirm_password.getText().toString().isEmpty() && !et_password.getText().toString().isEmpty()){
                            if(et_password.getText().toString().equals(et_confirm_password.getText().toString())){
                                tv_match.setVisibility(View.INVISIBLE);
                            }
                            else{
                                tv_match.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            tv_match.setVisibility(View.INVISIBLE);
                        }
                        if(et_confirm_password.getText().toString().isEmpty() || et_password.getText().toString().isEmpty()){
                            tv_match.setVisibility(View.INVISIBLE);
                        }
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        tv_match.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        thread.start();

        tv_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(getApplicationContext(), Log_In_Screen.class);
                startActivity(nextScreen);

            }
        });


        //custom code goes here


    }

}
	
	