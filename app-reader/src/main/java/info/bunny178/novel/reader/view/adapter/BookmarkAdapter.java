package info.bunny178.novel.reader.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.model.Bookmark;
import info.bunny178.novel.reader.model.Novel;
import info.bunny178.novel.reader.model.Page;

/**
 * [Novel title]    [page]
 * [Body...              ]
 *
 * @author ISHIMARU Sohei on 2016/03/02.
 */
public class BookmarkAdapter extends BaseAdapter {

    private List<Bookmark> mDataList;

    private Context mContext;

    private LayoutInflater mInflater;

    public BookmarkAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataList = new ArrayList<>();
    }

    public void add(@NonNull Bookmark data) {
        mDataList.add(data);
    }

    public void addAll(@NonNull List<Bookmark> data) {
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
        return mDataList.get(position).getBookmarkId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_bookmark, parent, false);
            h = new ViewHolder();
            h.numberView = (TextView) convertView.findViewById(R.id.text_page_number);
            h.titleView = (TextView) convertView.findViewById(R.id.text_title);
            h.bodyView = (TextView) convertView.findViewById(R.id.text_body);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        Bookmark bookmark = mDataList.get(position);
        if (bookmark != null) {
            Novel novel = Novel.loadNovel(mContext, bookmark.getNovelId());
            Page page = Page.loadPage(mContext, bookmark.getNovelId(), bookmark.getPageNumber());
            if (page != null) {
                int pageNumber = page.getPageNumber();
                h.numberView.setText(String.format(mContext.getString(R.string.page_unit), pageNumber));
                h.bodyView.setText(Html.fromHtml(page.getPageBody()));
            }
            if (novel != null) {
                h.titleView.setText(novel.getTitle());
            }

        }
        return convertView;
    }

    static class ViewHolder {

        TextView numberView;

        TextView titleView;

        TextView bodyView;
    }
}
