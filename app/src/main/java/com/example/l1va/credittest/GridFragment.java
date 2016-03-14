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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.l1va.credittest.utils.BitmapUtilsOld;

import java.util.ArrayList;

public class GridFragment extends Fragment {

    private static final String PACKAGE = "com.example.l1va.credittest";
    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    RecyclerView mGrid;
    BitmapUtilsOld mBitmapUtils = new BitmapUtilsOld();

    public static GridFragment newInstance(CharSequence title, int indicatorColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);

        GridFragment fragment = new GridFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid, container, false);

        mGrid = (RecyclerView) view.findViewById(R.id.grid);
        final Context context = getContext();
        final int inRowCellsCount = SettingsActivity.getCellsCount(context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, inRowCellsCount);
        mGrid.setLayoutManager(gridLayoutManager);

        final ArrayList<PictureData> pictures = mBitmapUtils.loadPhotos(null, getContext().getResources(), 10, inRowCellsCount);
        final GridAdapter adapter = new GridAdapter(context, pictures);
        mGrid.setAdapter(adapter);

        mGrid.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = 5;
                outRect.bottom = 5;
            }
        });
        mGrid.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                ArrayList<PictureData> loaded = mBitmapUtils.loadPhotos(pictures, getContext().getResources(), 10, inRowCellsCount);
                int curSize = adapter.getItemCount();
                pictures.addAll(loaded);
                adapter.notifyItemRangeInserted(curSize, pictures.size() - 1);
            }
        });
        return view;
    }

    private class GridHolder extends RecyclerView.ViewHolder {

        private RelativeLayout container;

        public GridHolder(View itemView) {
            super(itemView);
            container = (RelativeLayout) itemView;
        }

    }

    private class GridAdapter extends RecyclerView.Adapter<GridHolder> {

        private ArrayList<PictureData> pictures;
        private Resources resources;
        private ColorMatrixColorFilter grayscaleFilter;
        private LayoutInflater layoutInflater;
        private ThumbnailClickListener thumbnailClickListener;

        private GridAdapter(Context context, ArrayList<PictureData> pictures) {
            resources = context.getResources();
            this.pictures = pictures;
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
            BitmapDrawable thumbnailDrawable = new BitmapDrawable(resources, pictureData.thumbnail);
            thumbnailDrawable.setColorFilter(grayscaleFilter);

            ((ImageView) holder.container.findViewById(R.id.thumbnail)).setImageDrawable(thumbnailDrawable);
            holder.container.setOnClickListener(thumbnailClickListener);
            holder.container.setTag(pictureData);
            ((TextView) holder.container.findViewById(R.id.thumbnailId)).setText("" + (position + 1));
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PictureData info = (PictureData) v.getTag();
                Intent subActivity = new Intent(context,
                        ImageActivity.class);
                subActivity.putExtra(PACKAGE + ".resourceId", info.resourceId);
                context.startActivity(subActivity,
                        ActivityOptions.makeSceneTransitionAnimation((MainActivity) context, v.findViewById(R.id.thumbnail), "image_transition").toBundle());
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


    public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 50;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        private GridLayoutManager mLinearLayoutManager;

        public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
            this.mLinearLayoutManager = layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            int visibleItemCount = view.getChildCount();
            int totalItemCount = mLinearLayoutManager.getItemCount();

            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount);
                loading = true;
            }
        }

        public abstract void onLoadMore(int page, int totalItemsCount);

    }
}
