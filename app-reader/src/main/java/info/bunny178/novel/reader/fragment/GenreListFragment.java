package info.bunny178.novel.reader.fragment;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import info.bunny178.novel.reader.NovelReader;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.model.Genre;
import info.bunny178.novel.reader.net.GenreListRequest;
import info.bunny178.novel.reader.net.GenreListResponse;
import info.bunny178.novel.reader.net.RequestRunner;

/**
 * @author ISHIMARU Sohei on 2016/01/14.
 */
public class GenreListFragment extends Fragment {

    private static final String LOG_TAG = "GenreListFragment";

    private Handler mHandler = new Handler();

    private boolean mRequesting = false;

    private ProgressBar mProgressBar;

    private TextView mEmptyView;

    private ArrayAdapter<Genre> mAdapter;

    public static GenreListFragment newInstance() {
        return new GenreListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NovelReader.sendScreenName(LOG_TAG);
        return inflater.inflate(R.layout.fragment_genre_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, "+ onViewCreated(View, Bundle)");

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mEmptyView = (TextView) view.findViewById(R.id.text_empty);
        ListView listView = (ListView) view.findViewById(R.id.list_genre);
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mItemClickListener);

        requestGenre();
    }

    private void requestGenre() {
        if (mRequesting) {
            return;
        }
        mRequesting = true;
        mProgressBar.setVisibility(View.VISIBLE);

        GenreListRequest request = new GenreListRequest();
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

            Serializer serializer = new Persister();
            try {
                final GenreListResponse list = serializer.read(GenreListResponse.class, xml);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addAll(list.getGenreList());
                        mAdapter.notifyDataSetChanged();
                    }
                });
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

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
}
