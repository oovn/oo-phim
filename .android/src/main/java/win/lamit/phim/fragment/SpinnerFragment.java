package win.lamit.phim.fragment;

import win.lamit.phim.R;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;
import org.jetbrains.annotations.NotNull;

public class SpinnerFragment extends Fragment {
    @Override
    public View onCreateView(
            @NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ProgressBar progressBar = new ProgressBar(container.getContext());
        if (container instanceof FrameLayout) {
            Resources res = getResources();
            int width = res.getDimensionPixelSize(R.dimen.spinner_width);
            int height = res.getDimensionPixelSize(R.dimen.spinner_height);
            FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(width, height, Gravity.CENTER);
            progressBar.setLayoutParams(layoutParams);
        }
        return progressBar;
    }
}