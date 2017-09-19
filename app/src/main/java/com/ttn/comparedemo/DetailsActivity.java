package com.ttn.comparedemo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ttn.comparedemo.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {
    ActivityDetailsBinding activityDetailsBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        ProfileData profileData = (ProfileData) getIntent().getSerializableExtra("Details");
        setTitle("Details");
        activityDetailsBinding.setModel(profileData);

    }
}
