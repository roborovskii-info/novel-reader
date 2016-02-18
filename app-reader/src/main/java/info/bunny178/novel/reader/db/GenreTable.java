package info.bunny178.novel.reader.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author ISHIMARU Sohei on 2015/12/09.
 */
public class GenreTable implements TableInterface {
    private static final String LOG_TAG = "GenreTable";

    public static final String TABLE_NAME = "genre";

    public static final Uri CONTENT_URI = Uri.parse("content://" + NovelDataProvider.AUTHORITY + "/" + TABLE_NAME);

    public interface Columns {
        String GENRE_ID = "genre_id";
        String GENRE_NAME = "genre_name";
        String SORT_INDEX = "sort_index";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* @formatter:off */
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + Columns.GENRE_ID + " INTEGER PRIMARY KEY, "
                + Columns.GENRE_NAME + " TEXT, "
                + Columns.SORT_INDEX + " INTEGER"
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
                Columns.GENRE_ID,
                Columns.GENRE_NAME,
                Columns.SORT_INDEX,
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
            return Columns.GENRE_ID + " = " + value;
        }
    }
}
