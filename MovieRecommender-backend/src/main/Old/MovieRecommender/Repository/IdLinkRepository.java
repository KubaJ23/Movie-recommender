package com.MovieWebApp.MovieRecommender.Repository;

import com.MovieWebApp.MovieRecommender.Model.IdLink;
import com.MovieWebApp.MovieRecommender.Model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdLinkRepository extends JpaRepository<IdLink, Integer> {

    public IdLink findByMovieid(int movieid);
}
