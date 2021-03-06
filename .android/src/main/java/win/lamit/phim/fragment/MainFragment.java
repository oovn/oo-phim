package win.lamit.phim.fragment;

import androidx.annotation.Nullable;
import win.lamit.phim.R;
import win.lamit.phim.activity.GuidedStepActivity;
import win.lamit.phim.activity.SearchActivity;
import win.lamit.phim.activity.SettingsActivity;
import win.lamit.phim.activity.VerticalGridActivity;
import win.lamit.phim.activity.VideoDetailsActivity;
import win.lamit.phim.data.FetchVideoService;
import win.lamit.phim.data.VideoContract;
import win.lamit.phim.model.Video;
import win.lamit.phim.model.VideoCursorMapper;
import win.lamit.phim.presenter.CardPresenter;
import win.lamit.phim.presenter.GridItemPresenter;
import win.lamit.phim.recommendation.UpdateRecommendationsService;
import androidx.annotation.NonNull;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.CursorObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.loader.app.LoaderManager;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainFragment extends BrowseSupportFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mCategoryRowAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Runnable mBackgroundTask;
    private Uri mBackgroundURI;
    private BackgroundManager mBackgroundManager;
    private LoaderManager mLoaderManager;
    private static final int CATEGORY_LOADER = 123; // Unique ID for Category Loader.
    // Maps a Loader Id to its CursorObjectAdapter.
    private Map<Integer, CursorObjectAdapter> mVideoCursorAdapters;
    @SuppressLint("UseSparseArrays")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Create a list to contain all the CursorObjectAdapters.
        // Each adapter is used to render a specific row of videos in the MainFragment.
        mVideoCursorAdapters = new HashMap<>();
        // Start loading the categories from the database.
        mLoaderManager = LoaderManager.getInstance(this);
        mLoaderManager.initLoader(CATEGORY_LOADER, null, this);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Final initialization, modifying UI elements.
        super.onActivityCreated(savedInstanceState);
        // Prepare the manager that maintains the same background image between activities.
        prepareBackgroundManager();
        setupUIElements();
        setupEventListeners();
        prepareEntranceTransition();
        // Map category results from the database to ListRow objects.
        // This Adapter is used to render the MainFragment sidebar labels.
        mCategoryRowAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mCategoryRowAdapter);
        updateRecommendations();
    }
    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mBackgroundTask);
        mBackgroundManager = null;
        super.onDestroy();
    }
    @Override
    public void onStop() {
        mBackgroundManager.release();
        super.onStop();
    }
    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(Objects.requireNonNull(getActivity()));
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background, null);
        mBackgroundTask = new UpdateBackgroundTask();
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }
    private void setupUIElements() {
        setBadgeDrawable(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.videos_by_google_banner, null));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        // Set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        // Set search icon color.
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));
/*/
        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object o) {
                return new IconHeaderItemPresenter();
            }
        });
/*/
    }
    private void setupEventListeners() {
        setOnSearchClickedListener(view -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }
    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(mDefaultBackground);
        Glide.with(this)
                .asBitmap()
                .load(uri)
                .apply(options)
                .into(new CustomTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap resource,
                            Transition<? super Bitmap> transition) {
                        mBackgroundManager.setBitmap(resource);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
    private void startBackgroundTimer() {
        mHandler.removeCallbacks(mBackgroundTask);
        mHandler.postDelayed(mBackgroundTask, BACKGROUND_UPDATE_DELAY);
    }
    private void updateRecommendations() {
        Intent recommendationIntent = new Intent(getActivity(), UpdateRecommendationsService.class);
        Objects.requireNonNull(getActivity()).startService(recommendationIntent);
    }
    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CATEGORY_LOADER) {
            return new CursorLoader(
                    Objects.requireNonNull(getContext()),
                    VideoContract.VideoEntry.CONTENT_URI, // Table to query
                    new String[]{"DISTINCT " + VideoContract.VideoEntry.COLUMN_CATEGORY},
                    // Only categories
                    null, // No selection clause
                    null, // No selection arguments
                    null  // Default sort order
            );
        } else {
            // Assume it is for a video.
            String category = args.getString(VideoContract.VideoEntry.COLUMN_CATEGORY);
            // This just creates a CursorLoader that gets all videos.
            return new CursorLoader(
                    Objects.requireNonNull(getContext()),
                    VideoContract.VideoEntry.CONTENT_URI, // Table to query
                    null, // Projection to return - null means return all fields
                    VideoContract.VideoEntry.COLUMN_CATEGORY + " = ?", // Selection clause
                    new String[]{category},  // Select based on the category id.
                    null // Default sort order
            );
        }
    }
    @Override
    public void onLoadFinished(@NotNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            final int loaderId = loader.getId();
            if (loaderId == CATEGORY_LOADER) {
                // Every time we have to re-get the category loader, we must re-create the sidebar.
                mCategoryRowAdapter.clear();
                // Iterate through each category entry and add it to the ArrayAdapter.
                while (!data.isAfterLast()) {
                    int categoryIndex =
                            data.getColumnIndex(VideoContract.VideoEntry.COLUMN_CATEGORY);
                    String category = data.getString(categoryIndex);
                    // Create header for this category.
                    HeaderItem header = new HeaderItem(category);
                    int videoLoaderId = category.hashCode(); // Create unique int from category.
                    CursorObjectAdapter existingAdapter = mVideoCursorAdapters.get(videoLoaderId);
                    if (existingAdapter == null) {
                        // Map video results from the database to Video objects.
                        CursorObjectAdapter videoCursorAdapter =
                                new CursorObjectAdapter(new CardPresenter());
                        videoCursorAdapter.setMapper(new VideoCursorMapper());
                        mVideoCursorAdapters.put(videoLoaderId, videoCursorAdapter);
                        ListRow row = new ListRow(header, videoCursorAdapter);
                        mCategoryRowAdapter.add(row);
                        // Start loading the videos from the database for a particular category.
                        Bundle args = new Bundle();
                        args.putString(VideoContract.VideoEntry.COLUMN_CATEGORY, category);
                        mLoaderManager.initLoader(videoLoaderId, args, this);
                    } else {
                        ListRow row = new ListRow(header, existingAdapter);
                        mCategoryRowAdapter.add(row);
                    }
                    data.moveToNext();
                }
                // Create a row for this special case with more samples.
                HeaderItem gridHeader = new HeaderItem(getString(R.string.more_samples));
                GridItemPresenter gridPresenter = new GridItemPresenter();
                ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(gridPresenter);
                gridRowAdapter.add(getString(R.string.grid_view));
                gridRowAdapter.add(getString(R.string.guidedstep_first_title));
                gridRowAdapter.add(getString(R.string.error_fragment));
                gridRowAdapter.add(getString(R.string.personal_settings));
                ListRow row = new ListRow(gridHeader, gridRowAdapter);
                mCategoryRowAdapter.add(row);
                startEntranceTransition();
                // cursors have loaded.
            } else {
                // The CursorAdapter contains a Cursor pointing to all videos.
                Objects.requireNonNull(mVideoCursorAdapters.get(loaderId)).changeCursor(data);
            }
        } else {
            // Start an Intent to fetch the videos.
            Intent serviceIntent = new Intent(getActivity(), FetchVideoService.class);
            Objects.requireNonNull(getActivity()).startService(serviceIntent);
        }
    }
    @Override
    public void onLoaderReset(@NotNull Loader<Cursor> loader) {
        int loaderId = loader.getId();
        if (loaderId != CATEGORY_LOADER) {
            Objects.requireNonNull(mVideoCursorAdapters.get(loaderId)).changeCursor(null);
        } else {
            mCategoryRowAdapter.clear();
        }
    }
    private class UpdateBackgroundTask implements Runnable {
        @Override
        public void run() {
            if (mBackgroundURI != null) {
                updateBackground(mBackgroundURI.toString());
            }
        }
    }
    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Video) {
                Video video = (Video) item;
                Intent intent = new Intent(getActivity(), VideoDetailsActivity.class);
                intent.putExtra(VideoDetailsActivity.VIDEO, video);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        Objects.requireNonNull(getActivity()),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        VideoDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            } else if (item instanceof String) {
                if (((String) item).contains(getString(R.string.grid_view))) {
                    Intent intent = new Intent(getActivity(), VerticalGridActivity.class);
                    //noinspection unchecked
                    Bundle bundle =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()))
                                    .toBundle();
                    startActivity(intent, bundle);
                } else if (((String) item).contains(getString(R.string.guidedstep_first_title))) {
                    Intent intent = new Intent(getActivity(), GuidedStepActivity.class);
                    //noinspection unchecked
                    Bundle bundle =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()))
                                    .toBundle();
                    startActivity(intent, bundle);
                } else if (((String) item).contains(getString(R.string.error_fragment))) {
                    BrowseErrorFragment errorFragment = new BrowseErrorFragment();
                    Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(R.id.main_frame, errorFragment)
                            .addToBackStack(null).commit();
                } else if(((String) item).contains(getString(R.string.personal_settings))) {
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    //noinspection unchecked
                    Bundle bundle =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()))
                                    .toBundle();
                    startActivity(intent, bundle);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }
    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Video) {
                mBackgroundURI = Uri.parse(((Video) item).bgImageUrl);
                startBackgroundTimer();
            }
        }
    }
}
