package com.example.l1va.credittest.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.l1va.credittest.entity.PictureData;
import com.example.l1va.credittest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BitmapUtils {

    private static final int[] mPhotos = {
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p5,
            R.drawable.p6,
    };

    private static Map<Integer, Bitmap> sBitmapResourceMap = new HashMap<>();
    private static Map<Integer,Map<Integer,Bitmap>> thumbnailsMap = new HashMap<>();

    public static ArrayList<PictureData> loadPhotos(ArrayList<PictureData> loaded, int rows, int inRow) {
        ArrayList<PictureData> pictures = new ArrayList<>();
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

    public static Bitmap getThumbnailByWidth(Resources resources, int resourceId, int thumbnailWidth) {
        Map<Integer,Bitmap> mapSize = thumbnailsMap.get(resourceId);
        if (mapSize == null) {
            mapSize = new HashMap<>();
            thumbnailsMap.put(resourceId,mapSize);
        }
        Bitmap thumbnail = mapSize.get(thumbnailWidth);
        if (thumbnail == null) {
            Bitmap original = getBitmap(resources,resourceId);
            int width = original.getWidth();
            int height = original.getHeight();
            int scaledWidth, scaledHeight;
            if (width >= height) {
                float scaleFactor = (float) thumbnailWidth / width;
                scaledWidth = thumbnailWidth;
                scaledHeight = (int) (scaleFactor * height);
            } else {
                float scaleFactor = (float) thumbnailWidth / height;
                scaledWidth = (int) (scaleFactor * width);
                scaledHeight = thumbnailWidth;
            }
            thumbnail = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);
            mapSize.put(thumbnailWidth, thumbnail);
        }
        return thumbnail;
    }
}
