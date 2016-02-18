package info.bunny178.novel.reader.net;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;

import org.w3c.dom.Text;

import android.support.annotation.IntDef;
import android.text.TextUtils;

/**
 * string sort : ソート項目
 *   keyword キーワードの一致数(デフォルト)
 *   pv_cnt 閲覧数順
 *   rate 評価（★の平均）順
 *   lastmod 更新日順
 * string order : ソート順
 *   asc 昇順
 *   desc 降順(デフォルト)
 *
 * @author ISHIMARU Sohei on 2015/09/08.
 */
public class NovelSearchRequest extends BaseRequest {

    /** キーワードの一致数順 */
    public static final String SORT_KEYWORD = "keyword";

    /** 閲覧数順 */
    public static final String SORT_VIEW_COUNT = "pv_cnt";

    /** 評価順 */
    public static final String SORT_RATING = "rate";

    /** 最終更新日時 */
    public static final String SORT_NEW_ARRIVAL = "lastmod";

    public static final String PARAM_KEYWORD = "keyword";

    @IntDef({
            RATING_EVERYONE,
            RATING_ADULT,
            RATING_ALL
    })
    public @interface RatingCategory {/* NOP */
    }

    private String mKeyword;

    @RatingCategory
    private int mRating = RATING_EVERYONE;

    private String mSortBy;

    private String mSortOrder;

    private int mGenreId;

    public void setKeyword(String keyword) {
        mKeyword = keyword;
    }

    public void setRating(int rating) {
        mRating = rating;
    }

    public void setSortBy(String sortBy) {
        mSortBy = sortBy;
    }

    public void setSortOrder(String sortOrder) {
        mSortOrder = sortOrder;
    }

    public void setGenreId(int genreId) {
        mGenreId = genreId;
    }

    @Override
    public Request build() {
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = getBaseUrl();

        urlBuilder.addPathSegment("getNovelListByKeyword.php");

        urlBuilder.addQueryParameter(PARAM_KEYWORD, mKeyword);

        if (!TextUtils.isEmpty(mSortBy)) {
            urlBuilder.addQueryParameter(PARAM_SORT, mSortBy);
        }

        if (!TextUtils.isEmpty(mSortOrder)) {
            urlBuilder.addQueryParameter(PARAM_ORDER, mSortOrder);
        }

        requestBuilder.url(urlBuilder.build());
        return requestBuilder.build();
    }
}
