package win.lamit.phim.data;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class VideoContract {
    // The name for the entire content provider.
    static final String CONTENT_AUTHORITY = "win.lamit.phim";
    // Base of all URIs that will be used to contact the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // The content paths.
    static final String PATH_VIDEO = "video";
    public static final class VideoEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();
        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_VIDEO;
        // Name of the video table.
        static final String TABLE_NAME = "video";
        // Column with the foreign key into the category table.
        public static final String COLUMN_CATEGORY = "category";
        // Name of the video.
        public static final String COLUMN_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
        // Description of the video.
        public static final String COLUMN_DESC = SearchManager.SUGGEST_COLUMN_TEXT_2;
        // The url to the video content.
        public static final String COLUMN_VIDEO_URL = "video_url";
        // The url to the background image.
        public static final String COLUMN_BG_IMAGE_URL = "bg_image_url";
        // The studio name.
        public static final String COLUMN_STUDIO = "studio";
        // The card image for the video.
        public static final String COLUMN_CARD_IMG = SearchManager.SUGGEST_COLUMN_RESULT_CARD_IMAGE;
        // The content type of the video.
        static final String COLUMN_CONTENT_TYPE = SearchManager.SUGGEST_COLUMN_CONTENT_TYPE;
        // Whether the video is live or not.
        static final String COLUMN_IS_LIVE = SearchManager.SUGGEST_COLUMN_IS_LIVE;
        // The width of the video.
        static final String COLUMN_VIDEO_WIDTH = SearchManager.SUGGEST_COLUMN_VIDEO_WIDTH;
        // The height of the video.
        static final String COLUMN_VIDEO_HEIGHT = SearchManager.SUGGEST_COLUMN_VIDEO_HEIGHT;
        // The audio channel configuration.
        static final String COLUMN_AUDIO_CHANNEL_CONFIG = SearchManager.SUGGEST_COLUMN_AUDIO_CHANNEL_CONFIG;
        // The purchase price of the video.
        static final String COLUMN_PURCHASE_PRICE = SearchManager.SUGGEST_COLUMN_PURCHASE_PRICE;
        // The rental price of the video.
        static final String COLUMN_RENTAL_PRICE = SearchManager.SUGGEST_COLUMN_RENTAL_PRICE;
        // The rating style of the video.
        static final String COLUMN_RATING_STYLE = SearchManager.SUGGEST_COLUMN_RATING_STYLE;
        // The score of the rating.
        static final String COLUMN_RATING_SCORE = SearchManager.SUGGEST_COLUMN_RATING_SCORE;
        // The year the video was produced.
        static final String COLUMN_PRODUCTION_YEAR = SearchManager.SUGGEST_COLUMN_PRODUCTION_YEAR;
        // The duration of the video.
        static final String COLUMN_DURATION = SearchManager.SUGGEST_COLUMN_DURATION;
        // The action intent for the result.
        static final String COLUMN_ACTION = SearchManager.SUGGEST_COLUMN_INTENT_ACTION;
        // Returns the Uri referencing a video with the specified id.
        static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}