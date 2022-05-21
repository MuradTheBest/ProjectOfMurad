package com.example.projectofmurad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectofmurad.groups.ShowGroupsScreen;
import com.example.projectofmurad.helpers.CalendarUtils;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.example.projectofmurad.helpers.MyTextInputLayout;
import com.example.projectofmurad.helpers.Utils;
import com.google.android.material.textfield.TextInputLayout;

public class Log_In_Screen extends UserSigningActivity {

	private MyTextInputLayout et_email_address;
	private MyTextInputLayout et_password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in_page);
		getSupportActionBar().hide();

		TextView tv_sign_up_now = findViewById(R.id.tv_sign_up_now);

		et_email_address = findViewById(R.id.et_email_address);
		et_password = findViewById(R.id.et_password);

		Button btn_log_in = findViewById(R.id.btn_log_in);

		btn_log_in.setOnClickListener(v -> checkFields());

		tv_sign_up_now.setOnClickListener(v -> startActivity(new Intent(this, Sign_Up_Screen.class)));

		TextView tv_forgot_password = findViewById(R.id.tv_forgot_password);
		tv_forgot_password.setOnClickListener(v -> createPasswordResetDialog());

		btn_google = findViewById(R.id.btn_google);
		btn_google.setOnClickListener(v -> showGoogleSignIn());

		btn_phone = findViewById(R.id.btn_phone);
		btn_phone.setOnClickListener(v -> createPhoneAuthenticationDialog());
	}

	private void checkFields() {
		String email = et_email_address.getText();
		String password = et_password.getText();

		boolean editTextsFilled = true;

		if (email.isEmpty() || !CalendarUtils.isEmailValid(email)) {
			et_email_address.setError("E-mail invalid");
			editTextsFilled = false;
		}

		if(password.isEmpty()){
			et_password.setError("Password invalid");
			editTextsFilled = false;
		}

		if(editTextsFilled){
			login(email, password);
		}
	}

	private void createPasswordResetDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.password_reset_dialog, null);

		MyAlertDialogBuilder builder = new MyAlertDialogBuilder(this);

		builder.setView(view);
		builder.setTitle("Reset password");
		builder.setMessage("Enter email that will receive reset password mail");

		TextInputLayout et_email_address = view.findViewById(R.id.et_email_address);

		// Click on Recover and a email will be sent to your registered email id
		builder.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String email = Utils.getText(et_email_address);

				if (email.isEmpty() || !CalendarUtils.isEmailValid(email)){
					et_email_address.setError(getString(R.string.invalid_email));
				}
				else {
					sendPasswordUpdateEmail(email);
				}
			}
		}, false);

		builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

		builder.show();
	}

	public void login(String email, String password){
		loadingDialog.setMessage(R.string.logging_in_please_wait);
		loadingDialog.show();

		FirebaseUtils.getFirebaseAuth().signInWithEmailAndPassword(email, password)
				.addOnSuccessListener(authResult -> {
					loadingDialog.dismiss();
					startActivity(new Intent(getApplicationContext(), ShowGroupsScreen.class));
					finish();
				})
				.addOnFailureListener(e -> {
					loadingDialog.dismiss();
					Toast.makeText(Log_In_Screen.this, R.string.failed, Toast.LENGTH_SHORT).show();
				});
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