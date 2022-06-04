package com.example.projectofmurad;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectofmurad.groups.CreateOrJoinGroupScreen;
import com.example.projectofmurad.helpers.utils.CalendarUtils;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpScreen extends UserSigningActivity implements TextWatcher {

    private TextInputLayout et_username;
    private TextInputLayout et_email_address;
    private TextInputLayout et_password;
    private TextInputLayout et_confirm_password;

    private TextView tv_match;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);
        Objects.requireNonNull(getSupportActionBar()).hide();

        TextView tv_sign_in = findViewById(R.id.tv_sign_in);
        tv_sign_in.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LogInScreen.class)));

        et_username = findViewById(R.id.et_username);
        et_email_address = findViewById(R.id.et_email_address);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);

        tv_match = findViewById(R.id.tv_match);

        MaterialButton btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_sign_up.setOnClickListener(view -> checkFields());

        et_password.getEditText().addTextChangedListener(this);
        et_confirm_password.getEditText().addTextChangedListener(this);

        Utils.addDefaultTextChangedListener(et_username, et_email_address, et_password, et_confirm_password);

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(v -> showGoogleSignIn());

        btn_phone = findViewById(R.id.btn_phone);
        btn_phone.setOnClickListener(v -> createPhoneAuthenticationDialog());
    }

    public void checkFields() {
        String username = Utils.getText(et_username);
        String email = Utils.getText(et_email_address);
        String password = Utils.getText(et_password);
        String confirm_password = Utils.getText(et_confirm_password);

        boolean editTextsFilled = true;

        if(username.isEmpty()){
            et_username.setError(getString(R.string.username_invalid));
            editTextsFilled = false;
        }
        if (email.isEmpty() || !CalendarUtils.isEmailValid(email)) {
            et_email_address.setError(getString(R.string.invalid_email));
            editTextsFilled = false;
        }
        if (password.isEmpty()) {
            et_password.setError(getString(R.string.invalid_password));
            editTextsFilled = false;
        }
        if (confirm_password.isEmpty()) {
            et_confirm_password.setError(getString(R.string.invalid_password));
            editTextsFilled = false;
        }
        if (!password.equals(confirm_password)) {
            et_password.setError(getString(R.string.passwords_do_not_match));
            et_confirm_password.setError(getString(R.string.passwords_do_not_match));
            editTextsFilled = false;
        }

        if (editTextsFilled){
            createUser(email, username, password);
        }
    }

    public void createUser(String email, String username, String password){
        loadingDialog.setMessage(R.string.registering_please_wait);
        loadingDialog.show();

        FirebaseUtils.getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = authResult.getUser();

                        UserData data = new UserData(firebaseUser.getUid(), email, username);

                        FirebaseUtils.usersDatabase.child(firebaseUser.getUid()).setValue(data)
                                .addOnSuccessListener(unused -> {
                                    loadingDialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(), CreateOrJoinGroupScreen.class));
                                })
                                .addOnFailureListener(e -> {
                                    loadingDialog.dismiss();
                                    Toast.makeText(SignUpScreen.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(SignUpScreen.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(Utils.getText(et_password).isEmpty() && Utils.getText(et_confirm_password).isEmpty()) {
            tv_match.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        String password = Utils.getText(et_password);
        String confirm_password = Utils.getText(et_confirm_password);

        if (password.equals(confirm_password)) {
            tv_match.setVisibility(View.INVISIBLE);
        }
        else if (password.isEmpty() || confirm_password.isEmpty()) {
            tv_match.setVisibility(View.INVISIBLE);
        }
        else if (Utils.getText(et_password).isEmpty() && Utils.getText(et_confirm_password).isEmpty()) {
            tv_match.setVisibility(View.INVISIBLE);
        }
        else {
            tv_match.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}