package com.example.android.popularmoviespart1.MoviesData;

import com.example.android.popularmoviespart1.utilities.NetworkUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@SuppressWarnings({"CanBeFinal", "unused"})
public class Movie implements Serializable{

    @SerializedName("original_title")
    private String mTitle;
    @SerializedName("poster_path")
    private String mPosterUrl;
    @SerializedName("vote_average")
    private Double mMovieRating;
    @SerializedName("backdrop_path")
    private String mLargePosterUrl;
    @SerializedName("release_date")
    private String mDate;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("id")
    private int mId;

    public String getTitle() {
        return mTitle;
    }

    public String getPosterUrl() {
        return (NetworkUtils.IMAGE_URL + mPosterUrl);
    }

    public Double getMovieRating() {
        return mMovieRating;
    }

    public int getId() {
        return mId;
    }
}
