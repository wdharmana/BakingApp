package com.wdharmana.bakingapp.data.remote;

import com.wdharmana.bakingapp.data.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by dharmana on 7/13/17.
 */
public interface NetworkService {

    String ENDPOINT = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    @GET("baking.json")
    Call<List<Recipe>> recipe();

}

