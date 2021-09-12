package com.esgsubstitutionplanapp.content.model;

import java.util.Objects;

public class Date implements Comparable<Date> {
    private final String date;

    private final int month;
    private final int day;
    private String newsOfTheDay;

    public Date(String datum){
        this.date = datum;

        String[] split = datum.split("\\.");
        this.day = Integer.parseInt(split[0]);
        this.month = Integer.parseInt(split[1]);

    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Date){
            Date other = (Date) o;
            return this.day == other.day && this.month == other.month;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, month, day);
    }

    @Override
    public int compareTo(Date other) {
        if(this.day == other.day && this.month == other.month){
            return 0;
        }

        if(this.month < other.month){
            return -1;
        } else if(this.month == 12 && other.month == 1){
            return -1;
        } else if(this.month > other.month){
            return 1;
        } else {
            if(this.day < other.day){
                return -1;
            } else {
                return 1;
            }
        }
    }

    public void setNewsOfTheDay(String newsOfTheDay) {
        this.newsOfTheDay = newsOfTheDay;
    }

    public String getNewsOfTheDay() {
        return newsOfTheDay;
    }
}
