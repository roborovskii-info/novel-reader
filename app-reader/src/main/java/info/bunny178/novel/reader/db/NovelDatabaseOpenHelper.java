package info.bunny178.novel.reader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ISHIMARU Sohei on 2015/09/14.
 */
public class NovelDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "NovelDatabaseOpenHelper";

    /**
     * テーブルインスタンスを格納したマップ
     */

    private static Map<String, TableInterface> sTableMap;

    static {
        sTableMap = new HashMap<>();
        sTableMap.put(NovelTable.TABLE_NAME, new NovelTable());
        sTableMap.put(ChapterTable.TABLE_NAME, new ChapterTable());
        sTableMap.put(PageTable.TABLE_NAME, new PageTable());
        sTableMap.put(GenreTable.TABLE_NAME, new GenreTable());
    }

    public NovelDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "+ onCreate(SQLiteDatabase)");
        for (Map.Entry<String, TableInterface> table : sTableMap.entrySet()) {
            table.getValue().onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "+ onUpgrade(SQLiteDatabase, int, int)");
        for (Map.Entry<String, TableInterface> table : sTableMap.entrySet()) {
            table.getValue().onUpgrade(db, oldVersion, newVersion);
        }
    }


    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public static TableInterface[] getTables() {
        return sTableMap.values().toArray(new TableInterface[0]);
    }

    public TableInterface getTable(String tableName) {
        return sTableMap.get(tableName);
    }

    public void dropTable(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
}
