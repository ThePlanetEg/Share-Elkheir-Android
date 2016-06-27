package eg.com.theplanet.akram.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.model.LatLng;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.managers.UsersManager;
import eg.com.theplanet.akram.models.User;
import eg.com.theplanet.akram.ui.fragments.AccessibilityFragment;
import eg.com.theplanet.akram.ui.fragments.ContributionsFragment;
import eg.com.theplanet.akram.utils.App;
import eg.com.theplanet.akram.utils.Constants;
import eg.com.theplanet.akram.utils.Utils;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private User user;
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        user = UsersManager.getDefaultUser(getBaseContext());
        FloatingActionButton clothesFab = (FloatingActionButton) findViewById(R.id.clothes_fab);
        if (clothesFab != null) {
            clothesFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LatLng latLng = getTargetLocation();
                    float zoomLevel = getZoomLevel();

                    Intent intent = new Intent(MainActivity.this, PickLocationActivity.class);
                    intent.putExtra(Constants.FORM_TYPE, Constants.TYPE_CLOTHES);
                    intent.putExtra(Constants.ZOOM_LEVEL, zoomLevel);
                    intent.putExtra(Constants.LATITUDE, latLng.latitude);
                    intent.putExtra(Constants.LONGITUDE, latLng.longitude);

                    startActivity(intent);
                }
            });
        }

        FloatingActionButton hotMealsFab = (FloatingActionButton) findViewById(R.id.hot_meals_fab);
        if (hotMealsFab != null) {
            hotMealsFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LatLng latLng = getTargetLocation();
                    float zoomLevel = getZoomLevel();

                    Intent intent = new Intent(MainActivity.this, PickLocationActivity.class);
                    intent.putExtra(Constants.FORM_TYPE, Constants.TYPE_HOT_MEALS);
                    intent.putExtra(Constants.ZOOM_LEVEL, zoomLevel);
                    intent.putExtra(Constants.LATITUDE, latLng.latitude);
                    intent.putExtra(Constants.LONGITUDE, latLng.longitude);

                    startActivity(intent);
                }
            });
        }


        FloatingActionButton foodFab = (FloatingActionButton) findViewById(R.id.food_boxes_fab);
        if (foodFab != null) {
            foodFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LatLng latLng = getTargetLocation();
                    float zoomLevel = getZoomLevel();
                    Intent intent = new Intent(MainActivity.this, PickLocationActivity.class);
                    intent.putExtra(Constants.FORM_TYPE, Constants.TYPE_FOOD_BOXES);
                    intent.putExtra(Constants.ZOOM_LEVEL, zoomLevel);
                    intent.putExtra(Constants.LATITUDE, latLng.latitude);
                    intent.putExtra(Constants.LONGITUDE, latLng.longitude);
                    startActivity(intent);
                }
            });
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            ImageView userImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_imageView);
            TextView usernameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username_textView);

            Glide.with(this)
                    .load(user.imageURL)
                    .placeholder(R.drawable.ic_person_white_48dp)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(userImageView);

            usernameTextView.setText(user.username);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);

        }


        if (savedInstanceState == null) {
            initializeContributionsHeader();
            fragment = new ContributionsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_view,fragment )
                    .commit();
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
    }

    private LatLng getTargetLocation() {
        LatLng latLng = new LatLng(0, 0);
//        List<Fragment> fragments = getSupportFragmentManager()
//                .getFragments();
//
//        for (Fragment fragment : fragments) {
//            if (fragment.isInLayout())
//                if (fragment instanceof ContributionsFragment) {
//                    return ((ContributionsFragment) fragment).getTargetLocation();
//                } else if (fragment instanceof AccessibilityFragment) {
//                    return ((AccessibilityFragment) fragment).getTargetLocation();
//                }
//        }

        if (fragment instanceof ContributionsFragment) {
            return ((ContributionsFragment) fragment).getTargetLocation();
        } else if (fragment instanceof AccessibilityFragment) {
            return ((AccessibilityFragment) fragment).getTargetLocation();
        }
        return latLng;
    }

    private float getZoomLevel() {
//        Fragment fragment = getSupportFragmentManager()
//                .getFragments()
//                .get(0);
//        if (fragment instanceof ContributionsFragment) {
//            return ((ContributionsFragment) fragment).getZoomLevel();
//        } else if (fragment instanceof AccessibilityFragment) {
//            return ((AccessibilityFragment) fragment).getZoomLevel();
//        }
        if (fragment instanceof ContributionsFragment) {
            return ((ContributionsFragment) fragment).getZoomLevel();
        } else if (fragment instanceof AccessibilityFragment) {
            return ((AccessibilityFragment) fragment).getZoomLevel();
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contributions) {
            initializeContributionsHeader();
            fragment = new ContributionsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_view, fragment)
                    .commit();
        } else if (id == R.id.nav_accessibility) {
            initializeSafetyHeader();
            fragment = new AccessibilityFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_view, fragment)
                    .commit();
        } else if (id == R.id.action_language) {
            changeLanguage();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_imageView:
            case R.id.username_textView:
                openProfile();
                break;
        }
    }

    private void openProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }


    private void initializeContributionsHeader() {
        findViewById(R.id.accessibility_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.contributions_layout).setVisibility(View.VISIBLE);
    }

    private void initializeSafetyHeader() {


        findViewById(R.id.accessibility_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.contributions_layout).setVisibility(View.INVISIBLE);
    }

    public void setFoodBoxesCount(String text) {
        ((TextView) findViewById(R.id.count_food_boxes_textView)).setText(text);
    }

    public void setHotMealsCount(String text) {
        ((TextView) findViewById(R.id.count_hot_meals_textView)).setText(text);

    }

    public void setClothesCount(String text) {
        ((TextView) findViewById(R.id.count_clothes_textView)).setText(text);

    }


    private void changeLanguage() {


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getBaseContext(),
                android.R.layout.select_dialog_item);
        adapter.add("English");
        adapter.add("العربية");

        new AlertDialog.Builder(this)
                .setTitle("Set Language")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                setLanguage("en");
                                break;
                            case 1:
                                setLanguage("ar");
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();


    }

    private void setLanguage(String language) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).edit();
        editor.putString(Constants.LANGUAGE, language);
        editor.apply();
        Utils.setDefaultLocale(this, language);
        finish();
        startActivity(getIntent());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.getInstance().searchResultAddress !=null)
            gotoAddress();
    }

    public void gotoAddress(){
        Address address = App.getInstance().searchResultAddress;
        Log.d("Address","a");
        if (fragment instanceof ContributionsFragment) {
             ((ContributionsFragment) fragment).gotoAddress(address);
        } else if (fragment instanceof AccessibilityFragment) {
            ((AccessibilityFragment) fragment).gotoAddress(address);
        }
    }
}
