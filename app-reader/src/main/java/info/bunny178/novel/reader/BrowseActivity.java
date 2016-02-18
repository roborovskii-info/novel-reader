package info.bunny178.novel.reader;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import info.bunny178.novel.reader.fragment.LocalListFragment;
import info.bunny178.novel.reader.fragment.NovelPagerFragment;
import info.bunny178.novel.reader.fragment.NovelSearchFragment;
import info.bunny178.novel.reader.fragment.SettingsFragment;
import info.bunny178.novel.reader.model.Novel;
import io.fabric.sdk.android.Fabric;

/**
 *
 * 小説を探す/選ぶ側のActivity
 *
 * @author ISHIMARU Sohei on 2015/09/08.
 */

public class BrowseActivity extends AppCompatActivity {

    private static final String LOG_TAG = "BrowseActivity";

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Log.d(LOG_TAG, "# onCreate(Bundle)");
        setContentView(R.layout.activity_browse);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
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

        setupAds();
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
            case R.id.menu_about:
                startAboutActivity();
                return true;
            case R.id.menu_search:
                startSearchActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(mNavigationListener);
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

        AdView mAdView = (AdView) findViewById(R.id.ad_view);
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

    private void startAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
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
                case R.id.drawer_settings:
                    startSettingsActivity();
                    break;
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
}
