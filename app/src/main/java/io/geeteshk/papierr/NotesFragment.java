package io.geeteshk.papierr;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

public class NotesFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    Button mGotIt;
    CardView mCardView;
    CustomAdapter mAdapter;
    LinearLayout mLayout;
    ListView mListView;

    /**
     * Data required for each note.
     */
    Boolean[] mStars;
    String[] mTitles, mContents, mTimes;

    /**
     * Intent code to represent a note being edited or created.
     */
    public static final int EDIT_CODE = 21;

    public NotesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        mTitles = FileHandler.getTitles(getActivity());
        mContents = FileHandler.getContents(getActivity());
        mStars = FileHandler.getStars(getActivity());
        mTimes = FileHandler.getTimes(getActivity());

        mLayout = (LinearLayout) rootView.findViewById(R.id.layout);
        mCardView = (CardView) rootView.findViewById(R.id.cardview);
        mGotIt = (Button) rootView.findViewById(R.id.got_it);
        mGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardView.animate().scaleY(0.01f).translationY(-200f).alpha(0).setListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {}
                    @Override public void onAnimationRepeat(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLayout.removeView(mCardView);
                        getActivity().getSharedPreferences("io.geeteshk.papierr", Context.MODE_PRIVATE).edit().putBoolean("got_it", true).commit();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mLayout.removeView(mCardView);
                        getActivity().getSharedPreferences("io.geeteshk.papierr", Context.MODE_PRIVATE).edit().putBoolean("got_it", true).commit();
                    }
                });
            }
        });

        if (getActivity().getSharedPreferences("io.geeteshk.papierr", Context.MODE_PRIVATE).getBoolean("got_it", true)) {
            mLayout.removeView(mCardView);
        }

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mAdapter = new CustomAdapter(getActivity(), R.layout.list_item, mTitles, mContents, mTimes, mStars);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        FloatingActionButton button = (FloatingActionButton) rootView.findViewById(R.id.fab);
        button.attachToListView(mListView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra("position", mTitles.length);
                startActivityForResult(intent, EDIT_CODE);
            }
        });

        if (getActivity().getSharedPreferences("io.geeteshk.papierr", Context.MODE_PRIVATE).getBoolean("dark_scheme", false)) {
            mLayout.setBackgroundColor(0xff212121);
        }

        return rootView;
    }

    /**
     * Called when a list item is pressed. Allows user to edit note.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), EditActivity.class);
        intent.putExtra("title", mTitles[position]);
        intent.putExtra("text", mContents[position]);
        intent.putExtra("star", mStars[position].booleanValue());
        startActivityForResult(intent, EDIT_CODE);
    }

    /**
     * Called when a list item is long pressed. Allows user to delete note.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new MaterialDialog.Builder(getActivity())
                .title(getResources().getString(R.string.delete_note))
                .content(getResources().getString(R.string.delete_desc))
                .positiveText(getResources().getString(R.string.confirm))
                .positiveColor(0xff03a9f4)
                .negativeText(getResources().getString(R.string.cancel))
                .negativeColor(0xff03a9f4)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        getActivity().deleteFile(mTitles[position]);
                        FileHandler.removeStar(getActivity(), mTitles[position]);
                        getActivity().recreate();
                    }
                })
                .show();

        return true;
    }

    /**
     * Adapter for the list of notes.
     */
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

            RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.list_root);
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

            if (getActivity().getSharedPreferences("io.geeteshk.papierr", Context.MODE_PRIVATE).getBoolean("dark_scheme", false)) {
                layout.setBackgroundColor(0xff212121);
                title.setTextColor(0xffffffff);
            }

            return rootView;
        }
    }
}
