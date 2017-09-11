package com.wdharmana.bakingapp.ui.detail;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wdharmana.bakingapp.R;
import com.wdharmana.bakingapp.data.model.Ingredient;
import com.wdharmana.bakingapp.data.model.Recipe;
import com.wdharmana.bakingapp.data.model.Step;
import com.wdharmana.bakingapp.data.remote.RestManager;
import com.wdharmana.bakingapp.ui.main.StepAdapter;
import com.wdharmana.bakingapp.ui.step.RecipeDetailActivity;
import com.wdharmana.bakingapp.ui.step.RecipeDetailFragment;
import com.wdharmana.bakingapp.widget.AppWidgetProvider;

import java.util.List;

import static com.wdharmana.bakingapp.utils.AppConstant.PREF_INGREDIENT;
import static com.wdharmana.bakingapp.utils.AppConstant.PREF_NAME;
import static com.wdharmana.bakingapp.utils.AppConstant.PREF_RECIPE;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity implements StepAdapter.StepClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static StepAdapter stepAdapter;

    private RestManager restManager;

    private Recipe recipe;

    private StringBuilder ingredientList;

    private TextView tvIngredients;

    private CoordinatorLayout coordinatorLayout;

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);


        mSharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        recipe = new Gson().fromJson(
                getIntent().getStringExtra("data"),
                Recipe.class
        );

        if(recipe!=null) {
            getSupportActionBar().setTitle(recipe.getName());

        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.recipe_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if(!mTwoPane) {
            tvIngredients = (TextView) findViewById(R.id.tv_ingredients);

            if(recipe!=null) {
                setupIngredient();
            }
        }
    }

    private void setupIngredient() {
        List<Ingredient> ingredients = recipe.getIngredients();
        ingredientList = new StringBuilder();
        for(int i=0; i<ingredients.size(); i++) {

            String quantity = ingredients.get(i).getQuantity();
            String measure = ingredients.get(i).getMeasure();
            String name = ingredients.get(i).getIngredient();

            String ingredient = "- "+quantity + measure + " " + name+"\n";

            ingredientList.append(ingredient);

        }

        tvIngredients.setText(ingredientList);





    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        stepAdapter = new StepAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(stepAdapter);

        restManager = new RestManager();

        if(recipe!=null) {
            loadSteps();
        }
    }

    private void loadSteps() {
        List<Step> stepList = recipe.getSteps();

        for (int i=0; i<stepList.size(); i++) {
            stepAdapter.addItem(stepList.get(i));
        }
        stepAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(int position) {

        Step step = stepAdapter.getSelectedItem(position);
        Log.e("position", String.valueOf(position));


            String itemId = String.valueOf(stepAdapter.getSelectedItem(position).getId());

            String stepData = new Gson().toJson(stepAdapter.getSelectedItem(position));

            if (mTwoPane) {
                Bundle arguments = new Bundle();

                arguments.putString(RecipeDetailFragment.ARG_DATA, stepData);
                arguments.putBoolean("TWOPANE", mTwoPane);
                arguments.putInt("POSITION", position);
                arguments.putInt("LENGTH", stepAdapter.getItemCount());
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            } else {
                Intent intent = new Intent(getApplicationContext(), RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailFragment.ARG_DATA, stepData);
                intent.putExtra("POSITION", position);
                intent.putExtra("LENGTH", stepAdapter.getItemCount());

                startActivity(intent);
            }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id==R.id.menu_widget) {

            int[] ids = AppWidgetManager.getInstance(getApplicationContext())
                    .getAppWidgetIds(new ComponentName(getApplicationContext(), AppWidgetProvider.class));

            Intent intent = new Intent(this,AppWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            sendBroadcast(intent);


            mSharedPreferences.edit().putString(PREF_RECIPE, recipe.getName()).apply();
            mSharedPreferences.edit().putString(PREF_INGREDIENT, String.valueOf(ingredientList)).apply();

            openSnack("Successfully added to widget");

        }
        return super.onOptionsItemSelected(item);
    }

    private void openSnack(String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public static String getStep(Integer position) {
        Step step = stepAdapter.getSelectedItem(position);
        return new Gson().toJson(step);
    }

}
