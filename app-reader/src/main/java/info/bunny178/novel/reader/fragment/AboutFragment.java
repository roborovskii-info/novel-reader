package info.bunny178.novel.reader.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import info.bunny178.novel.reader.R;

/**
 * @author ISHIMARU Sohei on 2015/09/15.
 */
public class AboutFragment extends Fragment {

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_about, container, false);

        TextView versionView = (TextView) parent.findViewById(R.id.text_version);
        versionView.setText(String.format(getString(R.string.version), getVersionName()));

        TextView licenseView = (TextView) parent.findViewById(R.id.text_about);

        try {
            licenseView.setText(readFromRaw(R.raw.licenses));
        } catch (IOException e) {
            e.printStackTrace();
            licenseView.setText(e.getLocalizedMessage());
        }
        return parent;
    }

    private String readFromRaw(int resId) throws IOException {
        InputStream is = getActivity().getResources().openRawResource(resId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }

    private String getVersionName() {
        StringBuilder versionName = new StringBuilder();
        Activity activity = getActivity();
        try {
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            versionName.append(pi.versionName).append("(").append(pi.versionCode).append(")");
        } catch (PackageManager.NameNotFoundException e) {
            versionName.append("unknown");
        }
        return versionName.toString();
    }
}
