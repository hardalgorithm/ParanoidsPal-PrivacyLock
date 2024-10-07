package com.paranoid.privacylock.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.paranoid.privacylock.R;

public class SimpleViewAdapter extends RecyclerView.Adapter<SimpleViewAdapter.ProjectViewHolder> {

    private final int itemCount;
    private final int layout;

    public SimpleViewAdapter(int itemCount, int layout) {
        this.itemCount = itemCount;
        this.layout = layout;
    }

    @NonNull
    @Override
    public SimpleViewAdapter.ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(layout, parent, false);
        return new SimpleViewAdapter.ProjectViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewAdapter.ProjectViewHolder holder, int position) {

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int itemWidth;

        int orientation = Resources.getSystem().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            itemWidth = (int) (screenWidth * 0.935);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.width = itemWidth;
            holder.itemView.setLayoutParams(layoutParams);
        }

        switch (position){
            case (1):
            holder.ivInfo.setImageResource(R.drawable.battery_settings);
                holder.tvBatterySetting.setVisibility(View.VISIBLE);
                break;
            case (2):
                holder.ivInfo.setImageResource(R.drawable.battery_unreg);
                break;
            case (4):
                holder.ivInfo.setImageResource(R.drawable.tile_feature);
                holder.tvTileInfo.setVisibility(View.VISIBLE);
                break;
            case (3):
                holder.ivInfo.setImageResource(R.drawable.shortcut_feature);
                holder.tvShortcutInfo.setVisibility(View.VISIBLE);
                break;
            case (0):
                holder.ivInfo.setImageResource(R.drawable.famous_kyiv2);
                break;
            default:
                holder.ivInfo.setImageResource(R.drawable.famous_kyiv2);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return itemCount;
    }
    public static class ProjectViewHolder extends RecyclerView.ViewHolder{

        private final ImageView ivInfo;
        private final MaterialTextView tvTileInfo;
        private final MaterialTextView tvShortcutInfo;
        private final MaterialTextView tvBatterySetting;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            ivInfo = itemView.findViewById(R.id.ivInfo);
            tvTileInfo = itemView.findViewById(R.id.tvTileInfo);
            tvShortcutInfo = itemView.findViewById(R.id.tvShortcutInfo);
            tvBatterySetting = itemView.findViewById(R.id.tvBatterySetting);
        }

    }

}

