package info.bunny178.novel.reader.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.bunny178.novel.reader.NovelReader;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.model.Chapter;
import info.bunny178.novel.reader.model.Novel;
import info.bunny178.novel.reader.view.adapter.ChapterListAdapter;

/**
 *
 * 指定された小説IDより、チャプターの一覧をリスト表示し、選択されたチャプターのページ番号を返す。
 *
 * @author ISHIMARU Sohei on 2015/12/14.
 */
public class ChapterListFragment extends Fragment {

    private static final String LOG_TAG = "ChapterListFragment";

    public static final String ARGS_NOVEL_ID = "novel_id";

    public static final String EXTRA_CHAPTER_ID = "chapter_id";
    public static final String EXTRA_PAGE_NUMBER = "page_number";

    private ChapterListAdapter mAdapter;


    @BindView(R.id.list_chapter)
    ListView mListView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.text_empty)
    TextView mEmptyView;

    public static ChapterListFragment newInstance(int novelId) {
        Bundle args = new Bundle(1);
        args.putInt(ARGS_NOVEL_ID, novelId);
        ChapterListFragment fragment = new ChapterListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setResult(Activity.RESULT_CANCELED);

        NovelReader.sendScreenName(LOG_TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "+ onCreateView(LayoutInflater, ViewGroup, Bundle)");
        View parent =  inflater.inflate(R.layout.fragment_chapter_list, container, false);
        ButterKnife.bind(this, parent);
        return parent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, "+ onViewCreated(View, Bundle)");
        mAdapter = new ChapterListAdapter(getActivity());

        int novelId = getArguments().getInt(ARGS_NOVEL_ID);

        Novel novel = Novel.loadNovel(getActivity(), novelId);
        if (novel != null) {
            getActivity().setTitle(novel.getTitle());
        }

        List<Chapter> chapterList = Chapter.loadChapters(getActivity(), novelId);
        mAdapter.addAll(chapterList);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);

        if (chapterList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Chapter chapter = (Chapter) mAdapter.getItem(position);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CHAPTER_ID, chapter.getChapterId());
            intent.putExtra(EXTRA_PAGE_NUMBER, chapter.getPageNumber());
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    };

}
