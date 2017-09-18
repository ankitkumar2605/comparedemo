package com.ttn.comparedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class BitMapWorker extends AsyncTask<Void, File, Bitmap> {

    private final WeakReference<ImageView> imageViewWeakReference;
    private final WeakReference<Activity> activityWeakReference;
    private Uri mUri;
    private File mFile;

    public BitMapWorker(Activity activity, ImageView imageView, Uri mUri) {
        this.imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.activityWeakReference = new WeakReference<Activity>(activity);
        this.mUri = mUri;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Log.e("orientation", "" + orientation);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    @Override
    protected Bitmap doInBackground(Void... uris) {
        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            if (activityWeakReference.get() != null) {
                inputStream = activityWeakReference.get().getContentResolver().openInputStream(mUri);
            }
            bitmap = BitmapFactory.decodeStream(inputStream);
            mFile = File.createTempFile("tempImage", ".jpeg");
            OutputStream out = new FileOutputStream(mFile);
            // bitmap = rotateImageIfRequired(bitmap, mUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            Log.e("byte count", "" + bitmap.getByteCount());
            out.close();
            inputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

    public void onPostExecute(Bitmap bitmap) {
        if (imageViewWeakReference != null && bitmap != null && activityWeakReference.get() instanceof FormActivity) {
            final ImageView imageView = imageViewWeakReference.get();
            imageView.setImageBitmap(bitmap);
        }

    }
}
