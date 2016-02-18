package info.bunny178.novel.reader;

import com.squareup.picasso.Picasso;

import android.animation.Animator;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import info.bunny178.novel.reader.db.ChapterDao;
import info.bunny178.novel.reader.db.NovelDao;
import info.bunny178.novel.reader.db.NovelTable;
import info.bunny178.novel.reader.db.PageDao;
import info.bunny178.novel.reader.fragment.AboutFragment;
import info.bunny178.novel.reader.fragment.ChapterListFragment;
import info.bunny178.novel.reader.fragment.ImageViewerFragment;
import info.bunny178.novel.reader.fragment.SettingsFragment;
import info.bunny178.novel.reader.model.Chapter;
import info.bunny178.novel.reader.model.Novel;
import info.bunny178.novel.reader.model.Page;
import info.bunny178.util.DepthPageTransformer;
import info.bunny178.util.PreferenceProvider;

/**
 * @author ISHIMARU Sohei on 2015/12/09.
 */
public class ViewerActivity extends BaseActivity {

    private static final String LOG_TAG = "ViewerActivity";

    public static final String EXTRA_NOVEL_ID = "novel_id";

    private static final int REQUEST_CHAPTER_LIST = 1;

    private static final int HIDE_ANIM_DURATION = 300;

    /** キャッシュのサイズ。オブジェクト数で算出 */
    private static final int PAGE_CACHE_SIZE = 64;

    private LruCache<Integer, Page> mPageCache;

    private LruCache<Integer, String> mChapterCache;

    private Novel mNovel;

    private ViewPager mViewPager;

    private SeekBar mSeekBar;

    private TextView mPageView;

    private Toolbar mToolbar;

    private View mFooterView;

    private NovelPagerAdapter mAdapter;

    private LayoutInflater mLayoutInflater;

    private int mCurrentPosition;

    private boolean mShowToolBar = true;

    private float mLineSpacing;

    private int mFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        int novelId = getIntent().getIntExtra(EXTRA_NOVEL_ID, -1);
        if (0 < novelId) {
            mNovel = NovelDao.loadNovel(this, novelId);
            if (mNovel != null) {
                setTitle(mNovel.getTitle());
            }
        } else {
            /* Novel IDなしはエラー */
            Log.e(LOG_TAG, "  Error : Novel id not found.");
            finish();
            return;
        }
        mLayoutInflater = getLayoutInflater();

        mAdapter = new NovelPagerAdapter();

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setAdapter(mAdapter);

        mSeekBar = (SeekBar) findViewById(R.id.seek_page);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        mSeekBar.setMax(mNovel.getPageCount() - 1);

        mPageView = (TextView) findViewById(R.id.text_page_count);
        mFooterView = findViewById(R.id.container_footer);
        Drawable drawable = mToolbar.getBackground();
        mFooterView.setBackgroundColor(((ColorDrawable)drawable).getColor());

        mChapterCache = new LruCache<>(PAGE_CACHE_SIZE);
        mPageCache = new LruCache<>(PAGE_CACHE_SIZE);

        hideToolBar();

        /* 前回終了時のページポジションへ移動 */
        mCurrentPosition = mNovel.getReadIndex();
        mViewPager.setCurrentItem(mCurrentPosition, false);
        Log.d(LOG_TAG, "  Last page position : " + mCurrentPosition);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPreferences();
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        setPageLabel();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePagePosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewPager.removeOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_viewer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "# onActivityResult(int, int, Intent)");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHAPTER_LIST && resultCode == RESULT_OK) {
            int pageNumber = data.getIntExtra(ChapterListFragment.EXTRA_PAGE_NUMBER, 0);
            if (0 < pageNumber) {
                mCurrentPosition = pageNumber - 1;
                mViewPager.setCurrentItem(mCurrentPosition, true);
                mSeekBar.setProgress(mCurrentPosition);
                setPageLabel();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_chapter_list:
                attachChapterListFragment();
                break;
            case R.id.menu_settings:
                startSettingsActivity();
                return true;
            case R.id.menu_about:
                startAboutActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attachChapterListFragment() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.EXTRA_FRAGMENT_NAME, ChapterListFragment.class.getCanonicalName());
        intent.putExtra(ChapterListFragment.ARGS_NOVEL_ID, mNovel.getNovelId());
        startActivityForResult(intent, REQUEST_CHAPTER_LIST);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.EXTRA_FRAGMENT_NAME, SettingsFragment.class.getCanonicalName());
        startActivity(intent);
    }

    private void startAboutActivity() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.EXTRA_FRAGMENT_NAME, AboutFragment.class.getCanonicalName());
        startActivity(intent);
    }

    private void attachImageViewerFragment(String url) {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.EXTRA_FRAGMENT_NAME, ImageViewerFragment.class.getCanonicalName());
        intent.putExtra(ImageViewerFragment.EXTRA_IMAGE_URL, url);
        startActivity(intent);
    }

    private View.OnClickListener mPageTapListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, "+ onClick(View)");
            if (mShowToolBar) {
                hideToolBar();
            } else {
                showToolBar();
            }
        }
    };

    private void loadPreferences() {
        PreferenceProvider pp = new PreferenceProvider(this);
        mLineSpacing = Float.parseFloat(pp.readString(R.string.pref_key_line_spacing,
                getString(R.string.pref_default_line_spacing)));
        mFontSize = Integer.parseInt(pp.readString(R.string.pref_key_font_size,
                getString(R.string.pref_default_font_size)));
    }

    private void setPageLabel() {
        int total = mNovel.getPageCount();
        /* ページ番号は1基底 */
        String text = String.format(getString(R.string.page_progress_format), mCurrentPosition + 1, total);
        mPageView.setText(text);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            /* NOP */
        }

        @Override
        public void onPageSelected(int position) {
            Log.d(LOG_TAG, "+ onPageSelected(int) : " + position);
            mCurrentPosition = position;
            mSeekBar.setProgress(position);
            setPageLabel();

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            /* NOP */
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(LOG_TAG, "+ onProgressChanged(SeekBar, int, boolean) : " + progress);
            if (fromUser) {
                mCurrentPosition = progress;
                setPageLabel();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mViewPager.setCurrentItem(mCurrentPosition);
        }
    };

    private void showToolBar() {
        Log.d(LOG_TAG, "- showToolBar()");
        mToolbar.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);
        mPageView.setVisibility(View.VISIBLE);

        mToolbar.animate()
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(HIDE_ANIM_DURATION)
                .setListener(mAnimatorListener)
                .start();
        mToolbar.setVisibility(View.VISIBLE);
        mFooterView.animate()
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(HIDE_ANIM_DURATION)
                .start();
        mFooterView.setVisibility(View.VISIBLE);
        mPageView.animate()
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(HIDE_ANIM_DURATION)
                .start();
        mShowToolBar = true;
    }

    private void hideToolBar() {
        Log.d(LOG_TAG, "- hideToolBar()");

        mToolbar.animate()
                .alpha(0)
                .setDuration(HIDE_ANIM_DURATION)
                .setListener(mAnimatorListener)
                .start();
        mFooterView.animate()
                .alpha(0)
                .setDuration(HIDE_ANIM_DURATION)
                .start();
        mPageView.animate()
                .alpha(0)
                .setDuration(HIDE_ANIM_DURATION)
                .start();

        mShowToolBar = false;
    }

    private Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            /* NOP */
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Log.d(LOG_TAG, "  AnimatorListener#onAnimationEnd(Animator)");
            if (!mShowToolBar) {
                mToolbar.setVisibility(View.INVISIBLE);
                mFooterView.setVisibility(View.INVISIBLE);
                mPageView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            /* NOP */
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            /* NOP */
        }
    };

    private void savePagePosition() {
        ContentValues values = new ContentValues();
        values.put(NovelTable.Columns.READ_INDEX, mCurrentPosition);

        Uri uri = NovelTable.CONTENT_URI
                .buildUpon()
                .appendPath(Integer.toString(mNovel.getNovelId()))
                .build();
        getContentResolver().update(uri, values, null, null);
    }

    class NovelPagerAdapter extends PagerAdapter {

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            Log.d(LOG_TAG, "+ setPrimaryItem(ViewGroup, int, Object) : " + position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d(LOG_TAG, "+ instantiateItem(ViewGroup, int) : " + position);

            View pageRow = mLayoutInflater.inflate(R.layout.row_page, container, false);
            container.addView(pageRow);
            int pageNumber = position + 1;

            Page page = mPageCache.get(pageNumber);
            if (page == null) {
                Log.v(LOG_TAG, "  Load from DB at " + position);
                page = PageDao.loadPage(ViewerActivity.this, mNovel.getNovelId(), pageNumber);
            } else {
                Log.v(LOG_TAG, "  Page cache hit ! " + position);
            }
            if (page != null) {
                mPageCache.put(pageNumber, page);
                /* チャプター表示 */
                TextView chapterView = (TextView) pageRow.findViewById(R.id.text_chapter);
                String chapterTitle = mChapterCache.get(page.getChapterId());
                if (TextUtils.isEmpty(chapterTitle)) {
                    Chapter chapter = ChapterDao.loadChapter(ViewerActivity.this, page.getChapterId());
                    if (chapter != null) {
                        chapterTitle = chapter.toString();
                        mChapterCache.put(page.getChapterId(), chapterTitle);
                    } else {
                        chapterTitle = mNovel.getTitle();
                    }
                }
                chapterView.setText(chapterTitle);

                /* 本文表示 */
                String body = page.getPageBody();
                if (!TextUtils.isEmpty(body)) {
                    TextView bodyView = (TextView) pageRow.findViewById(R.id.text_body);
                    bodyView.setTextSize(mFontSize);
                    bodyView.setLineSpacing(bodyView.getTextSize(), mLineSpacing);
                    bodyView.setText(Html.fromHtml(body));
                    bodyView.setOnClickListener(mPageTapListener);
                }
                /* 挿絵の表示 */
                final String url = page.getLargeImageUrl();
                ImageView artView = (ImageView) pageRow.findViewById(R.id.image_artwork);
                if (TextUtils.isEmpty(url)) {
                    artView.setVisibility(View.GONE);
                } else {
                    Picasso.with(ViewerActivity.this).load(url).into(artView);
                    artView.setVisibility(View.VISIBLE);
                    artView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            attachImageViewerFragment(url);
                        }
                    });
                }
            }
            /* ページ番号表記 */
            TextView pageView = (TextView) pageRow.findViewById(R.id.text_page);
            pageView.setText(String.format(getString(R.string.page_unit), pageNumber));
            return pageRow;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d(LOG_TAG, "+ destroyItem(ViewGroup, int, Object) : position " + position);
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mNovel.getPageCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

}
