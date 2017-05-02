package info.bunny178.novel.reader.view;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.bunny178.novel.reader.NovelReader;
import info.bunny178.novel.reader.R;

/**
 * @author ISHIMARU Sohei on 2015/09/15.
 */
public class AboutActivity extends BaseActivity {

    private static final String LOG_TAG = "AboutActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.text_version)
    TextView mVersionView;

    @BindView(R.id.text_about)
    TextView mLicenseView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(4.0f);
        }

        mVersionView.setText(String.format(getString(R.string.version), getVersionName()));
        try {
            mLicenseView.setText(readFromRaw(R.raw.licenses));
        } catch (IOException e) {
            e.printStackTrace();
            mLicenseView.setText(e.getLocalizedMessage());
        }

        NovelReader.sendScreenName(LOG_TAG);
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
