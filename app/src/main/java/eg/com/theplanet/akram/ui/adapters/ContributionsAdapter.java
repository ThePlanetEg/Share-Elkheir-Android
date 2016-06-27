package eg.com.theplanet.akram.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.models.Contribution;
import eg.com.theplanet.akram.ui.adapters.holders.ContributionHolder;
import eg.com.theplanet.akram.utils.Utils;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class ContributionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Contribution> mDataSet;
    private Context mContext;

    public ContributionsAdapter(Context mContext,List<Contribution> mDataSet) {
        this.mDataSet = mDataSet;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContributionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.index_contribution, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Contribution contribution = mDataSet.get(position);
        ContributionHolder contributionHolder = (ContributionHolder) holder;


        contributionHolder.areaTextView.setText(contribution.area);
        contributionHolder.timeTextView.setText(Utils.formatDateString(mContext,contribution.timestamp));
        if (contribution.type.contentEquals("hot_meals")) {

            contributionHolder.typeImageView.setImageResource(R.drawable.ic_circle_hot_meals);
        }else if (contribution.type.contentEquals("clothes")) {

            contributionHolder.typeImageView.setImageResource(R.drawable.ic_circle_clothes);
        }else {

            contributionHolder.typeImageView.setImageResource(R.drawable.ic_circle_food_boxes);

        }

        contributionHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.share(mContext,contribution);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
