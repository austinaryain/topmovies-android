package com.example.aryai.topmovies;

/**
 * Created by aryai on 3/16/2017.
 */

public class Movie {

    private String title;
    private float rating;
    private String thumbnail;
    private String language;
    private String overview;
    private String releaseDate;

    public Movie(){ }

    public Movie(String title, float rating, String thumbnail){
        this.title = title;
        this.rating = rating;
        this.thumbnail = thumbnail;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public float getRating(){
        return rating;
    }

    public void setRating(float rating){
        this.rating = rating;
    }

    public String getThumbnail(){
        return thumbnail;
    }

    public void setThumbnail(String thumbnail){
        this.thumbnail = thumbnail;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}