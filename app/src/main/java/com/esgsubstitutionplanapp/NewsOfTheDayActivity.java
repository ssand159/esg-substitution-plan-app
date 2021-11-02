package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NewsOfTheDayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_of_the_day);

        String news = getIntent().getStringExtra("news");

        TextView info = findViewById(R.id.newsoftheday);
        info.setText(news);
    }
}