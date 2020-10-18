package com.esgsubstitutionplanapp.content.model;

public class Date implements Comparable{
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
    public int compareTo(Object o) {
        //TODO
        return 0;
    }
}
