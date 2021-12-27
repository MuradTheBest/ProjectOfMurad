
package com.example.projectofmurad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

        group_1.setOnClickListener(view -> {
            Intent nextScreen = new Intent(getApplicationContext(), Log_In_Screen.class);
            startActivity(nextScreen);
        });

        button = findViewById(R.id.button);
        button.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(5);
        arrayList.add(89);
        arrayList.add(410);
        arrayList.add(5203);
        arrayList.add(74265);

        arrayList.remove(0);

        Log.d("murad",  "size " + arrayList.size());
        Log.d("murad",  "place 0 " + arrayList.get(0));

        arrayList.remove(2);

        Log.d("murad",  "size " + arrayList.size());
        Log.d("murad",  "place 0 " + arrayList.get(2));

        arrayList.add(0, 0);

        Log.d("murad",  "size " + arrayList.size());
        Log.d("murad",  "place 0 " + arrayList.get(0));

        //custom code goes here

    }
}

	