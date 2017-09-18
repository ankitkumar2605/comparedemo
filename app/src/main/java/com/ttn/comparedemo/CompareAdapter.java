package com.ttn.comparedemo;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ttn.comparedemo.databinding.RecyclerViewCompareDataBinding;

import java.util.ArrayList;

public class CompareAdapter extends RecyclerView.Adapter<CompareAdapter.ViewHolder> {

    ArrayList<CompareData.SearchResponse> folioList;

    public CompareAdapter(ArrayList<CompareData.SearchResponse> folioList) {
        this.folioList = folioList;
    }

    @Override
    public CompareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewCompareDataBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.recycler_view_compare_data, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CompareAdapter.ViewHolder holder, int position) {

        holder.bind(folioList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return folioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerViewCompareDataBinding binding;

        public ViewHolder(RecyclerViewCompareDataBinding recyclerViewCompareDataBinding) {
            super(recyclerViewCompareDataBinding.getRoot());
            this.binding = recyclerViewCompareDataBinding;
        }

        void bind(String name) {
            binding.setName(name);
            binding.executePendingBindings();
        }
    }
}
