package com.example.projectofmurad;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectofmurad.groups.CreateOrJoinGroupScreen;
import com.example.projectofmurad.helpers.CalendarUtils;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.MyTextInputLayout;
import com.example.projectofmurad.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class Sign_Up_Screen extends UserSigningActivity implements TextWatcher {

    private MyTextInputLayout et_username;
    private MyTextInputLayout et_email_address;
    private MyTextInputLayout et_password;
    private MyTextInputLayout et_confirm_password;

    private TextView tv_match;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);
        getSupportActionBar().hide();

        TextView tv_sign_in = findViewById(R.id.tv_sign_in);
        tv_sign_in.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Log_In_Screen.class)));

        et_username = findViewById(R.id.et_username);
        et_email_address = findViewById(R.id.et_email_address);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);

        tv_match = findViewById(R.id.tv_match);

        Button btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_sign_up.setOnClickListener(view -> checkFields());

        et_password.getEditText().addTextChangedListener(this);
        et_confirm_password.getEditText().addTextChangedListener(this);

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(v -> showGoogleSignIn());

        btn_facebook = findViewById(R.id.btn_facebook);

        btn_phone = findViewById(R.id.btn_phone);
        btn_phone.setOnClickListener(v -> createPhoneAuthenticationDialog());
    }

    public void checkFields() {
        String username = et_username.getText();
        String email = et_email_address.getText();
        String password = et_password.getText();
        String confirm_password = et_confirm_password.getText();

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
                                    Toast.makeText(Sign_Up_Screen.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(Sign_Up_Screen.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
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