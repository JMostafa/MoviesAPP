package com.moviapp.mostafa.moviapp;

/**
 * Created by mostafa on 8/29/2016.
 */
public class video {

    private String id;
    private String key;
    private String name;
    private String site;
    private String type;

    public video(String id, String key, String name, String site, String type) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }

    public video() {
        this.id = "";
        this.key = "";
        this.name = "";
        this.site = "";
        this.type = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
