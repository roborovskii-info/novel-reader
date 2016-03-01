package info.bunny178.novel.reader.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import android.support.v13.app.FragmentStatePagerAdapter;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.bunny178.novel.reader.R;
import info.bunny178.novel.reader.net.NovelListRequest;

/**
 * @author ISHIMARU Sohei on 2015/09/15.
 */
public class NovelPagerFragment extends Fragment {

    public static NovelPagerFragment newInstance() {
        return new NovelPagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_novel_pager, container, false);

        ViewPager viewPager = (ViewPager) parent.findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentTabsAdapter(getFragmentManager(), getActivity()));

        TabLayout tabLayout = (TabLayout) parent.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        return parent;
    }

    class FragmentTabsAdapter extends FragmentStatePagerAdapter {

        private String[] sSortValues = {
                NovelListRequest.SORT_WEEKLY_RANKING,
                NovelListRequest.SORT_TOTAL_RANKING,
                NovelListRequest.SORT_DAILY_RANKING,
                NovelListRequest.SORT_NEW_ARRIVAL,
                NovelListRequest.SORT_HALL_OF_FAME,
                NovelListRequest.SORT_RATING_AVERAGE,
                NovelListRequest.SORT_BOOKMARK_COUNT,
                NovelListRequest.SORT_REVIEW_COUNT,
        };

        private int[] sLabelIds = {
                R.string.label_sort_weekly_ranking,
                R.string.label_sort_total_ranking,
                R.string.label_sort_daily_ranking,
                R.string.label_sort_new_arrival,
                R.string.label_sort_hall_of_fame,
                R.string.label_sort_rating_ranking,
                R.string.label_sort_bookmark_ranking,
                R.string.label_sort_review_ranking,
        };

        private Context context;

        public FragmentTabsAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            String value = sSortValues[position];
            return NovelListFragment.newInstance(0, 0, value);
        }

        @Override
        public int getCount() {
            return sSortValues.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return context.getString(sLabelIds[position]);
        }
    }
}
