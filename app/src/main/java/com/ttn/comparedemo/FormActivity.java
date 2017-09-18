package com.ttn.comparedemo;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ttn.comparedemo.databinding.ActivityFormBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static final int REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5 = 100;
    public static final int REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5_DIALOG = 1001;

    private Uri outputFileUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_form);
        formActivityBinding.setViewModel(this);
        setTitle("Sample Form");

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
    public boolean isPermissionGranted(String permission) {
        int permissionValue = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
        return permissionValue == PermissionChecker.PERMISSION_GRANTED;
    }

    public void requestPermission(final String permission, final int requestCode, String message, boolean showMessage) {

        if (showMessage && !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showAlertDialog("",message, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(FormActivity.this, new String[]{permission}, requestCode);
                }
            });
            return;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }



    public void onImageClick() {
        if (!isPermissionGranted(Manifest.permission.CAMERA)) {
            requestPermission(Manifest.permission.CAMERA, REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5_DIALOG, getString(R.string.message_need_storage_permission), false);
        } else if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5, getString(R.string.message_need_storage_permission), false);

        } else {
            uploadProfileIntent();
        }
        }

    public void uploadProfileIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        final File sdImageMainDirectory = new File(root, imageFileName);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = FormActivity.this.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        //galleryIntent.setAction();

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                new BitMapWorker(FormActivity.this, formActivityBinding.ib, selectedImageUri).execute();

            }
        }
    }
    public void showAlertDialog(String messageBold, String message, View.OnClickListener onPositiveButtonClickListener) {
            showCustomAlertDialog(null, messageBold, message, onPositiveButtonClickListener);

    }


    public void showCustomAlertDialog(View view, String messageBold, String message, final View.OnClickListener onPositiveButtonClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        try {
            alertDialog.show();
        } catch (WindowManager.BadTokenException ex) {
            Log.e("HDFC", "Issue in creating custom alert dialog", ex);
        }
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        if (view == null) {
            view = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null);
            TextView textViewMessage = (TextView) view.findViewById(R.id.tv_message);
            TextView textViewMessageBold = (TextView) view.findViewById(R.id.tv_message_bold);
            if (message.contains("\n"))
                textViewMessage.setText(message);
            else
                textViewMessage.setText(Html.fromHtml(message));
            if (!TextUtils.isEmpty(messageBold)) {
                textViewMessageBold.setText(messageBold);
                textViewMessageBold.setVisibility(View.VISIBLE);
            }
        }
        Button imageButtonOk = (Button) view.findViewById(R.id.btn_ok);
        alertDialog.setContentView(view);
        if (imageButtonOk != null) {
            imageButtonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    if (onPositiveButtonClickListener != null)
                        onPositiveButtonClickListener.onClick(view);
                }
            });
        }
    }


}
