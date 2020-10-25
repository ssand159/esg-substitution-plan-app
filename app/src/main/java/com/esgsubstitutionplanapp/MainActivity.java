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
    private TextView noContentView;
    private TextView myclassText;
    private TextView allclassesText;
    private TextView pauseText;
    private TextView errorText;

    // active values
    private String activeDate;

    // other fields
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up view fields;
        datePicker = findViewById(R.id.datePicker);
        contentView = findViewById(R.id.contentView);
        noContentView = findViewById(R.id.noContentView);
        myclassText = findViewById(R.id.myclassText);
        allclassesText = findViewById(R.id.allclassesText);
        pauseText = findViewById(R.id.pauseText);
        errorText = findViewById(R.id.errorView);

        // setup
        DB.setup(getSharedPreferences("userdata", MODE_PRIVATE));
        ScrollView contentScrollView = findViewById(R.id.contentScrollView);
        contentManager = new ContentManager(this, datePicker, contentView, noContentView, contentScrollView, errorText);

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
                    contentManager.loadContent();
                    activeDate = contentManager.paintDateViews();
                    contentManager.paintContent(DB.mySubstitutions, activeDate);
                } catch (Exception e){
                    showError();
                }
            }
        } else {
            // show welcome screen
            startWelcome(null);
        }

        if(DB.classChanged){
            DB.classChanged = false;
            myClassClicked(null);
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
        myClassClicked(null);
    }

    public void myClassClicked(View view){
        // update frontend
        myclassText.setBackgroundColor(getResources().getColor(R.color.activeSelector));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        pauseText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));

        // update content
        try {
            contentManager.paintContent(DB.mySubstitutions, activeDate);
        } catch (Exception e) {
            showError();
        }
    }

    public void allClassesClicked(View view){
        // update frontend
        myclassText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.activeSelector));
        pauseText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));

        // update content
        try {
            contentManager.paintContent(DB.allSubstitutions, activeDate);
        } catch (Exception e) {
            showError();
        }
    }

    public void pauseClicked(View view){
        // update frontend
        myclassText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        pauseText.setBackgroundColor(getResources().getColor(R.color.activeSelector));

        // update content
        try {
            contentManager.paintContent(DB.pauses, activeDate);
        } catch (Exception e) {
            showError();
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

    private void showError(){
        contentView.setVisibility(View.GONE);
        noContentView.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
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