package com.example.android.popularmoviespart1.utilities;

import android.util.Log;

import com.example.android.popularmoviespart1.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();
    private static final String STATUS_CODE = "status_code";
    private static final String STATUS_MESSAGE = "status_message";

    private static final String RESULTS_ARRAY = "results";

    private static final String MOVIE_TITLE = "title";

    private static final String MOVIE_RATING = "vote_average";

    private static final String MOVIE_POSTER = "poster_path";

    private static final String RELEASE_DATE = "release_date";

    private static final String MOVIE_POSTER_LARGE = "backdrop_path";

    private static final String MOVIE_OVERVIEW = "overview";

    private static final String IMAGE_URL =
            "https://image.tmdb.org/t/p/w185";

    private static final String LARGE_IMAGE_URL =
            "https://image.tmdb.org/t/p/w500";

    public static ArrayList<Movie> convertJSONToData(String jsonResponse) throws JSONException {

        ArrayList<Movie> movies = new ArrayList<>();
        String errorMessage;

        JSONObject object = new JSONObject(jsonResponse);

        if (object.has(STATUS_CODE)) {
            int code = object.getInt(STATUS_CODE);
            switch (code) {
                case 7:
                    errorMessage = object.getString(STATUS_MESSAGE);
                    Log.e(TAG, errorMessage);
                    return null;
                case 34:
                    errorMessage = object.getString(STATUS_MESSAGE);
                    Log.e(TAG, errorMessage);
                    return null;
                default:
                    return null;
            }
        }

        JSONArray resultsArray = object.getJSONArray(RESULTS_ARRAY);

        String title = "";
        Double rating = null;
        String posterURL = "";
        String largePosterURL = "";
        String date = "";
        String overview = "";

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject currentMovie = resultsArray.getJSONObject(i);

            if (currentMovie.has(MOVIE_TITLE)) {
                title = currentMovie.getString(MOVIE_TITLE);
            }

            if (currentMovie.has(MOVIE_RATING)) {
                rating = currentMovie.getDouble(MOVIE_RATING);
            }

            if (currentMovie.has(MOVIE_POSTER)) {
                posterURL = IMAGE_URL + currentMovie.getString(MOVIE_POSTER);
            }

            if (currentMovie.has(MOVIE_POSTER_LARGE)) {
                largePosterURL = LARGE_IMAGE_URL + currentMovie.getString(MOVIE_POSTER_LARGE);
            }

            if (currentMovie.has(RELEASE_DATE)) {
                date = currentMovie.getString(RELEASE_DATE);
            }

            if (currentMovie.has(MOVIE_OVERVIEW)) {
                overview = currentMovie.getString(MOVIE_OVERVIEW);
            }

            movies.add(new Movie(title, posterURL, rating, largePosterURL, date, overview));
        }
        return movies;
    }
}
