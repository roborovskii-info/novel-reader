package info.bunny178.novel.reader.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import info.bunny178.novel.reader.model.Novel;
import info.bunny178.novel.reader.model.Page;

/**
 * @author ISHIMARU Sohei on 2015/12/10.
 */
public class PageDao implements PageTable.Columns {

    public static int savePage(Context context, Page page) {
        ContentResolver resolver = context.getContentResolver();
        int pageId = page.getPageId();
        Uri uri = PageTable.CONTENT_URI.buildUpon().appendPath(Integer.toString(pageId)).build();
        int count = resolver.update(uri, page.toContentValues(), null, null);
        if (count == 0) {
            resolver.insert(PageTable.CONTENT_URI, page.toContentValues());
        }
        return pageId;
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
}
