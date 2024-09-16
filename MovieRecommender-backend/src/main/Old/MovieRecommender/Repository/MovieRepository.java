package com.MovieWebApp.MovieRecommender.Repository;

import com.MovieWebApp.MovieRecommender.Model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    public Movie findByMovieid(int id);
    public List<Movie> findByTitleLikeIgnoreCase(String title);

    @Query("SELECT m.movieid FROM Movie m")
    public List<Integer> findAllBy();
}
