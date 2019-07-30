package com.example.ziwenzhao.ui.movielist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ziwenzhao.models.MovieHttpResult;
import com.example.ziwenzhao.models.MovieModel;

import java.util.List;


public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ItemViewHolder> {
    private List<MovieModel> list;

    public MovieListAdapter(List<MovieModel> list) {
        this.list = list;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public ImageView image;
        public ItemViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            image = itemView.findViewById(R.id.image);
        }
    }

    @Override
    public MovieListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.text.setText(list.get(position).getTitle());

        holder.image.setImageBitmap(list.get(position).getBitmap());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
