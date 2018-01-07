package com.example.kevin.project7newsapp;

import java.util.ArrayList;

/**
 * Created by Kevin on 11/6/2017.
 */

public class Article {

    private String title;
    private ArrayList<String> authors;
    private String section;
    private String date;
    private String url;

    public Article(String title, ArrayList<String> authors, String section, String date, String url) {
        this.title = title;
        this.authors = authors;
        this.section = section;
        this.date = date;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public String getSection() {
        return section;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
