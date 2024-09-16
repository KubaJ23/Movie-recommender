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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public List<Movie> searchForMovie(String movieName) {
        return movieRepository.findByTitleLikeIgnoreCase(String.format("%%%s%%", movieName));
    }

    public List<Movie> recommendMovies(List<Integer> ids, int numMoviesToRecommend) {
        double[] averageRelevanceVector = calculateAverageRelevancyVector(ids);

        // Will be ordered closest (index 0) to nth furthest (index n-1)
        final List<Pair<Integer, Double>> recommendedMovies = new ArrayList<>(numMoviesToRecommend);

        int numTagsPerMovie = averageRelevanceVector.length;
        int numRelevanceVectorsToFetch = 4000;

        int pageSize = numTagsPerMovie * numRelevanceVectorsToFetch; // How many movie relevance vectors to be in each page
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<GenomeScore> genomeScorePage;


        double[] relevanceVector = new double[numTagsPerMovie];
        List<GenomeScore> genomeScores;

        do {
            genomeScorePage = genomeScoreRepository.findAllByOrderByIdMovieidAscIdTagidAsc(pageable);
            genomeScores = genomeScorePage.getContent();

            for (int i = 0; i < genomeScores.size(); i += numTagsPerMovie) {
                GenomeScore genomeScore = genomeScores.get(i);

                int movieId = genomeScore.getId().getMovieid();

                if(ids.contains(movieId))
                    continue;

                for (int j = 0; j < numTagsPerMovie; j++) {
                    relevanceVector[j] = genomeScores.get(i + j).getRelevance();
                }

                double movieClosenessToSelectedMovies = getSquaredDistanceBetweenVectors(relevanceVector, averageRelevanceVector);

                if (recommendedMovies.size() < numMoviesToRecommend || movieClosenessToSelectedMovies < recommendedMovies.getLast().getValue()) {
                    Pair<Integer, Double> movieRecommendation = new Pair<>(movieId, movieClosenessToSelectedMovies);
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
            }
            genomeScores= null;
            pageable = genomeScorePage.nextPageable();
            entityManager.clear();

        } while (genomeScorePage.hasNext());

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


//    public List<Movie> recommendMovies(List<Integer> ids, int numMoviesToRecommend) {
//        double[] averageRelevanceVector = calculateAverageRelevancyVector(ids);
//
//        // Will be ordered closest (index 0) to nth furthest (index n-1)
//        final List<Pair<Integer, Double>> recommendedMovies = new ArrayList<>(numMoviesToRecommend);
//
//        int numTagsPerMovie = averageRelevanceVector.length;
//        int numRelevanceVectorsToFetch = 10;
//
//        int pageNumber = 0;
//        int pageSize = numTagsPerMovie * numRelevanceVectorsToFetch; // How many movie relevance vectors to be in each page
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        Page<GenomeScore> genomeScorePage;
//
//        HashMap<Integer, Pair<Integer,Double[]>> movieRelevanceVectors = new HashMap<>();
//
//        do{
//            genomeScorePage = genomeScoreRepository.findAllByOrderByIdMovieidAscIdTagidAsc(pageable);
//            List<GenomeScore> genomeScores = genomeScorePage.getContent();
//
//            for (GenomeScore genomeScore : genomeScores) {
//                int movieId = genomeScore.getId().getMovieid();
//                int tagId = genomeScore.getId().getTagid();
//                double tagRelevance = genomeScore.getRelevance();
//
//                if (!movieRelevanceVectors.containsKey(movieId)) {
//                    Double[] relevanceVectors = new Double[numTagsPerMovie];
//                    movieRelevanceVectors.put(movieId, new Pair<>(numTagsPerMovie,relevanceVectors));
//                }
//                movieRelevanceVectors.get(movieId).getValue()[tagId-1] = tagRelevance;
//                movieRelevanceVectors.get(movieId).setKey(movieRelevanceVectors.get(movieId).getKey() - 1);
//
//                if (movieRelevanceVectors.get(movieId).getKey() <= 0){
//                    double movieClosenessToSelectedMovies = getSquaredDistanceBetweenVectors(movieRelevanceVectors.get(movieId).getValue(), averageRelevanceVector);
//
//                    if (recommendedMovies.size() >= numMoviesToRecommend && movieClosenessToSelectedMovies >= recommendedMovies.getLast().getValue()) {
//
//                    }else {
//                        Pair<Integer, Double> movieRecommendation = new Pair<>(movieId, movieClosenessToSelectedMovies);
//
//                        int insertPos = 0;
//                        for (insertPos = 0; insertPos < recommendedMovies.size(); insertPos++) {
//                            if (movieRecommendation.getValue() < recommendedMovies.get(insertPos).getValue()) {
//                                break;
//                            }
//                        }
//
//                        recommendedMovies.add(insertPos, movieRecommendation);
//                        if (recommendedMovies.size() > numMoviesToRecommend)
//                            recommendedMovies.removeLast();
//                    }
//                }
//            }
//
//            movieRelevanceVectors.clear();
//            pageable = genomeScorePage.nextPageable();
//
//        }while(genomeScorePage.hasNext());
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
