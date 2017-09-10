package com.wdharmana.bakingapp.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.wdharmana.bakingapp.data.local.RecipeContract.IngredientEntry.COLUMN_INGREDIENT_MEASURE;
import static com.wdharmana.bakingapp.data.local.RecipeContract.IngredientEntry.COLUMN_INGREDIENT_NAME;
import static com.wdharmana.bakingapp.data.local.RecipeContract.IngredientEntry.COLUMN_INGREDIENT_QUANTITY;
import static com.wdharmana.bakingapp.data.local.RecipeContract.IngredientEntry.TABLE_INGREDIENTS_NAME;
import static com.wdharmana.bakingapp.data.local.RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE;
import static com.wdharmana.bakingapp.data.local.RecipeContract.RecipeEntry.COLUMN_RECIPE_INGRIDIENT;
import static com.wdharmana.bakingapp.data.local.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;
import static com.wdharmana.bakingapp.data.local.RecipeContract.RecipeEntry.TABLE_RECIPES_NAME;

/**
 * Created by dharmana on 9/9/17.
 */

public class RecipeDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bakingapp.db";
    private static final int DATABASE_VERSION = 2;

    public RecipeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE_RECIPES = "CREATE TABLE " + TABLE_RECIPES_NAME + "(" +
                RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INGRIDIENT + " TEXT, " +
                COLUMN_RECIPE_IMAGE+ " TEXT);";
        db.execSQL(SQL_CREATE_TABLE_RECIPES);

        final String SQL_CREATE_TABLE_INGREDIENTS = "CREATE TABLE " + TABLE_INGREDIENTS_NAME + "(" +
                RecipeContract.IngredientEntry._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
                COLUMN_INGREDIENT_MEASURE+ " TEXT, " +
                COLUMN_INGREDIENT_QUANTITY+ " TEXT);";
        //db.execSQL(SQL_CREATE_TABLE_INGREDIENTS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_RECIPES_NAME);
        onCreate(db);
    }
}
