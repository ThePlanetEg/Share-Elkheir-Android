package eg.com.theplanet.akram.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.ui.fragments.FormFragment;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class FormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Fragment fragment = new FormFragment();
        fragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_view, fragment)
                .commit();

    }
}
