package com.example.projectofmurad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.blankFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.bottomNavigationView, new BlankFragment());
        }
        if(item.getItemId() == R.id.tables_Fragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.bottomNavigationView, new Tables_Fragment());
        }
        if(item.getItemId() == R.id.graph_Fragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.bottomNavigationView, new Graph_Fragment());
        }
        if(item.getItemId() == R.id.chat_Fragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.bottomNavigationView, new Chat_Fragment());
        }
        if(item.getItemId() == R.id.map_Fragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.bottomNavigationView, new Map_Fragment());
        }
        return false;
    }
}