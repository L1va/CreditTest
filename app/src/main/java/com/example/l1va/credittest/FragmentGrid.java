package com.example.l1va.credittest;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.example.l1va.credittest.entity.PictureData;
import com.example.l1va.credittest.utils.BitmapUtils;

import java.util.ArrayList;

public class FragmentGrid extends Fragment {

    private RecyclerView gridView;
    private ArrayList<PictureData> pictures;

    private final int ROWS_COUNT_TO_LOAD = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        gridView = (RecyclerView) view.findViewById(R.id.grid);
        final Context context = getContext();
        final int inRowCellsCount = ActivitySettings.getCellsCount(context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, inRowCellsCount);
        gridView.setLayoutManager(gridLayoutManager);

        pictures = BitmapUtils.loadPhotos(null, getContext().getResources(), ROWS_COUNT_TO_LOAD, inRowCellsCount);
        final GridAdapter adapter = new GridAdapter();
        gridView.setAdapter(adapter);

        gridView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = 5;
                outRect.left = 5;
                outRect.bottom = 5;
            }
        });
        gridView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                ArrayList<PictureData> loaded = BitmapUtils.loadPhotos(pictures, getContext().getResources(), ROWS_COUNT_TO_LOAD, inRowCellsCount);
                int curSize = adapter.getItemCount();
                pictures.addAll(loaded);
                adapter.notifyItemRangeInserted(curSize, pictures.size() - 1);
            }
        });
        return view;
    }

    private class GridHolder extends RecyclerView.ViewHolder {

        private View container;
        private ImageView imageView;
        private TextView textView;

        public GridHolder(View itemView, ImageView imageView, TextView textView) {
            super(itemView);
            this.container = itemView;
            this.imageView = imageView;
            this.textView = textView;
        }
    }

    private class GridAdapter extends RecyclerView.Adapter<GridHolder> {

        private Resources resources;
        private LayoutInflater layoutInflater;
        private ThumbnailClickListener thumbnailClickListener;

        private GridAdapter() {
            resources = getContext().getResources();
            layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            thumbnailClickListener = new ThumbnailClickListener();
        }

        @Override
        public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View image = layoutInflater.inflate(R.layout.fragment_grid_item, gridView, false);
            RelativeLayout container = (RelativeLayout) image;
            container.setOnClickListener(thumbnailClickListener);
            ImageView imageView = (ImageView) container.findViewById(R.id.thumbnail);
            TextView textView = (TextView) container.findViewById(R.id.thumbnailId);
            return new GridHolder(container, imageView, textView);
        }

        @Override
        public void onBindViewHolder(GridHolder holder, int position) {
            PictureData pictureData = pictures.get(position);

            holder.container.setTag(pictureData);
            holder.imageView.setImageBitmap(BitmapUtils.getThumbnail(getResources(), pictureData.getResourceId()));
            holder.textView.setText(String.valueOf(position + 1));
        }

        @Override
        public int getItemCount() {
            return pictures.size();
        }
    }

    private class ThumbnailClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PictureData data = (PictureData) v.getTag();
                Intent subActivity = new Intent(getContext(), ActivityImage.class);
                subActivity.putExtra(getString(R.string.thumbnail_id_key), data.getResourceId());
                getContext().startActivity(subActivity,
                        ActivityOptions.makeSceneTransitionAnimation((ActivityMain) getContext(), v.findViewById(R.id.thumbnail), getString(R.string.image_transition_key)).toBundle());
            } else {
                Toast.makeText(getActivity(), "Unsupported version! Support only starting from Lollipop", Toast.LENGTH_LONG).show();
            }
            /* else {
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
