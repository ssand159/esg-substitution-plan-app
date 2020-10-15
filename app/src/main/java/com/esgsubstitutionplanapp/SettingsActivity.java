package com.esgsubstitutionplanapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.esgsubstitutionplanapp.content.MyClass;

public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private EditText usernameField;
    private EditText passwordField;

    private Spinner gradeSpinner;
    private Spinner letterSpinner;

    private int oldLetterPosition = 0;

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
        gradeSpinner.setSelection(gradeAdapter.getPosition(DB.myClass.getGrade()));
        gradeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> letterAdapter = ArrayAdapter.createFromResource(this, R.array.letters, android.R.layout.simple_spinner_item);
        letterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        letterSpinner.setAdapter(letterAdapter);
        letterSpinner.setSelection(letterAdapter.getPosition(DB.myClass.getLetter()));
        letterSpinner.setOnItemSelectedListener(this);
    }

    public void saveData(View view){
        System.out.println("SettingsActivity - save");
        String user = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String grade = gradeSpinner.getSelectedItem().toString();
        String letter = letterSpinner.getSelectedItem().toString();

        SharedPreferences.Editor editor = getSharedPreferences("", 0).edit();
        editor.putString("user", user);
        editor.putString("password", password);
        editor.putString("grade", grade);
        editor.putString("letter", letter);
        editor.apply();

        DB.username = user;
        DB.password = password;
        DB.myClass = new MyClass(grade, letter);

        Toast.makeText(this, "Einstellungen wurden gespeichert", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.grade){
            if(position >= 6){
                oldLetterPosition = letterSpinner.getSelectedItemPosition();
                letterSpinner.setVisibility(View.INVISIBLE);
                letterSpinner.setSelection(0);
            } else {
                letterSpinner.setSelection(oldLetterPosition);
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