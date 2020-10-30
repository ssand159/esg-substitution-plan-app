package com.esgsubstitutionplanapp.content.model;

public class NewsOfTheDay {
    String date;
    String news;

    public NewsOfTheDay(String date, String news){
        this.date = date;
        this.news = news;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }
}
