package info.bunny178.novel.reader.view;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.bunny178.novel.reader.NovelReader;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.model.Novel;

import info.bunny178.novel.reader.net.NovelListRequest;
import info.bunny178.novel.reader.net.NovelListResponse;
import info.bunny178.novel.reader.net.RequestRunner;
import info.bunny178.novel.reader.service.DownloadService;
import info.bunny178.util.DateFormatTransformer;

public class DetailActivity extends BaseActivity {

    private static final String LOG_TAG = "DetailActivity";

    /**
     * Intentに付与される小説ID
     */
    public static final String EXTRA_NOVEL_ID = "novel_id";

    private static final int STATUS_REQUESTING = 0;

    private static final int STATUS_UPDATE = 600;

    private static final int STATUS_NO_DATA = 100;

    private static final int STATUS_PENDING = 190;

    private static final int STATUS_RUNNING = 192;

    private static final int STATUS_PROVIDING = 197;

    private static final int STATUS_SUCCESS = 200;

    private static final int STATUS_FILE_ERROR = 492;

    private Novel mNovelData;

    private int mNovelId;

    private InterstitialAd mInterstitialAd;

    @BindView(R.id.text_progress)
    TextView mProgressView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.btn_read)
    Button mDownloadButton;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.image_cover)
    ImageView mCoverImage;

    @BindView(R.id.text_r18)
    TextView mR18View;

    @BindView(R.id.text_status)
    TextView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_detail);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(4.0f);
        }

        setUi(STATUS_REQUESTING);
        setupAds();

        NovelReader.sendScreenName(LOG_TAG);
    }

    /**
     * インタースティシャル広告を初期化する処理。
     */
    private void setupAds() {
        String[] deviceIds = {
                /* 社内 Nexus5 */
                "3808129125D1716C",
                /* Xperia Z Ultra */
                "3E062544D47D0AA2",
                /* Xperia Z3 */
                "369C931A07D20553",
        };

        /* インタースティシャル広告の初期化と予めリクエスト */
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_unit_id));
        AdRequest.Builder builder = new AdRequest.Builder();
        for (String deviceId : deviceIds) {
            builder.addTestDevice(deviceId);
        }
        AdRequest adRequest = builder.build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                onReadButtonClicked();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle args = getIntent().getExtras();
        if (args == null) {
            Toast.makeText(this, R.string.error_novel_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
            /* IDから小説データを読み込み */
        mNovelId = args.getInt(EXTRA_NOVEL_ID);
        requestNovel(mNovelId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_open_in_browser:
                if (mNovelData != null) {
                    openBrowser(mNovelData);
                    NovelReader.sendEvent("Novel action", mNovelData.getTitle(), "Open browser");
                }
                break;
            case R.id.menu_share:
                if (mNovelData != null) {
                    shareNovel(mNovelData);
                    NovelReader.sendEvent("Novel action", mNovelData.getTitle(), "Share");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleNovelData() {
        if (mNovelData == null) {
            return;
        }
        /* カバー画像の表示 */
        String url = mNovelData.getLargeImageUrl();
        if (Patterns.WEB_URL.matcher(url).matches()) {
            displayCoverImage(url);
        } else {
            Log.e(LOG_TAG, "  URL Not matches");
        }
        bindNovelData(mNovelData);
        Novel novelData = Novel.loadNovel(this, mNovelId);
        if (novelData != null && 0 < novelData.getDownloadDate().getTime()) {
            /* ダウンロードしてあった場合、アップデートチェック */
            long local = novelData.getUpdateDate().getTime();
            long server = mNovelData.getUpdateDate().getTime();
            if (local < server) {
                /* アップデートしろ表示 */
                setUi(STATUS_UPDATE);
            } else {
                /* 最新でダウンロード済 */
                setUi(STATUS_SUCCESS);
            }
        } else {
            /* ダウンロードしろ表示 */
            setUi(STATUS_NO_DATA);
        }

        setTitle(mNovelData.getTitle());
    }

    private void requestNovel(int novelId) {
        setUi(STATUS_REQUESTING);

        NovelListRequest request = new NovelListRequest();
        request.setNovelId(novelId);

        RequestRunner.enqueue(request.build(), mCallback);
    }

    private Handler mHandler = new Handler();

    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            Log.d(LOG_TAG, "+ onFailure(Request, IOException)");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    handleNovelData();
                }
            });
        }

        @Override
        public void onResponse(Response response) throws IOException {
            Log.d(LOG_TAG, "+ onResponse(Response)");
            String xml = response.body().string();
            Log.d(LOG_TAG, "  " + xml);

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            RegistryMatcher matcher = new RegistryMatcher();
            matcher.bind(Date.class, new DateFormatTransformer(format));

            Serializer serializer = new Persister(matcher);
            try {
                final NovelListResponse list = serializer.read(NovelListResponse.class, xml);
                List<Novel> novels = list.getNovelList();
                if (0 < novels.size()) {
                    mNovelData = novels.get(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    handleNovelData();
                }
            });
        }
    };

    private void shareNovel(Novel novel) {
        if (novel == null) {
            Toast.makeText(this, R.string.error_novel_data_is_null, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, novel.getBrowserUrl());
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void openBrowser(Novel novel) {
        if (novel == null) {
            Toast.makeText(this, R.string.error_novel_data_is_null, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(novel.getBrowserUrl()));
        startActivity(Intent.createChooser(intent, novel.getTitle()));
    }

    private void displayCoverImage(String url) {
        Log.d(LOG_TAG, "- displayCoverImage(String)");
        Log.d(LOG_TAG, "  URL : " + url);
        Picasso.with(this).load(url).into(mCoverImage);
    }

    private void setUi(int status) {
        switch (status) {
            case STATUS_RUNNING:
                /* ダウンロード中 */
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.setText(R.string.notification_download_running);
                mDownloadButton.setEnabled(false);
                break;
            case STATUS_UPDATE:
                /* アップデートあります */
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.INVISIBLE);
                mDownloadButton.setText(R.string.update);
                mDownloadButton.setEnabled(true);
                break;
            case STATUS_SUCCESS:
                /* 完了 */
                mProgressView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mDownloadButton.setText(R.string.read);
                mDownloadButton.setEnabled(true);
                break;
            case STATUS_NO_DATA:
                /* 未ダウンロード */
                mProgressView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mDownloadButton.setText(R.string.download);
                mDownloadButton.setEnabled(true);
                break;
            case STATUS_PENDING:
                /* 待機中 */
                mProgressView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressView.setText(R.string.notification_download_pending);
                mDownloadButton.setEnabled(false);
                break;
            case STATUS_PROVIDING:
                /* データのパース中 */
                mProgressView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressView.setText(R.string.notification_download_providing);
                mDownloadButton.setEnabled(false);
                break;
            case STATUS_FILE_ERROR:
                /* ファイルエラー */
                mProgressView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressView.setText(R.string.notification_download_file_error);
                mDownloadButton.setEnabled(true);
                break;
            case STATUS_REQUESTING:
                /* データリクエスト中 */
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mDownloadButton.setEnabled(false);
                break;
            default:
                /* エラー */
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.setText(R.string.notification_download_failed);
                mDownloadButton.setEnabled(true);
                break;
        }
    }

    private void bindNovelData(Novel novelData) {
        if (novelData.getNovelStatus() == Novel.STATUS_COMPLETE) {
            mStatusView.setVisibility(View.VISIBLE);
        } else {
            mStatusView.setVisibility(View.GONE);
        }

        if (novelData.getContentRating() == Novel.RATING_ADULT) {
            mR18View.setVisibility(View.VISIBLE);
        } else {
            mR18View.setVisibility(View.GONE);
        }

        String title = novelData.getTitle();
        bindTextField(R.id.text_title, title);

        String author = novelData.getAuthorName();
        bindTextField(R.id.text_author, author);

        String genre = novelData.getGenreName();
        bindTextField(R.id.text_genre, genre);

        String pageCount = String.format(Locale.JAPAN, "%1$,3d", novelData.getPageCount());
        bindTextField(R.id.text_page_count, pageCount);

        String rating = novelData.getRatingText();
        bindTextField(R.id.text_review_count, rating);

        String viewCount = String.format(Locale.JAPAN, "%1$,3d", novelData.getViewCount());
        bindTextField(R.id.text_view_count, viewCount);

        String updateDate = novelData.getLocalizedUpdateDate(this);
        bindTextField(R.id.text_update_date, updateDate);

        if (!TextUtils.isEmpty(novelData.getNovelCaption())) {
            CharSequence caption = Html.fromHtml(novelData.getNovelCaption());
            bindTextField(R.id.text_caption, caption);
        }
    }

    private void bindTextField(@IdRes int resId, CharSequence text) {
        TextView tv = (TextView) findViewById(resId);
        tv.setText(text);
    }

    @OnClick(R.id.btn_read)
    void onDownloadButtonClicked() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            onReadButtonClicked();
        }
    }

    private void onReadButtonClicked() {
        Novel novelData = Novel.loadNovel(this, mNovelId);
        if (novelData != null && 0 < novelData.getDownloadDate().getTime()) {
            /* ダウンロードしてあった場合、アップデートチェック */
            long local = novelData.getUpdateDate().getTime();
            long server = mNovelData.getUpdateDate().getTime();
            if (local < server) {
                /* アップデート */
                Intent intent = new Intent(this, DownloadService.class);
                intent.putExtra(DownloadService.EXTRA_NOVEL_ID, mNovelData.getNovelId());
                intent.putExtra(DownloadService.EXTRA_RECEIVER, mReceiver);
                startService(intent);

                NovelReader.sendEvent("Novel action", mNovelData.getTitle(), "Update novel");
            } else {
                /* 読む */
                Intent intent = new Intent(this, ViewerActivity.class);
                intent.putExtra(ViewerActivity.EXTRA_NOVEL_ID, mNovelData.getNovelId());
                startActivity(intent);

                NovelReader.sendEvent("Novel action", mNovelData.getTitle(), "Read novel");
            }
        } else {
            /* 新規ダウンロード */
            mNovelData.save(this);
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra(DownloadService.EXTRA_NOVEL_ID, mNovelData.getNovelId());
            intent.putExtra(DownloadService.EXTRA_RECEIVER, mReceiver);
            startService(intent);

            NovelReader.sendEvent("Novel action", mNovelData.getTitle(), "Download novel");
        }
    }

    private ResultReceiver mReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int status, Bundle resultData) {
            Log.d(LOG_TAG, "# ResultReceiver#onReceiveResult(int, Bundle) : " + status);
            setUi(status);

            if (status == STATUS_SUCCESS) {
                mNovelData.save(DetailActivity.this);
            }
        }
    };

}
