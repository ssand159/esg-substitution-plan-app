package com.esgsubstitutionplanapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class SubstitutionPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitution_plan);
    }

    public void refresh (View view){
        try {

            // TODO show in app
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}