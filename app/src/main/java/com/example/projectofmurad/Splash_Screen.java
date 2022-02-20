
package com.example.projectofmurad;

import static com.example.projectofmurad.Utils.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    private Button button;
    private Button btn_sign_out;

    int LOCATION_REQUEST_CODE = 10001;

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

        btn_sign_out = findViewById(R.id.btn_sign_out);
        btn_sign_out.setOnClickListener(v -> FirebaseUtils.getFirebaseAuth().signOut());

        group_1.setOnClickListener(view -> {
            Intent nextScreen = new Intent(Splash_Screen.this,
                    FirebaseUtils.isUserLoggedIn() ? MainActivity.class : Log_In_Screen.class);
/*            if (FirebaseUtils.isUserLoggedIn()){
                nextScreen = new Intent(getApplicationContext(), MainActivity.class);
            }
            else {
                nextScreen = new Intent(getApplicationContext(), Log_In_Screen.class);
            }*/

            finish();
            startActivity(nextScreen);
        });

        button = findViewById(R.id.button);
        button.setVisibility(View.GONE);
        button.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });

        //custom code goes here

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
        } else {
            askNetworkPermission();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
        } else {
            askNetworkPermission();
        }
    }

    private void askNetworkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, LOCATION_REQUEST_CODE);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    LOCATION_REQUEST_CODE + 1);

        }
    }
}

	