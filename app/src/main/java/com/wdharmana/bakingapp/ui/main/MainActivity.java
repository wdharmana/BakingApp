package com.wdharmana.bakingapp.ui.main;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.wdharmana.bakingapp.R;
import com.wdharmana.bakingapp.RecipeListActivity;
import com.wdharmana.bakingapp.data.local.RecipeContract;
import com.wdharmana.bakingapp.data.model.Recipe;
import com.wdharmana.bakingapp.data.remote.RestManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    @BindView(R.id.rv)
    RecyclerView recyclerView;

    private RecipeAdapter recipeAdapter;
    private RestManager restManager;

    private boolean PORTRAIT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        Stetho.initializeWithDefaults(this);


        PORTRAIT = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);

        setupList();
    }

    private void setupList() {
        recipeAdapter = new RecipeAdapter(this);

        if(PORTRAIT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
        }


        recyclerView.setHasFixedSize(true);
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        recyclerView.setAdapter(recipeAdapter);

        restManager = new RestManager();

        loadRecipes();
    }

    private void loadRecipes() {
        Call<List<Recipe>> recipeCall = restManager.getAPIService().recipe();

        recipeCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if(response.isSuccessful()) {
                    List<Recipe> recipeList = response.body();

                    for (int i=0; i<recipeList.size(); i++) {
                        recipeAdapter.addItem(recipeList.get(i));
                        getContentResolver().delete(uriBuilder(recipeList.get(i).getId()),
                                null, null);
                        insertDb(recipeList.get(i));
                    }
                    recipeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e("FAILURE", t.getMessage());
            }
        });
    }

    private Uri uriBuilder(long id) {
        return ContentUris.withAppendedId(RecipeContract.RecipeEntry.CONTENT_URI, id);
    }


    private void insertDb(Recipe recipe) {

        String ingridients = new Gson().toJson(recipe.getIngredients());

        ContentValues cv = new ContentValues();
        cv.put(RecipeContract.RecipeEntry._ID, recipe.getId());
        cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, recipe.getName());
        cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE, recipe.getImage());
        cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_INGRIDIENT, ingridients);
        getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, cv);
    }

    @Override
    public void onClick(int position) {
        Recipe recipe = recipeAdapter.getSelectedItem(position);
        String recipeData = new Gson().toJson(recipe);

        if(recipeData!=null) {
            Intent intent = new Intent(getApplicationContext(), RecipeListActivity.class);
            intent.putExtra("data", recipeData);
            startActivity(intent);
        }

    }
}
