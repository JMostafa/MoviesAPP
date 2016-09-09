package com.moviapp.mostafa.moviapp;

/**
 * Created by mostafa on 8/29/2016.
 */
public class Review {
    private String author;
    private String content;
    private String url;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Review()
    {
        this.author = "";
        this.content = "";
        this.url = "";
    }

    public Review(String author,String content,String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
