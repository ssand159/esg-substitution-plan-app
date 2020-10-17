package com.esgsubstitutionplanapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.esgsubstitutionplanapp.connection.ConnectionClient;
import com.esgsubstitutionplanapp.content.ContentParser;
import com.esgsubstitutionplanapp.content.Substitution;
import com.esgsubstitutionplanapp.content.UserFilter;
import com.esgsubstitutionplanapp.ignorepackage.TestData;
import com.esgsubstitutionplanapp.R;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.ArrayList;

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