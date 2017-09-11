package com.wdharmana.bakingapp.ui.main;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wdharmana.bakingapp.R;
import com.wdharmana.bakingapp.data.model.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmana on 8/16/17.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

    private final StepClickListener mListener;
    private List<Step> mLists;

    public StepAdapter(StepClickListener listener) {
        mLists = new ArrayList<>();
        mListener = listener;
    }

    public Step getSelectedItem(int position) {
        return mLists.get(position);
    }

    public void reset() {
        mLists.clear();
        notifyDataSetChanged();
    }

    public void addItem(Step item) {
        mLists.add(item);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_step, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Step step = mLists.get(position);



        String videoUrl = step.getVideoURL();
        String stepOrder = String.valueOf(position+1);
        String name = stepOrder+": "+step.getShortDescription();
        holder.txtTitle.setText(name);

        if(TextUtils.isEmpty(videoUrl)) {
            holder.imgThumb.setImageResource(R.drawable.videocamoff);
        }

    }


    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public interface StepClickListener {
        void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtTitle;
        private ImageView imgThumb;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            txtTitle = (TextView) itemView.findViewById(R.id.tv_title);
            imgThumb = (ImageView) itemView.findViewById(R.id.img_thumb);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(getLayoutPosition());
        }
    }
}
