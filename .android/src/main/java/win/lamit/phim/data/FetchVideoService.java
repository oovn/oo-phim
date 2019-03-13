package win.lamit.phim.data;

import win.lamit.phim.R;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import org.json.JSONException;
import java.io.IOException;
import java.util.List;

public class FetchVideoService extends IntentService {
    private static final String TAG = "FetchVideoService";
    public FetchVideoService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {
        VideoDbBuilder builder = new VideoDbBuilder(getApplicationContext());
        try {
            List<ContentValues> contentValuesList =
                    builder.fetch(getResources().getString(R.string.catalog_url));
            ContentValues[] downloadedVideoContentValues =
                    contentValuesList.toArray(new ContentValues[0]);
            getApplicationContext().getContentResolver().bulkInsert(VideoContract.VideoEntry.CONTENT_URI,
                    downloadedVideoContentValues);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error occurred in downloading videos");
            e.printStackTrace();
        }
    }
}
