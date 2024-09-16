package com.MovieWebApp.MovieRecommender.Service;

import com.MovieWebApp.MovieRecommender.Pair;
import com.MovieWebApp.MovieRecommender.Model.*;
import com.MovieWebApp.MovieRecommender.Repository.GenomeScoreRepository;
import com.MovieWebApp.MovieRecommender.Repository.GenomeTagRepository;
import com.MovieWebApp.MovieRecommender.Repository.IdLinkRepository;
import com.MovieWebApp.MovieRecommender.Repository.MovieRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MovieService {
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    IdLinkRepository idLinkRepository;
    @Autowired
    GenomeScoreRepository genomeScoreRepository;
    @Autowired
    GenomeTagRepository genomeTagRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Movie> searchForMovie(String movieName, int numMoviesToFind) {
        List<Movie> movies =  movieRepository.findByTitleLikeIgnoreCase(String.format("%%%s%%", movieName));
        return movies.subList(0,Math.min(numMoviesToFind, movies.size()));
    }

    @Transactional(readOnly = true)
    public List<Movie> recommendMovies(List<Integer> ids, int numMoviesToRecommend) {
        final double[] averageRelevanceVector = calculateAverageRelevancyVector(ids);

        // Will be ordered closest (index 0) to nth furthest (index n-1)
        final List<Pair<Integer, Double>> recommendedMovies = new ArrayList<>(numMoviesToRecommend);

        final int numTagsPerMovie = averageRelevanceVector.length;


        // first Integer is the movie ID, second is the running total of the distance between the averageRelevanceVector
        // and the Relevance vector of the movie currently being processed
        Pair<Integer, Double> currentMovieData = new Pair<>(-1, 0.0);

        try (Stream<GenomeScore> stream = genomeScoreRepository.streamAllOrderedByIdMovieidAscIdTagidAsc()) {
            stream.forEach(genomeScore -> {
                if(ids.contains(genomeScore.getId().getMovieid()))
                    return;
                currentMovieData.setKey(genomeScore.getId().getMovieid());
                currentMovieData.setValue(
                        currentMovieData.getValue() + Math.pow(averageRelevanceVector[genomeScore.getId().getTagid()-1] - genomeScore.getRelevance(),2));

                if (genomeScore.getId().getTagid() >= numTagsPerMovie) {
                    if (recommendedMovies.size() < numMoviesToRecommend || currentMovieData.getValue() < recommendedMovies.getLast().getValue()) {
                        Pair<Integer, Double> movieRecommendation = new Pair<>(currentMovieData.getKey(),  currentMovieData.getValue());
                        int insertPos = 0;
                        for (insertPos = 0; insertPos < recommendedMovies.size(); insertPos++) {
                            if (movieRecommendation.getValue() < recommendedMovies.get(insertPos).getValue()) {
                                break;
                            }
                        }
                        recommendedMovies.add(insertPos, movieRecommendation);
                        if (recommendedMovies.size() > numMoviesToRecommend)
                            recommendedMovies.removeLast();
                    }
                    currentMovieData.setValue(0.0);
                    entityManager.clear();
                }
            });
            entityManager.clear();
        }

        // Change list of pairs to list of only the movie IDs - remove the distances
        List<Movie> movieRecommendations = new ArrayList<>(recommendedMovies.size());
        for (int i = 0; i < recommendedMovies.size(); i++) {
            movieRecommendations.add(movieRepository.findByMovieid(recommendedMovies.get(i).getKey()));
        }

        return movieRecommendations;
    }

    private double[] calculateAverageRelevancyVector(List<Integer> ids) {
        final int numSelectedMovies = ids.size();

        final List<double[]> selectedMovieRelevanceVectors = new ArrayList<>(numSelectedMovies);

        for (int i = 0; i < numSelectedMovies; i++) {
            double[] movieRelevanceVector = movieGenomeScoresToVector(genomeScoreRepository.findByIdMovieid(ids.get(i)));
            selectedMovieRelevanceVectors.add(movieRelevanceVector);
        }

        for (int i = 0; i < selectedMovieRelevanceVectors.size(); i++) {
            if (selectedMovieRelevanceVectors.get(i).length == 0)
                selectedMovieRelevanceVectors.remove(i);
        }

        final double[] averageRelevanceVector = new double[selectedMovieRelevanceVectors.getFirst().length];

        for (int i = 0; i < numSelectedMovies; i++) {
            for (int j = 0; j < averageRelevanceVector.length; j++) {
                averageRelevanceVector[j] += selectedMovieRelevanceVectors.get(i)[j];
            }
        }

        for (int j = 0; j < averageRelevanceVector.length; j++) {
            averageRelevanceVector[j] /= numSelectedMovies;
        }

        return averageRelevanceVector;
    }

    private double getSquaredDistanceBetweenVectors(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length)
            throw new IllegalArgumentException();

        double squaredDistance = 0;
        for (int i = 0; i < vec1.length; i++) {
            squaredDistance += Math.pow((vec1[i] - vec2[i]), 2);
        }
        return squaredDistance;
    }

    private double[] movieGenomeScoresToVector(List<GenomeScore> scores) {
        double[] tagVector = new double[scores.size()];

        for (int i = 0; i < scores.size(); i++) {
            tagVector[i] = scores.get(i).getRelevance();
        }
        return tagVector;
    }

    public Movie getMovie(int id) {
        return movieRepository.findByMovieid(id);
    }

    public IdLink getMovieIdLinks(int id) {
        return idLinkRepository.findByMovieid(id);
    }

    public GenomeScore getMovieGenomeScore(int movieid, int tagid) {
        return genomeScoreRepository.findById(new GenomeScoreKey(movieid, tagid)).orElse(new GenomeScore());
    }

    public GenomeTag getMovieGenomeTag(int tagid) {
        return genomeTagRepository.findByTagid(tagid);
    }

//    @Transactional(readOnly = true)
//    public List<Movie> recommendMovies(List<Integer> ids, int numMoviesToRecommend) {
//        final double[] averageRelevanceVector = calculateAverageRelevancyVector(ids);
//
//        // Will be ordered closest (index 0) to nth furthest (index n-1)
//        final List<Pair<Integer, Double>> recommendedMovies = new ArrayList<>(numMoviesToRecommend);
//
//        final int numTagsPerMovie = averageRelevanceVector.length;
//
//        Pair<Integer, double[]> currentMovieData = new Pair<>(-1, new double[numTagsPerMovie]);
//
//        try (Stream<GenomeScore> stream = genomeScoreRepository.streamAllOrderedByIdMovieidAscIdTagidAsc()) {
//            stream.forEach(genomeScore -> {
//                currentMovieData.setKey(genomeScore.getId().getMovieid());
//                currentMovieData.getValue()[genomeScore.getId().getTagid() - 1] = genomeScore.getRelevance();
//
//                if (genomeScore.getId().getTagid() >= numTagsPerMovie) {
//                    double movieClosenessToSelectedMovies = getSquaredDistanceBetweenVectors(currentMovieData.getValue(), averageRelevanceVector);
//
//                    if (recommendedMovies.size() < numMoviesToRecommend || movieClosenessToSelectedMovies < recommendedMovies.getLast().getValue()) {
//                        Pair<Integer, Double> movieRecommendation = new Pair<>(currentMovieData.getKey(), movieClosenessToSelectedMovies);
//                        int insertPos = 0;
//                        for (insertPos = 0; insertPos < recommendedMovies.size(); insertPos++) {
//                            if (movieRecommendation.getValue() < recommendedMovies.get(insertPos).getValue()) {
//                                break;
//                            }
//                        }
//                        recommendedMovies.add(insertPos, movieRecommendation);
//                        if (recommendedMovies.size() > numMoviesToRecommend)
//                            recommendedMovies.removeLast();
//                    }
//                    entityManager.clear();
//                }
//            });
//            entityManager.clear();
//        }
//
//        // Change list of pairs to list of only the movie IDs - remove the distances
//        List<Movie> movieRecommendations = new ArrayList<>(recommendedMovies.size());
//        for (int i = 0; i < recommendedMovies.size(); i++) {
//            movieRecommendations.add(movieRepository.findByMovieid(recommendedMovies.get(i).getKey()));
//        }
//
//        return movieRecommendations;
//    }

}
