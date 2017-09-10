package com.wdharmana.bakingapp.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wdharmana.bakingapp.R;
import com.wdharmana.bakingapp.data.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmana on 8/16/17.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final RecipeClickListener mListener;
    private List<Recipe> mLists;

    public RecipeAdapter(RecipeClickListener listener) {
        mLists = new ArrayList<>();
        mListener = listener;
    }

    public Recipe getSelectedItem(int position) {
        return mLists.get(position);
    }

    public void reset() {
        mLists.clear();
        notifyDataSetChanged();
    }

    public void addItem(Recipe item) {
        mLists.add(item);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_recipe, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = mLists.get(position);
        String name = recipe.getName();
        holder.txtTitle.setText(name);

        int steps = recipe.getSteps().size();

        holder.txtDesc.setText(steps+" Steps");

        String imgUrl = recipe.getImage();

        if(imgUrl!=null&&!imgUrl.equals("")) {
            Context context = holder.imgThumb.getContext();
            Glide.with(context)
                    .load(imgUrl)
                    .thumbnail(0.1f)
                    .into(holder.imgThumb);

        } else {
            if(name.equals("Nutella Pie")) {
                holder.imgThumb.setImageResource(R.drawable.nutellapie);
            } else if(name.equals("Brownies")) {
                holder.imgThumb.setImageResource(R.drawable.brownies);
            } else if(name.equals("Yellow Cake")) {
                holder.imgThumb.setImageResource(R.drawable.yellowcake);
            } else if(name.equals("Cheesecake")) {
                holder.imgThumb.setImageResource(R.drawable.cheesecake);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public interface RecipeClickListener {
        void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtTitle, txtDesc;
        private ImageView imgThumb;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            txtTitle = (TextView) itemView.findViewById(R.id.tv_title);
            txtDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            imgThumb = (ImageView) itemView.findViewById(R.id.img_thumb);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(getLayoutPosition());
        }
    }
}
