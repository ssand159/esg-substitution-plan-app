package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.esgsubstitutionplanapp.connection.ConnectionClient;
import com.esgsubstitutionplanapp.content.ContentParser;
import com.esgsubstitutionplanapp.content.MyClass;
import com.esgsubstitutionplanapp.content.Substitution;
import com.esgsubstitutionplanapp.content.UserFilter;
import com.esgsubstitutionplanapp.ignorepackage.TestData;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private boolean doubleBackToExitPressedOnce = false;
    private boolean wasStartedBefore = false;

    private SharedPreferences preferences;

    private MultiValuedMap<String, Substitution> allSubstitutions;
    private ArrayList<Substitution> mySubstitutions;
    private LinearLayout datePicker;
    private ScrollView contentView;
    private TextView myclassText;
    private TextView allclassesText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up view fields;
        datePicker = findViewById(R.id.datePicker);
        contentView = findViewById(R.id.contentView);
        myclassText = findViewById(R.id.myclassText);
        allclassesText = findViewById(R.id.allclassesText);

        // set up DB
        preferences= getSharedPreferences("userdata", MODE_PRIVATE);
        String username = preferences.getString("user", "");
        String password = preferences.getString("password", "");
        String grade = preferences.getString("grade", "05");
        String letter = preferences.getString("letter", "-");
        DB.username = username;
        DB.password = password;
        DB.myClass = new MyClass(grade, letter);

        // test data
        TestData.setUpTestData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check if this is the first start
        wasStartedBefore = preferences.getBoolean("wasStartedBefore", false);

        if(wasStartedBefore){
            if(DB.username.isEmpty() || DB.password.isEmpty()){
                // check if settings are loaded correct
                startSettings(null);
            } else {
                // everything looks fine, update content
                refreshContent(null);
                myClassClicked(null);
                myclassText.setText(DB.myClass.getFullName());
            }
        } else {
            // show welcome screen
            startWelcome(null);

            //TODO move this to WelcomeActivity
            wasStartedBefore = true;
            SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
            editor.putBoolean("wasStartedBefore", true);
            editor.apply();
        }
    }

    public void refreshContent(View view){
        try {
            // get content
            ConnectionClient connectionClient = new ConnectionClient(DB.endpoint, DB.username, DB.password, false);
            String html = connectionClient.getHtml();

            // parse content
            ContentParser contentParser = new ContentParser(false);
            allSubstitutions = contentParser.createSubstitutionList(html);

            // filter content according to user settings
            UserFilter userFilter= new UserFilter(true);
            mySubstitutions = userFilter.getSubstitutionsForMyClass(allSubstitutions, DB.myClass);
        } catch (Exception e){

        }
    }


    public void startSettings(View view){
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    public void startWelcome(View view){
//        Intent settings = new Intent(this, SettingsActivity.class);
//        startActivity(settings);
    }

    public void myClassClicked(View view){
        myclassText.setBackgroundColor(getResources().getColor(R.color.activeSelector));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
    }

    public void allClassesClicked(View view){
        myclassText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.activeSelector));
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