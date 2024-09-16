import React, { useState } from "react";

const currentIP = "192.168.0.7";

export default function MovieCard({ movie }) {
  const [imgSrc, setImgSrc] = useState(null);

  async function getMovieImageURL() {
    const response = await fetch(
      `http://${currentIP}:8080/movies/link/` + movie.movieid
    );
    const idLink = await response.json();
    const tmdbID = idLink.tmdbid;
    const api_key = "97006b5c5c480349afb5c2a7182086f0";
    const imagesInfo = await fetch(
      `https://api.themoviedb.org/3/movie/${tmdbID}/images?api_key=` + api_key
    );
    const imagesInfoJSON = await imagesInfo.json();
    if (imagesInfoJSON.posters.length === 0) {
      setImgSrc("image-not-found.jpg");
      return;
    }
    const filePath = imagesInfoJSON.posters[0].file_path;
    setImgSrc("https://image.tmdb.org/t/p/w1280" + filePath);
  }
  getMovieImageURL();
  return (
    <>
      <img
        src={imgSrc}
        alt={"Movie Poster of " + movie.title}
        className="movieCardImage"
      ></img>
      <br />
      <div className="movieCardId">{"ID: " + movie.movieid}</div>
      <br />
      <div className="movieCardTitle">{"Title: " + movie.title}</div>
      <br />
      <div className="movieCardGenre">
        {"Genres: " + movie.genres.split("|").join(" ")}
      </div>
      <br />
    </>
  );
}
