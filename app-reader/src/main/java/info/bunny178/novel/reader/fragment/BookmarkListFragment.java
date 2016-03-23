package info.bunny178.novel.reader.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import info.bunny178.novel.reader.NovelReader;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.ViewerActivity;
import info.bunny178.novel.reader.model.Bookmark;
import info.bunny178.novel.reader.model.Chapter;
import info.bunny178.novel.reader.model.Novel;
import info.bunny178.novel.reader.model.Page;
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

    private Bookmark mSelectedBookmark;

    public static BookmarkListFragment newInstance() {
        BookmarkListFragment fragment = new BookmarkListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NovelReader.sendScreenName(LOG_TAG);
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

        ListView listView = (ListView) view.findViewById(R.id.list_bookmark);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mItemClickListener);
        registerForContextMenu(listView);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        createAdapter();
    }

    private void createAdapter() {
        View parent = getView();
        if (parent == null) {
            return;
        }
        if (!mAdapter.isEmpty()) {
            mAdapter.clear();
        }

        List<Bookmark> chapterList = Bookmark.loadBookmarks(getActivity());
        mAdapter.addAll(chapterList);
        mAdapter.notifyDataSetChanged();

        if (mAdapter.isEmpty()) {
            parent.findViewById(R.id.text_empty).setVisibility(View.VISIBLE);
        } else {
            parent.findViewById(R.id.text_empty).setVisibility(View.GONE);
        }
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


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (adapterInfo == null) {
            return;
        }
        ListView listView = (ListView) v;

        Object o = listView.getItemAtPosition(adapterInfo.position);

        mSelectedBookmark = (Bookmark) o;
        android.view.MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_bookmark_list, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_remove:
                displayDeleteConfirm(mSelectedBookmark);
                return true;
        }
        return false;
    }


    /**
     * 削除しますかダイアログの表示
     *
     * @param bookmark 削除対象の小説
     */
    private void displayDeleteConfirm(final Bookmark bookmark) {
        Novel novel = Novel.loadNovel(getActivity(), bookmark.getNovelId());
        String title = novel != null ? novel.getTitle() : "Unknown";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(R.string.warn_delete_bookmark_confirm)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBookmark(bookmark.getBookmarkId());
                        createAdapter();
                    }
                }).setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setOwnerActivity(getActivity());
        dialog.show();
    }

    private void deleteBookmark(int bookmarkId) {
        Bookmark.deleteBookmark(getActivity(), bookmarkId);

        Toast.makeText(getActivity(), R.string.info_delete_complete, Toast.LENGTH_SHORT).show();
    }
}
