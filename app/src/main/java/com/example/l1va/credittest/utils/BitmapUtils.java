package com.example.l1va.credittest.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.l1va.credittest.entity.PictureData;
import com.example.l1va.credittest.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BitmapUtils {

    private static final int[] mPhotos = {
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p5,
            R.drawable.p6,
    };

    private static HashMap<Integer, Bitmap> sBitmapResourceMap = new HashMap<Integer, Bitmap>();
    private static HashMap<Integer, Bitmap> thumbnailsMap = new HashMap<Integer, Bitmap>();

    public static ArrayList<PictureData> loadPhotos(ArrayList<PictureData> loaded, Resources resources, int rows, int inRow) {
        ArrayList<PictureData> pictures = new ArrayList<PictureData>();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < inRow; ++j) {
                int id = (int) (Math.random() * mPhotos.length);
                int resourceId = mPhotos[id];

                while (wasInRow(pictures, j, resourceId)) {
                    id = (id + 1) % mPhotos.length;
                    resourceId = mPhotos[id];
                }

                pictures.add(new PictureData(resourceId));
            }
            checkLastTwoRows(loaded, pictures, i, inRow);

        }
        return pictures;
    }

    private static void checkLastTwoRows(ArrayList<PictureData> loaded, ArrayList<PictureData> pictures, int i, int inRow) {
        if (i == 0) {
            if (loaded != null) {
                for (int j = 0; j < inRow; ++j) {
                    if (loaded.get(loaded.size() - inRow + j).getResourceId() == pictures.get(j).getResourceId()) {
                        changeFirstAndLastInRow(pictures, inRow);
                        return;
                    }
                }
            }
        } else {
            for (int j = 0; j < inRow; ++j) {
                if (pictures.get(pictures.size() - 2 * inRow + j).getResourceId() == pictures.get(pictures.size() - inRow + j).getResourceId()) {
                    changeFirstAndLastInRow(pictures, inRow);
                    return;
                }
            }
        }
    }

    private static void changeFirstAndLastInRow(ArrayList<PictureData> pictures, int inRow) {
        PictureData toMove = pictures.get(pictures.size() - 1);
        pictures.set(pictures.size() - 1, pictures.get(pictures.size() - inRow));
        pictures.set(pictures.size() - inRow, toMove);
    }

    private static boolean wasInRow(ArrayList<PictureData> list, int j, int id) {
        int len = list.size();
        for (int i = 0; i < j; ++i) {
            if (list.get(len - i - 1).getResourceId() == id)
                return true;
        }
        return false;
    }

    public static Bitmap getBitmap(Resources resources, int resourceId) {
        Bitmap bitmap = sBitmapResourceMap.get(resourceId);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(resources, resourceId);
            sBitmapResourceMap.put(resourceId, bitmap);
        }
        return bitmap;
    }

    private static final int MAX_DIMENSION = 200;

    public static Bitmap getThumbnail(Resources resources, int resourceId) {
        Bitmap thumbnail = thumbnailsMap.get(resourceId);
        if (thumbnail == null) {
            Bitmap original = getBitmap(resources,resourceId);
            int width = original.getWidth();
            int height = original.getHeight();
            int scaledWidth, scaledHeight;
            if (width >= height) {
                float scaleFactor = (float) MAX_DIMENSION / width;
                scaledWidth = MAX_DIMENSION;
                scaledHeight = (int) (scaleFactor * height);
            } else {
                float scaleFactor = (float) MAX_DIMENSION / height;
                scaledWidth = (int) (scaleFactor * width);
                scaledHeight = MAX_DIMENSION;
            }
            thumbnail = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);
            thumbnailsMap.put(resourceId, thumbnail);
        }
        return thumbnail;
    }
}
