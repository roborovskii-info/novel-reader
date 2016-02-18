package info.bunny178.novel.reader.net;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.util.Log;

import java.io.IOException;
import java.util.PriorityQueue;

/**
 * @author ISHIMARU Sohei on 2015/09/08.
 */
public class RequestRunner {

    private static final String LOG_TAG = "RequestRunner";

    private static OkHttpClient sClient;

    static {
        sClient = new OkHttpClient();
    }

    public static void enqueue(Request request, Callback callback) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null.");
        }
        Log.d(LOG_TAG, "  Req : [" + request.method() + "] "  + request.urlString());
        sClient.newCall(request).enqueue(callback);
    }

    public static Response execute(Request request) throws IOException {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null.");
        }
        return sClient.newCall(request).execute();
    }
}
