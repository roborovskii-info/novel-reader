package info.bunny178.novel.reader.fragment;

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

import java.util.List;

import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.ViewerActivity;
import info.bunny178.novel.reader.model.Bookmark;
import info.bunny178.novel.reader.view.adapter.BookmarkAdapter;

/**
 *
 * しおり一覧のリスト
 *
 * @author ISHIMARU Sohei on 2015/12/14.
 */
public class BookmarkListFragment extends Fragment {

    private static final String LOG_TAG = "BookmarkListFragment";

    private BookmarkAdapter mAdapter;

    public static BookmarkListFragment newInstance() {
        BookmarkListFragment fragment = new BookmarkListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "+ onCreateView(LayoutInflater, ViewGroup, Bundle)");
        return inflater.inflate(R.layout.fragment_bookmark_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, "+ onViewCreated(View, Bundle)");

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mAdapter = new BookmarkAdapter(getActivity());

        List<Bookmark> chapterList = Bookmark.loadBookmarks(getActivity());
        mAdapter.addAll(chapterList);

        ListView listView = (ListView) view.findViewById(R.id.list_bookmark);
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
            Bookmark bookmark = Bookmark.load(getActivity(), (int) id);
            if (bookmark != null) {
                Intent intent = new Intent(getActivity(), ViewerActivity.class);
                intent.putExtra(ViewerActivity.EXTRA_NOVEL_ID, bookmark.getNovelId());
                intent.putExtra(ViewerActivity.EXTRA_PAGE_NUMBER, bookmark.getPageNumber());
                startActivity(intent);
            }
        }
    };
}
