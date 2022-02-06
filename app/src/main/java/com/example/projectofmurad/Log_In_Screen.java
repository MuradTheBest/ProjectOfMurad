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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.projectofmurad.calendar.Utils_Calendar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

	SharedPreferences sp;

	FirebaseAuth firebaseAuth;
	FirebaseUser firebaseUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in_page);
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

		sp = getSharedPreferences(BuildConfig.APPLICATION_ID + " savedData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		firebaseAuth = FirebaseAuth.getInstance();

		tv_sign_up_now = (TextView) findViewById(R.id.tv_sign_up_now);
		ellipse_4 = (View) findViewById(R.id.ellipse_4);
		tv_log_in = (TextView) findViewById(R.id.tv_log_in);
		vector_ek1 = (ImageView) findViewById(R.id.vector_ek1);

		et_email_address = (TextView) findViewById(R.id.et_email_address);

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
				else if(!Utils_Calendar.isEmailValid(email)){
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
					firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
							new OnCompleteListener<AuthResult>() {
								@Override
								public void onComplete(@NonNull Task<AuthResult> task) {
									if (task.isSuccessful()){
//										firebaseUser = firebaseAuth.getCurrentUser();
										finish();
										startActivity(new Intent(Log_In_Screen.this, MainActivity.class));
									}
									else {

									}
								}
							})
							.addOnFailureListener(new OnFailureListener() {
								@Override
								public void onFailure(@NonNull Exception e) {
									Toast.makeText(Log_In_Screen.this, e.getMessage(),
											Toast.LENGTH_SHORT).show();
								}
							});
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