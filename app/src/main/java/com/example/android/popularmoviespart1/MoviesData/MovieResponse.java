package com.example.android.popularmoviespart1.MoviesData;

import com.example.android.popularmoviespart1.utilities.NetworkUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class MovieResponse {

    @SuppressWarnings("CanBeFinal")
    @SerializedName("backdrop_path")
    private String backdrop_path;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("belongs_to_collection")
    private Belongs_to_collection belongs_to_collection;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("budget")
    private int budget;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("genres")
    private List<Genres> genres;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("homepage")
    private String homepage;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("imdb_id")
    private String imdb_id;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("original_language")
    private String original_language;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("original_title")
    private String original_title;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("overview")
    private String overview;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("popularity")
    private double popularity;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("poster_path")
    private String poster_path;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("production_companies")
    private List<Production_companies> production_companies;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("production_countries")
    private List<Production_countries> production_countries;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("release_date")
    private String release_date;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("revenue")
    private int revenue;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("runtime")
    private int runtime;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("spoken_languages")
    private List<Spoken_languages> spoken_languages;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("status")
    private String status;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("tagline")
    private String tagline;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("title")
    private String title;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("video")
    private boolean video;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("vote_average")
    private double vote_average;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("vote_count")
    private int vote_count;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("id")
    private int id;
    @SerializedName("page")
    public int page;
    @SerializedName("total_pages")
    public int total_pages;
    @SerializedName("total_results")
    public int total_results;

    @SuppressWarnings("unused")
    public static class Belongs_to_collection {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("poster_path")
        public String poster_path;
        @SerializedName("backdrop_path")
        public String backdrop_path;
    }

    @SuppressWarnings("unused")
    private static class Genres {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
    }

    @SuppressWarnings("unused")
    private static class Production_companies {
        @SerializedName("name")
        public String name;
        @SerializedName("id")
        public int id;
    }

    @SuppressWarnings("unused")
    private static class Production_countries {
        @SerializedName("iso_3166_1")
        public String iso_3166_1;
        @SerializedName("name")
        public String name;
    }

    @SuppressWarnings("unused")
    private static class Spoken_languages {
        @SerializedName("iso_639_1")
        public String iso_639_1;
        @SerializedName("name")
        public String name;
    }

    public String getBackdrop_path() {
        return (NetworkUtils.LARGE_IMAGE_URL + backdrop_path);
    }

    public Belongs_to_collection getBelongs_to_collection() {
        return belongs_to_collection;
    }

    public int getBudget() {
        return budget;
    }

    public List<Genres> getGenres() {
        return genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public int getId() {
        return id;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getPoster_path() {
        return (NetworkUtils.IMAGE_URL + poster_path);
    }

    public List<Production_companies> getProduction_companies() {
        return production_companies;
    }

    public List<Production_countries> getProduction_countries() {
        return production_countries;
    }

    public String getRelease_date() {
        return release_date;
    }

    public int getRevenue() {
        return revenue;
    }

    public String getRuntime() {
        return (runtime + " min");
    }

    public List<Spoken_languages> getSpoken_languages() {
        return spoken_languages;
    }

    public String getStatus() {
        return status;
    }

    public String getTagline() {
        return tagline;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    @SuppressWarnings("CanBeFinal")
    @SerializedName("results")
    private ArrayList<Results> results;

    @SuppressWarnings("unused")
    public static class Results {
        @SerializedName("id")
        public String id;

        //video variables
        @SerializedName("iso_639_1")
        public String iso_639_1;
        @SerializedName("iso_3166_1")
        public String iso_3166_1;
        @SuppressWarnings("CanBeFinal")
        @SerializedName("key")
        public String key;
        @SuppressWarnings("CanBeFinal")
        @SerializedName("name")
        public String name;
        @SerializedName("site")
        public String site;
        @SerializedName("size")
        public int size;
        @SerializedName("type")
        public String type;

        //review variables
        @SuppressWarnings("CanBeFinal")
        @SerializedName("author")
        public String author;
        @SuppressWarnings("CanBeFinal")
        @SerializedName("content")
        public String content;
        @SuppressWarnings("CanBeFinal")
        @SerializedName("url")
        public String url;

        public String getKey() {
            return key;
        }
    }

    public ArrayList<Results> getResults() {
        return results;
    }
}
