package com.ttn.comparedemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ttn.comparedemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setViewModel(this);
        setTitle("Welcome");

    }

    public void getData() {

        Intent intent = new Intent(this, CompareActivity.class);
        startActivity(intent);
    }

    public void openForm() {

        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }

    public void scanCard() {

    /*    Intent intent = new Intent(this, MyScanActivity.class);
        startActivity(intent);
    */}


}
