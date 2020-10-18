package com.esgsubstitutionplanapp;

import android.content.SharedPreferences;

import com.esgsubstitutionplanapp.content.MyClass;

public class DB {

    public static final String endpoint = "https://www.esg-landau.de/unterstuetzung/informationen/vertretungsplan";

    public static String username;
    public static String password;
    public static MyClass myClass;

    public static boolean wasStartedBefore;
    public static long lastUpdate = 0;

    public static final long fiveMinutesInMillis = 300_000L;

    private static SharedPreferences preferences;

    public static void saveUserData(String username, String password, MyClass myClass){
        DB.username = username;
        DB.password = password;
        DB.myClass = myClass;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", username);
        editor.putString("password", password);
        editor.putString("grade", myClass.getGrade());
        editor.putString("letter", myClass.getLetter());
        editor.apply();
    }

    public static void setup(SharedPreferences preferences){
        DB.preferences = preferences;
        String username = preferences.getString("user", "");
        String password = preferences.getString("password", "");
        String grade = preferences.getString("grade", "05");
        String letter = preferences.getString("letter", "-");
        DB.username = username;
        DB.password = password;
        DB.myClass = new MyClass(grade, letter);
        DB.lastUpdate = System.currentTimeMillis() - fiveMinutesInMillis - 1000;
    }

}
