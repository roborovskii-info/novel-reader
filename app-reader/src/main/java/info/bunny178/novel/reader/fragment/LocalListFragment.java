package info.bunny178.novel.reader.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import info.bunny178.novel.reader.DetailActivity;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.ViewerActivity;
import info.bunny178.novel.reader.db.NovelTable;
import info.bunny178.novel.reader.model.Chapter;
import info.bunny178.novel.reader.model.Novel;
import info.bunny178.novel.reader.model.Page;
import info.bunny178.novel.reader.view.adapter.LocalListAdapter;

/**
 * @author ISHIMARU Sohei on 2015/12/14.
 */
public class LocalListFragment extends Fragment {

    private static final String LOG_TAG = "LocalListFragment";

    private LocalListAdapter mAdapter;

    private Handler mHandler = new Handler();
    private Novel mSelectedNovel;
    private ProgressBar mProgressBar;

    public static LocalListFragment newInstance() {
        return new LocalListFragment();
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

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        createAdapter();

        ListView listView = (ListView) view.findViewById(R.id.list_novel);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mItemClickListener);
        registerForContextMenu(listView);

        mProgressBar.setVisibility(View.GONE);
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
        inflater.inflate(R.menu.context_local_list, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.menu_open_in_browser:
                openBrowser(mSelectedNovel);
                return true;
            case R.id.menu_remove:
                deleteNovel(mSelectedNovel.getNovelId());
                createAdapter();
                return true;
            case R.id.menu_show_details:
                startDetailsActivity(mSelectedNovel.getNovelId());
                return true;
        }
        return false;
    }

    private void createAdapter() {
        View parent = getView();
        if (parent == null) {
            return;
        }
        if (mAdapter == null) {
            mAdapter = new LocalListAdapter(getActivity());
        } else {
            mAdapter.clear();
        }
        String where = NovelTable.Columns.DOWNLOAD_DATE + " != 0";
        List<Novel> novelList = Novel.loadNovels(getActivity(), where);
        mAdapter.addAll(novelList);
        mAdapter.notifyDataSetChanged();

        if (mAdapter.isEmpty()) {
            parent.findViewById(R.id.container_empty).setVisibility(View.VISIBLE);
        } else {
            parent.findViewById(R.id.container_empty).setVisibility(View.GONE);
        }
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Novel data = (Novel) mAdapter.getItem(position);
            if (data != null) {
                int novelId = data.save(getActivity());
                startViewerActivity(novelId);
            }
        }
    };

    private void startDetailsActivity(int novelId) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_NOVEL_ID, novelId);
        startActivity(intent);
    }

    private void startViewerActivity(int novelId) {
        Intent intent = new Intent(getActivity(), ViewerActivity.class);
        intent.putExtra(ViewerActivity.EXTRA_NOVEL_ID, novelId);
        startActivity(intent);
    }

    private void deleteNovel(int novelId) {
        Novel.deleteNovel(getActivity(), novelId);
        Chapter.deleteChapters(getActivity(), novelId);
        Page.deletePages(getActivity(), novelId);
    }

    private void openBrowser(Novel novel) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(novel.getBrowserUrl()));
        startActivity(Intent.createChooser(intent, novel.getTitle()));
    }
}
