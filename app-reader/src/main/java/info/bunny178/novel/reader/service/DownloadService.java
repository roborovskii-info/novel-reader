package info.bunny178.novel.reader.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.db.NovelTable;
import info.bunny178.novel.reader.model.Chapter;
import info.bunny178.novel.reader.model.DownloadData;
import info.bunny178.novel.reader.model.Novel;
import info.bunny178.novel.reader.model.Page;
import info.bunny178.util.DateFormatTransformer;

/**
 * @author ISHIMARU Sohei on 2015/12/09.
 */
public class DownloadService extends IntentService {

    private static final String LOG_TAG = "DownloadService";

    public static final String EXTRA_NOVEL_ID = "novel_id";
    public static final String EXTRA_RECEIVER = "receiver";

    private static final int NOTIFICATION_ID = 0x140;

    public static final int STATUS_INIT = 100;
    public static final int STATUS_PENDING = 190;
    public static final int STATUS_RUNNING = 192;
    public static final int STATUS_PROVIDING = 197;
    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNKNOWN_ERROR = 491;
    public static final int STATUS_FILE_ERROR = 492;
    public static final int STATUS_HTTP_EXCEPTION = 496;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private ResultReceiver mReceiver;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        if (!extras.containsKey(EXTRA_NOVEL_ID)) {
            Log.e(LOG_TAG, "  Extra data must contains Novel ID");
            return;
        }
        mReceiver = extras.getParcelable(EXTRA_RECEIVER);
        int novelId = extras.getInt(EXTRA_NOVEL_ID);
        Novel novel = Novel.loadNovel(this, novelId);
        if (novel == null) {
            Log.e(LOG_TAG, "  Novel data not found. Novel id - " + novelId);
            return;
        }
        updateNotification(novel.getTitle(), STATUS_RUNNING);
        String filePath = doDownload(novelId, novel.getDownloadUrl());
        if (TextUtils.isEmpty(filePath)) {
            Log.e(LOG_TAG, "  Cannot save the file." + novelId);
            updateNotification(novel.getTitle(), STATUS_FILE_ERROR);
            return;
        }
        updateNotification(novel.getTitle(), STATUS_PROVIDING);
        Log.d(LOG_TAG, "  Download file path : " + filePath);
        cleanUpNovel(novelId);
        parseText(filePath, novelId);
        updateDownloadDate(novelId);
        updateNotification(novel.getTitle(), STATUS_SUCCESS);
    }

    /**
     * サーバーからgzip圧縮かかったファイルをダウンロードし、解凍したXML状態で保存する。
     */
    private String doDownload(int novelId, String downloadUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        String filePath = null;
        try {
            Response response = client.newCall(request).execute();
            InputStream is = response.body().byteStream();
            GZIPInputStream gzis = new GZIPInputStream(is);
            filePath = getDownloadDir(novelId);
            FileOutputStream fos = new FileOutputStream(filePath);
            byte[] line = new byte[1024];
            int size;
            while (true) {
                size = gzis.read(line);
                if (size <= 0) {
                    break;
                }
                fos.write(line, 0, size);
            }
            gzis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private void cleanUpNovel(int novelId) {
        Chapter.deleteChapters(this, novelId);
        Page.deletePages(this, novelId);
    }

    private void parseText(String path, int novelId) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        RegistryMatcher matcher = new RegistryMatcher();
        matcher.bind(Date.class, new DateFormatTransformer(format));

        Serializer serializer = new Persister(matcher);
        try {
            DownloadData data = serializer.read(DownloadData.class, new File(path));

            List<Chapter> chapterList = data.getChapterList();
            if (chapterList != null) {
                int chapterNumber = 1;
                int pageNumber = 0;
                for (Chapter chapter : chapterList) {
                    chapter.setNovelId(novelId);
                    chapter.setChapterNumber(chapterNumber);
                    chapter.setPageNumber(pageNumber + 1);

                    int chapterId = chapter.save(this);
                    Log.d(LOG_TAG, chapter.toString());
                    List<Page> pages = chapter.getPageList();
                    if (pages != null) {
                        for (Page page : pages) {
                            Log.d(LOG_TAG, "  Page." + page.getPageNumber());
                            page.setNovelId(novelId);
                            page.setChapterId(chapterId);
                            pageNumber = page.getPageNumber();
                        }
                        Page.savePages(this, pages);
                    }
                    chapterNumber++;
                }
            }
            Log.d(LOG_TAG, "  Insert end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDownloadDir(int novelId) throws IOException {
        File dir = new File(getCacheDir(), novelId + ".xml");
        return dir.getCanonicalPath();
    }

    private void updateNotification(String title, int status) {
        Log.d(LOG_TAG, "- updateNotification(int, int)");
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new NotificationCompat.Builder(this);
        }
        NotificationCompat.Builder builder = mNotificationBuilder;
        if (status == STATUS_RUNNING) {
            /* ダウンロード中 */
            builder.setSmallIcon(R.drawable.stat_sys_download);
            builder.setContentText(getString(R.string.notification_download_running));
            builder.setOngoing(true);

        } else if (status == STATUS_SUCCESS) {
            /* 完了 */
            builder.setSmallIcon(R.drawable.stat_sys_download_done);
            builder.setContentText(getString(R.string.notification_download_success));
            builder.setProgress(0, 0, false);
            builder.setOngoing(false);
        } else if (status == STATUS_PENDING) {
            /* 待機中 */
            builder.setSmallIcon(R.drawable.stat_sys_download);
            builder.setContentText(getString(R.string.notification_download_pending));
            builder.setProgress(0, 0, true);
            builder.setOngoing(false);
        } else if (status == STATUS_PROVIDING) {
            /* データのパース中 */
            builder.setSmallIcon(R.drawable.stat_sys_download);
            builder.setContentText(getString(R.string.notification_download_providing));
            builder.setProgress(0, 0, true);
            builder.setOngoing(false);
        } else if (status == STATUS_FILE_ERROR) {
            /* ファイルエラー */
            builder.setSmallIcon(R.drawable.stat_sys_download);
            builder.setContentText(getString(R.string.notification_download_file_error));
            builder.setProgress(0, 0, true);
            builder.setOngoing(false);
        } else {
            /* エラー */
            builder.setSmallIcon(R.drawable.stat_sys_download_done);
            builder.setContentText(getString(R.string.notification_download_failed));
            builder.setProgress(0, 0, false);
            builder.setOngoing(false);
        }
        builder.setContentTitle(title);
        mNotificationManager.cancel(NOTIFICATION_ID);
        mNotificationManager.notify("dl", NOTIFICATION_ID, builder.build());

        if (mReceiver != null) {
            mReceiver.send(status, null);
        }
    }

    private void updateDownloadDate(int novelId) {
        ContentValues values = new ContentValues(1);
        values.put(NovelTable.Columns.DOWNLOAD_DATE, System.currentTimeMillis());

        String where = NovelTable.Columns.NOVEL_ID + " = " + novelId;
        ContentResolver resolver = getContentResolver();
        resolver.update(NovelTable.CONTENT_URI,  values, where, null);
    }
}
