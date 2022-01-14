package com.example.projectofmurad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign_Up_Screen extends Activity implements TextWatcher {

    private TextView tv_sign_in;
    private TextView tv_already_have_an_account;
    private ImageView vector;
    private View rectangle_1;
    private TextView et_username;
    private View rectangle_2;
    private TextView et_email_address;
    private View rectangle_3;
    private TextView et_password;
    private View rectangle_4;
    private TextView et_confirm_password;
    private View ellipse_5;
    private TextView sign_up;

    private Button btn_sign_up;
    private TextView tv_match;


    boolean match = false;
    SharedPreferences sp;
    boolean go = true;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);

        tv_sign_in = (TextView) findViewById(R.id.tv_sign_in);
        tv_already_have_an_account = (TextView) findViewById(R.id.tv_already_have_an_account);
        vector = (ImageView) findViewById(R.id.vector);
        rectangle_1 = (View) findViewById(R.id.rectangle_1);
        et_username = (TextView) findViewById(R.id.et_username);
        rectangle_2 = (View) findViewById(R.id.rectangle_2);
        et_email_address = (TextView) findViewById(R.id.et_email_address);
        rectangle_3 = (View) findViewById(R.id.rectangle_3);
        et_password = (TextView) findViewById(R.id.et_password);
        rectangle_4 = (View) findViewById(R.id.rectangle_4);
        et_confirm_password = (TextView) findViewById(R.id.et_confirm_password);
        ellipse_5 = (View) findViewById(R.id.ellipse_5);
        sign_up = (TextView) findViewById(R.id.tv_sign_up);

        tv_match = findViewById(R.id.tv_match);
        tv_match.setVisibility(View.INVISIBLE);

        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString();
                String email = et_email_address.getText().toString();
                String password = et_password.getText().toString();
                String confirm_password = et_confirm_password.getText().toString();

                String msg = "";
                boolean editTextsFilled = true;

                if(username.isEmpty()){
                    et_username.setError("Username invalid");
                    msg += "Username, ";
                    editTextsFilled = false;
                    //Toast.makeText(getApplicationContext(), "Please enter e-mail address", Toast.LENGTH_SHORT).show();
                }

                if(email.isEmpty()){
                    et_email_address.setError("E-mail invalid");
                    msg += "E-mail, ";
                    editTextsFilled = false;
                    //Toast.makeText(getApplicationContext(), "Please enter e-mail address", Toast.LENGTH_SHORT).show();
                }
                else if(!Utils_Calendar.isEmailValid(email)){
                    et_email_address.setError("E-mail invalid");
                    msg += "valid E-mail, ";
                    editTextsFilled = false;
                    //Toast.makeText(getApplicationContext(), "Please enter valid e-mail address", Toast.LENGTH_SHORT).show();
                }

                if(password.isEmpty()){
                    et_password.setError("Password invalid");
                    msg += "password and ";
                    editTextsFilled = false;
                    //Toast.makeText(getApplicationContext(), "Please enter password address", Toast.LENGTH_SHORT).show();
                }

                if(confirm_password.isEmpty()){
                    et_confirm_password.setError("Password invalid");
                    msg += "confirm password";
                    editTextsFilled = false;
                    //Toast.makeText(getApplicationContext(), "Please enter password address", Toast.LENGTH_SHORT).show();
                }
                else {
                    msg = msg.replace(" and ", "");
                }

                if(editTextsFilled) {
                    //ToDo Firebase authentication
                    progressDialog.setMessage("Registering, please wait...");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(Sign_Up_Screen.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Sign_Up_Screen.this,
                                                "Successfully registered", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(Sign_Up_Screen.this,
                                                "Registration Error!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter " + msg, Toast.LENGTH_SHORT).show();
                }

            }
        });

        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!btn_sign_up.isPressed() || go){
                    TextView tv_match;
                    tv_match = findViewById(R.id.tv_match);
                    try {
                        if(!et_confirm_password.getText().toString().isEmpty() && !et_password.getText().toString().isEmpty()){
                            if(et_password.getText().toString().equals(et_confirm_password.getText().toString())){
                                try {
                                    tv_match.setVisibility(View.INVISIBLE);
                                } catch(Exception e) {
                                    e.printStackTrace();
                                    tv_match.setVisibility(View.INVISIBLE);
                                }
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
                        try {
                            e.printStackTrace();
                            tv_match.setVisibility(View.VISIBLE);
                        } catch(Exception exception) {
                            exception.printStackTrace();
                            tv_match.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        thread.start();*/

        et_password.addTextChangedListener(this);
        et_confirm_password.addTextChangedListener(this);

        tv_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextScreen = new Intent(getApplicationContext(), Log_In_Screen.class);
                startActivity(nextScreen);

            }
        });



    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(et_password.getText().toString().isEmpty() && et_confirm_password.getText().toString().isEmpty()){
            tv_match.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(et_password.getText().toString().equals(et_confirm_password.getText().toString())){
            tv_match.setVisibility(View.INVISIBLE);
        }
        else if(et_password.getText().toString().isEmpty() || et_confirm_password.getText().toString().isEmpty()){
            tv_match.setVisibility(View.INVISIBLE);
        }
        else if(et_password.getText().toString().isEmpty() && et_confirm_password.getText().toString().isEmpty()){
            tv_match.setVisibility(View.INVISIBLE);
        }
        else{
            tv_match.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}