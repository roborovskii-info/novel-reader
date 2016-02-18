package info.bunny178.novel.reader.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author ISHIMARU Sohei on 2015/09/14.
 */
public interface TableInterface {

    /**
     * テーブルを作成するときに呼び出されるメソッド
     *
     * @param db SQLiteDatabaseのインスタンス
     */
    void onCreate(SQLiteDatabase db);

    /**
     * DBバージョンのアップグレード時に呼び出すメソッド
     *
     * @param db         SQLiteDatabaseのインスタンス
     * @param oldVersion 今までのDBバージョン
     * @param newVersion 今回新しくなるDBのバージョン
     */
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * カラム名の配列を返す。
     *
     * @return テーブルに定義されているカラム名の配列
     */
    String[] getColumnNames();

    /**
     * テーブル名を返す
     *
     * @return テーブル名
     */
    String getTableName();

    /**
     * 一位のアイテムを選択するための Where clauseを返す
     *
     * "_id = 12" などの文字列
     *
     * @param value ユニークキー
     * @return Where clause
     */
    String getItemSelector(String value);
}
