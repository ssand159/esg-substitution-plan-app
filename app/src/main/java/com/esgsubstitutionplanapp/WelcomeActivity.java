package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

    private final AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // startup
        DB.wasStartedBefore = true;
        DB.setLastUpdate(System.currentTimeMillis() - DB.fiveMinutesInMillis - DB.fiveMinutesInMillis);
    }

    public void clickButton1(View view){
        View button1 = findViewById(R.id.welcomeButton1);
        button1.setVisibility(View.GONE);
        View button2 = findViewById(R.id.welcomeButton2);
        button2.setVisibility(View.VISIBLE);
        TextView welcome2 = findViewById(R.id.welcomeText2);
        welcome2.setVisibility(View.VISIBLE);
        animate(welcome2);
    }

    public void clickButton2(View view){
        finish();
    }

    private void animate(TextView textView){
        textView.startAnimation(fadeIn);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
    }
}