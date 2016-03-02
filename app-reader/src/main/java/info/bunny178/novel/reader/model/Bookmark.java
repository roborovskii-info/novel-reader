package info.bunny178.novel.reader.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import info.bunny178.novel.reader.db.BookmarkTable;


/**
 * @author ISHIMARU Sohei on 2016/03/01.
 */
public class Bookmark extends BaseModel implements BookmarkTable.Columns {

    private int mBookmarkId;
    private int mPageId;
    private long mCreateDate;

    public Bookmark() {
        /* NOP */
    }

    public Bookmark(Cursor c) {
        fromCursor(c);
    }

    public int getBookmarkId() {
        return mBookmarkId;
    }

    public void setBookmarkId(int bookmarkId) {
        mBookmarkId = bookmarkId;
    }

    public int getPageId() {
        return mPageId;
    }

    public void setPageId(int pageId) {
        mPageId = pageId;
    }

    public long getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(long createDate) {
        mCreateDate = createDate;
    }

    public int save(Context context) {
        ContentResolver resolver = context.getContentResolver();
        String where = BookmarkTable.Columns.BOOKMARK_ID + " = " + mBookmarkId;
        int count = resolver.update(BookmarkTable.CONTENT_URI, toContentValues(), where, null);
        if (count == 0) {
            Uri uri = resolver.insert(BookmarkTable.CONTENT_URI, toContentValues());
            if (uri != null) {
                mBookmarkId = Integer.parseInt(uri.getLastPathSegment());
            }
        }
        return mBookmarkId;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        if (0 < mBookmarkId) {
            values.put(BOOKMARK_ID, mBookmarkId);
        }
        values.put(PAGE_ID, mPageId);
        values.put(CREATE_DATE, mCreateDate);
        return values;
    }

    @Override
    public void fromCursor(Cursor c) {
        mBookmarkId = c.getInt(c.getColumnIndex(BOOKMARK_ID));
        mPageId = c.getInt(c.getColumnIndex(PAGE_ID));
        mCreateDate = c.getLong(c.getColumnIndex(CREATE_DATE));
    }
}
