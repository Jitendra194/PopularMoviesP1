package com.example.android.popularmoviespart1.retrofitutilities;

import com.example.android.popularmoviespart1.MoviesData.MovieResponse;
import com.example.android.popularmoviespart1.MoviesData.MoviesResponse;
import com.example.android.popularmoviespart1.utilities.NetworkUtils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jiten on 7/18/2017.
 */

@SuppressWarnings("ALL")
public interface ApiService {

    @GET(NetworkUtils.POPULAR_MOVIES)
    Call<MoviesResponse> getPopularMovies(@Query(NetworkUtils.API_KEY_PARAM) String key,
                                          @Query(NetworkUtils.LANG_PARAM) String language,
                                          @Query(NetworkUtils.PAGE) String page);

    @GET(NetworkUtils.TOP_RATED_MOVIES)
    Call<MoviesResponse> getTopRatedMovies(@Query(NetworkUtils.API_KEY_PARAM) String key,
                                           @Query(NetworkUtils.LANG_PARAM) String language,
                                           @Query(NetworkUtils.PAGE) String page);

    @GET(NetworkUtils.MOVIE_DETAIL)
    Call<MovieResponse> getMovieDetail(@Path(NetworkUtils.MOVIE_ID_PATH) int movieId,
                                       @Query(NetworkUtils.API_KEY_PARAM) String key,
                                       @Query(NetworkUtils.LANG_PARAM) String language);

    @GET(NetworkUtils.MOVIE_VIDEOS)
    Call<MovieResponse> getVideos(@Path(NetworkUtils.MOVIE_ID_PATH) int movieId,
                                  @Query(NetworkUtils.API_KEY_PARAM) String key,
                                  @Query(NetworkUtils.LANG_PARAM) String language);

    @GET(NetworkUtils.MOVIE_REVIEWS)
    Call<MovieResponse> getReviews(@Path(NetworkUtils.MOVIE_ID_PATH) int movieId,
                                   @Query(NetworkUtils.API_KEY_PARAM) String key,
                                   @Query(NetworkUtils.LANG_PARAM) String language);
}
