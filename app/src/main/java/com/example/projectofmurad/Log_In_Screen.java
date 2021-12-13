package com.example.projectofmurad;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatDelegate;

public class Log_In_Screen extends Activity {


	private TextView tv_sign_up_now;
	private View ellipse_4;
	private TextView tv_log_in;
	private ImageView vector_ek1;
	private View rectangle_1_ek4;
	private TextView et_e_mail_adress_ek1;
	private View rectangle_1_ek5;
	private TextView et_password;
	private TextView tv_don_t_have_an_account;

	String password = "";

	SharedPreferences sp;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in_page);
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

		sp = getSharedPreferences(BuildConfig.APPLICATION_ID + " savedData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();



		tv_sign_up_now = (TextView) findViewById(R.id.tv_sign_up_now);
		ellipse_4 = (View) findViewById(R.id.ellipse_4);
		tv_log_in = (TextView) findViewById(R.id.tv_log_in);
		vector_ek1 = (ImageView) findViewById(R.id.vector_ek1);
		rectangle_1_ek4 = (View) findViewById(R.id.rectangle_1_ek4);
		et_e_mail_adress_ek1 = (TextView) findViewById(R.id.et_email_address);
		rectangle_1_ek5 = (View) findViewById(R.id.rectangle_1_ek5);
		et_password = (TextView) findViewById(R.id.et_password);
		tv_don_t_have_an_account = (TextView) findViewById(R.id.tv_don_t_have_an_account);

		et_password.getTransformationMethod();
		tv_sign_up_now.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent nextScreen = new Intent(getApplicationContext(), Sign_Up_Screen.class);
				startActivity(nextScreen);

			}
		});


		//custom code goes here

	}
}