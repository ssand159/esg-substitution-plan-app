package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.esgsubstitutionplanapp.content.ContentManager;
import com.esgsubstitutionplanapp.ignorepackage.TestData;

public class MainActivity extends Activity {

    // data
    private ContentManager contentManager;

    // views
    private LinearLayout datePicker;
    private LinearLayout contentView;
    private TextView myclassText;
    private TextView allclassesText;

    // active values
    private String activeDate;
    private TextView activeClass;

    // other fields
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up view fields;
        datePicker = findViewById(R.id.datePicker);
        contentView = findViewById(R.id.contentView);
        myclassText = findViewById(R.id.myclassText);
        allclassesText = findViewById(R.id.allclassesText);

        // setup
        DB.setup(getSharedPreferences("userdata", MODE_PRIVATE));
        contentManager = new ContentManager();

        // set up test data
        TestData.setUpTestData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check if this is the first start
        if(DB.wasStartedBefore){
            if(DB.username.isEmpty() || DB.password.isEmpty()){
                // check if settings are loaded correct
                startSettings(null);
            } else {
                // everything looks fine, update content
                try {
                    myclassText.setText(DB.myClass.getFullName());
                    contentManager.showContent(this, datePicker, contentView, myclassText, null);
                } catch (Exception e){
                    // TODO
                }
            }
        } else {
            // show welcome screen
            startWelcome(null);
        }
    }

    public void dateClicked(TextView view, String date){
        activeDate = date;

        // update frontend
        for(int index = 0; index < datePicker.getChildCount(); index++) {
            datePicker.getChildAt(index).setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        }
        view.setBackgroundColor(getResources().getColor(R.color.activeSelector));

        // update content
        try {
            contentManager.showContent(this, datePicker, contentView, activeClass, activeDate);
        } catch (Exception e) {
            //Todo
        }
    }

    public void myClassClicked(View view){
        activeClass = myclassText;

        // update frontend
        myclassText.setBackgroundColor(getResources().getColor(R.color.activeSelector));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));

        // update content
        try {
            contentManager.showContent(this, datePicker, contentView, activeClass, activeDate);
        } catch (Exception e) {
            //Todo
        }
    }

    public void allClassesClicked(View view){
        activeClass = allclassesText;

        // update frontend
        myclassText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.activeSelector));

        // update content
        try {
            contentManager.showContent(this, datePicker, contentView, activeClass, activeDate);
        } catch (Exception e) {
            //Todo
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Drücke 'Zurück' erneut, um die App zu schließen.", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}