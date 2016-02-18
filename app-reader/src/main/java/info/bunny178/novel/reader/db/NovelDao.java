package info.bunny178.novel.reader.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import info.bunny178.novel.reader.db.NovelTable;
import info.bunny178.novel.reader.model.Novel;

/**
 * @author ISHIMARU Sohei on 2015/09/16.
 */
public class NovelDao {

    public static int saveNovel(Context context, Novel novel) {
        ContentResolver resolver = context.getContentResolver();
        int novelId = novel.getNovelId();
        Uri uri = NovelTable.CONTENT_URI.buildUpon().appendPath(Integer.toString(novelId)).build();
        int count = resolver.update(uri, novel.toContentValues(), null, null);
        if (count == 0) {
            resolver.insert(NovelTable.CONTENT_URI, novel.toContentValues());
        }
        return novelId;
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
}
