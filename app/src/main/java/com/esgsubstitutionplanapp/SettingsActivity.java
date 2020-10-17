package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.esgsubstitutionplanapp.content.MyClass;

public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private EditText usernameField;
    private EditText passwordField;

    private Spinner gradeSpinner;
    private Spinner letterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // set fields
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        gradeSpinner = findViewById(R.id.grade);
        letterSpinner = findViewById(R.id.letter);

        // presetting fields
        usernameField.setText(DB.username);
        passwordField.setText(DB.password);

        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(this, R.array.grades, android.R.layout.simple_spinner_item);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setSelection(gradeAdapter.getPosition(DB.myClass.getGrade()), true);
        gradeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> letterAdapter = ArrayAdapter.createFromResource(this, R.array.letters, android.R.layout.simple_spinner_item);
        letterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        letterSpinner.setAdapter(letterAdapter);
        letterSpinner.setSelection(letterAdapter.getPosition(DB.myClass.getLetter()), true);
        letterSpinner.setOnItemSelectedListener(this);
        System.out.println("SettingsActivity - " + DB.myClass.getLetter());
        System.out.println("SettingsActivity - " + letterAdapter.getPosition(DB.myClass.getLetter()));

        String grade = DB.myClass.getGrade();
        if(grade.equals("11") || grade.equals("12") || grade.equals("13")){
            letterSpinner.setVisibility(View.INVISIBLE);
        }

    }

    public void saveData(View view){
        System.out.println("SettingsActivity - save");
        String user = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String grade = gradeSpinner.getSelectedItem().toString();
        String letter = letterSpinner.getSelectedItem().toString();

        if(!validate(user, password)){
            return;
        }

        if(grade.equals("11") || grade.equals("12") || grade.equals("13")){
            letter = "-";
        }

        SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
        editor.putString("user", user);
        editor.putString("password", password);
        editor.putString("grade", grade);
        editor.putString("letter", letter);
        editor.apply();

        System.out.println("SettingsActivity - " + user);
        System.out.println("SettingsActivity - " + password);
        System.out.println("SettingsActivity - " + grade);
        System.out.println("SettingsActivity - " + letter);
        DB.username = user;
        DB.password = password;
        DB.myClass = new MyClass(grade, letter);

        Toast.makeText(this, "Einstellungen wurden gespeichert", Toast.LENGTH_SHORT).show();
    }

    private boolean validate(String user, String password){
        boolean validData = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(5);
            gd.setStroke(1, Color.RED);
            if(user.isEmpty()){
                usernameField.setBackground(gd);
                validData = false;
            } else {
                usernameField.setBackgroundResource(0);
            }
            if(password.isEmpty()){
                passwordField.setBackground(gd);
                validData = false;
            } else {
                passwordField.setBackgroundResource(0);
            }
        } else if(user.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Fehlende Angaben", Toast.LENGTH_SHORT).show();
            validData = false;
        }
        return validData;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.grade){
            if(position >= 6){
                letterSpinner.setVisibility(View.INVISIBLE);
            } else {
                letterSpinner.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}