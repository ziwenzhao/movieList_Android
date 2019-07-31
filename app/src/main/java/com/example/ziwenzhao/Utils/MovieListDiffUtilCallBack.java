package com.example.ziwenzhao.Utils;

import android.support.v7.util.DiffUtil;

import com.example.ziwenzhao.models.MovieModel;

import java.util.List;

/**
 * This is an class for creating customized DiffUtil Callback.
 * DiffUtil calculates the minimum operations to update lists which can improve the app performance.
 */
public class MovieListDiffUtilCallBack extends DiffUtil.Callback {
    private List<MovieModel> oldList;
    private List<MovieModel> newList;

    public MovieListDiffUtilCallBack(List<MovieModel> oldList, List<MovieModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldListIndex, int newListIndex) {
        return oldList.get(oldListIndex).getId() == newList.get(newListIndex).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldListIndex, int newListIndex) {
        return oldList.get(oldListIndex).equals(newList.get(newListIndex));
    }
}
