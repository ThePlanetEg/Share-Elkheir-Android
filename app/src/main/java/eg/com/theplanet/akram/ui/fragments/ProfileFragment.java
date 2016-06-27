package eg.com.theplanet.akram.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.managers.APIManager;
import eg.com.theplanet.akram.managers.UsersManager;
import eg.com.theplanet.akram.models.Contribution;
import eg.com.theplanet.akram.models.User;
import eg.com.theplanet.akram.models.responses.ContributionsResponse;
import eg.com.theplanet.akram.ui.adapters.ContributionsAdapter;
import eg.com.theplanet.akram.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private RecyclerView mRecyclerView;
//    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Contribution> contributions;
    private ContributionsAdapter mAdapter;
    private View rootView;
    private User user;
    private boolean isLoading = false;
    private boolean isAllPagesLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        initialize();
        loadContributions();
        return rootView;
    }

    private void initialize() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llm);

        contributions = new ArrayList<>();

//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh();
//            }
//        });
//        mSwipeRefreshLayout.setRefreshing(false);

        user = UsersManager.getDefaultUser(getContext());
    }

    private void loadContributions() {
        int offset = contributions.size();
        rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        APIManager apiManager = retrofit.create(APIManager.class);


        Call<ContributionsResponse> call = apiManager
                .getUserContributions(UsersManager.getToken(getContext()),user.userID, offset);

        call.enqueue(new Callback<ContributionsResponse>() {
            @Override
            public void onResponse(Call<ContributionsResponse> call, Response<ContributionsResponse> response) {
                ContributionsResponse contributionsResponse = response.body();
                isLoading = false;
                if (contributionsResponse.contributions == null || contributionsResponse.contributions.size() == 0)
                    isAllPagesLoaded = true;

                if (contributionsResponse.contributions != null) {
                    addContributions(contributionsResponse.contributions);
                } else {
                    addContributions(new ArrayList<Contribution>());

                }
            }

            @Override
            public void onFailure(Call<ContributionsResponse> call, Throwable t) {

            }
        });

    }

    private void addContributions(ArrayList<Contribution> contributions) {

        this.contributions.addAll(contributions);

        if (mAdapter == null) {
            mAdapter = new ContributionsAdapter(getContext(), this.contributions);
            mRecyclerView.setAdapter(mAdapter);
            setupPagination();
        }
        mAdapter.notifyDataSetChanged();

    }

    private void setupPagination() {
        Paginate.Callbacks callbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                loadContributions();
                isLoading = true;

            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return isAllPagesLoaded;
            }
        };

        Paginate.with(mRecyclerView, callbacks)
                .setLoadingTriggerThreshold(Constants.PAGINATION_LIMIT)
                .addLoadingListItem(true)
                .build();
    }

    private void refresh() {

    }



}
