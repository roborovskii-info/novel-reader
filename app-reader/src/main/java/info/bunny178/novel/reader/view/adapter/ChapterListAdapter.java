package info.bunny178.novel.reader.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.model.Chapter;

/**
 * @author ISHIMARU Sohei on 2015/12/15.
 */
public class ChapterListAdapter extends BaseAdapter {

    private List<Chapter> mDataList;

    private Context mContext;
    private LayoutInflater mInflater;

    private int[] mAccentColors;

    public ChapterListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataList = new ArrayList<>();


        int[] colorIds = {
                R.color.list_accent_1,
                R.color.list_accent_2,
                R.color.list_accent_3,
                R.color.list_accent_4,
                R.color.list_accent_5,
                R.color.list_accent_6,
                R.color.list_accent_7,
                R.color.list_accent_8,
                R.color.list_accent_9,
                R.color.list_accent_10,
                R.color.list_accent_11,
                R.color.list_accent_12,
        };
        mAccentColors = new int[colorIds.length];
        Resources r = context.getResources();
        for (int i = 0; i < colorIds.length; i++) {
            mAccentColors[i] = r.getColor(colorIds[i]);
        }
    }

    public void add(@NonNull Chapter data) {
        mDataList.add(data);
    }

    public void addAll(@NonNull List<Chapter> data) {
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
        return mDataList.get(position).getChapterId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_chapter, parent, false);
            h = new ViewHolder();
            h.numberView = (TextView) convertView.findViewById(R.id.text_chapter_number);
            h.titleView = (TextView) convertView.findViewById(R.id.text_title);
            h.pageView = (TextView) convertView.findViewById(R.id.text_page);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        Chapter chapter = mDataList.get(position);
        if (chapter != null) {
            String origin = chapter.getChapterTitle();
            char first = origin.charAt(0);
            String other = origin.substring(1, origin.length());
            h.numberView.setBackgroundColor(mAccentColors[first % mAccentColors.length]);
//            h.numberView.setText(String.valueOf(chapter.getChapterNumber()));
//            h.titleView.setText(chapter.getChapterTitle());
            h.numberView.setText(String.valueOf(first));
            h.titleView.setText(other);
            h.pageView.setText(String.format(mContext.getString(R.string.page_unit), chapter.getPageNumber()));
        }
        return convertView;
    }

    static class ViewHolder {
        TextView numberView;
        TextView titleView;
        TextView pageView;
    }
}
