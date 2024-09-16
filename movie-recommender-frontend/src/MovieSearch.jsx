import React, { useState } from "react";
import MovieCard from "./MovieCard.jsx";

const currentIP = "192.168.0.7";

export default function MovieSearch({ addMovie, exitSearch }) {
  const [searchResult, setSearchResult] = useState([]);

  const searchBarStyle = {};

  function handleMovieClick(movie) {
    addMovie(movie);
    exitSearch();
  }

  async function searchForMovie(e) {
    if (
      e.target === document.querySelector("#searchButton") ||
      e.key === "Enter"
    ) {
      const movieName = document.querySelector(".movieSearch").value;

      // fetch movies from API as JSON
      const response = await fetch(
        `http://${currentIP}:8080/movies/search?name=` +
          movieName +
          "&numMovies=20"
      );
      const moviesFound = await response.json();

      setSearchResult(moviesFound);
    }
  }

  return (
    <div>
      <div style={searchBarStyle}>
        <input
          className="movieSearch"
          type="text"
          placeholder="Movie Name..."
          onKeyDown={searchForMovie}
        />
        <button type="button" id="searchButton" onClick={searchForMovie}>
          Search
        </button>
      </div>
      <div className="movieContainer">
        {searchResult.map((movie, index) => {
          return (
            <div key={index} onClick={() => handleMovieClick(movie)}>
              <MovieCard movie={movie} />
            </div>
          );
        })}
      </div>
    </div>
  );
}
