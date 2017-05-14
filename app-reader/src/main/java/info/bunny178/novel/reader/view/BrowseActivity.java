package info.bunny178.novel.reader.view;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.view.fragment.BookmarkListFragment;
import info.bunny178.novel.reader.view.fragment.LocalListFragment;
import info.bunny178.novel.reader.view.fragment.NovelPagerFragment;
import info.bunny178.novel.reader.view.fragment.NovelSearchFragment;
import info.bunny178.novel.reader.view.fragment.SettingsFragment;
import info.bunny178.novel.reader.model.Novel;
import info.bunny178.util.AlertDialogFragment;
import info.bunny178.util.PreferenceProvider;
import io.fabric.sdk.android.Fabric;

/**
 * 小説を探す/選ぶ側のActivity
 *
 * @author ISHIMARU Sohei on 2015/09/08.
 */

public class BrowseActivity extends BaseActivity {

    private static final String LOG_TAG = "BrowseActivity";

    /**
     * URLに付与される小説ID。ブラウザなどから起動された場合に使用する。
     */
    private static final String QUERY_NOVEL_ID = "nid";

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.ad_view)
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Log.d(LOG_TAG, "# onCreate(Bundle)");
        setContentView(R.layout.activity_browse);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(4.0f);
        }

        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            if (Novel.hasAnyNovel(this)) {
                attachLocalList();
                setTitle(R.string.read_novel);
            } else {
                attachNovelList();
                setTitle(R.string.find_novel);
            }
        }

        /* 広告の設定 */
        setupAds();

        /* 起動回数でレビュー */
        handleLaunchCount();

        /* URLから起動した場合 */
        Uri uri = getIntent().getData();
        if (uri != null) {
            String nid = uri.getQueryParameter(QUERY_NOVEL_ID);
            if (nid == null || !TextUtils.isDigitsOnly(nid)) {
                Toast.makeText(this, R.string.error_novel_not_found, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            int novelId = Integer.parseInt(nid);
            startNovelDetailActivity(novelId);
        } else {
            handleReleaseNote();
        }
    }


    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "# onBackPressed()");
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_browse, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_search:
                startSearchActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleLaunchCount() {
        /* レビュー書いてのダイアログ */
        PreferenceProvider pp = new PreferenceProvider(this);
        int launchCount = pp.readInt(R.string.pref_key_launch_count, 0);
        if (0 < launchCount && launchCount % 4 == 0) {
            boolean review = pp.readBoolean(R.string.pref_key_write_review, false);
            if (!review) {
                showReviewDialog();
            }
        }
        launchCount++;
        pp.writeInt(R.string.pref_key_launch_count, launchCount);
    }

    /**
     * バージョンが変わっていたら更新内容を表示する
     */
    private void handleReleaseNote() {
        PreferenceProvider pp = new PreferenceProvider(this);
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            int versionCode = pp.readInt(R.string.pref_key_version_code);
            if (versionCode < pi.versionCode) {
                AlertDialogFragment fragment = AlertDialogFragment.newInstance(this,
                        R.string.info_release_note_title,
                        R.string.info_release_note, 0);
                fragment.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int witch) {
                        dialog.dismiss();
                    }
                });
                fragment.show(getSupportFragmentManager(), "release note");

                pp.writeInt(R.string.pref_key_version_code, pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            /* NOP */
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(mNavigationListener);
        View header = navigationView.inflateHeaderView(R.layout.navigation_header);

        /* メニューのアイコンをアクセントカラーで塗りつぶす */
        Menu menu = navigationView.getMenu();
        int size = menu.size();

        int accent = super.getAccentColor();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.getIcon().setColorFilter(accent, PorterDuff.Mode.MULTIPLY);
        }

        int primary = super.getPrimaryColor();
        if (header != null) {
            ImageView headerImage = (ImageView) header.findViewById(R.id.image_header);
            headerImage.setColorFilter(primary, PorterDuff.Mode.OVERLAY);
        } else {
            Log.e(LOG_TAG, "  Header is null");
        }
    }

    private void setupAds() {
        String[] deviceIds = {
                /* 社内 Nexus5 */
                "3808129125D1716C",
                /* Xperia Z Ultra */
                "3E062544D47D0AA2",
                /* Xperia Z3 */
                "369C931A07D20553",
        };

        AdRequest.Builder builder = new AdRequest.Builder();
        for (String deviceId : deviceIds) {
            builder.addTestDevice(deviceId);
        }
        AdRequest adRequest = builder.build();
        mAdView.loadAd(adRequest);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.EXTRA_FRAGMENT_NAME, SettingsFragment.class.getCanonicalName());
        startActivity(intent);
    }

    private void startSearchActivity() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.EXTRA_FRAGMENT_NAME, NovelSearchFragment.class.getCanonicalName());
        startActivity(intent);
    }

    private OnNavigationItemSelectedListener mNavigationListener = new OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            int menuId = menuItem.getItemId();
            switch (menuId) {
                case R.id.drawer_find:
                    attachNovelList();
                    setTitle(menuItem.getTitle());
                    break;
                case R.id.drawer_read:
                    attachLocalList();
                    setTitle(menuItem.getTitle());
                    break;
                case R.id.drawer_bookmark:
                    attachBookmarkList();
                    setTitle(menuItem.getTitle());
                    break;
                case R.id.drawer_settings:
                    startSettingsActivity();
                    break;
                case R.id.drawer_browser:
                    startFc2Novel();
            }
            mDrawerLayout.closeDrawers();
            return true;
        }
    };

    private void attachNovelList() {
        Fragment fragment = NovelPagerFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_main, fragment);
        ft.commit();
    }

    private void attachLocalList() {
        Fragment fragment = LocalListFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_main, fragment);
        ft.commit();
    }

    private void attachBookmarkList() {
        Fragment fragment = BookmarkListFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_main, fragment);
        ft.commit();
    }

    private void startNovelDetailActivity(int novelId) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_NOVEL_ID, novelId);
        startActivity(intent);
    }

    private void showReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.info_write_review);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_google_play)));
                startActivity(intent);
                PreferenceProvider pp = new PreferenceProvider(BrowseActivity.this);
                pp.writeBoolean(R.string.pref_key_write_review, true);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOwnerActivity(this);
        dialog.show();
    }

    private void startFc2Novel() {
        String uri = getString(R.string.url_fc2_novel);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }
}
