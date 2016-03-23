package info.bunny178.novel.reader;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

/**
 * @author ISHIMARU Sohei on 2016/01/08.
 */
public class NovelReader extends Application {

    public static Tracker sTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        if (sTracker == null) {
            sTracker = GoogleAnalytics.getInstance(this).newTracker("UA-73227111-7");
        }
    }

    public static void sendEvent(String category, String action, String label) {
        /* Google Analytics v4 */
        sTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    public static void sendScreenName(String screenName) {
        sTracker.setScreenName(screenName);
        sTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
