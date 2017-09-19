package com.ttn.comparedemo;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ttn.comparedemo.databinding.RecyclerViewCompareDataBinding;

import java.util.ArrayList;

public class CompareAdapter extends RecyclerView.Adapter<CompareAdapter.ViewHolder> {

    ArrayList<ProfileData> folioList;
    CompareActivity compareActivity;

    public CompareAdapter(ArrayList<ProfileData> folioList,CompareActivity compareActivity) {
        this.folioList = folioList;
        this.compareActivity = compareActivity;
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

        holder.bind(folioList.get(position));
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

        void bind(ProfileData profileData) {
            binding.setModel(profileData);
            binding.setViewModel(compareActivity);
            binding.executePendingBindings();
        }
    }

    @BindingAdapter("android:srcprofile1")
    public static void setRoundedImageFromUrl(ImageView imageView, String imageUrl) {

        Context context = imageView.getContext();
        Activity activity = null;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                activity = (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context != null) {
            Glide.with(activity).load(imageUrl).thumbnail(0.1f).transform(new GlideCircleTransform(context)).placeholder(R.drawable.profile_default).error(R.drawable.profile_default).into(imageView);
        }
    }

}
