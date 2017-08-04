package com.example.android.popularmoviespart1.utilities;

public final class NetworkUtils {

    public static final String POPULAR_MOVIES =
            "movie/popular";

    public static final String TOP_RATED_MOVIES =
            "movie/top_rated";

    public static final String MOVIE_DETAIL =
            "movie/{movie_id}";

    public static final String MOVIE_VIDEOS =
            "movie/{movie_id}/videos";

    public static final String MOVIE_REVIEWS =
            "movie/{movie_id}/reviews";

    public static final String YOUTUBE_VIDEO_LINK =
            "https://www.youtube.com/watch?v=";

    public static final String BASE_URL = "http://api.themoviedb.org/3/";

    public static final String LANG = "en-US";
    public static final String PAGE = "page";

    public static final String API_KEY_PARAM = "api_key";
    public static final String LANG_PARAM = "language";

    public static final String MOVIE_ID_PATH = "movie_id";

    public static final int POPULAR_MOVIES_MODE = 1;
    public static final int TOP_RATED_MOVIES_MODE = 2;
    public static final int FAVORITE_MOVIES_MODE = 3;


    public static final String IMAGE_URL =
            "https://image.tmdb.org/t/p/w185";

    public static final String LARGE_IMAGE_URL =
            "https://image.tmdb.org/t/p/w500";

}
