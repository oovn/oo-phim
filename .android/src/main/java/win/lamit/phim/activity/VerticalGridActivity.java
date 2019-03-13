package win.lamit.phim.activity;

import win.lamit.phim.R;
import android.os.Bundle;

public class VerticalGridActivity extends LeanbackActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.grid_bg);
    }
}
