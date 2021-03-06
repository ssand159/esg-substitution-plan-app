package com.esgsubstitutionplanapp;

import android.content.SharedPreferences;

import com.esgsubstitutionplanapp.content.MyClass;
import com.esgsubstitutionplanapp.content.model.Date;
import com.esgsubstitutionplanapp.content.model.NewsOfTheDay;
import com.esgsubstitutionplanapp.content.model.Substitution;

import java.util.ArrayList;
import java.util.SortedSet;

public class DB {

    private static SharedPreferences userPreferences;
    private static SharedPreferences contentPreferences;

    // static fields
    public static final String endpoint = "https://www.esg-landau.de/unterstuetzung/informationen/vertretungsplan";
    public static final long fiveMinutesInMillis = 300_000L;

    // user settings
    public static String username;
    public static String password;
    public static MyClass myClass;

    // general settings
    public static boolean wasStartedBefore;
    private static long lastUpdate = 0;
    public static boolean classChanged = false;

    // content
    public static SortedSet<Date> dates;
    public static ArrayList<Substitution> allSubstitutions;
    public static ArrayList<NewsOfTheDay> newsOfTheDays;

    public static void saveUserData(String username, String password, MyClass myClass){
        DB.username = username;
        DB.password = password;
        DB.myClass = myClass;

        DB.classChanged = true;

        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putString("user", username);
        editor.putString("password", password);
        editor.putString("grade", myClass.getGrade());
        editor.putString("letter", myClass.getLetter());
        editor.apply();
    }

    public static void setPreferences(SharedPreferences userPreferences, SharedPreferences dataPreferences){
        DB.userPreferences = userPreferences;
        DB.contentPreferences = dataPreferences;
        DB.wasStartedBefore = userPreferences.getBoolean("wasStartedBefore", false);
    }

    public static void setup(){
        // user
        String grade = userPreferences.getString("grade", "05");
        String letter = userPreferences.getString("letter", "alle");
        DB.username = userPreferences.getString("user", "");
        DB.password = userPreferences.getString("password", "");
        DB.myClass = new MyClass(grade, letter);

        // content
        DB.lastUpdate = System.currentTimeMillis() - fiveMinutesInMillis - 1000;
//        DB.lastUpdate = dataPreferences.getLong("lastUpdate", System.currentTimeMillis() - fiveMinutesInMillis - 1000);
    }

    public static void setLastUpdate(long lastUpdate) {
        SharedPreferences.Editor editor = contentPreferences.edit();
        editor.putLong("lastUpdate", lastUpdate);
        editor.apply();
        DB.lastUpdate = lastUpdate;
    }

    public static void setWasStartedBefore(boolean wasStartedBefore) {
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putBoolean("wasStartedBefore", wasStartedBefore);
        editor.apply();
        DB.wasStartedBefore = true;
    }

    public static long getLastUpdate() {
        return lastUpdate;
    }
}
