package info.bunny178.util;

import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * SharedPreferencesのラッパークラス. Preference用のキーを,リソースIDで指定できる.
 *
 * @author ISHIMARU Sohei
 */
public class PreferenceProvider {

    private Context mContext;

    private SharedPreferences mPrefs;

    private String mFilename = "default";

    /**
     * デフォルトのコンストラクタ.このコンストラクタでインスタンスを生成した場合は,デフォルトのSharedPreferenceで書込・読込を行う. ファイル名を指定する場合は,
     * {@link PreferenceProvider#PreferenceProvider(Context, String)}で初期化を行う.
     *
     * @see PreferenceProvider#PreferenceProvider(Context, String)
     */
    public PreferenceProvider(Context context) {
        this(context, "");
    }

    public PreferenceProvider(Context context, SharedPreferences pref) {
        mContext = context;
        mPrefs = pref;
    }

    /**
     * 引数で,ファイル名の指定がない場合はデフォルトのSharedPreferences,ファイル名の指定がある場合は, そのファイル名のSharedPreferencesを作成・読込を行う.
     *
     * @param context  コンテキスト
     * @param filename SharedPreferencesのファイル名.指定がない場合はデフォルトのSharedPreference
     */
    public PreferenceProvider(Context context, String filename) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        mContext = context;
        if (TextUtils.isEmpty(filename)) {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            mFilename = filename;
            mPrefs = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE);
        }
    }

    /**
     * {@link PreferenceProvider#contains(String)}を参照.
     *
     * @param keyId 調べるSharedPreferencesキーのリソースID
     * @return SharedPreferences内に指定されたキーがあるかどうか.
     */
    public boolean contains(int keyId) {
        return contains(mContext.getString(keyId));
    }

    /**
     * 引数で指定されたキーが,SharedPreferences内にあるかどうかを返す.
     *
     * @param key 調べるSharedPreferencesキー
     * @return SharedPreferences内に指定されたキーがあるかどうか.
     */
    public boolean contains(String key) {
        return mPrefs.contains(key);
    }

    /**
     * SharedPreferencesを返す.削除など,細かな操作が必要な場合はこのメソッドを使用する.
     *
     * @return SharedPreferences
     */
    public SharedPreferences getSharedPreferences() {
        return mPrefs;
    }

    public boolean readBoolean(int keyId, boolean defValue) {
        return readBoolean(mContext.getString(keyId), defValue);
    }

    public boolean readBoolean(String key, boolean defValue) {
        return mPrefs.getBoolean(key, defValue);
    }

    public void writeBoolean(int keyId, boolean value) {
        writeBoolean(mContext.getString(keyId), value);
    }

    public void writeBoolean(String key, boolean value) {
        mPrefs.edit().putBoolean(key, value).apply();
    }

    /**
     * 引数で指定されたリソースIDをキーとするint型のプリファレンス値を返す. 存在しない場合は,-1を返す.デフォルト値の指定が必要な場合は,
     * {@link PreferenceProvider#readInt(int, int)} または, {@link PreferenceProvider#readInt(String, int)}
     * を利用し,デフォルト値を指定する.
     */
    public int readInt(int keyId) {
        return readInt(keyId, -1);
    }

    public int readInt(int keyId, int defValue) {
        return readInt(mContext.getString(keyId), defValue);
    }

    public int readInt(String key) {
        return readInt(key, -1);
    }

    public int readInt(String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }

    public void writeInt(int keyId, int value) {
        writeInt(mContext.getString(keyId), value);
    }

    public void writeInt(String key, int value) {
        mPrefs.edit().putInt(key, value).apply();
    }

    public long readLong(int keyId) {
        return readLong(keyId, -1);
    }

    public long readLong(int keyId, long defValue) {
        return readLong(mContext.getString(keyId), defValue);
    }

    public long readLong(String key) {
        return readLong(key, -1);
    }

    public long readLong(String key, long defValue) {
        return mPrefs.getLong(key, defValue);
    }

    public void writeLong(int keyId, long value) {
        writeLong(mContext.getString(keyId), value);
    }

    public void writeLong(String key, long value) {
        mPrefs.edit().putLong(key, value).apply();
    }

    public float readFloat(int keyId) {
        return readFloat(keyId, 0.0f);
    }

    public float readFloat(int keyId, float defValue) {
        return readFloat(mContext.getString(keyId), defValue);
    }

    public float readFloat(String key) {
        return readFloat(key, 0.0f);
    }

    public float readFloat(String key, float defValue) {
        return mPrefs.getFloat(key, defValue);
    }

    public void writeFloat(int keyId, float value) {
        writeFloat(mContext.getString(keyId), value);
    }

    public void writeFloat(String key, float value) {
        mPrefs.edit().putFloat(key, value).apply();
    }

    /**
     * 指定されたキーの文字列を返す。キーが存在しない場合は、空白の文字列を返す。
     */
    public String readString(int keyId) {
        return readString(keyId, "");
    }

    /**
     * 指定されたキーの文字列を返す。キーが存在しない場合は、引数で指定されたデフォルトの文字列を返す。
     */
    public String readString(int keyId, String defValue) {
        return readString(mContext.getString(keyId), defValue);
    }

    public String readString(String key) {
        return readString(key, "");
    }

    public String readString(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }

    public void writeString(int keyId, String value) {
        writeString(mContext.getString(keyId), value);
    }

    public void writeString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    public void clear(int keyId) {
        clear(mContext.getString(keyId));
    }

    public void clear(String key) {
        if (!mPrefs.contains(key)) {
            return;
        }
        mPrefs.edit().remove(key).apply();
    }

    public void clearAll() {
        mPrefs.edit().clear().apply();
    }

    /**
     * 保持しているキー・値のすべてを文字列化し,結果を返す.主にデバッグ用.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Map<String, ?> map = mPrefs.getAll();
        Set<String> keys = map.keySet();
        String br = System.getProperty("line.separator");
        for (String key : keys) {
            builder.append(mFilename).append(br);
            builder.append("[").append(key).append("] : ");
            builder.append(map.get(key).toString()).append(br);
        }
        return builder.toString();
    }
}
