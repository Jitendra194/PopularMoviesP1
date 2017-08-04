package com.example.android.popularmoviespart1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmoviespart1.data.MoviesContract.MoviesEntry;


class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 14;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                        MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                        MoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_MOVIE_DURATION + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_MOVIE_BACKDROP_IMAGE_PATH + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_MOVIE_VIDEO_NAME + " TEXT, " +
                        MoviesEntry.COLUMN_MOVIE_VIDEO_LINK_ID + " TEXT, " +
                        MoviesEntry.COLUMN_MOVIE_REVIEW + " TEXT, " +
                        MoviesEntry.COLUMN_MOVIE_REVIEW_AUTHOR + " TEXT, " +
                        MoviesEntry.COLUMN_MOVIE_REVIEW_LINK + " TEXT, " +
                        " UNIQUE (" + MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
