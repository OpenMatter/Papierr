package io.geeteshk.papierr;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener
{
    String[] mTitles, mContents, mTimes;
    Boolean[] mStars;
    CardView mCardView;
    Button mGotIt;
    ImageButton mSettings;
    CustomAdapter mAdapter;
    LinearLayout mLayout;
    ListView mListView;
    Toolbar mToolbar;

    private static final int EDIT_CODE = 21;
    private static final int SETTINGS_CODE = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitles = FileHandler.getTitles(this);
        mContents = FileHandler.getContents(this);
        mStars = FileHandler.getStars(this);
        mTimes = FileHandler.getTimes(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_name));

        mLayout = (LinearLayout) findViewById(R.id.layout);
        mCardView = (CardView) findViewById(R.id.cardview);
        mGotIt = (Button) findViewById(R.id.got_it);
        mGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardView.animate().scaleY(0.01f).translationY(-200f).alpha(0).setListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {}
                    @Override public void onAnimationRepeat(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLayout.removeView(mCardView);
                        getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).edit().putBoolean("got_it", true).commit();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mLayout.removeView(mCardView);
                        getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).edit().putBoolean("got_it", true).commit();
                    }
                });
            }
        });

        if (getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).getBoolean("got_it", false)) {
            mLayout.removeView(mCardView);
        }

        mSettings = (ImageButton) findViewById(R.id.settings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(settingsIntent, SETTINGS_CODE);
            }
        });

        mListView = (ListView) findViewById(R.id.list_view);
        mAdapter = new CustomAdapter(this, R.layout.list_item, mTitles, mContents, mTimes, mStars);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setTextFilterEnabled(true);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        button.attachToListView(mListView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("position", mTitles.length);
                startActivityForResult(intent, EDIT_CODE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("title", mTitles[position]);
        intent.putExtra("text", mContents[position]);
        intent.putExtra("star", mStars[position].booleanValue());
        startActivityForResult(intent, EDIT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_CODE:
                recreate();
                break;
            case SETTINGS_CODE:
                if (resultCode == RESULT_OK) {
                    getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).edit().putBoolean("enable_autosave", data.getBooleanExtra("save", false)).commit();
                }
                break;
        }
    }

    private class CustomAdapter extends ArrayAdapter<String>
    {
        int mResource;
        Context mContext;
        String[] mTitles, mContents, mTimes;
        Boolean[] mStars;

        public CustomAdapter(Context context, int resource, String[] titles, String[] contents, String[] times, Boolean[] stars) {
            this(context, resource, titles);
            this.mContents = contents;
            this.mTimes = times;
            this.mStars = stars;
        }

        public CustomAdapter(Context context, int resource, String[] titles) {
            super(context, resource, titles);
            this.mContext = context;
            this.mResource = resource;
            this.mTitles = titles;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView;

            if (convertView == null) {
                rootView = inflater.inflate(mResource, parent, false);
            } else {
                rootView = convertView;
            }

            TextView title = (TextView) rootView.findViewById(R.id.item_title);
            TextView desc = (TextView) rootView.findViewById(R.id.item_desc);
            ImageView star = (ImageView) rootView.findViewById(R.id.item_star);
            TextView time = (TextView) rootView.findViewById(R.id.item_time);

            title.setText(mTitles[position]);
            desc.setText(mContents[position]);
            time.setText(mTimes[position]);

            if (mStars[position]) {
                star.setImageResource(R.drawable.ic_toggle_star_grey);
            }

            return rootView;
        }
    }
}
