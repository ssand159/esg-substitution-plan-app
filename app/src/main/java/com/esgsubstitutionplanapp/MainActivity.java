package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.esgsubstitutionplanapp.content.ContentManager;
import com.esgsubstitutionplanapp.content.model.SubstitutionType;
import com.esgsubstitutionplanapp.ignorepackage.TestData;

public class MainActivity extends Activity {

    // data
    private ContentManager contentManager;

    // views
    private LinearLayout datePicker;
    private TextView noContentView;
    private TextView myclassText;
    private TextView allclassesText;
    private TextView pauseText;
    private TextView errorText;
    private View contentContainer;

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
        noContentView = findViewById(R.id.noContentView);
        myclassText = findViewById(R.id.myclassText);
        allclassesText = findViewById(R.id.allclassesText);
        pauseText = findViewById(R.id.pauseText);
        errorText = findViewById(R.id.errorView);
        contentContainer = findViewById(R.id.contentContainer);

        // setup
        DB.setup();
        TextView newsOfTheDayText = findViewById(R.id.newsoftheday);
        LinearLayout contentView = findViewById(R.id.contentView);
        contentManager = new ContentManager(this, datePicker, contentView, noContentView, contentContainer, errorText);

        // set up test data
        TestData.setUpTestData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check if this is the first start
        if(DB.username.isEmpty() || DB.password.isEmpty()){
            // check if settings are loaded correct
            startSettings(null);
        } else {
            // everything looks fine, update content and show it
            findViewById(R.id.mainActivity).setVisibility(View.VISIBLE);
            try {
                myclassText.setText(DB.myClass.getFullName());
                contentManager.loadContent();
                activeDate = contentManager.paintDateViews();
                contentManager.setUpContent();
                myClassClicked(null);
            } catch (Exception e){
                showError(e);
            }
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
            TextView current = (TextView) datePicker.getChildAt(index);
            current.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
            current.setTextColor(getResources().getColor(R.color.inactiveText));
        }
        view.setBackgroundColor(getResources().getColor(R.color.activeSelector));
        view.setTextColor(getResources().getColor(R.color.activeText));

        // update content
        myClassClicked(null);
    }

    public void myClassClicked(View view){
        // update frontend
        myclassText.setBackgroundColor(getResources().getColor(R.color.activeSelector));
        myclassText.setTextColor(getResources().getColor(R.color.activeText));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        allclassesText.setTextColor(getResources().getColor(R.color.inactiveText));
        pauseText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        pauseText.setTextColor(getResources().getColor(R.color.inactiveText));

        // update content
        try {
            contentManager.updateContent(SubstitutionType.SUBSTITUTION_OF_MYCLASS, activeDate);
        } catch (Exception e) {
            showError(e);
        }
    }

    public void allClassesClicked(View view){
        // update frontend
        myclassText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        myclassText.setTextColor(getResources().getColor(R.color.inactiveText));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.activeSelector));
        allclassesText.setTextColor(getResources().getColor(R.color.activeText));
        pauseText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        pauseText.setTextColor(getResources().getColor(R.color.inactiveText));

        // update content
        try {
            contentManager.updateContent(SubstitutionType.SUBSTITUTION_ALL, activeDate);
        } catch (Exception e) {
            showError(e);
        }
    }

    public void pauseClicked(View view){
        // update frontend
        myclassText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        myclassText.setTextColor(getResources().getColor(R.color.inactiveText));
        allclassesText.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
        allclassesText.setTextColor(getResources().getColor(R.color.inactiveText));
        pauseText.setBackgroundColor(getResources().getColor(R.color.activeSelector));
        pauseText.setTextColor(getResources().getColor(R.color.activeText));

        // update content
        try {
            contentManager.updateContent(SubstitutionType.SUBSTITUTION_PAUSE, activeDate);
        } catch (Exception e) {
            showError(e);
        }
    }

    public void startSettings(View view){
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    private void showError(Exception e){
        e.printStackTrace();
        contentContainer.setVisibility(View.GONE);
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