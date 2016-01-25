package com.example.aleksandr.jetrubytest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.ArrayList;

/**
 * Created by aleksandr on 24.01.16.
 * Base adapter class for view flipper
 */
public class SliderBaseAdapter extends BaseAdapter {

    private final ArrayList<String> mArrayList;
    private final SharedPreferences mSharedPreferences;
    private final DisplayImageOptions mDefaultOptions;
    private Context mContext;
    private ProgressBar mProgressBar;

    public SliderBaseAdapter(Context context, ArrayList<String> arrayList) {
        this.mContext = context;
        this.mArrayList = arrayList;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(config);

        mDefaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .build();
    }


    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        if (mArrayList != null && mArrayList.size() > 0) {
            Boolean isRemote = mSharedPreferences.getBoolean(mContext.getResources().getString(R.string.pref_switch_key), false);
            if (!isRemote) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap bitmap = Utils.decodeSampledBitmapFromFile(mArrayList.get(position), Const.IMAGE_RES, Const.IMAGE_RES);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(mArrayList.get(position), imageView, mDefaultOptions, mImageLoadingListener, mImageLoadingProgressListener);
            }
        }
        return view;
    }

    /**
     * Image loader download listeners
     */

    ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(mContext, mContext.getResources().getString(R.string.failed_download), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            mProgressBar.setMax(100);
            mProgressBar.setProgress(100);
            mProgressBar.setVisibility(View.GONE);

        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            mProgressBar.setVisibility(View.GONE);
        }

    };

    ImageLoadingProgressListener mImageLoadingProgressListener = new ImageLoadingProgressListener() {
        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            mProgressBar.setMax(total);
            mProgressBar.setProgress(current);
        }
    };


}

