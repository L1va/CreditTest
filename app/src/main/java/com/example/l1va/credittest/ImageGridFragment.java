package com.example.l1va.credittest;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.l1va.credittest.utils.BitmapUtils;

import java.util.ArrayList;

public class ImageGridFragment extends Activity {//implements AdapterView.OnItemClickListener {

    RecyclerView mGrid;
    BitmapUtils mBitmapUtils = new BitmapUtils();
    boolean newApi = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        mGrid = (RecyclerView) findViewById(R.id.grid);
        mGrid.setLayoutManager(new GridLayoutManager(this, 5));
        mGrid.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = 5;
                outRect.bottom = 5;
            }
        });

        mGrid.setAdapter(new GridAdapter(this));
    }

    private View.OnClickListener thumbnailClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && newApi) {
                PictureData info = (PictureData) v.getTag();
                Intent subActivity = new Intent(ActivityAnimations.this,
                        PictureDetailsActivity2.class);
                subActivity.putExtra(PACKAGE + ".resourceId", info.resourceId);
                startActivity(subActivity,
                        ActivityOptions.makeSceneTransitionAnimation(ActivityAnimations.this, v.findViewById(R.id.imageView), "image").toBundle());
            } else {
                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)
                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);
                PictureData info = (PictureData) v.getTag();
                Intent subActivity = new Intent(ActivityAnimations.this,
                        PictureDetailsActivity.class);
                int orientation = getResources().getConfiguration().orientation;
                subActivity.
                        putExtra(PACKAGE + ".orientation", orientation).
                        putExtra(PACKAGE + ".resourceId", info.resourceId).
                        putExtra(PACKAGE + ".left", screenLocation[0]).
                        putExtra(PACKAGE + ".top", screenLocation[1]).
                        putExtra(PACKAGE + ".width", v.getWidth()).
                        putExtra(PACKAGE + ".height", v.getHeight()).
                        putExtra(PACKAGE + ".description", info.description);
                startActivity(subActivity);

                // Override transitions: we don't want the normal window animation in addition
                // to our custom one
                overridePendingTransition(0, 0);
            }*/
        }
    };
    private class GridHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public GridHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView;
        }

    }
    private class GridAdapter extends RecyclerView.Adapter<GridHolder> {

        private ArrayList<PictureData> pictures;
        private Context context;
        private Resources resources;
        private ColorMatrixColorFilter grayscaleFilter;
        private LayoutInflater layoutInflater;

        private GridAdapter(Context context) {
            this.context = context;
            resources = context.getResources();
            pictures = mBitmapUtils.loadPhotos(resources);
            ColorMatrix grayMatrix = new ColorMatrix();
            grayMatrix.setSaturation(0);
            grayscaleFilter = new ColorMatrixColorFilter(grayMatrix);
            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View image = layoutInflater.inflate(R.layout.grid_item, mGrid, false);
            return new GridHolder(image);
        }

        @Override
        public void onBindViewHolder(GridHolder holder, int position) {
            PictureData pictureData = pictures.get(position);

            BitmapDrawable thumbnailDrawable =
                    new BitmapDrawable(resources, pictureData.thumbnail);
            thumbnailDrawable.setColorFilter(grayscaleFilter);

            holder.image.setImageDrawable(thumbnailDrawable);
            holder.image.setOnClickListener(thumbnailClickListener);
            holder.image.setTag(pictureData);
        }

        @Override
        public int getItemCount() {
            return pictures.size();
        }
    }
}
