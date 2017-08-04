package com.example.android.popularmoviespart1.utilities;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmoviespart1.DetailsActivity;
import com.example.android.popularmoviespart1.MoviesData.MovieResponse;
import com.example.android.popularmoviespart1.retrofitutilities.ApiClient;
import com.example.android.popularmoviespart1.retrofitutilities.ApiService;

import java.io.IOException;

import retrofit2.Call;

@SuppressWarnings("unused")
public class MovieLoader extends AsyncTaskLoader<MovieResponse> {

    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
    private Context mContext;
    @SuppressWarnings("CanBeFinal")
    private int mMovieId;
    private MovieResponse movieResponse;

    public MovieLoader(Context context, Bundle args) {
        super(context);
        mContext = context;
        mMovieId = args.getInt("MovieId");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (movieResponse != null) {
            deliverResult(movieResponse);
        } else {
            forceLoad();
        }
    }


    @Override
    public MovieResponse loadInBackground() {
        ApiService service = ApiClient.getClient().create(ApiService.class);
        Log.v("LOADER", String.valueOf(getId()));

        Call<MovieResponse> call = null;

        if (getId() == DetailsActivity.MOVIE_DETAIL_ID) {
            call = service.getMovieDetail(mMovieId, "KEY", NetworkUtils.LANG);
        } else if (getId() == DetailsActivity.MOVIE_VIDEO_ID) {
            call = service.getVideos(mMovieId, "KEY", NetworkUtils.LANG);
        } else if (getId() == DetailsActivity.MOVIE_REVIEW_ID) {
            call = service.getReviews(mMovieId, "KEY", NetworkUtils.LANG);
        }

        try {
            if (call != null) {
                return call.execute().body();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("load error!", "load in background error");
            return null;
        }
    }

    @Override
    public void deliverResult(MovieResponse data) {
        movieResponse = data;
        super.deliverResult(data);
    }
}
