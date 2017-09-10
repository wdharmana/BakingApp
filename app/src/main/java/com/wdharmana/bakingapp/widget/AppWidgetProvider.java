package com.wdharmana.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.wdharmana.bakingapp.R;

import static com.wdharmana.bakingapp.utils.AppConstant.PREF_INGREDIENT;
import static com.wdharmana.bakingapp.utils.AppConstant.PREF_NAME;
import static com.wdharmana.bakingapp.utils.AppConstant.PREF_RECIPE;

/**
 * Created by dharmana on 9/10/17.
 */

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_list_widget);

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String recipe = sharedPreferences.getString(PREF_RECIPE, "");
        String ingredients = sharedPreferences.getString(PREF_INGREDIENT, "");

        // Set up the intent that starts the StackViewService, which will
        // provide the views for this collection.
        Intent intent = new Intent(context, AppWidgetIntentService.class);
        // Add the app widget ID to the intent extras.
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        if (recipe.equals("")) {
            views.setTextViewText(R.id.widget_recipe_name,"No Recipe Added");
            views.setTextViewText(R.id.widget_info,
                    "Add recipe by select recipe and select option menu > Add to Widget");
        } else {
            views.setTextViewText(R.id.widget_info,"");
            views.setTextViewText(R.id.widget_recipe_name, recipe + " Ingredients");
            views.setTextViewText(R.id.widget_ingredients, ingredients);
        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
