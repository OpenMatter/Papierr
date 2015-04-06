package io.geeteshk.papierr;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.astuetz.PagerSlidingTabStrip;
import com.melnykov.fab.FloatingActionButton;

/**
 * {@link android.app.Activity} containing a {@link android.widget.ListView} to display all notes created by user.
 */
public class MainActivity extends ActionBarActivity
{
    ImageButton mSettings;
    PagerSlidingTabStrip mStrip;
    Toolbar mToolbar;
    ViewPager mPager;

    /**
     * Intent code to represent settings being changed.
     */
    private static final int SETTINGS_CODE = 22;

    /**
     * Called when the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_name));

        mSettings = (ImageButton) findViewById(R.id.settings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(settingsIntent, SETTINGS_CODE);
            }
        });

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlideAdapter(getSupportFragmentManager()));

        mStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mStrip.setViewPager(mPager);
    }

    /**
     * Called when returning from a different activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    private class SlideAdapter extends FragmentStatePagerAdapter {

        public SlideAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new NotesFragment();
                case 1:
                    return new ListsFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.notes_tab);
                case 1:
                    return getString(R.string.lists_tab);
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
