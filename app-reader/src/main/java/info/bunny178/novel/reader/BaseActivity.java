package info.bunny178.novel.reader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import info.bunny178.util.PreferenceProvider;

/**
 * @author ISHIMARU Sohei on 2016/01/08.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceProvider pp = new PreferenceProvider(this);
        String theme = pp.readString(R.string.pref_key_theme);
        if (getString(R.string.theme_value_dark).equals(theme)) {
            setTheme(R.style.Theme_Bunny178_Dark);
        } else if (getString(R.string.theme_value_light).equals(theme)) {
            setTheme(R.style.Theme_Bunny178);
        }

        super.onCreate(savedInstanceState);
    }
}
