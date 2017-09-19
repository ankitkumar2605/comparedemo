package com.ttn.comparedemo;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {
    @GET("form/list")
    Observable<CompareData> getData();


    @Multipart
    @POST("form/profile/upload")
    Observable<ProfileImageUploadResponse> uploadImage(@Part MultipartBody.Part file);

    @POST("form")
    Observable<SubmitData> saveDetails(@Body ProfileData sipTransactionModel);




}
