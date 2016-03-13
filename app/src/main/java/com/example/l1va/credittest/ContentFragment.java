package com.example.l1va.credittest;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.example.l1va.credittest.utils.BitmapUtils;
import com.example.l1va.credittest.utils.BitmapUtilsOld;

import java.util.ArrayList;

public class ContentFragment extends Fragment {

    private static final String PACKAGE = "com.example.l1va.credittest";
    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    RecyclerView mGrid;
    BitmapUtilsOld mBitmapUtils = new BitmapUtilsOld();

    public static ContentFragment newInstance(CharSequence title, int indicatorColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);

        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.grid, container, false);

        mGrid = (RecyclerView) view.findViewById(R.id.grid);
        Context context = getContext();
        mGrid.setLayoutManager(new GridLayoutManager(context, 5));
        mGrid.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = 5;
                outRect.bottom = 5;
            }
        });

        mGrid.setAdapter(new GridAdapter(context));
        return view;
    }

  /*  @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText("Title: " + args.getCharSequence(KEY_TITLE));

            int indicatorColor = args.getInt(KEY_INDICATOR_COLOR);
            TextView indicatorColorView = (TextView) view.findViewById(R.id.item_indicator_color);
            indicatorColorView.setText("Indicator: #" + Integer.toHexString(indicatorColor));
            indicatorColorView.setTextColor(indicatorColor);

        }
    }*/

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
        private ThumbnailClickListener thumbnailClickListener;

        private GridAdapter(Context context) {
            this.context = context;
            resources = context.getResources();
            pictures = mBitmapUtils.loadPhotos(resources);
            ColorMatrix grayMatrix = new ColorMatrix();
            grayMatrix.setSaturation(0);
            grayscaleFilter = new ColorMatrixColorFilter(grayMatrix);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            thumbnailClickListener = new ThumbnailClickListener(context);
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

    private class ThumbnailClickListener implements View.OnClickListener {

        private Context context;
        private ThumbnailClickListener(Context context) {
            this.context = context;

        }

        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                PictureData info = (PictureData) v.getTag();
                Intent subActivity = new Intent(context,
                        ImageActivity.class);
                subActivity.putExtra(PACKAGE + ".resourceId", info.resourceId);
                context.startActivity(subActivity,
                        ActivityOptions.makeSceneTransitionAnimation((MainActivity)context, v.findViewById(R.id.imageView), "image").toBundle());
            }/* else {
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
    }
}
