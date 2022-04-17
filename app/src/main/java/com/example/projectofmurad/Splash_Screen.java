
package com.example.projectofmurad;

import static com.example.projectofmurad.helpers.Utils.LOG_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.projectofmurad.helpers.Utils;

public class Splash_Screen extends Activity {

    private View ellipse_3;
    private ImageView vector_ek2;
    private TextView tv_welcome;
    private View rectangle_1_ek6;
    private TextView g;
    private TextView e_1;
    private TextView t_1;
    private TextView __;
    private TextView s;
    private TextView t_2;
    private TextView a;
    private TextView r;
    private TextView t_3;
    private TextView e_2;
    private TextView d;
    private RelativeLayout group_1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

        ellipse_3 = (View) findViewById(R.id.ellipse_3);
        vector_ek2 = (ImageView) findViewById(R.id.vector_ek2);
        tv_welcome = (TextView) findViewById(R.id.tv_welcome);
        rectangle_1_ek6 = (View) findViewById(R.id.rectangle_1_ek6);
        g = (TextView) findViewById(R.id.g);
        e_1 = (TextView) findViewById(R.id.e_1);
        t_1 = (TextView) findViewById(R.id.t_1);
        __ = (TextView) findViewById(R.id.__);
        s = (TextView) findViewById(R.id.s);
        t_2 = (TextView) findViewById(R.id.t_2);
        a = (TextView) findViewById(R.id.a);
        r = (TextView) findViewById(R.id.r);
        t_3 = (TextView) findViewById(R.id.t_3);
        e_2 = (TextView) findViewById(R.id.e_2);
        d = (TextView) findViewById(R.id.d);
        group_1 = findViewById(R.id.group_1);

        SQLiteDatabase db = openOrCreateDatabase(Utils.DATABASE_NAME, MODE_PRIVATE, null);
//        Utils.createAllTables(db);

        group_1.setOnClickListener(view -> {
            Intent nextScreen = new Intent(Splash_Screen.this,
                    FirebaseUtils.isUserLoggedIn() ? MainActivity.class : Log_In_Screen.class);

            startActivity(nextScreen);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    public void checkPermissions(){
        for (String permission : new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}){
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Permission " + permission + " is granted");
            } else {
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

            requestPermissions(new String[]{permission}, 10000);

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Log.d(LOG_TAG, "Permission " + permissions[i] + " is yet not granted");
                    }
                }

            }
            else {
                //Permission not granted
            }
//            triggerRebirth(requireContext());
        }
    }
}

	