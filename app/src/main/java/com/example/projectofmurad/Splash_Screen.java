
	 
	/*
	 *	This content is generated from the API File Info.
	 *	(Alt+Shift+Ctrl+I).
	 *
	 *	@desc 		
	 *	@file 		projectforsubmit
	 *	@date 		1635162014809
	 *	@title 		Page 1
	 *	@author 	
	 *	@keywords 	
	 *	@generator 	Export Kit v1.3.figma
	 *
	 */
	

package com.example.projectofmurad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        group_1.setOnClickListener(view -> {
            Intent nextScreen = new Intent(getApplicationContext(), Log_In_Screen.class);
            startActivity(nextScreen);
        });

        //custom code goes here

    }
}

	