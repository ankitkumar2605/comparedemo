package com.ttn.comparedemo;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class FormActivity extends AppCompatActivity implements OnImageUploadListener {
    ActivityFormBinding formActivityBinding;
    private static int RESULT_LOAD_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5 = 100;
    public static final int REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5_DIALOG = 1001;

    private Uri outputFileUri;
    public ObservableField<String> logoUrl = new ObservableField<>();

    private boolean isFronFolio5;


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
        if (TextUtils.isEmpty(formActivityBinding.tvPhone.getText().toString())) {
            formActivityBinding.tvPhone.setError("Please fill this field");
            return;
        }
        if (TextUtils.isEmpty(formActivityBinding.tvStreetAddress.getText().toString())) {
            formActivityBinding.tvStreetAddress.setError("Please fill this field");
            return;
        }
        if (TextUtils.isEmpty(formActivityBinding.tvCity.getText().toString())) {
            formActivityBinding.tvCity.setError("Please fill this field");
            return;
        }
        if (TextUtils.isEmpty(formActivityBinding.tvPincode.getText().toString())) {
            formActivityBinding.tvPincode.setError("Please fill this field");
            return;
        }
        if (!isValidEmailId(formActivityBinding.tvHoldingMode2.getText().toString())) {
            formActivityBinding.tvHoldingMode2.setError("Enter valid Email ID");
            return;
        }

        ProfileData profileData = new ProfileData();
        profileData.name = formActivityBinding.tvHoldingMode.getText().toString();
        profileData.email = formActivityBinding.tvHoldingMode2.getText().toString();
        profileData.phone = formActivityBinding.tvPhone.getText().toString();
        profileData.street = formActivityBinding.tvStreetAddress.getText().toString();
        profileData.city = formActivityBinding.tvCity.getText().toString();
        profileData.profilePicUrl = logoUrl.get();
        profileData.pin = formActivityBinding.tvPincode.getText().toString();
        formActivityBinding.included.rlProgress.setVisibility(View.VISIBLE);

        APIService service = getRetrofitAPIClient();
        Observable<SubmitData> compareDataObservable = service.saveDetails(profileData);
        compareDataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SubmitData>() {

                    @Override
                    public void onError(Throwable e) {
                        formActivityBinding.included.rlProgress.setVisibility(View.GONE);

                        Log.e("mn",e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SubmitData compareData) {
                        formActivityBinding.included.rlProgress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

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

    public boolean isPermissionGranted(String permission) {
        int permissionValue = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
        return permissionValue == PermissionChecker.PERMISSION_GRANTED;
    }

    public void requestPermission(final String permission, final int requestCode, String message, boolean showMessage) {

        if (showMessage && !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showAlertDialog("", message, new View.OnClickListener() {
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

                formActivityBinding.included.rlProgress.setVisibility(View.VISIBLE);
                new BitMapWorker(FormActivity.this, formActivityBinding.ivProfile, selectedImageUri).execute();

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

    public static boolean isValidEmailId(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("nnn", "jj");

        if (requestCode == REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5) {
            isFronFolio5 = true;
            switch (grantResults[0]) {
                case PackageManager.PERMISSION_GRANTED:
                    uploadProfileIntent();
                    break;

                case PackageManager.PERMISSION_DENIED:
                    break;

            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE_GALLERY_FOLIO5_DIALOG) {
            isFronFolio5 = false;
            switch (grantResults[0]) {
                case PackageManager.PERMISSION_GRANTED:
                    uploadProfileIntent();
                    break;

                case PackageManager.PERMISSION_DENIED:
                    break;

            }
        }
    }

    @BindingAdapter("android:srcprofile")
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

    public void uploadImage(File file) {
        MediaType mediaType = MediaType.parse("image/jpeg");
        RequestBody requestFile =
                RequestBody.create(mediaType, file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        APIService service = getRetrofitAPIClient();
        Observable<ProfileImageUploadResponse> compareDataObservable = service.uploadImage(body);

        compareDataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProfileImageUploadResponse>() {

                    @Override
                    public void onError(Throwable e) {
                        formActivityBinding.included.rlProgress.setVisibility(View.GONE);

                        Log.e("kl",e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ProfileImageUploadResponse compareData) {
                        formActivityBinding.included.rlProgress.setVisibility(View.GONE);
                        Log.e("url",compareData.profilePicUrl);
                       logoUrl.set(compareData.profilePicUrl);
                    }
                });



    }



}
