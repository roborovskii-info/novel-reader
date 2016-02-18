package info.bunny178.novel.reader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.content.ContentValues;
import android.database.Cursor;

import info.bunny178.novel.reader.db.GenreTable;

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
