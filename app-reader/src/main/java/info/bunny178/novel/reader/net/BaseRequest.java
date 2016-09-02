package info.bunny178.novel.reader.net;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;

import android.support.annotation.IntDef;

import info.bunny178.novel.reader.model.Novel;

/**
 * @author ISHIMARU Sohei on 2015/09/08.
 */
public abstract class BaseRequest {
    public static final String API_SCHEME = "http";
    public static final String API_HOST = "novel.fc2.com";
    public static final String API_PATH = "api";

    @IntDef({
            Novel.RATING_EVERYONE,
            Novel.RATING_ADULT,
            Novel.RATING_ALL
    })
    public @interface RatingCategory {/* NOP */}

    public static final String ORDER_ASC = "asc";
    public static final String ORDER_DESC = "desc";

    /** defaultのアイテム取得件数 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final String PARAM_KIND = "kind";
    public static final String PARAM_RATING = "adult";
    public static final String PARAM_SIZE = "kensu";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_ANY_ID = "id";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_ORDER = "order";

    /** アイテムの取得件数 */
    protected int mSize;
    /** アイテムの取得ページ番号 */
    protected int mPage;

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public HttpUrl.Builder getBaseUrl() {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme(API_SCHEME);
        builder.host(API_HOST);
        builder.addPathSegment(API_PATH);
        return builder;
    }

    public abstract Request build();
}
