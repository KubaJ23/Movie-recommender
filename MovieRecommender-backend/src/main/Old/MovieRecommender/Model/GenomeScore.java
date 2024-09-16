package com.MovieWebApp.MovieRecommender.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "genomescores")
public class GenomeScore {
    @EmbeddedId
    GenomeScoreKey id;

    @Column(name = "relevance")
    private double relevance;

    public GenomeScore() {
    }

    public GenomeScore(int movieid, int tagid, double relevance) {
        this.id = new GenomeScoreKey(movieid,tagid);
        this.relevance = relevance;
    }

    public GenomeScoreKey getId() {
        return id;
    }

    public void setId(GenomeScoreKey id) {
        this.id = id;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }
}
