package info.bunny178.novel.reader.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author ISHIMARU Sohei on 2016/03/01.
 */
public class BookmarkTable implements TableInterface {

    private static final String LOG_TAG = "BookmarkTable";

    public static final String TABLE_NAME = "bookmark";

    public static final Uri CONTENT_URI = Uri.parse("content://" + NovelDataProvider.AUTHORITY + "/" + TABLE_NAME);

    public interface Columns {
        String BOOKMARK_ID = "bookmark_id";
        String PAGE_ID = "page_id";
        String CREATE_DATE = "create_date";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
                /* @formatter:off */
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + Columns.BOOKMARK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Columns.PAGE_ID + " INTEGER NOT NULL, "
                + Columns.CREATE_DATE + " INTEGER"
                + ")";
        /* @formatter:on */
        db.execSQL(sql);
        Log.d(LOG_TAG, "  sql : " + sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            onCreate(db);
        }
    }

    @Override
    public String[] getColumnNames() {
        /* @formatter:off */
        return new String[] {
                Columns.BOOKMARK_ID,
                Columns.PAGE_ID,
                Columns.CREATE_DATE,
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
            return Columns.BOOKMARK_ID + " = " + value;
        }
    }
}
