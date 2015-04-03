package io.geeteshk.papierr;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

/**
 * {@link android.app.Activity} used when editing or creating a note.
 */
public class EditActivity extends ActionBarActivity {

    /**
     * {@link android.content.Context} used for different file operations.
     */
    final Context mContext = this;

    EditText mTitle, mText;
    ImageButton mShare, mStar, mDelete;
    String mName;
    Toolbar mToolbar;

    /**
     * Different booleans used within the activity.
     */
    boolean mStarBool, mDeleteBool = false, mActionBool = false;

    /**
     * Called when the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mToolbar = (Toolbar) findViewById(R.id.editbar);
        mToolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);

        mName = getIntent().getStringExtra("title");
        mStarBool = getIntent().getBooleanExtra("star", false);
        mTitle = (EditText) findViewById(R.id.edit_title);
        mText = (EditText) findViewById(R.id.edit_text);
        mShare = (ImageButton) findViewById(R.id.share);
        mStar = (ImageButton) findViewById(R.id.star);
        mDelete = (ImageButton) findViewById(R.id.delete);
        mTitle.getBackground().setColorFilter(0xffffffff, PorterDuff.Mode.SRC_IN);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mText.getText().toString());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_note)));
            }
        });

        mStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStarBool) {
                    mStar.setImageResource(R.drawable.ic_action_toggle_star_outline);
                    FileHandler.setStar(EditActivity.this, mName, false);
                    mStarBool = false;
                } else {
                    mStar.setImageResource(R.drawable.ic_action_toggle_star);
                    FileHandler.setStar(EditActivity.this, mName, true);
                    mStarBool = true;
                }
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mName != null) {
                    new MaterialDialog.Builder(mContext)
                            .title(getResources().getString(R.string.delete_note))
                            .content(getResources().getString(R.string.delete_desc))
                            .positiveText(getResources().getString(R.string.confirm))
                            .positiveColor(0xff03a9f4)
                            .negativeText(getResources().getString(R.string.cancel))
                            .negativeColor(0xff03a9f4)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    deleteFile(mName);
                                    deleteFile(mTitle.getText().toString());
                                    FileHandler.removeStar(mContext, mName);
                                    FileHandler.removeStar(mContext, mTitle.getText().toString());
                                    mDeleteBool = true;
                                    finish();
                                }
                            })
                            .show();
                } else {
                    deleteFile(mTitle.getText().toString());
                    FileHandler.removeStar(mContext, mTitle.getText().toString());
                    mDeleteBool = true;
                    finish();
                }
            }
        });

        if (getIntent().getStringExtra("title") != null && getIntent().getStringExtra("text") != null) {
            mTitle.setText(getIntent().getStringExtra("title"));
            mText.setText(getIntent().getStringExtra("text"));

            if (mStarBool) {
                mStar.setImageResource(R.drawable.ic_action_toggle_star);
            }
        }

        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.edit_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionBool) {
                    floatingActionButton.setImageResource(R.drawable.ic_content_up);
                    getSupportActionBar().show();
                    mActionBool = false;
                } else {
                    floatingActionButton.setImageResource(R.drawable.ic_content_down);
                    getSupportActionBar().hide();
                    mActionBool = true;
                }
            }
        });

        if (getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).getBoolean("enable_autosave", false)) {
            mText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!mDeleteBool) {
                        if (mName != null) {
                            deleteFile(mName);
                            FileHandler.removeStar(mContext, mName);
                        }

                        deleteFile(mTitle.getText().toString());
                        FileHandler.removeStar(mContext, mTitle.getText().toString());

                        FileHandler.writeToFile(mContext, mTitle.getText().toString(), mText.getText().toString());
                        FileHandler.setStar(mContext, mTitle.getText().toString(), mStarBool);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        if (getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).getBoolean("dark_scheme", false)) {
            mText.setBackgroundColor(0xff212121);
            mText.setTextColor(0xffffffff);
        }
    }

    /**
     * Called when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (!mDeleteBool && !getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).getBoolean("enable_autosave", false)) {
            if (mName != null) {
                deleteFile(mName);
                FileHandler.removeStar(mContext, mName);
            }

            deleteFile(mTitle.getText().toString());
            FileHandler.removeStar(mContext, mTitle.getText().toString());

            FileHandler.writeToFile(mContext, mTitle.getText().toString(), mText.getText().toString());
            FileHandler.removeStar(mContext, mName);
            FileHandler.setStar(mContext, mTitle.getText().toString(), mStarBool);
        }
    }
}
