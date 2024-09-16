package com.MovieWebApp.MovieRecommender.Model.old;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class GenomeScoreKey{
    private int movieid;
    private int tagid;

    public GenomeScoreKey() {
    }

    public GenomeScoreKey(int movieid, int tagid) {
        this.movieid = movieid;
        this.tagid = tagid;
    }

    public int getMovieid() {
        return movieid;
    }

    public void setMovieid(int movieid) {
        this.movieid = movieid;
    }

    public int getTagid() {
        return tagid;
    }

    public void setTagid(int tagid) {
        this.tagid = tagid;
    }
}
