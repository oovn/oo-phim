package win.lamit.phim.activity;

import win.lamit.phim.R;
import android.os.Bundle;

public class VideoDetailsActivity extends LeanbackActivity {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String VIDEO = "Video";
    public static final String NOTIFICATION_ID = "NotificationId";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_details);
    }
}
