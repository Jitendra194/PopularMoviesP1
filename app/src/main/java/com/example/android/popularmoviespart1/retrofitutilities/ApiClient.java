package com.example.android.popularmoviespart1.retrofitutilities;

import com.example.android.popularmoviespart1.utilities.NetworkUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static Retrofit mRetrofit = null;

    public static Retrofit getClient() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit
                    .Builder()
                    .baseUrl(NetworkUtils.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }
}
