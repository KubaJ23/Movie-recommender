package com.MovieWebApp.MovieRecommender.Repository;

import com.MovieWebApp.MovieRecommender.Model.GenomeScore;
import com.MovieWebApp.MovieRecommender.Model.GenomeScoreKey;
import com.MovieWebApp.MovieRecommender.Model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenomeScoreRepository extends JpaRepository<GenomeScore, GenomeScoreKey> {

    public List<GenomeScore> findByIdMovieid(int movieid);

    public Page<GenomeScore> findAllByOrderByIdMovieidAscIdTagidAsc(Pageable pageable);
}
