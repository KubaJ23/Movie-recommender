import { useState } from "react";
import "./MovieSelector.jsx";
import MovieSelector from "./MovieSelector.jsx";
import MovieRecommender from "./MovieRecommender.jsx";

export default function App() {
  const [movies, setMovies] = useState([]);

  return (
    <div className="appContainer">
      <h1>Movie Recommender</h1>
      <MovieSelector movies={movies} setMovies={setMovies} />
      <MovieRecommender movies={movies} />
    </div>
  );
}
