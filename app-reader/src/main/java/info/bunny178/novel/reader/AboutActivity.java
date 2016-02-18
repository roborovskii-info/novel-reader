package info.bunny178.novel.reader;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import info.bunny178.novel.reader.R;

/**
 * @author ISHIMARU Sohei on 2015/09/15.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView versionView = (TextView) findViewById(R.id.text_version);
        versionView.setText(String.format(getString(R.string.version), getVersionName()));

        TextView licenseView = (TextView) findViewById(R.id.text_about);

        try {
            licenseView.setText(readFromRaw(R.raw.licenses));
        } catch (IOException e) {
            e.printStackTrace();
            licenseView.setText(e.getLocalizedMessage());
        }
    }

    private String readFromRaw(int resId) throws IOException {
        InputStream is = getResources().openRawResource(resId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }

    private String getVersionName() {
        StringBuilder versionName = new StringBuilder();
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            versionName.append(pi.versionName).append("(").append(pi.versionCode).append(")");
        } catch (PackageManager.NameNotFoundException e) {
            versionName.append("unknown");
        }
        return versionName.toString();
    }
}
