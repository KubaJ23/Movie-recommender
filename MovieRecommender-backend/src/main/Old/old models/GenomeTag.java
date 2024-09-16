package com.MovieWebApp.MovieRecommender.Model.old;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "genometags")
public class GenomeTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tagid")
    private int tagid;
    @Column(name = "tag")
    private String tag;

    @OneToMany(mappedBy = "tag")
    @JsonIgnore
    private List<GenomeScore> genomeScoreList;

    public GenomeTag() {
    }

    public GenomeTag(int tagid, String tag) {
        this.tagid = tagid;
        this.tag = tag;
    }
}
