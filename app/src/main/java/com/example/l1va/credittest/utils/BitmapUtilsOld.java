package com.example.l1va.credittest.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.l1va.credittest.PictureData;
import com.example.l1va.credittest.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BitmapUtilsOld {

    int[] mPhotos = {
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p5,
            R.drawable.p6,
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
            "Test",
            "test666"
    };

    static HashMap<Integer, Bitmap> sBitmapResourceMap = new HashMap<Integer, Bitmap>();
    static HashMap<Integer, Bitmap> thumbnailsMap = new HashMap<Integer, Bitmap>();

    /**
     * Load pictures and descriptions. A real app wouldn't do it this way, but that's
     * not the point of this animation demo. Loading asynchronously is a better way to go
     * for what can be time-consuming operations.
     */
    public ArrayList<PictureData> loadPhotos(ArrayList<PictureData> loaded, Resources resources, int rows, int inRow) {
        ArrayList<PictureData> pictures = new ArrayList<PictureData>();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < inRow; ++j) {
                int id = (int) (Math.random() * mPhotos.length);
                int resourceId = mPhotos[id];

                while (wasInRow(pictures, j, resourceId)) {
                    id = (id + 1) % mPhotos.length;
                    resourceId = mPhotos[id];
                }

                Bitmap bitmap = getBitmap(resources, resourceId);
                Bitmap thumbnail = getThumbnail(bitmap, resourceId, 200);
                String description = mDescriptions[(int) (Math.random() * mDescriptions.length)];
                pictures.add(new PictureData(resourceId, description, thumbnail));
            }
            checkLastTwoRows(loaded, pictures, i, inRow);

        }
        return pictures;
    }

    private boolean checkLastTwoRows(ArrayList<PictureData> loaded, ArrayList<PictureData> pictures, int i, int inRow) {
        if (i == 0) {
            if (loaded != null) {
                for (int j = 0; j < inRow; ++j) {
                    if (loaded.get(loaded.size() - inRow + j).getResourceId() == pictures.get(j).getResourceId()) {
                        PictureData toMove = pictures.get(pictures.size()-1);
                        pictures.set(pictures.size()-1, pictures.get(pictures.size()-inRow));
                        pictures.set(pictures.size()-inRow, toMove);
                    }
                }
            }
        } else {
            for (int j = 0; j < inRow; ++j) {
                if (pictures.get(pictures.size() - 2 * inRow + j).getResourceId() == pictures.get(pictures.size() - inRow + j).getResourceId()) {
                    PictureData toMove = pictures.get(pictures.size()-1);
                    pictures.set(pictures.size()-1, pictures.get(pictures.size()-inRow));
                    pictures.set(pictures.size()-inRow, toMove);
                }
            }
        }
        return false;
    }

    private boolean wasInRow(ArrayList<PictureData> list, int j, int id) {
        int len = list.size();
        for (int i = 0; i < j; ++i) {
            if (list.get(len - i - 1).getResourceId() == id)
                return true;
        }
        return false;
    }

    /**
     * Utility method to get bitmap from cache or, if not there, load it
     * from its resource.
     */
    public static Bitmap getBitmap(Resources resources, int resourceId) {
        Bitmap bitmap = sBitmapResourceMap.get(resourceId);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(resources, resourceId);
            sBitmapResourceMap.put(resourceId, bitmap);
        }
        return bitmap;
    }

    /**
     * Create and return a thumbnail image given the original source bitmap and a max
     * dimension (width or height).
     */
    private Bitmap getThumbnail(Bitmap original, int resourceId, int maxDimension) {
        Bitmap thumbnail = thumbnailsMap.get(resourceId);
        if (thumbnail == null) {
            int width = original.getWidth();
            int height = original.getHeight();
            int scaledWidth, scaledHeight;
            if (width >= height) {
                float scaleFactor = (float) maxDimension / width;
                scaledWidth = 200;
                scaledHeight = (int) (scaleFactor * height);
            } else {
                float scaleFactor = (float) maxDimension / height;
                scaledWidth = (int) (scaleFactor * width);
                scaledHeight = 200;
            }
            thumbnail = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);
            thumbnailsMap.put(resourceId, thumbnail);
        }
        return thumbnail;
    }
}
