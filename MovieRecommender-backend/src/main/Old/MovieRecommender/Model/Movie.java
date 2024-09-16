package com.MovieWebApp.MovieRecommender.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movieid")
    private int movieid;
    @Column(name = "title")
    private String title;
    @Column(name = "genres")
    private String genres;

    public Movie() {
    }

    public Movie(int movieid, String title, String genres) {
        this.movieid = movieid;
        this.title = title;
        this.genres = genres;
    }

    public int getMovieid() {
        return movieid;
    }

    public void setMovieid(int movieid) {
        this.movieid = movieid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
