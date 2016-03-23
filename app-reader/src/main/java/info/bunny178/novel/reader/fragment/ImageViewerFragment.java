package info.bunny178.novel.reader.fragment;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import info.bunny178.novel.reader.NovelReader;
import info.bunny178.novel.reader.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 画像のプレビューを表示するFragment
 * <p/>
 * 表示するためには必ずURLの指定が必要.
 * PhotoViewのライブラリを使用し、画像の保存が可能.
 *
 * @author ISHIMARU Sohei
 */
public class ImageViewerFragment extends Fragment {

    public static final String EXTRA_IMAGE_URL = "url";

    private static final String LOG_TAG = "ImageViewerFragment";

    private PhotoViewAttacher mAttacher;

    public static ImageViewerFragment newInstance(String url) {
        ImageViewerFragment fragment = new ImageViewerFragment();
        Bundle args = new Bundle(1);
        args.putString(EXTRA_IMAGE_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "+ onCreateView(LayoutInflater, ViewGroup, Bundle)");
        setHasOptionsMenu(true);
        NovelReader.sendScreenName(LOG_TAG);
        return inflater.inflate(R.layout.fragment_image_viewer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            getActivity().finish();
            return;
        }

        String imageUrl = args.getString(EXTRA_IMAGE_URL);
        if (imageUrl == null) {
            getActivity().finish();
            return;
        }
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image_content);
        Picasso.with(getActivity())
                .load(imageUrl)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (mAttacher != null) {
                            mAttacher.update();
                        } else {
                            mAttacher = new PhotoViewAttacher(imageView);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAttacher.cleanup();
    }
}
