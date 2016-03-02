package info.bunny178.novel.reader.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * <pre>
 * CREATE TABLE IF NOT EXISTS chapter(
 *      chapter_id INTEGER PRIMARY KEY,
 *      novel_id INTEGER DEFAULT 0,
 *      chapter_number INTEGER DEFAULT 0,
 *      page_number INTEGER DEFAULT 0,
 *      chapter_title TEXT
 * );
 * CREATE TABLE IF NOT EXISTS genre(
 *      genre_id INTEGER PRIMARY KEY,
 *      genre_name TEXT,
 *      sort_index INTEGER
 * );
 * CREATE TABLE IF NOT EXISTS novel(
 *      novel_id INTEGER PRIMARY KEY,
 *      title TEXT NOT NULL,
 *      genre_name TEXT,
 *      author_id INTEGER NOT NULL,
 *      author_name TEXT,
 *      update_date TEXT,
 *      novel_status INTEGER, 
 *      page_count INTEGER,
 *      view_count INTEGER,
 *      review_count INTEGER,
 *      rating_average REAL DEFAULT 0,
 *      content_rating INTEGER,
 *      novel_caption TEXT,
 *      large_image_url TEXT,
 *      medium_image_url TEXT,
 *      small_image_url TEXT,
 *      download_url TEXT,
 *      download_date INTEGER DEFAULT 0,
 *      read_index INTEGER DEFAULT 0
 * );
 * CREATE TABLE IF NOT EXISTS bookmark(
 *      bookmark_id INTEGER PRIMARY KEY AUTOINCREMENT,
 *      page_id INTEGER NOT NULL,
 *      create_date INTEGER
 * )
 * CREATE TABLE IF NOT EXISTS page(
 *      page_id INTEGER PRIMARY KEY AUTOINCREMENT,
 *      page_number INTEGER,
 *      update_date INTEGER,
 *      page_body TEXT,
 *      image_url_large TEXT,
 *      image_url_medium TEXT,
 *      image_url_small TEXT,
 *      chapter_id INTEGER,
 *      novel_id INTEGER
 * );
 * CREATE INDEX index_page_num ON page (
 *      novel_id, page_number
 * );
 * </pre>
 *
 * @author ISHIMARU Sohei on 2015/09/14.
 */
public class NovelDataProvider extends ContentProvider {

    private static final String LOG_TAG = "NovelDataProvider";

    public static final String DATABASE_NAME = "novels.sqlite";

    public static final int DATABASE_VERSION = 2;

    public static final String AUTHORITY = "info.bunny178.novel.reader";

    private static final int URI_PATTERN_DIR = 1;

    private static final int URI_PATTERN_ITEM = 2;

    private NovelDatabaseOpenHelper mDatabase;

    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        TableInterface[] tables = NovelDatabaseOpenHelper.getTables();
        for (TableInterface table : tables) {
            String tableName = table.getTableName();
            sUriMatcher.addURI(AUTHORITY, tableName, URI_PATTERN_DIR);
            sUriMatcher.addURI(AUTHORITY, tableName + "/#", URI_PATTERN_ITEM);
        }
    }

    public NovelDataProvider() {
        /* NOP */
    }

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "+ onCreate()");
        mDatabase = new NovelDatabaseOpenHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        Log.v(LOG_TAG, "+ query(Uri, String[], String, String[] String) : " + uri.toString());
        int pattern = sUriMatcher.match(uri);
        String tableName = pickTableName(uri);
        if (!isValidUri(tableName)) {
            throw new IllegalArgumentException("Unsupported URI " + uri + " (" + pattern + ")");
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableName);

        switch (pattern) {
            case URI_PATTERN_DIR:
                break;
            case URI_PATTERN_ITEM:
                String uniqueKey = uri.getLastPathSegment();
                String itemSelector = mDatabase.getTable(tableName).getItemSelector(uniqueKey);
                selection = DatabaseUtils.concatenateWhere(itemSelector, selection);
                break;
        }

        /* limitを付与 */
        String limit = uri.getQueryParameter("limit");

        /* distinctを付与 */
        if (uri.getQueryParameter("distinct") != null) {
            qb.setDistinct(true);
        }

        SQLiteDatabase db = mDatabase.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "+ insert(Uri, ContentValues)");
        int pattern = sUriMatcher.match(uri);
        String tableName = pickTableName(uri);
        if (!isValidUri(tableName)) {
            throw new IllegalArgumentException("Unsupported URI " + uri + " (" + pattern + ")");
        }

        /* Item指定は許可しない */
        if (pattern == URI_PATTERN_ITEM) {
            throw new IllegalArgumentException("Only the / URI is valid for insertion.");
        }

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        long rowId = db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Uri returnUri = ContentUris.withAppendedId(uri, rowId);

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
        Log.d(LOG_TAG, "+ bulkInsert(Uri, @NonNull ContentValues) : " + uri.toString());
        int pattern = sUriMatcher.match(uri);
        String tableName = pickTableName(uri);

        if (!isValidUri(tableName)) {
            throw new IllegalArgumentException("Unsupported URI " + uri + " (" + pattern + ")");
        }

        /* Item指定は許可しない */
        if (pattern == URI_PATTERN_ITEM) {
            throw new IllegalArgumentException("Only the / URI is valid for insertion.");
        }

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        db.enableWriteAheadLogging();
        int count = 0;
        try {
            db.beginTransaction();
            for (ContentValues values : valuesArray) {
                db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                ++count;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "+ update(Uri, ContentValues, String, String[]) : " + uri.toString());
        int pattern = sUriMatcher.match(uri);
        String tableName = pickTableName(uri);
        if (!isValidUri(tableName)) {
            throw new IllegalArgumentException("Unsupported URI " + uri + " (" + pattern + ")");
        }

        if (pattern == URI_PATTERN_ITEM) {
            String uniqueKey = uri.getLastPathSegment();
            String itemSelector = mDatabase.getTable(tableName).getItemSelector(uniqueKey);
            selection = DatabaseUtils.concatenateWhere(itemSelector, selection);
        }

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        int count = db.update(tableName, values, selection, selectionArgs);
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "+ delete(Uri, String, String[]) : " + uri.toString());
        int pattern = sUriMatcher.match(uri);
        String tableName = pickTableName(uri);
        if (!isValidUri(tableName)) {
            throw new IllegalArgumentException("Unsupported URI " + uri + " (" + pattern + ")");
        }

        if (pattern == URI_PATTERN_ITEM) {
            String uniqueKey = uri.getLastPathSegment();
            String itemSelector = mDatabase.getTable(tableName).getItemSelector(uniqueKey);
            selection = DatabaseUtils.concatenateWhere(itemSelector, selection);
        }

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        int count = db.delete(tableName, selection, selectionArgs);

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int pattern = sUriMatcher.match(uri);
        String lastPath = uri.getLastPathSegment();
        switch (pattern) {
            case URI_PATTERN_DIR:
                return "vnd.android.cursor.dir/vnd.bunny178." + lastPath;
            case URI_PATTERN_ITEM:
                return "vnd.android.cursor.item/vnd.bunny178." + lastPath;

        }
        throw new IllegalArgumentException("Unsupported URI " + uri + " (" + pattern + ")");
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase.getWritableDatabase();
    }

    /**
     * URIからテーブル名を取り出す
     *
     * @param uri リクエストされたURI
     * @return URIで指定されたテーブル名。
     */
    private String pickTableName(@NonNull Uri uri) {
        return uri.getPathSegments().get(0);
    }

    /**
     * テーブルが存在するものかどうかをチェックし、結果を返す。
     */
    private boolean isValidUri(@NonNull String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            return false;
        }
        TableInterface[] tables = NovelDatabaseOpenHelper.getTables();
        for (TableInterface table : tables) {
            if (table.getTableName().equals(tableName)) {
                return true;
            }
        }
        return false;
    }
}
