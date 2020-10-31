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
    public static ArrayList<Substitution> mySubstitutions;
    public static ArrayList<Substitution> allSubstitutions;
    public static ArrayList<Substitution> pauses;
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

    public static void setup(SharedPreferences userPreferences, SharedPreferences dataPreferences){
        // user
        DB.userPreferences = userPreferences;
        String grade = userPreferences.getString("grade", "05");
        String letter = userPreferences.getString("letter", "-");
        DB.username = userPreferences.getString("user", "");
        DB.password = userPreferences.getString("password", "");
        DB.myClass = new MyClass(grade, letter);
        DB.wasStartedBefore = userPreferences.getBoolean("wasStartedBefore", false);

        // content
        DB.contentPreferences = dataPreferences;
        DB.lastUpdate = dataPreferences.getLong("lastUpdate", System.currentTimeMillis() - fiveMinutesInMillis - 1000);
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
    }

    public static long getLastUpdate() {
        return lastUpdate;
    }
}
