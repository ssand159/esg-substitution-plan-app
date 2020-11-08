package com.esgsubstitutionplanapp.content;

public class MyClass {

    private final String grade;
    private final String letter;

    public MyClass(String grade, String letter){
        this.grade = grade;
        this.letter = letter;
    }

    public String getFullName(){
        String name = grade + letter;
        return name.trim().replace("alle", "");
    }

    public String getGrade() {
        return grade;
    }

    public String getLetter() {
        return letter;
    }
}
