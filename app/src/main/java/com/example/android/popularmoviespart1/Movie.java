package com.example.android.popularmoviespart1;

import java.io.Serializable;


public class Movie implements Serializable {

    private final String mTitle;
    private final String mPosterUrl;
    private final Double mMovieRating;
    private final String mLargePosterUrl;
    private final String mDate;
    private final String mOverview;

    public Movie(String title, String posterUrl, Double movieRating,
                 String largePosterUrl, String date, String overview) {
        mTitle = title;
        mPosterUrl = posterUrl;
        mMovieRating = movieRating;
        mLargePosterUrl = largePosterUrl;
        mDate = date;
        mOverview = overview;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public Double getMovieRating() {
        return mMovieRating;
    }

    public String getLargePosterUrl() {
        return mLargePosterUrl;
    }

    public String getDate() {
        return mDate;
    }

    public String getOverview() {
        return mOverview;
    }
}
