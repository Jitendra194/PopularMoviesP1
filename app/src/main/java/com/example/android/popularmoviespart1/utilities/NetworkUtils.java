package com.example.android.popularmoviespart1.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String POPULAR_MOVIES =
            "https://api.themoviedb.org/3/movie/popular";

    private static final String TOP_RATED_MOVIES =
            "https://api.themoviedb.org/3/movie/top_rated";

    private static String BASE_URL = POPULAR_MOVIES;

    private static final String KEY = "13ea7a503b57a536f20b8b3f79d3363b";  // INSERT YOUR API KEY HERE //
    private static final String LANG = "en-US";
    private static final String PAGE = "page";

    private static final String API_KEY_PARAM = "api_key";
    private static final String LANG_PARAM = "language";

    public static final int POPULAR_MOVIES_MODE = 0;
    public static final int TOP_RATED_MOVIES_MODE = 1;


    public static URL buildURL(int page) {
        String PAGE_VALUE = String.valueOf(page);
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, KEY)
                .appendQueryParameter(LANG_PARAM, LANG)
                .appendQueryParameter(PAGE, PAGE_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "URL = " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static void chooseMode(int mode) {
        if (mode == POPULAR_MOVIES_MODE) {
            BASE_URL = POPULAR_MOVIES;
        } else if (mode == TOP_RATED_MOVIES_MODE) {
            BASE_URL = TOP_RATED_MOVIES;
        }
    }
}
