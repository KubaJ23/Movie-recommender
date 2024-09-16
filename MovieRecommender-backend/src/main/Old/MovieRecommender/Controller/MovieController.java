package com.MovieWebApp.MovieRecommender.Controller;

import com.MovieWebApp.MovieRecommender.Model.GenomeScore;
import com.MovieWebApp.MovieRecommender.Model.GenomeTag;
import com.MovieWebApp.MovieRecommender.Model.IdLink;
import com.MovieWebApp.MovieRecommender.Model.Movie;
import com.MovieWebApp.MovieRecommender.Service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    MovieService movieService;

    // For when user wants to search for a movie
    @GetMapping("/search")
    public List<Movie> searchForMovie(@RequestParam("name") String movieName){
        return movieService.searchForMovie(movieName);
    }

    // For returning recommended movies based on given movies
    @GetMapping("/recommend")
    public List<Movie> recommendMovies(@RequestParam("id") List<Integer> ids, @RequestParam("getNumMovies") int numRecommendMovies){
        return movieService.recommendMovies(ids, numRecommendMovies);
    }

    // for testing if connections and querying each relation works
    @GetMapping("/{id}")
    public Movie getMovie(@PathVariable(name = "id") int id){
        return movieService.getMovie(id);
    }
    @GetMapping("/link/{id}")
    public IdLink getIdLink(@PathVariable(name = "id") int id){
        return movieService.getMovieIdLinks(id);
    }
    @GetMapping("/genomescore/{movieid}/{tagid}")
    public GenomeScore getGenomeScore(@PathVariable(name = "movieid") int movieid, @PathVariable(name = "tagid") int tagid){
        return movieService.getMovieGenomeScore(movieid, tagid);
    }
    @GetMapping("/genometag/{tagid}")
    public GenomeTag getGenomeScore(@PathVariable(name = "tagid") int tagid){
        return movieService.getMovieGenomeTag(tagid);
    }
}
