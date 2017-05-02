package info.bunny178.novel.reader.view.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.bunny178.novel.reader.view.DetailActivity;
import info.bunny178.novel.reader.NovelReader;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.view.ViewerActivity;
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

    private Novel mSelectedNovel;

    @BindView(R.id.list_novel)
    ListView mListView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.text_empty)
    TextView mEmptyView;

    public static LocalListFragment newInstance() {
        return new LocalListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "+ onCreateView(LayoutInflater, ViewGroup, Bundle)");
        View parent = inflater.inflate(R.layout.fragment_local_list, container, false);
        ButterKnife.bind(this, parent);
        return parent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, "+ onViewCreated(View, Bundle)");

        mProgressBar.setVisibility(View.VISIBLE);

        mAdapter = new LocalListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);
        registerForContextMenu(mListView);
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
    public void onResume() {
        super.onResume();

        createAdapter();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_open_in_browser:
                openBrowser(mSelectedNovel);
                return true;
            case R.id.menu_remove:
                displayDeleteConfirm(mSelectedNovel);
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
        if (!mAdapter.isEmpty()) {
            mAdapter.clear();
        }
        String where = NovelTable.Columns.DOWNLOAD_DATE + " != 0";
        List<Novel> novelList = Novel.loadNovels(getActivity(), where);
        mAdapter.addAll(novelList);
        mAdapter.notifyDataSetChanged();

        if (mAdapter.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Novel data = (Novel) mAdapter.getItem(position);
            if (data != null) {
                int novelId = data.save(getActivity());
                startViewerActivity(novelId);

                NovelReader.sendEvent("Novel action", data.getTitle(), "Read novel");
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

    /**
     * 削除しますかダイアログの表示
     *
     * @param novel 削除対象の小説
     */
    private void displayDeleteConfirm(final Novel novel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(novel.getTitle())
                .setMessage(R.string.warn_delete_novel_confirm)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NovelReader.sendEvent("Novel action", novel.getTitle(), "Delete novel");
                        deleteNovel(novel.getNovelId());
                        createAdapter();
                    }
                }).setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setOwnerActivity(getActivity());
        dialog.show();
    }

    private void deleteNovel(int novelId) {
        Novel.deleteNovel(getActivity(), novelId);
        Chapter.deleteChapters(getActivity(), novelId);
        Page.deletePages(getActivity(), novelId);

        Toast.makeText(getActivity(), R.string.info_delete_complete, Toast.LENGTH_SHORT).show();
    }

    private void openBrowser(Novel novel) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(novel.getBrowserUrl()));
        startActivity(Intent.createChooser(intent, novel.getTitle()));
    }
}
