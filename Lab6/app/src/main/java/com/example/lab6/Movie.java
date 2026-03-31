package com.example.lab6;

import java.util.List;

public class Movie {
    public int id;
    public String title;
    public String original_title;
    public String overview;
    public double vote_average;
    public int vote_count;
    public String poster_path;
    public String backdrop_path;
    public String release_date;
    public String original_language;
    public boolean adult;

    public List<Genre> genres;

    public String tagline;
    public int runtime;

    public static class Genre {
        public int id;
        public String name;
    }
}