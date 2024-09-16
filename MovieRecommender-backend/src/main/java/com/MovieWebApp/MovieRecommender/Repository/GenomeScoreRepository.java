package com.MovieWebApp.MovieRecommender.Repository;

import com.MovieWebApp.MovieRecommender.Model.GenomeScore;
import com.MovieWebApp.MovieRecommender.Model.GenomeScoreKey;
import com.MovieWebApp.MovieRecommender.Model.Movie;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;

@Repository
public interface GenomeScoreRepository extends JpaRepository<GenomeScore, GenomeScoreKey> {

    public List<GenomeScore> findByIdMovieid(int movieid);

    @Transactional(readOnly = true)
    @Query(value = "SELECT * FROM genomescores gs ORDER BY movieid, tagid", nativeQuery = true)
    @QueryHints(value = {@QueryHint(name = HINT_FETCH_SIZE, value = "" + 500)})
    Stream<GenomeScore> streamAllOrderedByIdMovieidAscIdTagidAsc();
}
