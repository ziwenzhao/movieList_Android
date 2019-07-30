package com.example.ziwenzhao.Utils;

import android.support.v7.util.DiffUtil;

import com.example.ziwenzhao.models.MovieHttpResult;

import java.util.List;

public class MovieListDiffUtilCallBack extends DiffUtil.Callback {
    private List<MovieHttpResult> oldList;
    private List<MovieHttpResult> newList;

    public MovieListDiffUtilCallBack(List<MovieHttpResult> oldList, List<MovieHttpResult> newList) {
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
