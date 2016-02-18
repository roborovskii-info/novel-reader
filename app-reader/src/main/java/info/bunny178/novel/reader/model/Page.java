package info.bunny178.novel.reader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

import info.bunny178.novel.reader.db.PageTable;


/**
 * @author ISHIMARU Sohei on 2015/09/18.
 */
@Root(name = "page")
public class Page extends BaseModel implements PageTable.Columns{

    private int mPageId;
    private int mChapterId;
    private int mNovelId;

    @Element(name = "page_number")
    private int mPageNumber;
    @Element(name = "last_mod")
    private Date mUpdateDate;
    @Element(name = "body", required = false)
    private String mPageBody;
    @Element(name = "image", required = false)
    private String mLargeImageUrl;
    @Element(name = "image_mobile", required = false)
    private String mMediumImageUrl;
    @Element(name = "image_thumb", required = false)
    private String mSmallImageUrl;

    public Page() {
        /* NOP */
    }

    public Page(Cursor cursor) {
        fromCursor(cursor);
    }

    public int getPageId() {
        return mPageId;
    }

    public void setPageId(int pageId) {
        mPageId = pageId;
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

    public int getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(int pageNumber) {
        mPageNumber = pageNumber;
    }

    public Date getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(Date updateDate) {
        mUpdateDate = updateDate;
    }

    public String getPageBody() {
        return mPageBody;
    }

    public void setPageBody(String pageBody) {
        mPageBody = pageBody;
    }

    public String getLargeImageUrl() {
        return mLargeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        mLargeImageUrl = largeImageUrl;
    }

    public String getMediumImageUrl() {
        return mMediumImageUrl;
    }

    public void setMediumImageUrl(String mediumImageUrl) {
        mMediumImageUrl = mediumImageUrl;
    }

    public String getSmallImageUrl() {
        return mSmallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        mSmallImageUrl = smallImageUrl;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        if (0 < mPageId) {
            values.put(PAGE_ID, mPageId);
        }
        values.put(CHAPTER_ID, mChapterId);
        values.put(NOVEL_ID, mNovelId);
        values.put(PAGE_NUMBER, mPageNumber);
        values.put(UPDATE_DATE, mUpdateDate.getTime());
        values.put(PAGE_BODY, mPageBody);
        values.put(IMAGE_URL_LARGE, mLargeImageUrl);
        values.put(IMAGE_URL_MEDIUM, mMediumImageUrl);
        values.put(IMAGE_URL_SMALL, mSmallImageUrl);

        return values;
    }

    @Override
    public void fromCursor(Cursor c) {
        mPageId = c.getInt(c.getColumnIndex(PAGE_ID));
        mChapterId = c.getInt(c.getColumnIndex(CHAPTER_ID));
        mNovelId = c.getInt(c.getColumnIndex(NOVEL_ID));
        mPageNumber = c.getInt(c.getColumnIndex(PAGE_NUMBER));
        long tempUpdate = c.getLong(c.getColumnIndex(UPDATE_DATE));
        mUpdateDate = new Date(tempUpdate);
        mPageBody = c.getString(c.getColumnIndex(PAGE_BODY));
        mLargeImageUrl = c.getString(c.getColumnIndex(IMAGE_URL_LARGE));
        mMediumImageUrl = c.getString(c.getColumnIndex(IMAGE_URL_MEDIUM));
        mSmallImageUrl = c.getString(c.getColumnIndex(IMAGE_URL_SMALL));
    }
}
