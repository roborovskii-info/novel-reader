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

import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.model.Novel;

/**
 * 小説のデータをバインドするリストアダプタ
 *
 * @author ISHIMARU Sohei on 2015/09/14.
 */
public class LocalListAdapter extends BaseAdapter {

    private List<Novel> mDataList;

    private Context mContext;
    private LayoutInflater mInflater;

    public LocalListAdapter(Context context) {
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
            convertView = mInflater.inflate(R.layout.row_local_novel, parent, false);
            h = new ViewHolder();
            h.coverView = (ImageView) convertView.findViewById(R.id.image_cover);
            h.titleView = (TextView) convertView.findViewById(R.id.text_title);
            h.authorView = (TextView) convertView.findViewById(R.id.text_author);
            h.progressView = (TextView) convertView.findViewById(R.id.text_progress);
            h.pageView = (TextView) convertView.findViewById(R.id.text_page);
            h.statusView = (TextView) convertView.findViewById(R.id.text_status);
            h.r18View = (TextView) convertView.findViewById(R.id.text_r18);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        Novel data = mDataList.get(position);
        if (data != null) {
            h.titleView.setText(data.getTitle());

            String author = mContext.getString(R.string.author_format);
            h.authorView.setText(String.format(author, data.getAuthorName()));

            String pageBase = mContext.getString(R.string.page_progress_format);
            int current = data.getReadIndex();
            int total = data.getPageCount();

            String pageCount = String.format(pageBase, current + 1, total);
            h.pageView.setText(pageCount);

            String progress = String.format("%.1f", (float) current / (float) total * 100.0f) + "%";
            h.progressView.setText(progress);

            String url = data.getLargeImageUrl();
            if (Patterns.WEB_URL.matcher(url).matches()) {
                Picasso.with(mContext).load(url).into(h.coverView);
            }

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
        ImageView coverView;
        TextView titleView;
        TextView authorView;
        TextView progressView;
        TextView pageView;
        TextView statusView;
        TextView r18View;
    }
}
