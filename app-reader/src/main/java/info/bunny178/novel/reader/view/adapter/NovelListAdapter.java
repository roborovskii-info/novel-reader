package info.bunny178.novel.reader.view.adapter;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.model.Novel;

/**
 * 小説のデータをバインドするリストアダプタ
 *
 * @author ISHIMARU Sohei on 2015/09/14.
 */
public class NovelListAdapter extends BaseAdapter {

    private List<Novel> mDataList;

    private Context mContext;
    private LayoutInflater mInflater;

    public NovelListAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataList = new ArrayList<>();
        mContext = context;
    }

    public void add(@NonNull Novel data) {
        mDataList.add(data);
    }

    public void addAll(@NonNull List<Novel> data) {
        mDataList.addAll(data);
    }

    public synchronized void clear() {
        mDataList.clear();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).getNovelId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_novel, parent, false);
            h = new ViewHolder(convertView);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        Novel data = mDataList.get(position);
        if (data != null) {
            h.titleView.setText(data.getTitle());
            h.authorView.setText(data.getAuthorName());
            h.genreView.setText(data.getGenreName());

            String url = data.getLargeImageUrl();
            if (Patterns.WEB_URL.matcher(url).matches()) {
                Picasso.with(mContext).load(url).into(h.coverView);
            }

            String ratingAvg = data.getRatingText();
            h.ratingView.setText(String.format(mContext.getString(R.string.rating_format), ratingAvg));

            if (data.getNovelStatus() == Novel.STATUS_COMPLETE) {
                h.statusView.setText(R.string.status_complete);
                h.statusView.setVisibility(View.VISIBLE);
            } else {
                h.statusView.setVisibility(View.GONE);
            }

            if (data.getContentRating() == Novel.RATING_ADULT) {
                h.r18View.setVisibility(View.VISIBLE);
            } else {
                h.r18View.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.image_cover)
        ImageView coverView;
        @BindView(R.id.text_title)
        TextView titleView;
        @BindView(R.id.text_author)
        TextView authorView;
        @BindView(R.id.text_genre)
        TextView genreView;
        @BindView(R.id.text_rating)
        TextView ratingView;
        @BindView(R.id.text_status)
        TextView statusView;
        @BindView(R.id.text_r18)
        TextView r18View;

        ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
