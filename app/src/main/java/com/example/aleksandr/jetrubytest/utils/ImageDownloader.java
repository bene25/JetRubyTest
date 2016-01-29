package com.example.aleksandr.jetrubytest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.aleksandr.jetrubytest.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

/**
 * Created by aleksandr on 28.01.16.
 * Class for loading images
 */
public class ImageDownloader {

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;
    private int mHeight = Const.DEF_IMAGE_RESOLUTION;
    private int mWidth = Const.DEF_IMAGE_RESOLUTION;
    private final DisplayImageOptions mDefaultOptions;

    public ImageDownloader(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(config);

        mDefaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .build();

        DisplayMetrics displaymetrics = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displaymetrics);
            mWidth = displaymetrics.widthPixels;
            mHeight = displaymetrics.heightPixels;
        }

    }

    public void displayImages(String fileUrl, final ImageView imageView, final ProgressBar progressBar) {
        if (fileUrl != null) {
            Boolean isRemote = mSharedPreferences.getBoolean(mContext.getResources().getString(R.string.pref_switch_key), false);
            if (isRemote) {
                ImageLoader.getInstance().displayImage(fileUrl, imageView, mDefaultOptions, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_error_placeholder));
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        progressBar.setVisibility(View.GONE);

                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        progressBar.setProgress(Math.round(100.0f * current / total));
                    }
                });
            } else {
                Bitmap bitmap = Utils.decodeSampledBitmapFromFile(fileUrl, mWidth, mHeight);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

            }
        }

    }
}
