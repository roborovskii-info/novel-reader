package info.bunny178.novel.reader.view.fragment;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.bunny178.novel.reader.NovelReader;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.model.Genre;
import info.bunny178.novel.reader.net.GenreListRequest;
import info.bunny178.novel.reader.net.GenreListResponse;
import info.bunny178.novel.reader.net.RequestRunner;

/**
 * getNovelListByKeyword
 *
 * @author ISHIMARU Sohei on 2015/09/09.
 */
public class NovelSearchFragment extends Fragment {

    private static final String LOG_TAG = "SearchFragment";

    @BindView(R.id.field_keyword)
    EditText mKeywordView;

    @BindView(R.id.spinner_genre)
    Spinner mGenreSpinner;

    @BindView(R.id.spinner_sort)
    Spinner mSortSpinner;

    @BindView(R.id.spinner_order)
    Spinner mOrderSpinner;

    public static NovelSearchFragment newInstance() {
//        NovelSearchFragment fragment = new NovelSearchFragment();
        return new NovelSearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "+ onCreateView(LayoutInflater, ViewGroup, Bundle)");
        NovelReader.sendScreenName(LOG_TAG);
        View parent = inflater.inflate(R.layout.fragment_novel_search, container, false);
        ButterKnife.bind(this, parent);
        return parent;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Genre> genres = Genre.loadGenres(getActivity());
        if (genres == null || genres.isEmpty()) {
            requestGenre();
        } else {
            createGenreSpinner(genres);
        }
    }

    private void createGenreSpinner(List<Genre> genres) {
        View view = getView();
        if (view == null) {
            return;
        }
        genres.add(0, new Genre(-1, getString(R.string.all)));

        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                genres);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenreSpinner.setAdapter(genreAdapter);
    }

    private void requestGenre() {
        GenreListRequest request = new GenreListRequest();
        RequestRunner.enqueue(request.build(), mCallback);
    }

    private Handler mHandler = new Handler();

    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            Log.d(LOG_TAG, "+ onFailure(Request, IOException)");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.error_network_error, Toast.LENGTH_SHORT).show();
                    }
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
                Genre.saveGenres(getActivity(), list.getGenreList());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        createGenreSpinner(list.getGenreList());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @OnClick(R.id.button_search)
    void doSearch() {
        String keyword = mKeywordView.getText().toString();
        Genre genre = (Genre) mGenreSpinner.getSelectedItem();

        Resources r = getActivity().getResources();
        String[] sortValues = r.getStringArray(R.array.search_sort_entry_values);
        String sort = sortValues[mSortSpinner.getSelectedItemPosition()];
        String[] orderValues = r.getStringArray(R.array.search_order_entry_values);
        String order = orderValues[mOrderSpinner.getSelectedItemPosition()];

        Fragment fragment = NovelListFragment.newInstance(genre.getGenreId(), keyword, sort, order);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_main, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
