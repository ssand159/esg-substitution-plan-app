package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.content.Intent;
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

        DB.setPreferences(getSharedPreferences("userdata", MODE_PRIVATE));
        DB.lastUpdate = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(DB.wasStartedBefore){
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            finish();
        }
    }

    public void clickButton1(View view){
        // hide clicked button
        View button1 = findViewById(R.id.welcomeButton1);
        button1.setVisibility(View.INVISIBLE);
        // show next button
        View button2 = findViewById(R.id.welcomeButton2);
        button2.setVisibility(View.VISIBLE);
        // animate text
        TextView welcome2 = findViewById(R.id.welcomeText2);
        welcome2.setVisibility(View.VISIBLE);
        animate(welcome2);
    }

    public void clickButton2(View view){
        // hide clicked button
        View button2 = findViewById(R.id.welcomeButton2);
        button2.setVisibility(View.INVISIBLE);
        // show next button
        TextView button3 = findViewById(R.id.welcomeButton3);
        button3.setVisibility(View.VISIBLE);
        // animate text
        TextView welcome3 = findViewById(R.id.welcomeText3);
        welcome3.setVisibility(View.VISIBLE);
        animate(welcome3);
    }

    public void finish(View view){
        // start main
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    }

    private void animate(TextView textView){
        textView.startAnimation(fadeIn);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
    }
}