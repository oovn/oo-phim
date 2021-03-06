package win.lamit.phim.data;

import win.lamit.phim.R;
import androidx.annotation.NonNull;
import android.content.ContentValues;
import android.content.Context;
import android.media.Rating;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

class VideoDbBuilder {
    private static final String TAG_MEDIA = "videos";
    private static final String TAG_GOOGLE_VIDEOS = "googlevideos";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_STUDIO = "studio";
    private static final String TAG_SOURCES = "sources";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_CARD_THUMB = "card";
    private static final String TAG_BACKGROUND = "background";
    private static final String TAG_TITLE = "title";
    private static final String TAG = "VideoDbBuilder";
    private Context mContext;
    VideoDbBuilder(Context mContext) {
        this.mContext = mContext;
    }
    @NonNull List<ContentValues> fetch(String url)
            throws IOException, JSONException {
        JSONObject videoData = fetchJSON(url);
        return buildMedia(videoData);
    }
    private List<ContentValues> buildMedia(JSONObject jsonObj) throws JSONException {
        JSONArray categoryArray = jsonObj.getJSONArray(TAG_GOOGLE_VIDEOS);
        List<ContentValues> videosToInsert = new ArrayList<>();
        for (int i = 0; i < categoryArray.length(); i++) {
            JSONArray videoArray;
            JSONObject category = categoryArray.getJSONObject(i);
            String categoryName = category.getString(TAG_CATEGORY);
            videoArray = category.getJSONArray(TAG_MEDIA);
            for (int j = 0; j < videoArray.length(); j++) {
                JSONObject video = videoArray.getJSONObject(j);
                // If there are no URLs, skip this video entry.
                JSONArray urls = video.optJSONArray(TAG_SOURCES);
                if (urls == null || urls.length() == 0) {
                    continue;
                }
                String title = video.optString(TAG_TITLE);
                String description = video.optString(TAG_DESCRIPTION);
                String videoUrl = (String) urls.get(0); // Get the first video only.
                String bgImageUrl = video.optString(TAG_BACKGROUND);
                String cardImageUrl = video.optString(TAG_CARD_THUMB);
                String studio = video.optString(TAG_STUDIO);
                //
                ContentValues videoValues = new ContentValues();
                videoValues.put(VideoContract.VideoEntry.COLUMN_CATEGORY, categoryName);
                videoValues.put(VideoContract.VideoEntry.COLUMN_NAME, title);
                videoValues.put(VideoContract.VideoEntry.COLUMN_DESC, description);
                videoValues.put(VideoContract.VideoEntry.COLUMN_VIDEO_URL, videoUrl);
                videoValues.put(VideoContract.VideoEntry.COLUMN_CARD_IMG, cardImageUrl);
                videoValues.put(VideoContract.VideoEntry.COLUMN_BG_IMAGE_URL, bgImageUrl);
                videoValues.put(VideoContract.VideoEntry.COLUMN_STUDIO, studio);
                // Fixed defaults.
                videoValues.put(VideoContract.VideoEntry.COLUMN_CONTENT_TYPE, "video/mp4");
                videoValues.put(VideoContract.VideoEntry.COLUMN_IS_LIVE, false);
                videoValues.put(VideoContract.VideoEntry.COLUMN_AUDIO_CHANNEL_CONFIG, "2.0");
                videoValues.put(VideoContract.VideoEntry.COLUMN_PRODUCTION_YEAR, 2014);
                videoValues.put(VideoContract.VideoEntry.COLUMN_DURATION, 0);
                videoValues.put(VideoContract.VideoEntry.COLUMN_RATING_STYLE,
                        Rating.RATING_5_STARS);
                videoValues.put(VideoContract.VideoEntry.COLUMN_RATING_SCORE, 3.5f);
                if (mContext != null) {
                    videoValues.put(VideoContract.VideoEntry.COLUMN_PURCHASE_PRICE,
                            mContext.getResources().getString(R.string.buy_2));
                    videoValues.put(VideoContract.VideoEntry.COLUMN_RENTAL_PRICE,
                            mContext.getResources().getString(R.string.rent_2));
                    videoValues.put(VideoContract.VideoEntry.COLUMN_ACTION,
                            mContext.getResources().getString(R.string.global_search));
                }
                // Get these dimensions.
                videoValues.put(VideoContract.VideoEntry.COLUMN_VIDEO_WIDTH, 1280);
                videoValues.put(VideoContract.VideoEntry.COLUMN_VIDEO_HEIGHT, 720);
                videosToInsert.add(videoValues);
            }
        }
        return videosToInsert;
    }
    private JSONObject fetchJSON(String urlString) throws JSONException, IOException {
        BufferedReader reader = null;
        java.net.URL url = new java.net.URL(urlString);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
                    StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            return new JSONObject(json);
        } finally {
            urlConnection.disconnect();
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "JSON feed closed", e);
                }
            }
        }
    }
}
