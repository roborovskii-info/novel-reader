package info.bunny178.novel.reader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public int save(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = PageTable.CONTENT_URI.buildUpon().appendPath(Integer.toString(mPageId)).build();
        int count = resolver.update(uri, toContentValues(), null, null);
        if (count == 0) {
            uri = resolver.insert(PageTable.CONTENT_URI, toContentValues());
            if (uri != null) {
                mPageId = Integer.parseInt(uri.getLastPathSegment());
            }
        }
        return mPageId;
    }

    public static boolean hasPages(Context context, int novelId) {
        List<Page> pageList = loadPages(context, novelId);
        return 0 < pageList.size();
    }

    public static int savePages(Context context, List<Page> pages) {
        ContentResolver resolver = context.getContentResolver();
        int size = pages.size();
        ContentValues[] valuesArray = new ContentValues[size];
        for (int i = 0; i < size; i++) {
            valuesArray[i] = pages.get(i).toContentValues();
        }
        return resolver.bulkInsert(PageTable.CONTENT_URI, valuesArray);
    }

    public static int deletePages(Context context, int novelId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = PageTable.CONTENT_URI;
        String where = NOVEL_ID + " = " + novelId;

        return resolver.delete(uri, where, null);
    }

    public static List<Page> loadPages(Context context, int novelId) {
        List<Page> pageList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = PageTable.CONTENT_URI;
        Cursor c = null;
        try {
            String where = NOVEL_ID + " = " + novelId;
            String sortOrder = PAGE_NUMBER + " ASC";
            c = resolver.query(uri, null, where, null, sortOrder);
            if (c != null && c.moveToFirst()) {
                do {
                    pageList.add(new Page(c));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return pageList;
    }

    public static Page loadPage(Context context, int novelId, int pageNumber) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = PageTable.CONTENT_URI;
        String where = PAGE_NUMBER + " = " + pageNumber + " AND " + NOVEL_ID + " = " + novelId;
        Cursor c = null;
        try {
            c = resolver.query(uri, null, where, null, null);
            if (c != null && c.moveToFirst()) {
                return new Page(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
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
