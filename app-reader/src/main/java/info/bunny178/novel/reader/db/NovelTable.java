package info.bunny178.novel.reader.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/*

 */

/**
 * @author ISHIMARU Sohei on 2015/09/14.
 */
public class NovelTable implements TableInterface {

    private static final String LOG_TAG = "NovelTable";

    public static final String TABLE_NAME = "novel";

    public static final Uri CONTENT_URI = Uri.parse("content://" + NovelDataProvider.AUTHORITY + "/" + TABLE_NAME);

    public interface Columns {

        String NOVEL_ID = "novel_id";
        String NOVEL_TITLE = "title";
        String GENRE_NAME = "genre_name";
        String AUTHOR_ID = "author_id";
        String AUTHOR_NAME = "author_name";
        String UPDATE_DATE = "update_date";
        String NOVEL_STATUS = "novel_status";
        String PAGE_COUNT = "page_count";
        String VIEW_COUNT = "view_count";
        String REVIEW_COUNT = "review_count";
        String RATING_AVERAGE = "rating_average";
        String CONTENT_RATING = "content_rating";
        String NOVEL_CAPTION = "novel_caption";
        String LARGE_IMAGE_URL = "large_image_url";
        String MEDIUM_IMAGE_URL = "medium_image_url";
        String SMALL_IMAGE_URL = "small_image_url";
        /** ダウンロードURL */
        String DOWNLOAD_URL = "download_url";
        /** ダウンロード日時 */
        String DOWNLOAD_DATE = "download_date";
        /** どこまで読んだか。0基底 */
        String READ_INDEX = "read_index";
    }

    public NovelTable() {
        /* NOP */
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* @formatter:off */
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + Columns.NOVEL_ID + " INTEGER PRIMARY KEY, "
                + Columns.NOVEL_TITLE + " TEXT NOT NULL, "
                + Columns.GENRE_NAME + " TEXT, "
                + Columns.AUTHOR_ID + " INTEGER NOT NULL, "
                + Columns.AUTHOR_NAME + " TEXT, "
                + Columns.UPDATE_DATE + " TEXT, "
                + Columns.NOVEL_STATUS + " INTEGER, "
                + Columns.PAGE_COUNT + " INTEGER, "
                + Columns.VIEW_COUNT + " INTEGER, "
                + Columns.REVIEW_COUNT + " INTEGER, "
                + Columns.RATING_AVERAGE + " REAL DEFAULT 0, "
                + Columns.CONTENT_RATING + " INTEGER, "
                + Columns.NOVEL_CAPTION + " TEXT, "
                + Columns.LARGE_IMAGE_URL + " TEXT, "
                + Columns.MEDIUM_IMAGE_URL + " TEXT, "
                + Columns.SMALL_IMAGE_URL + " TEXT, "
                + Columns.DOWNLOAD_URL + " TEXT, "
                + Columns.DOWNLOAD_DATE + " INTEGER DEFAULT 0,"
                + Columns.READ_INDEX + " INTEGER DEFAULT 0"
                + ")";
        /* @formatter:on */
        db.execSQL(sql);
        Log.d(LOG_TAG, "  sql : " + sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* NOP */
    }

    @Override
    public String[] getColumnNames() {
        /* @formatter:off */
        return new String[] {
                Columns.NOVEL_ID,
                Columns.NOVEL_TITLE,
                Columns.GENRE_NAME,
                Columns.AUTHOR_ID,
                Columns.AUTHOR_NAME,
                Columns.UPDATE_DATE,
                Columns.NOVEL_STATUS,
                Columns.PAGE_COUNT,
                Columns.VIEW_COUNT,
                Columns.REVIEW_COUNT,
                Columns.RATING_AVERAGE,
                Columns.CONTENT_RATING,
                Columns.NOVEL_CAPTION,
                Columns.LARGE_IMAGE_URL,
                Columns.MEDIUM_IMAGE_URL,
                Columns.SMALL_IMAGE_URL,
                Columns.DOWNLOAD_URL,
                Columns.DOWNLOAD_DATE,
                Columns.READ_INDEX,
        };
        /* @formatter:on */
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getItemSelector(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        } else {
            return Columns.NOVEL_ID + " = " + value;
        }
    }

}
