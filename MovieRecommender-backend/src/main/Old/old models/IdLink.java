package com.MovieWebApp.MovieRecommender.Model.old;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "links")
public class IdLink {

    @Id
    @Column(name = "movieid")
    private int movieid;
    @Column(name = "imdbid")
    private Integer imdbid;
    @Column(name = "tmdbid")
    private Integer tmdbid;

    @OneToOne
    @JoinColumn(name = "movieid")
    @JsonIgnore
    private Movie movie;

    public IdLink() {
    }

    public IdLink(int movieid, int imdbid, int tmdbid) {
        this.movieid = movieid;
        this.imdbid = imdbid;
        this.tmdbid = tmdbid;
    }
}
