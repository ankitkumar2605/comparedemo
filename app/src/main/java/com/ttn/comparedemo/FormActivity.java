package com.ttn.comparedemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.ttn.comparedemo.databinding.ActivityFormBinding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class FormActivity extends AppCompatActivity {
    ActivityFormBinding formActivityBinding;
    private static int RESULT_LOAD_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_form);
        formActivityBinding.setViewModel(this);
        setTitle("Login Page");

    }

    public void onSubmit() {
        if (TextUtils.isEmpty(formActivityBinding.tvHoldingMode.getText().toString())) {
            formActivityBinding.tvHoldingMode.setError("Please fill this field");
            return;
        }
        if (TextUtils.isEmpty(formActivityBinding.tvHoldingMode1.getText().toString())) {
            formActivityBinding.tvHoldingMode1.setError("Please fill this field");
            return;
        }
        APIService service = getRetrofitAPIClient();

        Observable<SubmitData> compareDataObservable = service.submitData();
        compareDataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SubmitData>() {

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
                    public void onNext(SubmitData compareData) {
                        Toast.makeText(getApplicationContext(), compareData.msg, Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public static APIService getRetrofitAPIClient() {
        APIService apiService = null;
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://www.mocky.io/v2/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
            apiService = retrofit.create(APIService.class);
        }
        return apiService;
    }


    public void onImageClick() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                ImageView imageView = (ImageView) findViewById(R.id.ib);
                imageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
            } catch (FileNotFoundException e) {
                // Handle the error
            } finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        // Ignore the exception
                    }
                }
            }
        }
    }

}
