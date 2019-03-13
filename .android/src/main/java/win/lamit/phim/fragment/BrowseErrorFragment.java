package win.lamit.phim.fragment;

import win.lamit.phim.R;
import android.os.Bundle;
import android.os.Handler;
import androidx.leanback.app.ErrorSupportFragment;
import java.util.Objects;

public class BrowseErrorFragment extends ErrorSupportFragment {
    private static final boolean TRANSLUCENT = true;
    private static final int TIMER_DELAY = 1000;
    private final Handler mHandler = new Handler();
    private SpinnerFragment mSpinnerFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.app_name));
        mSpinnerFragment = new SpinnerFragment();
        Objects.requireNonNull(getFragmentManager()).beginTransaction().add(R.id.main_frame, mSpinnerFragment).commit();
    }
    @Override
    public void onStart() {
        super.onStart();
        mHandler.postDelayed(() -> {
            Objects.requireNonNull(getFragmentManager()).beginTransaction().remove(mSpinnerFragment).commit();
            setErrorContent();
        }, TIMER_DELAY);
    }
    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
        Objects.requireNonNull(getFragmentManager()).beginTransaction().remove(mSpinnerFragment).commit();
    }
    private void setErrorContent() {
        setImageDrawable(getResources().getDrawable(R.drawable.lb_ic_sad_cloud, null));
        setMessage(getResources().getString(R.string.error_fragment_message));
        setDefaultBackground(TRANSLUCENT);
        setButtonText(getResources().getString(R.string.dismiss_error));
        setButtonClickListener(arg0 -> {
            Objects.requireNonNull(getFragmentManager()).beginTransaction().remove(BrowseErrorFragment.this).commit();
            getFragmentManager().popBackStack();
        });
    }
}
