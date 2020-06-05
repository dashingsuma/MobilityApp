package com.wathsumit.mobility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;


public abstract class RecyclerAdapter<T, T2 extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T2> implements Filterable {
    /*public static final int SELECTION_MODE_NONE = 0;
    public static final int SELECTION_MODE_ONE = 1;
    public static final int SELECTION_MODE_MULTI = 2;

    private int mSelectionMode = SELECTION_MODE_NONE;

    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();
*/
    private List<T> mDataSet;

    private Context mContext;

    private LayoutInflater mInflater;

    public RecyclerAdapter(Context context, T[] dataSet) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataSet = Arrays.asList(dataSet);
    }

    public RecyclerAdapter(Context context, List<T> dataSet) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataSet = dataSet;
    }

    public void setData(List<T> dataSet) {
        if (mDataSet != null) {
            mDataSet.clear();
            mDataSet.addAll(dataSet);
        } else mDataSet = dataSet;
        notifyDataSetChanged();
    }

    public List<T> getDataSet() {
        return mDataSet;
    }

    public T getItem(int position) {
        if (mDataSet == null) return null;
        return mDataSet.get(position);
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null) return 0;
        return mDataSet.size();
    }

    public View inflateView(int layoutResource, ViewGroup parent) {
        return mInflater.inflate(layoutResource, parent, false);
    }

    public void removeAt(int position) {
        getDataSet().remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getDataSet().size());
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
