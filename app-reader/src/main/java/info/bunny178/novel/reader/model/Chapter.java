package info.bunny178.novel.reader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import info.bunny178.novel.reader.db.ChapterTable;

/**
 * @author ISHIMARU Sohei on 2015/09/18.
 */
@Root(name = "chapter")
public class Chapter extends BaseModel implements ChapterTable.Columns {

    private int mChapterId;

    private int mNovelId;

    private int mChapterNumber;

    private int mPageNumber;

    @Element(name = "title")
    private String mChapterTitle;

    @ElementList(name = "pages")
    private List<Page> mPageList;

    public Chapter() {
        /* NOP */
    }

    public Chapter(Cursor cursor) {
        fromCursor(cursor);
    }

    public int save(Context context) {
        ContentResolver resolver = context.getContentResolver();
        String where = ChapterTable.Columns.CHAPTER_ID + " = " + mChapterId;
        int count = resolver.update(ChapterTable.CONTENT_URI, toContentValues(), where, null);
        if (count == 0) {
            Uri uri = resolver.insert(ChapterTable.CONTENT_URI, toContentValues());
            if (uri != null) {
                mChapterId = Integer.parseInt(uri.getLastPathSegment());
            }
        }
        return mChapterId;
    }

    public static Chapter load(Context context, int chapterId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = ChapterTable.CONTENT_URI;
        String where = ChapterTable.Columns.CHAPTER_ID + " = " + chapterId;
        Cursor c = null;
        try {
            c = resolver.query(uri, null, where, null, null);
            if (c != null && c.moveToFirst()) {
                return new Chapter(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public static int deleteChapters(Context context, int novelId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = ChapterTable.CONTENT_URI;
        String where = ChapterTable.Columns.NOVEL_ID + " = " + novelId;

        return resolver.delete(uri, where, null);
    }

    public static List<Chapter> loadChapters(Context context, int novelId) {
        List<Chapter> chapterList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = ChapterTable.CONTENT_URI;
        Cursor c = null;
        try {
            String where = ChapterTable.Columns.NOVEL_ID + " = " + novelId;
            String sortOrder = ChapterTable.Columns.CHAPTER_NUMBER + " ASC";
            c = resolver.query(uri, null, where, null, sortOrder);
            if (c != null && c.moveToFirst()) {
                do {
                    chapterList.add(new Chapter(c));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return chapterList;
    }

    public int getChapterId() {
        return mChapterId;
    }

    public void setChapterId(int chapterId) {
        mChapterId = chapterId;
    }

    public int getNovelId() {
        return mNovelId;
    }

    public void setNovelId(int novelId) {
        mNovelId = novelId;
    }

    public int getChapterNumber() {
        return mChapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        mChapterNumber = chapterNumber;
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(int pageNumber) {
        mPageNumber = pageNumber;
    }

    public String getChapterTitle() {
        return mChapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        mChapterTitle = chapterTitle;
    }

    public List<Page> getPageList() {
        return mPageList;
    }

    public void setPageList(List<Page> pageList) {
        mPageList = pageList;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        if (0 < mChapterId) {
            values.put(CHAPTER_ID, mChapterId);
        }
        values.put(CHAPTER_NUMBER, mChapterNumber);
        values.put(NOVEL_ID, mNovelId);
        values.put(CHAPTER_TITLE, mChapterTitle);
        values.put(PAGE_NUMBER, mPageNumber);
        return values;
    }

    @Override
    public void fromCursor(Cursor c) {
        mChapterId = c.getInt(c.getColumnIndex(CHAPTER_ID));
        mNovelId = c.getInt(c.getColumnIndex(NOVEL_ID));
        mChapterNumber = c.getInt(c.getColumnIndex(CHAPTER_NUMBER));
        mChapterTitle = c.getString(c.getColumnIndex(CHAPTER_TITLE));
        mPageNumber = c.getInt(c.getColumnIndex(PAGE_NUMBER));
    }

    @Override
    public String toString() {
        return mChapterTitle;
    }
}
