package com.example.projectofmurad;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

public class Log_In_Screen extends Activity {


	private TextView tv_sign_up_now;
	private View ellipse_4;
	private TextView tv_log_in;
	private ImageView vector_ek1;
	private View rectangle_1_ek4;
	private TextView et_email_address;
	private View rectangle_1_ek5;
	private TextView et_password;
	private TextView tv_don_t_have_an_account;
	private Button btn_log_in;

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
		et_email_address = (TextView) findViewById(R.id.et_email_address);
		rectangle_1_ek5 = (View) findViewById(R.id.rectangle_1_ek5);
		et_password = (TextView) findViewById(R.id.et_password);
		tv_don_t_have_an_account = (TextView) findViewById(R.id.tv_don_t_have_an_account);
		btn_log_in = findViewById(R.id.btn_log_in);

		et_password.getTransformationMethod();

		btn_log_in.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String email = et_email_address.getText().toString();
				String password = et_password.getText().toString();
				String msg = "";
				boolean editTextsFilled = true;

				if(email.isEmpty()){
					et_email_address.setError("E-mail invalid");
					msg += "E-mail and ";
					editTextsFilled = false;
					//Toast.makeText(getApplicationContext(), "Please enter e-mail address", Toast.LENGTH_SHORT).show();
				}
				else if(!Utils.isEmailValid(email)){
					et_email_address.setError("E-mail invalid");
					msg += "valid E-mail and ";
					editTextsFilled = false;
					//Toast.makeText(getApplicationContext(), "Please enter valid e-mail address", Toast.LENGTH_SHORT).show();
				}

				if(password.isEmpty()){
					et_password.setError("Password invalid");
					msg += "password";
					editTextsFilled = false;
					//Toast.makeText(getApplicationContext(), "Please enter password address", Toast.LENGTH_SHORT).show();
				}
				else {
					msg = msg.replace(" and ", " ");
				}

				if(editTextsFilled){
					//ToDo Firebase authentication

				}
				else {
					Toast.makeText(getApplicationContext(), "Please enter " + msg, Toast.LENGTH_SHORT).show();

				}
			}
		});

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