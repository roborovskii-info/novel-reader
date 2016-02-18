package info.bunny178.novel.reader.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import info.bunny178.novel.reader.db.NovelDataProvider;
import info.bunny178.novel.reader.db.TableInterface;

/**
 * @author ISHIMARU Sohei on 2015/12/09.
 */
public class PageTable implements TableInterface {

    private static final String LOG_TAG = "PageTable";

    public static final String TABLE_NAME = "page";

    public static final Uri CONTENT_URI = Uri.parse("content://" + NovelDataProvider.AUTHORITY + "/" + TABLE_NAME);

    public interface Columns {

        String PAGE_ID = "page_id";
        String PAGE_NUMBER = "page_number";
        String UPDATE_DATE = "update_date";
        String PAGE_BODY = "page_body";
        String IMAGE_URL_LARGE = "image_url_large";
        String IMAGE_URL_MEDIUM = "image_url_medium";
        String IMAGE_URL_SMALL = "image_url_small";
        String CHAPTER_ID = "chapter_id";
        String NOVEL_ID = "novel_id";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* @formatter:off */
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + Columns.PAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Columns.PAGE_NUMBER + " INTEGER, "
                + Columns.UPDATE_DATE + " INTEGER, "
                + Columns.PAGE_BODY + " TEXT, "
                + Columns.IMAGE_URL_LARGE + " TEXT, "
                + Columns.IMAGE_URL_MEDIUM + " TEXT, "
                + Columns.IMAGE_URL_SMALL + " TEXT, "
                + Columns.CHAPTER_ID + " INTEGER, "
                + Columns.NOVEL_ID + " INTEGER"
                + ")";
        /* @formatter:on */
        db.execSQL(sql);
        Log.d(LOG_TAG, "  sql : " + sql);
        String index = "CREATE INDEX index_page_num ON " + TABLE_NAME + " ("
                + Columns.NOVEL_ID + ", "
                + Columns.PAGE_NUMBER
                + ");";
        Log.d(LOG_TAG, "  sql : " + index);
        db.execSQL(index);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* NOP */
    }

    @Override
    public String[] getColumnNames() {
        /* @formatter:off */
        return new String[] {
                Columns.PAGE_ID,
                Columns.PAGE_NUMBER,
                Columns.UPDATE_DATE,
                Columns.PAGE_BODY,
                Columns.IMAGE_URL_LARGE,
                Columns.IMAGE_URL_MEDIUM,
                Columns.IMAGE_URL_SMALL,
                Columns.CHAPTER_ID,
                Columns.NOVEL_ID,
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
            return Columns.PAGE_ID + " = " + value;
        }
    }
}
