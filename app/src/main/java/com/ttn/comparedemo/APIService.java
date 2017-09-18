package com.ttn.comparedemo;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface APIService {
    @GET("59bb99040f0000d901ff8640")
    Observable<CompareData> getData();

    @GET("59bf5bf6260000be0452624d")
    Observable<SubmitData> submitData();


}
