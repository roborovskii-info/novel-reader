package info.bunny178.novel.reader.model;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @author ISHIMARU Sohei on 2015/09/14.
 */
public abstract class BaseModel {

    public abstract ContentValues toContentValues();
    public abstract void fromCursor(Cursor c);
}
