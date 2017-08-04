package com.example.android.popularmoviespart1.utilities;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmoviespart1.MainActivity;
import com.example.android.popularmoviespart1.MoviesData.Movie;
import com.example.android.popularmoviespart1.MoviesData.MoviesResponse;
import com.example.android.popularmoviespart1.retrofitutilities.ApiClient;
import com.example.android.popularmoviespart1.retrofitutilities.ApiService;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;


@SuppressWarnings("unused")
public class MoviesLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private static final String TAG = MoviesLoader.class.getSimpleName();

    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
    private Context mContext;
    private static int mPage;

    private ArrayList<Movie> mMovies;

    public MoviesLoader(Context context, int page) {
        super(context);
        mContext = context;
        mPage = page;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mMovies != null) {
            Log.v(TAG, "delivered result@@@@@@@@@@@@@@@@@@@@@@@@@");
            deliverResult(mMovies);
        } else {
            forceLoad();
        }
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        ApiService service = ApiClient.getClient().create(ApiService.class);
        Call<MoviesResponse> call;
        int mode = MainActivity.MOVIE_LOADER_ID;
        switch (mode) {
            case NetworkUtils.POPULAR_MOVIES_MODE:
                call = service
                        .getPopularMovies("KEY",
                                NetworkUtils.LANG, String.valueOf(mPage));
                break;
            case NetworkUtils.TOP_RATED_MOVIES_MODE:
                call = service
                        .getTopRatedMovies("KEY",
                                NetworkUtils.LANG, String.valueOf(mPage));
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader ID:" + mode);
        }
        try {
            return call.execute().body().getResults();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(ArrayList<Movie> data) {
        mMovies = data;
        super.deliverResult(data);
    }
}
