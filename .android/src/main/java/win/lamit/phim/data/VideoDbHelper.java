package win.lamit.phim.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import win.lamit.phim.data.VideoContract.VideoEntry;

public class VideoDbHelper extends SQLiteOpenHelper {
    // Change this when you change the database schema.
    private static final int DATABASE_VERSION = 4;
    // The name of our database.
    private static final String DATABASE_NAME = "leanback.db";
    VideoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold videos.
        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY," +
                VideoEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_URL + " TEXT UNIQUE NOT NULL, " + // Make the URL unique.
                VideoEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_DESC + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_BG_IMAGE_URL + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_STUDIO + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_CARD_IMG + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_CONTENT_TYPE + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_IS_LIVE + " INTEGER DEFAULT 0, " +
                VideoEntry.COLUMN_VIDEO_WIDTH + " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_HEIGHT + " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_AUDIO_CHANNEL_CONFIG + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_PURCHASE_PRICE + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_RENTAL_PRICE + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_RATING_STYLE + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_RATING_SCORE + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_PRODUCTION_YEAR + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_DURATION + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_ACTION + " TEXT NOT NULL " +
                " );";
        // Do the creating of the databases.
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simply discard all old data and start over when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do the same thing as upgrading...
        onUpgrade(db, oldVersion, newVersion);
    }
}