package com.example.l1va.credittest.utils;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.l1va.credittest.PictureData;
import com.example.l1va.credittest.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BitmapUtils {

    int[] mPhotos = {
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p5,
            R.drawable.p6
    };

    String[] mDescriptions = {
            "This picture was taken while sunbathing in a natural hot spring, which was " +
                    "unfortunately filled with acid, which is a lasting memory from that trip, whenever I " +
                    "I look at my own skin.",
            "I took this shot with a pinhole camera mounted on a tripod constructed out of " +
                    "soda straws. I felt that that combination best captured the beauty of the landscape " +
                    "in juxtaposition with the detritus of mankind.",
            "I don't remember where or when I took this picture. All I know is that I was really " +
                    "drunk at the time, and I woke up without my left sock.",
            "Right before I took this picture, there was a busload of school children right " +
                    "in my way. I knew the perfect shot was coming, so I quickly yelled 'Free candy!!!' " +
                    "and they scattered.",
            "5 Right before I took this picture, there was a busload of school children right ",
            "6 Right before I took this picture, there was a busload of school children right ",
    };

    static HashMap<Integer, Bitmap> thumbnailsMap = new HashMap<Integer, Bitmap>();

    public ArrayList<PictureData> loadPhotos(Resources resources) {
        ArrayList<PictureData> pictures = new ArrayList<PictureData>();
        for (int i = 0; i < 30; ++i) {
            int resourceId = mPhotos[(int) (Math.random() * mPhotos.length)];
            Bitmap thumbnail = getThumbnail(resources, resourceId, 200);
            String description = mDescriptions[(int) (Math.random() * mDescriptions.length)];
            pictures.add(new PictureData(resourceId, description, thumbnail));
        }
        return pictures;
    }

    /*static Bitmap getBitmap(Resources resources, int resourceId) {
        Bitmap bitmap = sBitmapResourceMap.get(resourceId);
        if (bitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.id.myimage, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;


            bitmap = BitmapFactory.decodeResource(resources, resourceId);
            sBitmapResourceMap.put(resourceId, bitmap);
        }
        return bitmap;
    }*/

    private Bitmap getThumbnail(Resources resources, int resourceId, int maxDimension) {
        Bitmap bitmap = thumbnailsMap.get(resourceId);
        if (bitmap == null) {
            bitmap = decodeSampledBitmapFromResource(resources, resourceId, 200, 200);
            thumbnailsMap.put(resourceId, bitmap);
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
