package com.darurats.popularmovies.services;

import com.darurats.popularmovies.BuildConfig;
import com.darurats.popularmovies.utils.MovieConstants;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

public class MovieService {

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(MovieConstants.API.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl url = original.url()
                        .newBuilder()
                        .addQueryParameter(MovieConstants.API.APP_KEY_QUERY_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                        .build();
                Request request = original.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}