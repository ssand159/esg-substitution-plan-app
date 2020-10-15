package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.esgsubstitutionplanapp.ignorepackage.TestData;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up DB
        DB.username = TestData.username;
        DB.password = TestData.password;
        DB.myClass = TestData.myClass;

        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);

    }

}