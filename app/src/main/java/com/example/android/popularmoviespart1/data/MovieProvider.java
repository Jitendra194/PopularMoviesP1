package com.example.android.popularmoviespart1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmoviespart1.data.MoviesContract.MoviesEntry;


public class MovieProvider extends ContentProvider {

    private static final String TAG = MovieProvider.class.getSimpleName();

    private static final int CODE_MOVIE = 100;
    private static final int CODE_MOVIE_WITH_ID = 101;
    private static final int CODE_MOVIE_WITH_MOVIEID = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIE, CODE_MOVIE);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);
        uriMatcher.addURI(authority,
                MoviesContract.PATH_MOVIE + "/" + MoviesContract.PATH_MOVIE_ID + "/#",
                CODE_MOVIE_WITH_MOVIEID);

        return uriMatcher;
    }

    private MovieDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_MOVIE:
                cursor = db.query(MoviesEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1);
                break;
            case CODE_MOVIE_WITH_MOVIEID:
                String movieId = uri.getLastPathSegment();
                String selection = MoviesEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArguments = new String[]{movieId};
                cursor = db.query(MoviesEntry.TABLE_NAME,
                        strings,
                        selection,
                        selectionArguments,
                        null,
                        null,
                        s1);
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id;
        switch (match) {
            case CODE_MOVIE:
                id = database.insert(MoviesEntry.TABLE_NAME, null, contentValues);
                Log.v(TAG, String.valueOf(id));
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_MOVIE:
                rowsDeleted = db.delete(MoviesEntry.TABLE_NAME, s, strings);
                break;
            case CODE_MOVIE_WITH_MOVIEID:
                String movieId = uri.getLastPathSegment();
                String selection = MoviesEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArguments = new String[]{movieId};
                rowsDeleted = db.delete(MoviesEntry.TABLE_NAME, selection, selectionArguments);
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case CODE_MOVIE_WITH_MOVIEID:
                String movieId = uri.getLastPathSegment();
                String selection = MoviesEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArguments = new String[]{movieId};
                rowsUpdated = db.update(MoviesEntry.TABLE_NAME, contentValues, selection, selectionArguments);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
