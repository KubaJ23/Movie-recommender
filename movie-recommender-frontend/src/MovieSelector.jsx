import React, { useState } from "react";
import MovieSearch from "./MovieSearch.jsx";
import MovieCard from "./MovieCard.jsx";

export default function MovieSelector({ movies, setMovies }) {
  function handleOpenModal() {
    document.querySelector("[data-modal]").showModal();
  }

  function handleCloseModal() {
    document.querySelector("[data-modal]").close();
  }

  function removeMovie(index) {
    setMovies((m) => m.filter((item, i) => i !== index));
  }
  function addMovie(movie) {
    const movieAlreadyExists =
      movies.filter((item, index) => item.movieid === movie.movieid).length > 0;
    if (!movieAlreadyExists) setMovies((m) => [...m, movie]);
  }

  return (
    <div id="selectMoviesContainer" className="container">
      <p>Selected movies:</p>
      <div id="selectedMovies" className="movieContainer">
        {movies.map((movie, index) => {
          return (
            <div key={index} onClick={() => removeMovie(index)}>
              <MovieCard movie={movie} />
            </div>
          );
        })}
      </div>
      <dialog data-modal>
        <MovieSearch addMovie={addMovie} exitSearch={handleCloseModal} />
        <button onClick={handleCloseModal}>Close</button>
      </dialog>
      <button
        id="addMovieBtn"
        type="button"
        className="addBtn"
        onClick={handleOpenModal}
      >
        + Add movie
      </button>
    </div>
  );
}
