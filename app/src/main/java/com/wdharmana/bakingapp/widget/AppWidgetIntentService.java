package com.wdharmana.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.wdharmana.bakingapp.R;
import com.wdharmana.bakingapp.data.local.RecipeContract;

/**
 * Created by dharmana on 9/10/17.
 */

public class AppWidgetIntentService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);

    }


    class WidgetRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private int mAppWidgetId;
        private Cursor cursor;


        public WidgetRemoteViewsFactory(Context context, Intent intent) {

            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }


        private void initCursor() {

            if (cursor != null) {
                cursor.close();
            }

            Uri uri = RecipeContract.RecipeEntry.CONTENT_URI;
            cursor = mContext.getContentResolver().query(uri, null, null, null, null);

        }

        @Override
        public void onCreate() {
            initCursor();

        }

        @Override
        public void onDataSetChanged() {
            initCursor();
        }

        @Override
        public void onDestroy() {
            cursor.close();
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.recipe_list_widget_item);
            return rv;

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


    }

}