package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.esgsubstitutionplanapp.content.MyClass;

public class MainActivity extends Activity {

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up DB
        SharedPreferences preferences= getSharedPreferences("userdata", MODE_PRIVATE);
        String username = preferences.getString("user", "");
        String password = preferences.getString("password", "");
        String grade = preferences.getString("grade", "05");
        String letter = preferences.getString("letter", "-");
        DB.username = username;
        DB.password = password;
        DB.myClass = new MyClass(grade, letter);

        // check if this is the first start
        boolean wasStartedBefore = preferences.getBoolean("wasStartedBefore", false);
        if(!wasStartedBefore){
            // show welcome screen
//            Intent settings = new Intent(this, SettingsActivity.class);
//            startActivity(settings);
        } else {
            // check if settings are loaded correct
            if(username.isEmpty() || password.isEmpty()){
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
            }
        }
    }

    public void startSettings(View view){
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Drücke 'Zurück' erneut, um die App zu schließen.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    } }