package info.bunny178.novel.reader.view.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import info.bunny178.novel.reader.NovelReader;
import info.bunny178.novel.reader.R;
import info.bunny178.util.PreferenceProvider;

/**
 * @author ISHIMARU Sohei on 2015/12/15.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final String LOG_TAG = "SettingsFragment";

    private PreferenceProvider mPreference;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "+ onCreate(Bundle)");
        addPreferencesFromResource(R.xml.app_settings);

        mPreference = new PreferenceProvider(getActivity());
        NovelReader.sendScreenName(LOG_TAG);
        getActivity().setTitle(R.string.settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "+ onResume()");
        reloadSummary();
        mPreference.getSharedPreferences().registerOnSharedPreferenceChangeListener(mChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "+ onPause()");
        mPreference.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mChangeListener);
    }

    private void reloadSummary() {
        ListAdapter adapter = getPreferenceScreen().getRootAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            Object item = adapter.getItem(i);
            if (item instanceof ListPreference) {
                ListPreference preference = (ListPreference) item;
                CharSequence entry = TextUtils.isEmpty(preference.getEntry()) ? "" : preference.getEntry();
                preference.setSummary(entry);
            }
        }
    }

    private OnSharedPreferenceChangeListener mChangeListener = new OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            reloadSummary();
            /* テーマ変更はアプリ再起動してください */
            if (key.equals(getString(R.string.pref_key_theme))) {
                Toast.makeText(getActivity(), R.string.info_restart_please, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
