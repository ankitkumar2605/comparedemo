package com.ttn.comparedemo;

import android.databinding.DataBindingUtil;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      activityCompareBinding = DataBindingUtil.setContentView(this,R.layout.activity_compare);

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
                     setRudList(compareData.data.searchResponses);

                    }
                });
    }

    public static  APIService getRetrofitAPIClient(){
        APIService  apiService = null;
        if(apiService == null){
            Retrofit retrofit =new Retrofit.Builder()
                    .baseUrl("http://www.mocky.io/v2/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
            apiService = retrofit.create(APIService.class);
        }
        return  apiService;
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

    public void setRudList(ArrayList<CompareData.SearchResponse> schemeNameList) {
        mAdapter = new CompareAdapter(schemeNameList);
        activityCompareBinding.rvData.setAdapter(mAdapter);

    }
}
