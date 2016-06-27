package eg.com.theplanet.akram.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.ui.adapters.SearchAdapter;
import eg.com.theplanet.akram.utils.App;


/**
 * Created by georgenaiem on 1/21/16.
 */
public class SearchActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView searchRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        searchRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        searchRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecyclerView.setLayoutManager(llm);
//        searchRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            if (query.contentEquals(""))
                return;

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(query);
            }
            search(query);
//            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
//            findViewById(R.id.recyclerView).setVisibility(View.GONE);
        }
    }

    private void search(String query) {
        Geocoder geoCoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;
        try {

            addresses = geoCoder.getFromLocationName(query, 20);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final List<Address> finalAddresses = addresses;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSearch(finalAddresses);

            }
        });

    }

    private void setSearch(List<Address> addresses) {
//        findViewById(R.id.progressBar).setVisibility(View.GONE);
        searchRecyclerView.setVisibility(View.VISIBLE);
        if (addresses == null)
            addresses = new ArrayList<>();
        SearchAdapter searchAdapter = new SearchAdapter(this, addresses);
        searchRecyclerView.setAdapter(searchAdapter);
        searchAdapter.listener = new SearchAdapter.OnClickListener() {
            @Override
            public void onClick(Address address) {
                App.getInstance().searchResultAddress= address;
                finish();

            }
        };
    }
}
