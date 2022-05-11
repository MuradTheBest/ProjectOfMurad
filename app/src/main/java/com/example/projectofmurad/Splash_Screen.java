
package com.example.projectofmurad;

import static com.example.projectofmurad.helpers.Utils.LOG_TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.projectofmurad.groups.CreateOrJoinGroupScreen;
import com.example.projectofmurad.groups.ShowGroupsScreen;
import com.example.projectofmurad.helpers.Constants;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;

public class Splash_Screen extends MyActivity {

    private MaterialButton btn_get_started;
    private ProgressBar progressBar;
    public final static int STORAGE_REQUEST_CODE = 10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);
        getSupportActionBar().hide();

        btn_get_started = findViewById(R.id.btn_get_started);
        progressBar = findViewById(R.id.progressBar);

        SQLiteDatabase db = openOrCreateDatabase(Utils.DATABASE_NAME, MODE_PRIVATE, null);
        Utils.createAllTables(db);

        btn_get_started.setOnClickListener(this::checkGroup);

        checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    /**
     * Checks intent that the activity receives.
     * In case if the user is not logged in, user is navigated to {@link Log_In_Screen} in order to sign in.
     * <p>
     * If the intent's action is {@link Intent#ACTION_VIEW} and it has data, new activity is started through intent that passes the link from uri.
     * </p>
     */
    private void checkIntent(Intent intent) {
        if (intent == null){
            intent = getIntent();
        }

        if(intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_VIEW)){
            return;
        }

        Uri uri = intent.getData();

        Log.d(Utils.LOG_TAG, "uri is " + uri);

        if (uri == null){
            return;
        }

        if (!FirebaseUtils.isUserLoggedIn()){
            startActivity(new Intent(Splash_Screen.this, Log_In_Screen.class));
        }

        String link = uri.toString();

        Log.d(Utils.LOG_TAG, "uri is " + link);

        if (link.contains("group-join-link")){
            Intent i = new Intent(Splash_Screen.this, CreateOrJoinGroupScreen.class);
            i.putExtra(Constants.KEY_LINK, link);
            startActivity(i);
        }

    }

    public void startLoading(){
        progressBar.setVisibility(View.VISIBLE);
        btn_get_started.setText(R.string.setting_the_data);
    }

    public void checkGroup(View view){

        startLoading();

        if(FirebaseUtils.isUserLoggedIn()){
            FirebaseUtils.checkCurrentGroup()
                    .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String key = dataSnapshot.getValue(String.class);
                                FirebaseUtils.changeGroup(Splash_Screen.this, key);
                                goToAnotherScreen(new Intent(Splash_Screen.this, MainActivity.class));
                            }
                            else {
                                goToAnotherScreen(new Intent(Splash_Screen.this, ShowGroupsScreen.class));
                            }
                        }
                    })
                    .addOnFailureListener(e -> goToAnotherScreen(new Intent(Splash_Screen.this, Log_In_Screen.class)));
        }
        else {
            goToAnotherScreen(new Intent(Splash_Screen.this, Log_In_Screen.class));
        }

    }

    public void goToAnotherScreen(Intent intent){
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 500);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    public void checkPermissions(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Permission " + permission + " is granted");
            }
            else {
                Log.d(LOG_TAG, "Permission " + permission + " is not granted");
                askPermission(permission);
            }
        }
    }

    private void askPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Asking for the permission " + permission);
            if (shouldShowRequestPermissionRationale(permission)) {
                Log.d(LOG_TAG, "askPermission: you should show an alert dialog...");
            }

            requestPermissions(new String[]{permission}, STORAGE_REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Log.d(LOG_TAG, "Permission " + permissions[i] + " is yet not granted");
                    }
                }

            }
            else {
                //Permission not granted
                Utils.createAlertDialog(this, null,
                        "You have to grant permission. Otherwise you can't use the app",
                        R.string.ok, (dialog, which) -> checkPermissions(),
                        R.string.no, (dialog, which) -> System.exit(0),
                        null).show();
            }
        }
    }
}

	