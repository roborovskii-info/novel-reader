package info.bunny178.novel.reader.fragment;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import info.bunny178.novel.reader.DetailActivity;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.model.Novel;

import info.bunny178.novel.reader.net.NovelListResponse;
import info.bunny178.novel.reader.net.NovelListRequest;
import info.bunny178.novel.reader.net.NovelSearchRequest;
import info.bunny178.novel.reader.net.RequestRunner;
import info.bunny178.novel.reader.view.adapter.NovelListAdapter;
import info.bunny178.util.DateFormatTransformer;
import info.bunny178.util.PreferenceProvider;

/**
 * @author ISHIMARU Sohei on 2015/09/09.
 */
public class NovelListFragment extends Fragment {

    private static final String LOG_TAG = "NovelListFragment";

    public static final String ARGS_GENRE_ID = "genre_id";

    public static final String ARGS_AUTHOR_ID = "author_id";

    public static final String ARGS_KIND = "kind";


    public static final String ARGS_KEYWORD = "keyword";

    public static final String ARGS_SORT = "sort";

    /**
     * 検索のときは、sort / orderが別
     */
    public static final String ARGS_ORDER = "order";


    private static final int ITEM_PER_PAGE = 20;

    private int mGenreId;
    private int mAuthorId;
    private String mSortKind;
    private String mKeyword;
    private String mSort;
    private String mOrder;

    private int mTotalItem;

    private int mPageNumber;

    private NovelListAdapter mAdapter;

    private Handler mHandler = new Handler();

    private boolean mRequesting = false;

    private ProgressBar mProgressBar;

    private View mEmptyView;

    private Novel mSelectedNovel;

    public static NovelListFragment newInstance() {
        return newInstance(0, 0, null);
    }

    public static NovelListFragment newInstance(int genreId, int authorId) {
        return newInstance(genreId, authorId, null);
    }

    public static NovelListFragment newInstance(String sortOrder) {
        return newInstance(0, 0, sortOrder);
    }

    public static NovelListFragment newInstance(int genreId, int authorId, String kind) {
        Bundle args = new Bundle();
        if (!TextUtils.isEmpty(kind)) {
            args.putString(ARGS_KIND, kind);
        }
        if (0 < genreId) {
            args.putInt(ARGS_GENRE_ID, genreId);
        }
        if (0 < authorId) {
            args.putInt(ARGS_AUTHOR_ID, authorId);
        }
        NovelListFragment fragment = new NovelListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NovelListFragment newInstance(int genreId, String keyword, String sort, String order) {
        Bundle args = new Bundle();

        if (0 < genreId) {
            args.putInt(ARGS_GENRE_ID, genreId);
        }
        if (!TextUtils.isEmpty(keyword)) {
            args.putString(ARGS_KEYWORD, keyword);
        }

        if (!TextUtils.isEmpty(sort) && !TextUtils.isEmpty(order)) {
            args.putString(ARGS_SORT, sort);
            args.putString(ARGS_ORDER, order);
        }

        NovelListFragment fragment = new NovelListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "+ onCreateView(LayoutInflater, ViewGroup, Bundle)");
        return inflater.inflate(R.layout.fragment_novel_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, "+ onViewCreated(View, Bundle)");

        Bundle args = getArguments();
        if (args != null) {
            mGenreId = args.getInt(ARGS_GENRE_ID);
            mAuthorId = args.getInt(ARGS_AUTHOR_ID);
            mSortKind = args.getString(ARGS_KIND);

            mKeyword = args.getString(ARGS_KEYWORD);
            mSort = args.getString(ARGS_SORT);
            mOrder = args.getString(ARGS_ORDER);
        }

        mAdapter = new NovelListAdapter(getActivity());

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mEmptyView = view.findViewById(R.id.container_empty);
        ListView listView = (ListView) view.findViewById(R.id.list_novel);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(mScrollListener);
        listView.setOnItemClickListener(mItemClickListener);

        if (TextUtils.isEmpty(mKeyword)) {
            requestNovelList();
        } else {
            requestNovelSearch();
            getActivity().setTitle(String.format(getString(R.string.search_result), mKeyword));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (adapterInfo == null) {
            return;
        }
        ListView listView = (ListView) v;

        Object o = listView.getItemAtPosition(adapterInfo.position);

        mSelectedNovel = (Novel) o;
        android.view.MenuInflater inflater = getActivity().getMenuInflater();
        menu.setHeaderTitle(mSelectedNovel.getTitle());
        inflater.inflate(R.menu.context_local_list, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        return false;
    }

    private void requestNovelList() {
        if (mRequesting) {
            return;
        }
        mRequesting = true;
        mProgressBar.setVisibility(View.VISIBLE);

        PreferenceProvider pp = new PreferenceProvider(getActivity());
        boolean showAll = pp.readBoolean(R.string.pref_key_show_all_content, false);

        NovelListRequest request = new NovelListRequest();

        request.setSortKind(mSortKind);
        request.setGenreId(mGenreId);
        request.setAuthorId(mAuthorId);
        request.setRating(showAll ? NovelListRequest.RATING_ALL : NovelListRequest.RATING_EVERYONE);
        request.setSize(ITEM_PER_PAGE);
        request.setPage(mPageNumber);

        RequestRunner.enqueue(request.build(), mCallback);
    }

    private void requestNovelSearch() {
        if (mRequesting) {
            return;
        }
        mRequesting = true;
        mProgressBar.setVisibility(View.VISIBLE);

        PreferenceProvider pp = new PreferenceProvider(getActivity());
        boolean showAll = pp.readBoolean(R.string.pref_key_show_all_content, false);

        NovelSearchRequest request = new NovelSearchRequest();

        request.setKeyword(mKeyword);
        request.setSortBy(mSort);
        request.setSortOrder(mOrder);
        if (0 < mGenreId) {
            request.setGenreId(mGenreId);
        }
        request.setRating(showAll ? NovelListRequest.RATING_ALL : NovelListRequest.RATING_EVERYONE);
        request.setSize(ITEM_PER_PAGE);
        request.setPage(mPageNumber);

        RequestRunner.enqueue(request.build(), mCallback);
    }

    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            Log.d(LOG_TAG, "+ onFailure(Request, IOException)");
            mRequesting = false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.INVISIBLE);
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
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addAll(list.getNovelList());
                        mAdapter.notifyDataSetChanged();
                    }
                });
                mTotalItem = list.getTotalCount();
                mPageNumber++;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mRequesting = false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyView.setVisibility(View.INVISIBLE);
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });

        }
    };

    private OnScrollListener mScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            /* NOP */
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (totalItemCount == firstVisibleItem + visibleItemCount && !mRequesting) {
                if (hasNextItem()) {
                    Log.v(LOG_TAG, "  Start request");
                    requestNovelList();
                }
            }
        }
    };

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Novel data = (Novel) mAdapter.getItem(position);
            if (data != null) {
                int novelId = data.getNovelId();
                startNovelDetailActivity(novelId);
            }
        }
    };

    private void startNovelDetailActivity(int novelId) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_NOVEL_ID, novelId);
        startActivity(intent);
    }

    private boolean hasNextItem() {
        return (mPageNumber + 1) * ITEM_PER_PAGE < mTotalItem;
    }
}
