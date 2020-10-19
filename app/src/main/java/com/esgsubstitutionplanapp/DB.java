package com.esgsubstitutionplanapp;

import android.content.SharedPreferences;

import com.esgsubstitutionplanapp.content.MyClass;
import com.esgsubstitutionplanapp.content.model.Date;
import com.esgsubstitutionplanapp.content.model.Substitution;

import java.util.ArrayList;
import java.util.SortedSet;

public class DB {

    private static SharedPreferences preferences;

    // static fields
    public static final String endpoint = "https://www.esg-landau.de/unterstuetzung/informationen/vertretungsplan";
    public static final long fiveMinutesInMillis = 300_000L;

    // user settings
    public static String username;
    public static String password;
    public static MyClass myClass;

    // general settings
    public static boolean wasStartedBefore;
    public static long lastUpdate = 0;
    public static boolean classChanged = false;

    // content
    public static SortedSet<Date> dates;
    public static ArrayList<Substitution> mySubstitutions;
    public static ArrayList<Substitution> allSubstitutions;
    public static ArrayList<Substitution> pauses;

    public static void saveUserData(String username, String password, MyClass myClass){
        DB.username = username;
        DB.password = password;
        DB.myClass = myClass;

        DB.classChanged = true;

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
