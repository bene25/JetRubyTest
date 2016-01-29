package com.example.aleksandr.jetrubytest.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.aleksandr.jetrubytest.R;
import com.example.aleksandr.jetrubytest.utils.ImageDownloader;

import java.util.ArrayList;

/**
 * Created by aleksandr on 24.01.16.
 * Base adapter class for view flipper
 */
public class SliderBaseAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mArrayList;
    private ImageDownloader mImageDownloader;

    public SliderBaseAdapter(Context context, ArrayList<String> arrayList) {
        this.mContext = context;
        this.mArrayList = arrayList;
        mImageDownloader = new ImageDownloader(mContext);
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

        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.slide_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.view);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (mArrayList != null && mArrayList.size() > 0) {
            mImageDownloader.displayImages(mArrayList.get(position), viewHolder.imageView, viewHolder.progressBar);
        }
        return view;
    }


    static class ViewHolder {
        public ImageView imageView;
        public ProgressBar progressBar;
    }

}

