package com.MovieWebApp.MovieRecommender.Repository;

import com.MovieWebApp.MovieRecommender.Model.GenomeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface GenomeTagRepository extends JpaRepository<GenomeTag, Integer> {

    public GenomeTag findByTagid(int tagid);
}
