package com.example.android.popularmoviespart1.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmoviespart1";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final String PATH_MOVIE_ID = "movie_id";

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        public static final String COLUMN_MOVIE_RATING = "movie_rating";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";

        public static final String COLUMN_MOVIE_SYNOPSIS = "movie_synopsis";

        public static final String COLUMN_MOVIE_DURATION = "movie_duration";

        public static final String COLUMN_MOVIE_POSTER_IMAGE_PATH = "movie_poster_image_path";

        public static final String COLUMN_MOVIE_BACKDROP_IMAGE_PATH = "movie_backdrop_image_path";

        public static final String COLUMN_MOVIE_VIDEO_NAME = "movie_video_name";

        public static final String COLUMN_MOVIE_VIDEO_LINK_ID = "movie_video_link_id";

        public static final String COLUMN_MOVIE_REVIEW = "movie_review";

        public static final String COLUMN_MOVIE_REVIEW_AUTHOR = "movie_review_author";

        public static final String COLUMN_MOVIE_REVIEW_LINK = "movie_review_link";


        public static final Uri CONTENT_MOVIE_ID_URI =
                CONTENT_URI.buildUpon().appendPath(String.valueOf(COLUMN_MOVIE_ID)).build();
    }
}
