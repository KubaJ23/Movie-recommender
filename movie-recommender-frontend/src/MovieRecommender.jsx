import React, { useState } from "react";
import MovieCard from "./MovieCard.jsx";

const currentIP = "192.168.0.7";

export default function MovieRecommender({ movies }) {
  const [recommendations, setRecommendations] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  async function recommendMovies() {
    const queryParams = movies
      .map((movie) => {
        return `id=${movie.movieid}&`;
      })
      .join("");

    setIsLoading(true);
    const recommendationsResponse = await fetch(
      `http://${currentIP}:8080/movies/recommend?${queryParams}getNumMovies=${20}`
    );
    setIsLoading(false);
    const recommendations = await recommendationsResponse.json();

    setRecommendations(recommendations);
  }

  return (
    <div id="recommendedMoviesContainer" className="container">
      <div className="topContainer">
        <button type="button" className="searchBtn" onClick={recommendMovies}>
          Recommend movies
        </button>
      </div>
      <div id="resultsContainer" className="movieContainer">
        {isLoading ? (
          <img
            src="../loading-gear.svg"
            alt="Recommendations are loading"
            className="spinner"
          />
        ) : (
          recommendations.map((movie, index) => {
            return (
              <div key={index}>
                <MovieCard movie={movie} />
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}
