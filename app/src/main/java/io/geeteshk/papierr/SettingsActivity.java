package io.geeteshk.papierr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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

        CheckBox autoBox = (CheckBox) findViewById(R.id.auto_box);
        autoBox.setChecked(getSharedPreferences("io.geeteshk.papierr", MODE_PRIVATE).getBoolean("enable_autosave", false));
        autoBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setResult(RESULT_OK, new Intent().putExtra("save", isChecked));
            }
        });
    }
}
