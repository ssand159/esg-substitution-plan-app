package com.esgsubstitutionplanapp.content.model;

public class Date implements Comparable<Date>{
    private String date;

    public Date(String date){
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(Date other) {
        //TODO
        return 0;
    }
}
