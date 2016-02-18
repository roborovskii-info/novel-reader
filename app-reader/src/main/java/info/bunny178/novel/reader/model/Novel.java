package info.bunny178.novel.reader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import info.bunny178.novel.reader.db.NovelTable;

/**
 * @author ISHIMARU Sohei on 2015/09/10.
 */

@Root(name = "bookdata")
public class Novel extends BaseModel implements NovelTable.Columns {

    private static final String BROWSER_URL_BASE = "http://novel.fc2.com/novel.php?mode=tc&nid=%s";

    /**
     * 執筆中を表すステータス
     */
    public static final int STATUS_WORKING = 0;

    /**
     * 完結を表すステータス
     */
    public static final int STATUS_COMPLETE = 1;

    @Element(name = "id")
    private int mNovelId;

    @Element(name = "title")
    private String mTitle;

    @Element(name = "genre")
    private String mGenreName;

    @Element(name = "author_id")
    private int mAuthorId;

    @Element(name = "author_name")
    private String mAuthorName;

    @Element(name = "last_mod")
    private Date mUpdateDate;

    @Element(name = "status")
    private int mNovelStatus;

    @Element(name = "page")
    private int mPageCount;

    @Element(name = "view")
    private int mViewCount;

    @Element(name = "review_cnt")
    private int mReviewCount;

    @Element(name = "review_avg")
    private float mRatingAverage;

    @Element(name = "adult")
    private int mContentRating;

    @Element(name = "caption", required = false)
    private String mNovelCaption;

    @Element(name = "top_img")
    private String mLargeImageUrl;

    @Element(name = "top_img_mobile")
    private String mMediumImageUrl;

    @Element(name = "top_img_thumb")
    private String mSmallImageUrl;

    @Element(name = "dl_url")
    private String mDownloadUrl;

    private Date mDownloadDate;

    private int mReadIndex;

    public Novel() {
        /* NOP */
    }

    public Novel(Cursor cursor) {
        fromCursor(cursor);
    }

    public int save(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = NovelTable.CONTENT_URI.buildUpon().appendPath(Integer.toString(mNovelId)).build();
        int count = resolver.update(uri, toContentValues(), null, null);
        if (count == 0) {
            uri = resolver.insert(NovelTable.CONTENT_URI, toContentValues());
            if (uri != null) {
                mNovelId = Integer.parseInt(uri.getLastPathSegment());
            }
        }
        return mNovelId;
    }

    public static boolean hasAnyNovel(Context context) {
        boolean hasNovel = false;
        ContentResolver resolver = context.getContentResolver();
        Cursor c = null;
        try {
            c = resolver.query(NovelTable.CONTENT_URI, null, null, null, null);
            if (c != null && 0 < c.getCount()) {
                hasNovel = true;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return hasNovel;
    }

    public static boolean hasNovel(Context context, int novelId) {
        Novel novel = loadNovel(context, novelId);
        return novel != null;
    }

    public static Novel loadNovel(Context context, int novelId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = NovelTable.CONTENT_URI.buildUpon().appendPath(Integer.toString(novelId)).build();
        Cursor c = null;

        try {
            c = resolver.query(uri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                return new Novel(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public static List<Novel> loadNovels(Context context, String where) {
        List<Novel> novelList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = NovelTable.CONTENT_URI;
        Cursor c = null;

        try {
            c = resolver.query(uri, null, where, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    novelList.add(new Novel(c));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return novelList;
    }

    public static int deleteNovel(Context context, int novelId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = NovelTable.CONTENT_URI.buildUpon().appendPath(Integer.toString(novelId)).build();
        return resolver.delete(uri, null, null);
    }

    public String getLocalizedUpdateDate(Context context) {
        DateFormat outputFormat = android.text.format.DateFormat.getLongDateFormat(context);
        return outputFormat.format(mUpdateDate);
    }

    public int getNovelId() {
        return mNovelId;
    }

    public void setNovelId(int novelId) {
        mNovelId = novelId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getGenreName() {
        return mGenreName;
    }

    public void setGenreName(String genreName) {
        mGenreName = genreName;
    }

    public int getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(int authorId) {
        mAuthorId = authorId;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    public Date getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(Date updateDate) {
        mUpdateDate = updateDate;
    }

    public int getNovelStatus() {
        return mNovelStatus;
    }

    public void setNovelStatus(int novelStatus) {
        mNovelStatus = novelStatus;
    }

    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int pageCount) {
        mPageCount = pageCount;
    }

    public int getViewCount() {
        return mViewCount;
    }

    public void setViewCount(int viewCount) {
        mViewCount = viewCount;
    }

    public String getRatingText() {
        return mRatingAverage + " (" + mReviewCount + ")";
    }

    public int getReviewCount() {
        return mReviewCount;
    }

    public void setReviewCount(int reviewCount) {
        mReviewCount = reviewCount;
    }

    public float getRatingAverage() {
        return mRatingAverage;
    }

    public void setRatingAverage(float ratingAverage) {
        mRatingAverage = ratingAverage;
    }

    public int getContentRating() {
        return mContentRating;
    }

    public void setContentRating(int contentRating) {
        mContentRating = contentRating;
    }

    public String getNovelCaption() {
        return mNovelCaption;
    }

    public void setNovelCaption(String novelCaption) {
        mNovelCaption = novelCaption;
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

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        mDownloadUrl = downloadUrl;
    }

    public Date getDownloadDate() {
        return mDownloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        mDownloadDate = downloadDate;
    }

    public int getReadIndex() {
        return mReadIndex;
    }

    public void setReadIndex(int readIndex) {
        mReadIndex = readIndex;
    }

    public String getBrowserUrl() {
        return String.format(BROWSER_URL_BASE, mNovelId);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        if (0 < mNovelId) {
            values.put(NOVEL_ID, mNovelId);
        }
        values.put(NOVEL_TITLE, mTitle);
        values.put(GENRE_NAME, mGenreName);
        values.put(AUTHOR_ID, mAuthorId);
        values.put(AUTHOR_NAME, mAuthorName);
        values.put(UPDATE_DATE, mUpdateDate.getTime());
        values.put(NOVEL_STATUS, mNovelStatus);
        values.put(PAGE_COUNT, mPageCount);
        values.put(VIEW_COUNT, mViewCount);
        values.put(REVIEW_COUNT, mReviewCount);
        values.put(RATING_AVERAGE, mRatingAverage);
        values.put(CONTENT_RATING, mContentRating);
        values.put(NOVEL_CAPTION, mNovelCaption);
        values.put(LARGE_IMAGE_URL, mLargeImageUrl);
        values.put(MEDIUM_IMAGE_URL, mMediumImageUrl);
        values.put(SMALL_IMAGE_URL, mSmallImageUrl);
        values.put(DOWNLOAD_URL, mDownloadUrl);
        values.put(READ_INDEX, mReadIndex);
        return values;
    }

    @Override
    public void fromCursor(@NonNull Cursor c) {
        mNovelId = c.getInt(c.getColumnIndex(NOVEL_ID));
        mTitle = c.getString(c.getColumnIndex(NOVEL_TITLE));
        mGenreName = c.getString(c.getColumnIndex(GENRE_NAME));
        mAuthorId = c.getInt(c.getColumnIndex(AUTHOR_ID));
        mAuthorName = c.getString(c.getColumnIndex(AUTHOR_NAME));
        long  tempDate = c.getLong(c.getColumnIndex(UPDATE_DATE));
        mUpdateDate = new Date(tempDate);
        mNovelStatus = c.getInt(c.getColumnIndex(NOVEL_STATUS));
        mPageCount = c.getInt(c.getColumnIndex(PAGE_COUNT));
        mViewCount = c.getInt(c.getColumnIndex(VIEW_COUNT));
        mReviewCount = c.getInt(c.getColumnIndex(REVIEW_COUNT));
        mRatingAverage = c.getFloat(c.getColumnIndex(RATING_AVERAGE));
        mContentRating = c.getInt(c.getColumnIndex(CONTENT_RATING));
        mNovelCaption = c.getString(c.getColumnIndex(NOVEL_CAPTION));
        mLargeImageUrl = c.getString(c.getColumnIndex(LARGE_IMAGE_URL));
        mMediumImageUrl = c.getString(c.getColumnIndex(MEDIUM_IMAGE_URL));
        mSmallImageUrl = c.getString(c.getColumnIndex(SMALL_IMAGE_URL));
        mDownloadUrl = c.getString(c.getColumnIndex(DOWNLOAD_URL));
        tempDate = c.getLong(c.getColumnIndex(DOWNLOAD_DATE));
        mDownloadDate = new Date(tempDate);
        mReadIndex = c.getInt(c.getColumnIndex(READ_INDEX));
    }

    @Override
    public String toString() {
        return mTitle;
    }

}
