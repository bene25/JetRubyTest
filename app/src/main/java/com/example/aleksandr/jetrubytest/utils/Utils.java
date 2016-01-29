package com.example.aleksandr.jetrubytest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by aleksandr on 22.01.16.
 * Helper methods for decoding large bitmaps
 */
public class Utils {

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a
            // power of 2 and keeps both
            // height and width larger than the requested height and
            // width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * This will return a bitmap that is loaded and appropriately scaled
     * from the filepath parameter.
     */
    public static Bitmap decodeSampledBitmapFromFile(String filepath,
                                                     int reqWidth,
                                                     int reqHeight) {
        // First decode with inJustDecodeBounds=true to check
        // dimensions.
        final BitmapFactory.Options options =
                new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath,
                options);

        // Calculate inSampleSize
        options.inSampleSize =
                calculateInSampleSize(options,
                        reqWidth,
                        reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath,
                options);
    }
}
