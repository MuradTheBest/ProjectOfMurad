package com.example.projectofmurad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectofmurad.groups.ShowGroupsScreen;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.example.projectofmurad.helpers.utils.CalendarUtils;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LogInScreen extends UserSigningActivity {

	private TextInputLayout et_email_address;
	private TextInputLayout et_password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in_page);
		Objects.requireNonNull(getSupportActionBar()).hide();

		TextView tv_sign_up_now = findViewById(R.id.tv_sign_up_now);
		tv_sign_up_now.setOnClickListener(v -> startActivity(new Intent(this, SignUpScreen.class)));

		MaterialButton btn_log_in = findViewById(R.id.btn_log_in);
		btn_log_in.setOnClickListener(v -> checkFields());

		TextView tv_forgot_password = findViewById(R.id.tv_forgot_password);
		tv_forgot_password.setOnClickListener(v -> createPasswordResetDialog());

		et_email_address = findViewById(R.id.et_email_address);
		et_password = findViewById(R.id.et_password);

		Utils.addDefaultTextChangedListener(et_email_address, et_password);

		btn_google = findViewById(R.id.btn_google);
		btn_google.setOnClickListener(v -> showGoogleSignIn());

		btn_phone = findViewById(R.id.btn_phone);
		btn_phone.setOnClickListener(v -> createPhoneAuthenticationDialog());
	}

	private void checkFields() {
		String email = Utils.getText(et_email_address);
		String password = Utils.getText(et_password);

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
					Toast.makeText(LogInScreen.this, R.string.failed, Toast.LENGTH_SHORT).show();
				});
	}

}