package com.example.projectofmurad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectofmurad.calendar.UtilsCalendar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.UploadTask;

public class Sign_Up_Screen extends UserSigningActivity implements TextWatcher {

    private TextView tv_sign_in;
    private TextView tv_already_have_an_account;
    private ImageView vector;
    private View rectangle_1;
    private EditText et_username;
    private View rectangle_2;
    private EditText et_email_address;
    private View rectangle_3;
    private EditText et_password;
    private View rectangle_4;
    private EditText et_confirm_password;
    private View ellipse_5;
    private TextView sign_up;

    private Button btn_sign_up;
    private TextView tv_match;


    boolean match = false;
    boolean go = true;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        getSupportActionBar().hide();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



        tv_sign_in = (TextView) findViewById(R.id.tv_sign_in);
        tv_already_have_an_account = (TextView) findViewById(R.id.tv_already_have_an_account);
        vector = (ImageView) findViewById(R.id.vector);
        ellipse_5 = (View) findViewById(R.id.ellipse_5);
        sign_up = (TextView) findViewById(R.id.tv_sign_up);


        et_username = ((TextInputLayout) findViewById(R.id.et_username)).getEditText();
        et_email_address = ((TextInputLayout) findViewById(R.id.et_email_address)).getEditText();
        et_password = ((TextInputLayout) findViewById(R.id.et_password)).getEditText();
        et_confirm_password = ((TextInputLayout) findViewById(R.id.et_confirm_password)).getEditText();

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
                else if(!UtilsCalendar.isEmailValid(email)){
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
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Sign_Up_Screen.this,
                                                "Successfully registered!", Toast.LENGTH_SHORT).show();

                                        firebaseUser = firebaseAuth.getCurrentUser();

                                        UserData data = new UserData(firebaseUser.getUid(), email, username);

                                        FirebaseUtils.usersDatabase.child(firebaseUser.getUid()).setValue(data);

                                        Uri sample_profile = Utils.getUriToDrawable(Sign_Up_Screen.this, R.drawable.images);

                                        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder().setPhotoUri(sample_profile);
                                        builder.setDisplayName(username);

                                        Log.d("murad", "username " + username + "\n uri " + sample_profile.toString());

                                        FirebaseUtils.getCurrentFirebaseUser().updateProfile(builder.build());
                                        FirebaseUtils.getCurrentFirebaseUser().updateEmail(email);


                                        FirebaseUtils.getProfilePicturesRef().child(FirebaseUtils.getCurrentUID()).putFile(sample_profile)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    String profile_picture = taskSnapshot.getUploadSessionUri().toString();
                                                        FirebaseUtils.getProfilePicturesRef().child(FirebaseUtils.getCurrentUID()).getDownloadUrl().addOnCompleteListener(
                                                                new OnCompleteListener<Uri>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                                        if (task.isSuccessful()){
                                                                            String profile_picture = task.getResult().toString();
                                                                            FirebaseUtils.getCurrentUserDataRef().child("profile_picture").setValue(profile_picture);

                                                                            getToken();
                                                                            subscribeToTopics();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                    else{
/*                                        Toast.makeText(Sign_Up_Screen.this,
                                                "Registration Error!",
                                                Toast.LENGTH_SHORT).show();*/
                                    }
                                    progressDialog.dismiss();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Sign_Up_Screen.this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
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

        btn_log_in_with_google = findViewById(R.id.sign_up_with_google);
        btn_log_in_with_google.setOnClickListener(v -> showGoogleSignIn());

        btn_log_in_with_facebook = findViewById(R.id.sign_up_with_facebook);

        btn_log_in_with_phone = findViewById(R.id.sign_up_with_phone);
        btn_log_in_with_phone.setOnClickListener(v -> createPhoneAuthenticationDialog());

    }

    public void createNewAccount(){

    }

    private void uploadToken() {
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(et_password.getText().toString().isEmpty() && et_confirm_password.getText().toString().isEmpty()){
            tv_match.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        String password = et_password.getText().toString();
        String confirm_password = et_confirm_password.getText().toString();

        if(password.equals(confirm_password)){
            tv_match.setVisibility(View.INVISIBLE);
        }
        else if(password.isEmpty() || confirm_password.isEmpty()){
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