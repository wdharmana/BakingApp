package com.wdharmana.bakingapp.data.local;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dharmana on 9/10/17.
 */

public class RecipeContract {

    public static final String AUTHORITY = "com.wdharmana.bakingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_LISTS = "lists";

    public static final class RecipeEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LISTS).build();

        public static final String TABLE_RECIPES_NAME = "recipes";
        public static final String COLUMN_RECIPE_NAME = "name";
        public static final String COLUMN_RECIPE_IMAGE = "image";
        public static final String COLUMN_RECIPE_INGRIDIENT = "ingridient";

    }

    public static final class IngredientEntry implements BaseColumns {

        //public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_INGREDIENTS_NAME = "ingredients";

        public static final String COLUMN_INGREDIENT_NAME = "ingredient";
        public static final String COLUMN_INGREDIENT_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT_QUANTITY = "quantity";

    }

}
