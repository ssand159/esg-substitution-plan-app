package com.esgsubstitutionplanapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.esgsubstitutionplanapp.connection.ConnectionClient;
import com.esgsubstitutionplanapp.content.ContentParser;
import com.esgsubstitutionplanapp.content.Substitution;
import com.esgsubstitutionplanapp.content.UserFilter;
import com.esgsubstitutionplanapp.ignorepackage.TestData;
import com.example.esgsubstitutionplanapp.R;

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
            // get content
            ConnectionClient connectionClient = new ConnectionClient(DB.endpoint, DB.username, DB.password, false);
            String html = connectionClient.getHtml();

            // parse content
            ContentParser contentParser = new ContentParser(false);
            MultiValuedMap<String, Substitution> allSubstitutions = contentParser.createSubstitutionList(html);

            // filter content according to user settings
            UserFilter userFilter= new UserFilter(true);
            ArrayList<Substitution> mySubstitutions = userFilter.getSubstitutionsForMyClass(allSubstitutions, TestData.myClass);

            // TODO show in app
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}