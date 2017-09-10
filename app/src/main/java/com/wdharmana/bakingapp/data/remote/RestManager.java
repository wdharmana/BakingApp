package com.wdharmana.bakingapp.data.remote;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestManager {

    private NetworkService mAPIService;

    public NetworkService getAPIService() {
        if (mAPIService == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(NetworkService.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mAPIService = retrofit.create(NetworkService.class);
        }
        return mAPIService;
    }

}
