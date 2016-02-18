package info.bunny178.novel.reader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import info.bunny178.novel.reader.db.GenreTable;
import info.bunny178.novel.reader.db.NovelTable;

/**
 * @author ISHIMARU Sohei on 2016/01/14.
 */
@Root(name = "genre")
public class Genre extends BaseModel implements GenreTable.Columns {

    @Element(name = "id")
    private int mGenreId;

    @Element(name = "sort_no")
    private int mSortIndex;

    @Element(name = "caption", required = false)
    private String mGenreName;

    public Genre() {
        /* NOP */
    }

    public Genre(Cursor cursor) {
        fromCursor(cursor);
    }

    public Genre(int id, String name) {
        this.mGenreId = id;
        this.mGenreName = name;
    }

    public int save(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = NovelTable.CONTENT_URI.buildUpon().appendPath(Integer.toString(mGenreId)).build();
        int count = resolver.update(uri, toContentValues(), null, null);
        if (count == 0) {
            uri = resolver.insert(NovelTable.CONTENT_URI, toContentValues());
            if (uri != null) {
                mGenreId = Integer.parseInt(uri.getLastPathSegment());
            }
        }
        return mGenreId;
    }

    public static int saveGenres(Context context, List<Genre> genres) {
        ContentResolver resolver = context.getContentResolver();
        int size = genres.size();
        ContentValues[] valuesArray = new ContentValues[size];
        for (int i = 0; i < size; i++) {
            valuesArray[i] = genres.get(i).toContentValues();
        }
        return resolver.bulkInsert(GenreTable.CONTENT_URI, valuesArray);
    }

    public static List<Genre> loadGenres(Context context) {
        List<Genre> genreList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = GenreTable.CONTENT_URI;
        Cursor c = null;
        try {
            c = resolver.query(uri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    genreList.add(new Genre(c));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return genreList;
    }

    /**
     * ジャンルの全件削除
     *
     * @param context コンテキスト
     * @return 削除件数
     */
    public static int deleteGenres(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = GenreTable.CONTENT_URI;
        return resolver.delete(uri, null, null);
    }

    public int getGenreId() {
        return mGenreId;
    }

    public String getGenreName() {
        return mGenreName;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(GENRE_ID, mGenreId);
        values.put(GENRE_NAME, mGenreName);
        values.put(SORT_INDEX, mSortIndex);
        return values;
    }

    @Override
    public void fromCursor(Cursor c) {
        mGenreId = c.getInt(c.getColumnIndex(GENRE_ID));
        mGenreName = c.getString(c.getColumnIndex(GENRE_NAME));
        mSortIndex = c.getInt(c.getColumnIndex(SORT_INDEX));
    }

    @Override
    public String toString() {
        return mGenreName;
    }
}
