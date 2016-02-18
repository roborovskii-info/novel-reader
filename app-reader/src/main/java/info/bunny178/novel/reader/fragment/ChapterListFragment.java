package info.bunny178.novel.reader.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.db.ChapterDao;
import info.bunny178.novel.reader.db.NovelDao;
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "+ onCreateView(LayoutInflater, ViewGroup, Bundle)");
        return inflater.inflate(R.layout.fragment_chapter_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, "+ onViewCreated(View, Bundle)");

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mAdapter = new ChapterListAdapter(getActivity());

        int novelId = getArguments().getInt(ARGS_NOVEL_ID);

        Novel novel = NovelDao.loadNovel(getActivity(), novelId);
        if (novel != null) {
            getActivity().setTitle(novel.getTitle());
        }

        List<Chapter> chapterList = ChapterDao.loadChapters(getActivity(), novelId);
        mAdapter.addAll(chapterList);

        ListView listView = (ListView) view.findViewById(R.id.list_chapter);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mItemClickListener);

        if (chapterList.isEmpty()) {
            view.findViewById(R.id.text_empty).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.text_empty).setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
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
