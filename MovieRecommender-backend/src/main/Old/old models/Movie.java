package com.MovieWebApp.MovieRecommender.Model.old;


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

    //Relationship to table with imdbid & tmdbid
    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL)
    @JsonIgnore
    private IdLink idLink;

    //Relationship to table of relevance of each tag to each movie
    @OneToMany(mappedBy = "movie")
    @JsonIgnore
    private List<GenomeScore> genomeScoreList;

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

    public IdLink getIdLink() {
        return idLink;
    }

    public void setIdLink(IdLink idLink) {
        this.idLink = idLink;
    }

    public List<GenomeScore> getGenomeScoreList() {
        return genomeScoreList;
    }

    public void setGenomeScoreList(List<GenomeScore> genomeScoreList) {
        this.genomeScoreList = genomeScoreList;
    }
}
