package info.bunny178.novel.reader;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;

import info.bunny178.util.PreferenceProvider;

/**
 * @author ISHIMARU Sohei on 2016/01/08.
 */
public class BaseActivity extends AppCompatActivity {

    private static int sPrimaryColor;

    private static int sAccentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceProvider pp = new PreferenceProvider(this);
        String theme = pp.readString(R.string.pref_key_theme);
        if (getString(R.string.theme_value_dark).equals(theme)) {
            setTheme(R.style.Theme_Bunny178_Dark);
        } else if (getString(R.string.theme_value_cute).equals(theme)) {
            setTheme(R.style.Theme_Bunny178_Cute);
        } else if (getString(R.string.theme_value_cool).equals(theme)) {
            setTheme(R.style.Theme_Bunny178_Cool);
        } else {
            setTheme(R.style.Theme_Bunny178);
        }

        super.onCreate(savedInstanceState);
    }

    protected int getPrimaryColor() {
        if (0 < sPrimaryColor) {
            return sPrimaryColor;
        }
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, outValue, true);
        sPrimaryColor = getResources().getColor(outValue.resourceId);
        return sPrimaryColor;
    }

    protected int getAccentColor() {
        if (0 < sAccentColor) {
            return sAccentColor;
        }
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
        sAccentColor = getResources().getColor(outValue.resourceId);
        return sAccentColor;
    }
}
