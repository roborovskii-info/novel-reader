package info.bunny178.novel.reader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;

import info.bunny178.util.PreferenceProvider;

/**
 * @author ISHIMARU Sohei on 2016/01/08.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String LOG_TAG = "BaseActivity";

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


    protected boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(LOG_TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);

        }
    }
}
