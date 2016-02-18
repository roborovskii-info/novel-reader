package info.bunny178.novel.reader;

import com.google.android.gms.ads.InterstitialAd;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import info.bunny178.novel.reader.model.Novel;

import info.bunny178.novel.reader.service.DownloadService;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "DetailActivity";

    public static final String EXTRA_NOVEL_ID = "novel_id";

    private Novel mNovelData;

    private int mNovelId;

    private InterstitialAd mInterstitialAd;

    private TextView mProgressView;

    private Button mDownloadButton;

    private int[] sActionIds = {
            R.id.btn_read,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        for (int id : sActionIds) {
            findViewById(id).setOnClickListener(mOnClickListener);
        }
        mProgressView = (TextView) findViewById(R.id.text_progress);
        mDownloadButton = (Button) findViewById(R.id.btn_read);

        /* インタースティシャル広告の初期化と予めリクエスト */
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_unit_id));

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
        mNovelData = Novel.loadNovel(this, mNovelId);
        if (mNovelData == null) {
            Toast.makeText(this, R.string.error_novel_not_found, Toast.LENGTH_SHORT).show();
            finish();
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

        if (0 < mNovelData.getDownloadDate().getTime()) {
            setUi(DownloadService.STATUS_SUCCESS);
        } else {
            mDownloadButton.setText(R.string.download);
        }

        setTitle(mNovelData.getTitle());
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
                openBrowser(mNovelData);
                break;
            case R.id.menu_share:
                shareNovel(mNovelData);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareNovel(Novel novel) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, novel.getBrowserUrl());
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void openBrowser(Novel novel) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(novel.getBrowserUrl()));
        startActivity(Intent.createChooser(intent, novel.getTitle()));
    }

    private void displayCoverImage(String url) {
        Log.d(LOG_TAG, "- displayCoverImage(String)");
        Log.d(LOG_TAG, "  URL : " + url);
        ImageView imageView = (ImageView) findViewById(R.id.image_cover);
        Picasso.with(this).load(url).into(imageView);
    }

    private void setUi(int status) {
        switch (status) {
            case DownloadService.STATUS_RUNNING:
                /* ダウンロード中 */
                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.setText(R.string.notification_download_running);
                mDownloadButton.setEnabled(false);
                break;
            case DownloadService.STATUS_SUCCESS:
                /* 完了 */
                mProgressView.setVisibility(View.INVISIBLE);
                mDownloadButton.setText(R.string.read);
                mDownloadButton.setEnabled(true);
                mNovelData = Novel.loadNovel(this, mNovelId);
                break;
            case DownloadService.STATUS_INIT:
                /* 未ダウンロード */
                mProgressView.setVisibility(View.INVISIBLE);
                mDownloadButton.setText(R.string.download);
                mDownloadButton.setEnabled(true);
                break;
            case DownloadService.STATUS_PENDING:
                /* 待機中 */
                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.setText(R.string.notification_download_pending);
                mDownloadButton.setEnabled(false);
                break;
            case DownloadService.STATUS_PROVIDING:
                /* データのパース中 */
                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.setText(R.string.notification_download_providing);
                mDownloadButton.setEnabled(false);
                break;
            case DownloadService.STATUS_FILE_ERROR:
                /* ファイルエラー */
                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.setText(R.string.notification_download_file_error);
                mDownloadButton.setEnabled(true);
                break;
            default:
                /* エラー */
                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.setText(R.string.notification_download_failed);
                mDownloadButton.setEnabled(true);
                break;
        }
    }

    private void bindNovelData(Novel novelData) {
        String title = novelData.getTitle();
        bindTextField(R.id.text_title, title);

        String author = novelData.getAuthorName();
        bindTextField(R.id.text_author, author);

        String genre = novelData.getGenreName();
        bindTextField(R.id.text_genre, genre);

        String pageCount = String.format("%1$,3d", novelData.getPageCount());
        bindTextField(R.id.text_page_count, pageCount);

        String rating = novelData.getRatingText();
        bindTextField(R.id.text_review_count, rating);

        String viewCount = String.format("%1$,3d", novelData.getViewCount());
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

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = DetailActivity.this;
            int id = v.getId();
            switch (id) {
                case R.id.btn_read:
                    if (0 < mNovelData.getDownloadDate().getTime()) {
                        Intent intent = new Intent(context, ViewerActivity.class);
                        intent.putExtra(ViewerActivity.EXTRA_NOVEL_ID, mNovelData.getNovelId());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, DownloadService.class);
                        intent.putExtra(DownloadService.EXTRA_NOVEL_ID, mNovelData.getNovelId());
                        intent.putExtra(DownloadService.EXTRA_RECEIVER, mReceiver);
                        startService(intent);
                    }
                    break;
            }
        }
    };

    private ResultReceiver mReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int status, Bundle resultData) {
            Log.d(LOG_TAG, "# ResultReceiver#onReceiveResult(int, Bundle) : " + status);
            setUi(status);
        }
    };

}
