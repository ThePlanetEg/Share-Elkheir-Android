package eg.com.theplanet.akram.ui.adapters;

import android.content.Context;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.ui.adapters.holders.SearchHolder;
import eg.com.theplanet.akram.utils.Utils;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Address> mDataSet;
    private Context mContext;
    public OnClickListener listener;

    public SearchAdapter(Context mContext, List<Address> mDataSet) {
        this.mDataSet = mDataSet;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.index_search, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Address address = mDataSet.get(position);
        SearchHolder searchHolder = (SearchHolder) holder;


        searchHolder.titleTextView.setText(address.getFeatureName());
        String line = Utils.addressLineFromAddress(address);
        if (line.contentEquals(""))
            searchHolder.addressTextView.setVisibility(View.GONE);
        else
            searchHolder.addressTextView.setVisibility(View.VISIBLE);

        searchHolder.addressTextView.setText(line);


        searchHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)
                    listener.onClick(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface OnClickListener{
        void onClick(Address address);
    }
}
