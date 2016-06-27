package eg.com.theplanet.akram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.managers.UsersManager;
import eg.com.theplanet.akram.models.User;
import eg.com.theplanet.akram.ui.fragments.ProfileFragment;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_view,new ProfileFragment())
                .commit();

        User user = UsersManager.getDefaultUser(getBaseContext());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(user.username);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                if (isTaskRoot()) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
