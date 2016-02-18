package info.bunny178.novel.reader.net;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;

import android.support.annotation.IntDef;
import android.text.TextUtils;

/**
 * @author ISHIMARU Sohei on 2015/09/08.
 */
public class NovelListRequest extends BaseRequest {

    /** 新着小説順 */
    public static final String SORT_NEW_ARRIVAL = "new";
    /** 殿堂入り */
    public static final String SORT_HALL_OF_FAME = "fame";
    /** 総合ランキング */
    public static final String SORT_TOTAL_RANKING = "rank_gnrl";
    /** デイリーランキング */
    public static final String SORT_DAILY_RANKING = "rank_day";
    /** ウィークリーランキング */
    public static final String SORT_WEEKLY_RANKING = "rank_wk";
    /** 評価順 */
    public static final String SORT_RATING_AVERAGE = "rank_rtn";
    /** ブックマーク順 */
    public static final String SORT_BOOKMARK_COUNT = "rank_bm";
    /** コメント数順 */
    public static final String SORT_REVIEW_COUNT = "rank_opnn";

    /** 小説IDで検索 */
    public static final String SEARCH_BY_NOVEL_ID = "by_id";

    /** 著者IDで検索 */
    public static final String SEARCH_BY_AUTHOR_ID = "by_author";

    /** ジャンルIDで検索 */
    public static final String SEARCH_BY_GENRE_ID = "by_genre";


    @RatingCategory
    private int mRating;

    private String mSortOrder;

    private int mNovelId;

    private int mAuthorId;

    private int mGenreId;


    public void setRating(@RatingCategory int rating) {
        mRating = rating;
    }

    public void setSortKind(String sortOrder) {
        mSortOrder = sortOrder;
    }

    public void setNovelId(int novelId) {
        mNovelId = novelId;
    }

    public void setAuthorId(int authorId) {
        mAuthorId = authorId;
    }

    public void setGenreId(int genreId) {
        mGenreId = genreId;
    }

    @Override
    public Request build() {
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = getBaseUrl();

        urlBuilder.addPathSegment("getNovelList.php");
        if (0 < mNovelId) {
            urlBuilder.addQueryParameter(PARAM_KIND, SEARCH_BY_NOVEL_ID);
            urlBuilder.addQueryParameter(PARAM_ANY_ID, String.valueOf(mNovelId));
        } else if (0 < mAuthorId) {
            urlBuilder.addQueryParameter(PARAM_KIND, SEARCH_BY_AUTHOR_ID);
            urlBuilder.addQueryParameter(PARAM_ANY_ID, String.valueOf(mAuthorId));
        } else if (!TextUtils.isEmpty(mSortOrder)) {
            urlBuilder.addQueryParameter(PARAM_KIND, mSortOrder);
        } else {
            urlBuilder.addQueryParameter(PARAM_KIND, SORT_NEW_ARRIVAL);
        }
        if (0 < mGenreId) {
            urlBuilder.addQueryParameter(PARAM_KIND, SEARCH_BY_GENRE_ID);
            urlBuilder.addQueryParameter(PARAM_ANY_ID, String.valueOf(mGenreId));
        }
        if (mRating == RATING_ADULT || mRating == RATING_ALL || mRating == RATING_EVERYONE) {
            urlBuilder.addQueryParameter(PARAM_RATING, Integer.toString(mRating));
        }
        if (0 < mSize) {
            urlBuilder.addQueryParameter(PARAM_SIZE, Integer.toString(mSize));
        }
        urlBuilder.addQueryParameter(PARAM_PAGE, Integer.toString(mPage + 1));

        requestBuilder.url(urlBuilder.build());
        return requestBuilder.build();
    }
}
