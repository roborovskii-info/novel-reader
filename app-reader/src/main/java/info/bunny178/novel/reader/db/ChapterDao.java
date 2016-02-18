package info.bunny178.novel.reader.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import info.bunny178.novel.reader.model.Chapter;
import info.bunny178.novel.reader.model.Novel;

/**
 * @author ISHIMARU Sohei on 2015/09/16.
 */
public class ChapterDao {

    public static int saveChapter(Context context, Chapter chapter) {
        ContentResolver resolver = context.getContentResolver();
        int chapterId = chapter.getChapterId();
        String where = ChapterTable.Columns.CHAPTER_ID + " = " + chapterId;
        int count = resolver.update(ChapterTable.CONTENT_URI, chapter.toContentValues(), where, null);
        if (count == 0) {
            Uri uri = resolver.insert(ChapterTable.CONTENT_URI, chapter.toContentValues());
            if (uri != null) {
                chapterId = Integer.parseInt(uri.getLastPathSegment());
            }
        }
        return chapterId;
    }

    public static int deleteChapters(Context context, int novelId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = ChapterTable.CONTENT_URI;
        String where = ChapterTable.Columns.NOVEL_ID + " = " + novelId;

        return resolver.delete(uri, where, null);
    }

    public static Chapter loadChapter(Context context, int chapterId) {
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
}
