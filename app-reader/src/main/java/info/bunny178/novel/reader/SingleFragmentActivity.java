package info.bunny178.novel.reader;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

/**
 * 指定されたFragmentのみをAttachするためのActivity.
 * Attachするには、EXTRA_FRAGMENT_NAMEを指定し、その他パラメータをEXTRASに格納しておく。
 *
 * @author ISHIMARU Sohei on 2015/04/06.
 */
public class SingleFragmentActivity extends BaseActivity {

    private static final String LOG_TAG = "SingleFragmentActivity";

    public static final String EXTRA_FRAGMENT_NAME = "fragment_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "# onCreate(Bundle)");
        setContentView(R.layout.activity_simple_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            attachFragment(extras);
        }
    }

    private void attachFragment(Bundle extras) {
        if (extras == null) {
            Log.e(LOG_TAG, "Error : extras is null");
            return;
        }
        String fragmentName = extras.getString(EXTRA_FRAGMENT_NAME);
        Fragment fragment = Fragment.instantiate(this, fragmentName, extras);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container_main, fragment, fragmentName).commit();
    }
}
