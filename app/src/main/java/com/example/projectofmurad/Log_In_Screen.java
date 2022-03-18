package com.example.projectofmurad;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectofmurad.calendar.UtilsCalendar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;

public class Log_In_Screen extends UserSigningActivity {

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

	private MaterialButton btn_log_in_with_google;
	private MaterialButton btn_log_in_with_facebook;
	private MaterialButton btn_log_in_with_phone;

	SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in_page);

		getSupportActionBar().hide();

		sp = getSharedPreferences(BuildConfig.APPLICATION_ID + " savedData", Context.MODE_PRIVATE);

		progressDialog = new ProgressDialog(this);

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
				else if(!UtilsCalendar.isEmailValid(email)){
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

					progressDialog.setMessage("Registering, please wait...");
					progressDialog.show();

					FirebaseUtils.getFirebaseAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(
							new OnCompleteListener<AuthResult>() {
								@Override
								public void onComplete(@NonNull Task<AuthResult> task) {
									if (task.isSuccessful()){
										progressDialog.dismiss();
										startActivity(new Intent(Log_In_Screen.this, MainActivity.class));
									}
									else {

									}
								}
							})
							.addOnFailureListener(new OnFailureListener() {
								@Override
								public void onFailure(@NonNull Exception e) {
									progressDialog.dismiss();
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

		btn_log_in_with_google = findViewById(R.id.sign_up_with_google);
		btn_log_in_with_google.setOnClickListener(v -> showGoogleSignIn());

		btn_log_in_with_facebook = findViewById(R.id.sign_up_with_facebook);

		btn_log_in_with_phone = findViewById(R.id.sign_up_with_phone);
		btn_log_in_with_phone.setOnClickListener(v -> createPhoneAuthenticationDialog());

	}

	@Override
	public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (v instanceof EditText) {
				Rect outRect = new Rect();
				v.getGlobalVisibleRect(outRect);
				if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
					v.clearFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		}
		return super.dispatchTouchEvent(event);
	}

}