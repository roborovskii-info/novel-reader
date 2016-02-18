package info.bunny178.novel.reader.net;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;

import android.support.annotation.IntDef;
import android.text.TextUtils;

/**
 * http://novel.fc2.com/api/getGenreList.php
 *
 * @author ISHIMARU Sohei on 2016/01/14.
 */
public class GenreListRequest extends BaseRequest {

    @Override
    public Request build() {
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = getBaseUrl();
        urlBuilder.addPathSegment("getGenreList.php");

        requestBuilder.url(urlBuilder.build());
        return requestBuilder.build();
    }
}
