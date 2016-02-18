package info.bunny178.novel.reader.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author ISHIMARU Sohei on 2015/12/09.
 */
public class ChapterTable implements TableInterface {

    private static final String LOG_TAG = "ChapterTable";

    public static final String TABLE_NAME = "chapter";

    public static final Uri CONTENT_URI = Uri.parse("content://" + NovelDataProvider.AUTHORITY + "/" + TABLE_NAME);


    public interface Columns {
        String NOVEL_ID = "novel_id";
        String CHAPTER_ID = "chapter_id";
        String CHAPTER_TITLE = "chapter_title";
        String CHAPTER_NUMBER = "chapter_number";
        String PAGE_NUMBER = "page_number";
    }

    public ChapterTable() {
        /* NOP */
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* @formatter:off */
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + Columns.CHAPTER_ID + " INTEGER PRIMARY KEY, "
                + Columns.NOVEL_ID + " INTEGER DEFAULT 0, "
                + Columns.CHAPTER_NUMBER + " INTEGER DEFAULT 0, "
                + Columns.PAGE_NUMBER + " INTEGER DEFAULT 0, "
                + Columns.CHAPTER_TITLE + " TEXT"
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
                Columns.CHAPTER_ID,
                Columns.CHAPTER_TITLE,
                Columns.NOVEL_ID,
                Columns.PAGE_NUMBER,
                Columns.CHAPTER_NUMBER,
        };
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
            return Columns.CHAPTER_ID + " = " + value;
        }
    }
}
