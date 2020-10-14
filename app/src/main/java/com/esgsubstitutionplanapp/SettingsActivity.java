package com.esgsubstitutionplanapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.esgsubstitutionplanapp.content.MyClass;
import com.example.esgsubstitutionplanapp.R;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final EditText usernameField = findViewById(R.id.username);
    private final EditText passwordField = findViewById(R.id.password);

    private final Spinner gradeSpinner = findViewById(R.id.grade);
    private final Spinner letterSpinner = findViewById(R.id.letter);

    private final Button saveButton = findViewById(R.id.save);
    private int oldLetterPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // set focus listener to text fields
        usernameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateData();
                }
            }
        });
        passwordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateData();
                }
            }
        });


        // presetting fields
        usernameField.setText(DB.username);

        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(this, R.array.grades, android.R.layout.simple_spinner_item);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setSelection(gradeAdapter.getPosition(DB.myClass.getGrade()));

        ArrayAdapter<CharSequence> letterAdapter = ArrayAdapter.createFromResource(this, R.array.letters, android.R.layout.simple_spinner_item);
        letterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        letterSpinner.setAdapter(letterAdapter);
        letterSpinner.setSelection(letterAdapter.getPosition(DB.myClass.getLetter()));
    }

    private void validateData(){
        String user = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        int gradePosition = gradeSpinner.getSelectedItemPosition();
        int letterPosition = letterSpinner.getSelectedItemPosition();

        boolean isValidClass = true;
        if(gradePosition >= 6 && letterPosition == 0){
            isValidClass = false;
        }

        if(!user.isEmpty() && !password.isEmpty() && isValidClass){
            saveButton.setActivated(true);
        } else {
            saveButton.setActivated(false);
        }
    }

    public void saveData(View view){
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
        if(view.getId() == R.id.grade){
            if(position >= 6){
                oldLetterPosition = letterSpinner.getSelectedItemPosition();
                letterSpinner.setVisibility(View.INVISIBLE);
                letterSpinner.setSelection(0);
            } else {
                letterSpinner.setSelection(oldLetterPosition);
                letterSpinner.setVisibility(View.VISIBLE);
            }
        }
        validateData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}