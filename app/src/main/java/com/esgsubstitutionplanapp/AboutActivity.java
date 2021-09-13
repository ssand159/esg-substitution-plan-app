package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void sendEmail(View view){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:die.schul.app@gmail.com")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, "die.schul.app@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Vertretungsplan-App");
        startActivity(intent);
    }
}