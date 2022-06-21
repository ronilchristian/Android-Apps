package com.example.newsaggregator;

import java.io.Serializable;

public class Articles implements Serializable {

    private String headline;
    private String date;
    private String author;
    private String image;
    private String description;
    private String url;

    public Articles(String headline, String author, String description, String date, String image, String url) {
        this.headline = headline;
        this.date = date;
        this.author = author;
        this.image = image;
        this.description = description;
        this.url = url;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
