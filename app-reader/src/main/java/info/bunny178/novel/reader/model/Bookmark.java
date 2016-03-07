package info.bunny178.novel.reader.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import info.bunny178.novel.reader.db.BookmarkTable;
import info.bunny178.novel.reader.db.ChapterTable;
import info.bunny178.novel.reader.db.NovelTable;


/**
 * @author ISHIMARU Sohei on 2016/03/01.
 */
public class Bookmark extends BaseModel implements BookmarkTable.Columns {

    private int mBookmarkId;
    private int mPageNumber;
    private int mNovelId;
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

    public int getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(int pageNumber) {
        mPageNumber = pageNumber;
    }

    public long getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(long createDate) {
        mCreateDate = createDate;
    }

    public int getNovelId() {
        return mNovelId;
    }

    public void setNovelId(int novelId) {
        mNovelId = novelId;
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

    public static Bookmark load(Context context, int bookmarkId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = BookmarkTable.CONTENT_URI;
        String where = BookmarkTable.Columns.BOOKMARK_ID + " = " + bookmarkId;
        Cursor c = null;
        try {
            c = resolver.query(uri, null, where, null, null);
            if (c != null && c.moveToFirst()) {
                return new Bookmark(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public static List<Bookmark> loadBookmarks(Context context) {
        List<Bookmark> bookmarks = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = BookmarkTable.CONTENT_URI;
        Cursor c = null;
        try {
            String sortOrder = BookmarkTable.Columns.CREATE_DATE + " DESC";
            c = resolver.query(uri, null, null, null, sortOrder);
            if (c != null && c.moveToFirst()) {
                do {
                    bookmarks.add(new Bookmark(c));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bookmarks;
    }

    public static int deleteBookmark(Context context, int bookmarkId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = BookmarkTable.CONTENT_URI.buildUpon().appendPath(Integer.toString(bookmarkId)).build();
        return resolver.delete(uri, null, null);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        if (0 < mBookmarkId) {
            values.put(BOOKMARK_ID, mBookmarkId);
        }
        values.put(PAGE_NUMBER, mPageNumber);
        values.put(NOVEL_ID, mNovelId);
        values.put(CREATE_DATE, mCreateDate);
        return values;
    }

    @Override
    public void fromCursor(Cursor c) {
        mBookmarkId = c.getInt(c.getColumnIndex(BOOKMARK_ID));
        mPageNumber = c.getInt(c.getColumnIndex(PAGE_NUMBER));
        mNovelId = c.getInt(c.getColumnIndex(NOVEL_ID));
        mCreateDate = c.getLong(c.getColumnIndex(CREATE_DATE));
    }
}
