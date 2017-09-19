package com.ttn.comparedemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ttn.comparedemo.databinding.ActivityCompareBinding;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompareActivity extends AppCompatActivity {
    LinearLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ActivityCompareBinding activityCompareBinding;
    public ObservableField<String> logoUrl = new ObservableField<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCompareBinding = DataBindingUtil.setContentView(this, R.layout.activity_compare);
        setTitle("Sample List View");
        setupRecyclerView();
        APIService service = getRetrofitAPIClient();

        Observable<CompareData> compareDataObservable = service.getData();
        compareDataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareData>() {

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CompareData compareData) {
                        setRudList(compareData.data);

                    }
                });
    }

    public static APIService getRetrofitAPIClient() {
        APIService apiService = null;
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.1.1.71:8080/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
            apiService = retrofit.create(APIService.class);
        }
        return apiService;
    }

    protected void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                activityCompareBinding.rvData.getContext(),
                mLayoutManager.getOrientation()
        );
        activityCompareBinding.rvData.addItemDecoration(mDividerItemDecoration);

        activityCompareBinding.rvData.setLayoutManager(mLayoutManager);
    }

    public void setRudList(ArrayList<ProfileData> schemeNameList) {
        mAdapter = new CompareAdapter(schemeNameList,this);
        activityCompareBinding.rvData.setAdapter(mAdapter);
    }

    public void showDetails(ProfileData profileData){
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("Details",profileData);
        startActivity(intent);
    }
}
