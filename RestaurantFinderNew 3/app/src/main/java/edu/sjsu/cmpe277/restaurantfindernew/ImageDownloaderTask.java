package edu.sjsu.cmpe277.restaurantfindernew;

/**
 * Created by divya.chittimalla on 3/20/16.
 */
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.os.AsyncTask;
import java.lang.ref.WeakReference;
import android.graphics.drawable.Drawable;
import java.net.HttpURLConnection;
import java.net.URL;
import android.graphics.BitmapFactory;
import java.io.InputStream;

class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    public String url;
    private final WeakReference<ImageView> imageViewReference;

    public ImageDownloaderTask(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        url = params[0];
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.mipmap.ic_launcher);
                    imageView.setImageDrawable(placeholder);
                }
            }
        }
    }

    private Bitmap downloadBitmap(String src) {
        String urldisplay = src;
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }
}