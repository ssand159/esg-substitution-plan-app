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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.esgsubstitutionplanapp.content.ContentManager;
import com.esgsubstitutionplanapp.ignorepackage.TestData;

public class MainActivity extends Activity {

    // data
    private ContentManager contentManager;

    // views
    private LinearLayout datePicker;
    private TextView noContentView;
    private TextView errorText;
    private View contentContainer;

    // manual refresh
    private SwipeRefreshLayout swipeContainer;

    // other fields
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up view fields;
        datePicker = findViewById(R.id.datePicker);
        noContentView = findViewById(R.id.noContentView);
        errorText = findViewById(R.id.errorView);
        contentContainer = findViewById(R.id.contentContainer);
        TextView newsOfTheDayText = findViewById(R.id.newsoftheday);
        swipeContainer = findViewById(R.id.mainActivity);
        LinearLayout contentView = findViewById(R.id.contentView);

        // setup data
        DB.setup();
        contentManager = new ContentManager(this, datePicker, contentView, noContentView, contentContainer, newsOfTheDayText);

        // force refresh
        swipeContainer.setOnRefreshListener(() -> {
            downloadAndShowContent(true);
            datePicker.getChildAt(0).performClick();
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // set up test data
//        TestData.setUpTestData();

        // set text for noContentView
        noContentView.setText("Heute ist keine Ankündigung für " + DB.myClass.getFullName() + " vorhanden");

        // show settings or content
        if(DB.username.isEmpty() || DB.password.isEmpty()){
            startSettings(null);
        } else {
            downloadAndShowContent(false);
            try {
                if(DB.dates.isEmpty()){
                    noContentView.setVisibility(View.VISIBLE);
                } else {
                    datePicker.getChildAt(0).performClick();
                }
            } catch (Exception e){
                showError(e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(DB.classChanged){
            DB.classChanged = false;
            datePicker.getChildAt(0).performClick();
        }
    }

    public void dateClicked(TextView view, String date){
        // update frontend
        for(int index = 0; index < datePicker.getChildCount(); index++) {
            TextView current = (TextView) datePicker.getChildAt(index);
            current.setBackgroundColor(getResources().getColor(R.color.inActiveSelector));
            current.setTextColor(getResources().getColor(R.color.inactiveText));
        }
        view.setBackgroundColor(getResources().getColor(R.color.activeSelector));
        view.setTextColor(getResources().getColor(R.color.activeText));

        // update content
        updateContent(date);
    }

    private void downloadAndShowContent(boolean forceDownload){
        try {
            errorText.setVisibility(View.GONE);
            contentManager.downloadAndShowContent(forceDownload);
            swipeContainer.setRefreshing(false);
        } catch (Exception e){
            showError(e);
        }
    }

    private void updateContent(String date){
        try {
            errorText.setVisibility(View.GONE);
            contentManager.changeDay(date);
        } catch (Exception e){
            showError(e);
        }
    }

    //
    //
    // helper methods for starting and ending activities or showing messages
    //
    //

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