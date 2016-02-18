package info.bunny178.novel.reader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import info.bunny178.novel.reader.db.ChapterTable;

/**
 * @author ISHIMARU Sohei on 2015/09/18.
 */
@Root(name = "chapter")
public class Chapter extends BaseModel implements ChapterTable.Columns{

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
