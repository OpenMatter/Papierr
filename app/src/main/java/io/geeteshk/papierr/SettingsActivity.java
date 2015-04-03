package io.geeteshk.papierr;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * {@link android.app.Activity} used to edit settings within the app.
 */
public class SettingsActivity extends ActionBarActivity {

    /**
     * Called when the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_bar);
        toolbar.setTitle(getResources().getString(R.string.action_settings));

        final LinearLayout mSettingsLayout = (LinearLayout) findViewById(R.id.settings_layout);
        final TextView autoTitle = (TextView) findViewById(R.id.auto_title);
        final TextView darkTitle = (TextView) findViewById(R.id.dark_title);

        CheckBox autoBox = (CheckBox) findViewById(R.id.auto_box);
        autoBox.setChecked(getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).getBoolean("enable_autosave", false));
        autoBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).edit().putBoolean("enable_autosave", isChecked).commit();
            }
        });

        CheckBox darkBox = (CheckBox) findViewById(R.id.dark_box);
        darkBox.setChecked(getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).getBoolean("dark_scheme", false));
        darkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).edit().putBoolean("dark_scheme", isChecked).commit();
                if (isChecked) {
                    Integer colorFrom = 0xffffffff;
                    Integer colorTo = 0xff212121;
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            mSettingsLayout.setBackgroundColor((Integer) animator.getAnimatedValue());
                        }

                    });

                    colorAnimation.start();

                    autoTitle.setTextColor(0xffffffff);
                    darkTitle.setTextColor(0xffffffff);
                } else {
                    Integer colorFrom = 0xff212121;
                    Integer colorTo = 0xffffffff;
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            mSettingsLayout.setBackgroundColor((Integer) animator.getAnimatedValue());
                        }

                    });

                    colorAnimation.start();

                    autoTitle.setTextColor(0xff000000);
                    darkTitle.setTextColor(0xff000000);
                }
            }
        });

        if (getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).getBoolean("dark_scheme", false)) {
            mSettingsLayout.setBackgroundColor(0xff212121);
            autoTitle.setTextColor(0xffffffff);
            darkTitle.setTextColor(0xffffffff);
        }
    }
}
