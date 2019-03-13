package win.lamit.phim.fragment;

import win.lamit.phim.R;
import win.lamit.phim.activity.SearchActivity;
import win.lamit.phim.activity.VideoDetailsActivity;
import win.lamit.phim.data.VideoContract;
import win.lamit.phim.model.Video;
import win.lamit.phim.model.VideoCursorMapper;
import win.lamit.phim.presenter.CardPresenter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.CursorObjectAdapter;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class VerticalGridFragment extends VerticalGridSupportFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int NUM_COLUMNS = 5;
    private final CursorObjectAdapter mVideoCursorAdapter = new CursorObjectAdapter(new CardPresenter());
    private static final int ALL_VIDEOS_LOADER = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoCursorAdapter.setMapper(new VideoCursorMapper());
        setAdapter(mVideoCursorAdapter);
        setTitle(getString(R.string.vertical_grid_title));
        if (savedInstanceState == null) {
            prepareEntranceTransition();
        }
        setupFragment();
    }
    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
        setGridPresenter(gridPresenter);
        //noinspection deprecation
        getLoaderManager().initLoader(ALL_VIDEOS_LOADER, null, this);
        // After 500ms, start the animation to transition the cards into view.
        new Handler().postDelayed(this::startEntranceTransition, 500);
        setOnSearchClickedListener(view -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }
    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                Objects.requireNonNull(getActivity()),
                VideoContract.VideoEntry.CONTENT_URI,
                null, // projection
                null, // selection
                null, // selection clause
                null  // sort order
        );
    }
    @Override
    public void onLoadFinished(@NotNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == ALL_VIDEOS_LOADER && cursor != null && cursor.moveToFirst()) {
            mVideoCursorAdapter.changeCursor(cursor);
        }
    }
    @Override
    public void onLoaderReset(@NotNull Loader<Cursor> loader) {
        mVideoCursorAdapter.changeCursor(null);
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
            }
        }
    }
    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                RowPresenter.ViewHolder rowViewHolder, Row row) {
        }
    }
}
