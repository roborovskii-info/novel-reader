package info.bunny178.novel.reader.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import info.bunny178.novel.reader.model.Genre;
import info.bunny178.novel.reader.model.Novel;

/**
 * @author ISHIMARU Sohei on 2015/09/16.
 */
public class GenreDao {

    public static void saveGenre(Context context, Genre genre) {
        ContentResolver resolver = context.getContentResolver();
        int genreId = genre.getGenreId();
        Uri uri = NovelTable.CONTENT_URI.buildUpon().appendPath(Integer.toString(genreId)).build();
        int count = resolver.update(uri, genre.toContentValues(), null, null);
        if (count == 0) {
            resolver.insert(NovelTable.CONTENT_URI, genre.toContentValues());
        }
    }

    public static int saveGenres(Context context, List<Genre> genres) {
        ContentResolver resolver = context.getContentResolver();
        int size = genres.size();
        ContentValues[] valuesArray = new ContentValues[size];
        for (int i = 0; i < size; i++) {
            valuesArray[i] = genres.get(i).toContentValues();
        }
        return resolver.bulkInsert(GenreTable.CONTENT_URI, valuesArray);
    }

    public static List<Genre> loadGenres(Context context) {
        List<Genre> genreList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = GenreTable.CONTENT_URI;
        Cursor c = null;
        try {
            c = resolver.query(uri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    genreList.add(new Genre(c));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return genreList;
    }

    /**
     * ジャンルの全件削除
     *
     * @param context コンテキスト
     * @return 削除件数
     */
    public static int deleteGenres(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = GenreTable.CONTENT_URI;
        return resolver.delete(uri, null, null);
    }
}
