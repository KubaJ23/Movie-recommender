# Movie-recommender
My full stack application for a website where you can search for movies, select a collection of movies to then get recommended 10 movies that are similar to your collection based on hundreds of different tags that identify features of a movie.

The Backend code was written in Java using the Swing framework. The database containing the movie data was a relational PostgreSQL database.
The Frontend code was written in JS using react.

The biggest challenge in this project was to find the recommendations given a selection of movies since it required processing the very large dataset of movies and their corresponding tags that I used for this project. I was able to optimize the code using methods such as pagination and streaming to find the movie recommendations in a reasonable amount of time for the user.
