package com.example.lab6;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("search/movie")
    Call<MovieResponse> searchMovie(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String append
    );
}